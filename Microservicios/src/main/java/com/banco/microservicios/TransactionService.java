package com.banco.microservicios;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class TransactionService {
    public static void main(String[] args) {
        try {
            // Puerto del microservicio de transacciones
            HttpServer server = HttpServer.create(new InetSocketAddress(8083), 0);

            // Asociar rutas a su manejador
            server.createContext("/deposito", new OperacionHandler("deposito"));
            server.createContext("/retiro", new OperacionHandler("retiro"));
            server.createContext("/transferencia", new OperacionHandler("transferencia"));

            server.setExecutor(null); // Executor por defecto
            server.start();

            System.out.println("TransactionService iniciado en http://localhost:8083");

        } catch (Exception e) {
            System.out.println("Error al iniciar TransactionService: " + e.getMessage());
        }
    }
}
