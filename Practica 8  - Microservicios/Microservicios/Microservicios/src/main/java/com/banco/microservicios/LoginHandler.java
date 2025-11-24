package com.banco.microservicios;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;

import java.io.*;

public class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // Sin contenido
            return;
        }
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // Método no permitido
            return;
        }
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        StringBuilder jsonBuilder = new StringBuilder();
        String linea;
        while ((linea = br.readLine()) != null) {
            jsonBuilder.append(linea);
        }
        Gson gson = new Gson();
        Login login = gson.fromJson(jsonBuilder.toString(), Login.class);
        boolean valido = BaseDatos.validarCuenta(login.getNumero(), login.getNip());
        String respuesta = valido ? "Autenticacion Exitosa" : "Error en autenticación";
        int codigo = valido ? 200 : 401;
        exchange.sendResponseHeaders(codigo, respuesta.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(respuesta.getBytes());
        os.close();
    }
}
