package com.mycompany.biblioteca;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorSincronizacionBiblioteca {
    private static boolean enUso = false;
    private static final Object lock = new Object();

    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(10000)) {
            while (true) {
                Socket cl = ss.accept();
                new Thread(() -> {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(cl.getInputStream()));
                         PrintWriter out = new PrintWriter(cl.getOutputStream(), true)) {
                        String s = in.readLine();
                        if (!"S".equals(s)) {
                            out.println("ERR");
                            return;
                        }
                        synchronized (lock) {
                            while (enUso) {
                                out.println("E");
                                lock.wait();
                            }
                            enUso = true;
                            out.println("P");
                        }
                        String fin = in.readLine();
                        if ("L".equals(fin)) {
                            synchronized (lock) {
                                enUso = false;
                                lock.notifyAll();
                            }
                        }
                    } catch (IOException | InterruptedException e) {
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
