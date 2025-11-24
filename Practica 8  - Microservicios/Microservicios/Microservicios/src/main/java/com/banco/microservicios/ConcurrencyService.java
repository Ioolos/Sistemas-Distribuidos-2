package com.banco.microservicios;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class ConcurrencyService {
    public static void main(String[] args) {
        try {
            // Crear servidor HTTP en el puerto 8084
            HttpServer server = HttpServer.create(new InetSocketAddress(8084), 0);

            // Rutas
            server.createContext("/acceso", new AccesoHandler(true));
            server.createContext("/liberar", new AccesoHandler(false));

            server.setExecutor(null); // Usa el executor por defecto
            server.start();

            System.out.println("ConcurrencyService iniciado en http://localhost:8084");

        } catch (Exception e) {
            System.out.println("Error al iniciar ConcurrencyService: " + e.getMessage());
        }
    }
}
