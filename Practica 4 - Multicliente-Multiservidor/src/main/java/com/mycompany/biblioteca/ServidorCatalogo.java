package com.mycompany.biblioteca;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorCatalogo {
    private static ConcurrentHashMap<String, Integer> libros = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        libros.put("B100", 3);
        libros.put("B200", 1);
        libros.put("B300", 0);
        try (ServerSocket ss = new ServerSocket(8000)) {
            while (true) {
                Socket cl = ss.accept();
                new Thread(() -> {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(cl.getInputStream()));
                         PrintWriter out = new PrintWriter(cl.getOutputStream(), true)) {
                        String opc = in.readLine();
                        String id = in.readLine();
                        if (opc == null || id == null) {
                            out.println("Invalid");
                            return;
                        }
                        switch (opc) {
                            case "C":
                                Integer v = libros.get(id);
                                if (v == null) out.println("No existe"); else out.println(v);
                                break;
                            case "U":
                                String nStr = in.readLine();
                                try {
                                    int n = Integer.parseInt(nStr);
                                    if (libros.containsKey(id)) {
                                        libros.put(id, n);
                                        out.println("Actualizado");
                                    } else {
                                        out.println("No existe");
                                    }
                                } catch (NumberFormatException e) {
                                    out.println("Error");
                                }
                                break;
                            default:
                                out.println("Operacion no valida");
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
