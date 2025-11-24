package com.banco.microservicios;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class AccountService {
    public static void main(String[] args) {
        try {
            // Puerto 8082 para AccountService
            HttpServer server = HttpServer.create(new InetSocketAddress(8082), 0);

            // Ruta para operaciones de saldo
            server.createContext("/saldo", new SaldoHandler());

            server.setExecutor(null); // Usa el executor por defecto
            server.start();

            System.out.println("AccountService iniciado en http://localhost:8082/saldo");

        } catch (Exception e) {
            System.out.println("Error al iniciar AccountService: " + e.getMessage());
        }
    }
}

