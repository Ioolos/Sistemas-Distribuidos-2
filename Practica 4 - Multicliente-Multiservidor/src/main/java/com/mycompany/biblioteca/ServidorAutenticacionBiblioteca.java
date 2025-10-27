package com.mycompany.biblioteca;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServidorAutenticacionBiblioteca {
    private static Map<String, String> usuarios = new HashMap<>();

    public static void main(String[] args) {
        usuarios.put("user1", "1111");
        usuarios.put("user2", "2222");
        usuarios.put("user3", "3333");
        try (ServerSocket ss = new ServerSocket(7000)) {
            while (true) {
                Socket cl = ss.accept();
                new Thread(() -> {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(cl.getInputStream()));
                         PrintWriter out = new PrintWriter(cl.getOutputStream(), true)) {
                        String u = in.readLine();
                        String p = in.readLine();
                        if (u != null && p != null && p.equals(usuarios.get(u))) {
                            out.println("OK");
                        } else {
                            out.println("ERROR");
                        }
                    } catch (IOException e) {
                    } finally {
                        try {
                            cl.close();
                        } catch (IOException e) {
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
        }
    }
}
