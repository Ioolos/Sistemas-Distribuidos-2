package com.banco.microservicios;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class OperacionHandler implements HttpHandler {
    private final String tipoOperacion;
    private final Gson gson = new Gson();

    public OperacionHandler(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // No Content
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // Método no permitido
            return;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder body = new StringBuilder();
        String linea;
        while ((linea = br.readLine()) != null) {
            body.append(linea);
        }

        if (tipoOperacion.equals("transferencia")) {
            TransferenciaDTO dto = gson.fromJson(body.toString(), TransferenciaDTO.class);
            procesarTransferencia(exchange, dto);
        } else {
            OperacionDTO dto = gson.fromJson(body.toString(), OperacionDTO.class);
            if (tipoOperacion.equals("deposito")) {
                procesarDeposito(exchange, dto);
            } else if (tipoOperacion.equals("retiro")) {
                procesarRetiro(exchange, dto);
            }
        }
    }


    private void procesarDeposito(HttpExchange exchange, OperacionDTO dto) throws IOException {
        System.out.println(">> Cuenta: " + dto.getCuenta());
        System.out.println(">> Monto recibido: " + dto.getMonto());

        
        double saldoActual = consultarSaldo(dto.getCuenta());
        if (saldoActual < 0) {
            enviarRespuesta(exchange, 404, "Cuenta no encontrada");
            return;
        }

        double nuevoSaldo = saldoActual + dto.getMonto();
        if (actualizarSaldo(dto.getCuenta(), nuevoSaldo)) {
            BaseDatos.registrarMovimiento(dto.getCuenta(), "deposito", dto.getMonto());
            enviarRespuesta(exchange, 200, "Depósito exitoso. Nuevo saldo: $" + nuevoSaldo);
        } else {
            enviarRespuesta(exchange, 500, "Error al actualizar saldo");
        }
    }

    private void procesarRetiro(HttpExchange exchange, OperacionDTO dto) throws IOException {
        if (!solicitarAcceso(dto.getCuenta())) {
            enviarRespuesta(exchange, 423, "La cuenta está en uso. Intente más tarde.");
            return;
        }

        try {
            double saldoActual = consultarSaldo(dto.getCuenta());
            if (saldoActual < 0) {
                enviarRespuesta(exchange, 404, "Cuenta no encontrada");
                return;
            }

            if (dto.getMonto() > saldoActual) {
                enviarRespuesta(exchange, 400, "Saldo insuficiente");
                return;
            }

            double nuevoSaldo = saldoActual - dto.getMonto();
            if (actualizarSaldo(dto.getCuenta(), nuevoSaldo)) {
                BaseDatos.registrarMovimiento(dto.getCuenta(), "retiro", dto.getMonto());
                enviarRespuesta(exchange, 200, "Retiro exitoso. Nuevo saldo: $" + nuevoSaldo);
            } else {
                enviarRespuesta(exchange, 500, "Error al actualizar saldo");
            }
        } finally {
            liberarAcceso(dto.getCuenta());
        }
    }


    private void procesarTransferencia(HttpExchange exchange, TransferenciaDTO dto) throws IOException {
        boolean origenOk = false;
        boolean destinoOk = false;
        try {
            if (!solicitarAcceso(dto.getCuentaOrigen())) {
                enviarRespuesta(exchange, 423, "Cuenta origen en uso. Intente más tarde.");
                return;
            }
            origenOk = true;
            if (!solicitarAcceso(dto.getCuentaDestino())) {
                enviarRespuesta(exchange, 423, "Cuenta destino en uso. Intente más tarde.");
                return;
            }
            destinoOk = true;
            double saldoOrigen = consultarSaldo(dto.getCuentaOrigen());
            double saldoDestino = consultarSaldo(dto.getCuentaDestino());
            if (saldoOrigen < 0 || saldoDestino < 0) {
                enviarRespuesta(exchange, 404, "Cuenta origen o destino no encontrada");
                return;
            }
            if (dto.getMonto() <= 0 || dto.getMonto() > saldoOrigen) {
                enviarRespuesta(exchange, 400, "Saldo insuficiente o monto inválido");
                return;
            }
            double nuevoOrigen = saldoOrigen - dto.getMonto();
            double nuevoDestino = saldoDestino + dto.getMonto();
            if (actualizarSaldo(dto.getCuentaOrigen(), nuevoOrigen) &&
                actualizarSaldo(dto.getCuentaDestino(), nuevoDestino)) {

                BaseDatos.registrarMovimiento(dto.getCuentaOrigen(), "transferencia", dto.getMonto());
                BaseDatos.registrarTransferencia(dto.getCuentaOrigen(), dto.getCuentaDestino(), dto.getMonto());

                enviarRespuesta(exchange, 200, "Transferencia exitosa");
            } else {
                enviarRespuesta(exchange, 500, "Error al actualizar los saldos");
            }
        } finally {
            if (destinoOk) liberarAcceso(dto.getCuentaDestino());
            if (origenOk) liberarAcceso(dto.getCuentaOrigen());
        }
    }



    private double consultarSaldo(String cuenta) {
        try {
            URL url = new URL("http://localhost:8082/saldo?cuenta=" + cuenta);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                return Double.parseDouble(in.readLine());
            }
        } catch (Exception e) {
            System.out.println("Error al consultar saldo: " + e.getMessage());
        }
        return -1;
    }

    private boolean actualizarSaldo(String cuenta, double nuevoSaldo) {
        try {
            URL url = new URL("http://localhost:8082/saldo/actualizar");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = gson.toJson(new ActualizarSaldoDTO(cuenta, nuevoSaldo));
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            os.close();

            System.out.println(">> Actualizando saldo en cuenta: " + cuenta);
            System.out.println(">> Nuevo saldo en BD: " + nuevoSaldo);
            System.out.println(">> Código de respuesta: " + conn.getResponseCode());

            return conn.getResponseCode() == 200;

        } catch (Exception e) {
            System.out.println("Error al actualizar saldo: " + e.getMessage());
            return false;
        }
    }


    private void enviarRespuesta(HttpExchange exchange, int codigo, String mensaje) throws IOException {
        byte[] respuesta = mensaje.getBytes();
        exchange.sendResponseHeaders(codigo, respuesta.length);
        OutputStream os = exchange.getResponseBody();
        os.write(respuesta);
        os.close();
    }
    
    private boolean solicitarAcceso(String cuenta) {
        System.out.println("⚠️ LLAMANDO A solicitarAcceso PARA: " + cuenta);

        try {
            for (int i = 0; i < 3; i++) {
                URL url = new URL("http://localhost:8084/acceso");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String json = gson.toJson(new OperacionDTO(cuenta, 0)); // solo usamos el campo cuenta
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes());
                    os.flush();
                }

                int code = conn.getResponseCode();
                if (code == 200) {
                    return true;
                }

                Thread.sleep(500); // espera antes de reintentar
            }
        } catch (Exception e) {
            System.out.println("Error al solicitar acceso de concurrencia: " + e.getMessage());
        }
        return false;
    }

    private void liberarAcceso(String cuenta) {
        System.out.println("⚠️ LLAMANDO A liberarAcceso PARA: " + cuenta);

        try {
            URL url = new URL("http://localhost:8084/liberar");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = gson.toJson(new OperacionDTO(cuenta, 0));
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
                os.flush();
            }

            System.out.println(">> Se envió liberación para cuenta: " + cuenta);
            System.out.println(">> Código respuesta liberar: " + conn.getResponseCode());

        } catch (Exception e) {
            System.out.println("❌ Error al liberar acceso de concurrencia: " + e.getMessage());
        }
    }

}


    

