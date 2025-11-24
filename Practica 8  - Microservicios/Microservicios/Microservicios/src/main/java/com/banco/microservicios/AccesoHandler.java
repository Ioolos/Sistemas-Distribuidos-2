package com.banco.microservicios;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;

public class AccesoHandler implements HttpHandler {
    private final boolean esSolicitud;
    private final Gson gson = new Gson();

    public AccesoHandler(boolean esSolicitud) {
        this.esSolicitud = esSolicitud; // true = /acceso, false = /liberar
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // Método no permitido
            return;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder json = new StringBuilder();
        String linea;
        while ((linea = reader.readLine()) != null) {
            json.append(linea);
        }
        CuentaBloqueoDTO dto = gson.fromJson(json.toString(), CuentaBloqueoDTO.class);
        String cuenta = dto.getCuenta();
        String respuesta;
        int codigo;
        if (esSolicitud) {
            boolean concedido = BloqueoManager.obtenerAcceso(cuenta);
            if (concedido) {
                respuesta = "ACCESO_CONCEDIDO";
                codigo = 200;
            } else {
                respuesta = "CUENTA_EN_USO";
                codigo = 423; // Locked
            }
        } else {
            BloqueoManager.liberarAcceso(cuenta);
            respuesta = "ACCESO_LIBERADO";
            codigo = 200;
        }
        byte[] resp = respuesta.getBytes();
        exchange.sendResponseHeaders(codigo, resp.length);
        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }
}
