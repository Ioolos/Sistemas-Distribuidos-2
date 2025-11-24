package com.banco.microservicios;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;

import java.io.*;
import java.net.URI;

public class SaldoHandler implements HttpHandler {
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");

        String metodo = exchange.getRequestMethod();

        if ("OPTIONS".equalsIgnoreCase(metodo)) {
            exchange.sendResponseHeaders(204, -1); // No Content
            return;
        }

        if ("GET".equalsIgnoreCase(metodo)) {
            manejarConsultaSaldo(exchange);
        } else if ("POST".equalsIgnoreCase(metodo)) {
            manejarActualizacionSaldo(exchange);
        } else {
            exchange.sendResponseHeaders(405, -1); // Método no permitido
        }
    }

    private void manejarConsultaSaldo(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        String query = uri.getQuery(); // Ej: "cuenta=1234567890"

        String cuenta = null;
        if (query != null && query.startsWith("cuenta=")) {
            cuenta = query.substring("cuenta=".length());
        }

        String respuesta;
        int codigo;
        if (cuenta != null) {
            double saldo = BaseDatos.consultarSaldo(cuenta);
            if (saldo >= 0) {
                respuesta = String.valueOf(saldo);
                codigo = 200;
            } else {
                respuesta = "Cuenta no encontrada";
                codigo = 404;
            }
        } else {
            respuesta = "Parámetro 'cuenta' faltante";
            codigo = 400;
        }

        enviarRespuesta(exchange, codigo, respuesta);
    }

    private void manejarActualizacionSaldo(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        StringBuilder json = new StringBuilder();
        String linea;
        while ((linea = br.readLine()) != null) {
            json.append(linea);
        }

        ActualizarSaldoDTO datos = gson.fromJson(json.toString(), ActualizarSaldoDTO.class);

        boolean actualizado = BaseDatos.actualizarSaldo(datos.getCuenta(), datos.getNuevoSaldo());
        String respuesta = actualizado ? "Saldo actualizado correctamente" : "Error: cuenta no encontrada";
        int codigo = actualizado ? 200 : 404;

        enviarRespuesta(exchange, codigo, respuesta);
    }

    private void enviarRespuesta(HttpExchange exchange, int codigo, String mensaje) throws IOException {
        byte[] resp = mensaje.getBytes();
        exchange.sendResponseHeaders(codigo, resp.length);
        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }
}
