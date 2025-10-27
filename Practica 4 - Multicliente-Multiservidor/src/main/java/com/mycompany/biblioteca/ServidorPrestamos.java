package com.mycompany.biblioteca;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorPrestamos {
    // Track which user has which borrowed book (enforce one book per user)
    private static ConcurrentHashMap<String, String> prestamoPorUsuario = new ConcurrentHashMap<>();
    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(9000)) {
            while (true) {
                Socket cl = ss.accept();
                new Thread(new MHandler(cl)).start();
            }
        } catch (IOException e) {
        }
    }

    private static class MHandler implements Runnable {
        private Socket cl;

        public MHandler(Socket cl) {
            this.cl = cl;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(cl.getInputStream()));
                 PrintWriter out = new PrintWriter(cl.getOutputStream(), true)) {
                String opc = in.readLine();
                String user = in.readLine();
                if (opc == null || user == null) {
                    out.println("Error");
                    return;
                }
                switch (opc) {
                    case "B":
                        String idB = in.readLine();
                        // Check if user already has a borrowed book
                        if (prestamoPorUsuario.containsKey(user)) {
                            out.println("Error: ya tienes un libro prestado. Devuélvelo antes de pedir otro.");
                            return;
                        }
                        int avail = consultarCatalogo(idB);
                        if (avail <= 0) {
                            out.println("No disponible");
                            return;
                        }
                        if (!solicitarAcceso()) {
                            out.println("No se obtuvo sincronizacion");
                            return;
                        }
                        int nuevo = avail - 1;
                        if (actualizarCatalogo(idB, nuevo)) {
                            // record the loan for the user
                            prestamoPorUsuario.put(user, idB);
                            out.println("Prestamo realizado. Disponibles: " + nuevo);
                            System.out.println("[Prestamos] Usuario " + user + " prestó " + idB + ". Disponibles: " + nuevo);
                        } else {
                            out.println("Error al actualizar catalogo");
                            System.out.println("[Prestamos] Error al actualizar catálogo al prestar " + idB + " para usuario " + user);
                        }
                        liberarAcceso();
                        break;
                    case "R":
                        String idR = in.readLine();
                        // Check that the user actually has this book borrowed
                        String prestado = prestamoPorUsuario.get(user);
                        System.out.println("[Prestamos] Devolucion solicitada por " + user + " para libro " + idR + ". Prestado registrado: " + prestado);
                        if (prestado == null) {
                            out.println("Error: no tienes ningún libro prestado.");
                            return;
                        }
                        if (!prestado.equals(idR)) {
                            out.println("Error: el libro que intentas devolver no coincide con el que tienes prestado.");
                            return;
                        }
                        int av2 = consultarCatalogo(idR);
                        if (av2 < 0) {
                            out.println("Libro no existe");
                            return;
                        }
                        if (!solicitarAcceso()) {
                            out.println("No se obtuvo sincronizacion");
                            return;
                        }
                        int nuevo2 = av2 + 1;
                        if (actualizarCatalogo(idR, nuevo2)) {
                            // remove user's loan record
                            prestamoPorUsuario.remove(user);
                            out.println("Devolucion registrada. Disponibles: " + nuevo2);
                            System.out.println("[Prestamos] Usuario " + user + " devolvió " + idR + ". Disponibles: " + nuevo2);
                        } else {
                            out.println("Error al actualizar catalogo");
                            System.out.println("[Prestamos] Error al actualizar catálogo al devolver " + idR + " por usuario " + user);
                        }
                        liberarAcceso();
                        break;
                    default:
                        out.println("Operacion no valida");
                }
            } catch (IOException | InterruptedException e) {
            } finally {
                try {
                    cl.close();
                } catch (IOException e) {
                }
            }
        }

        private boolean solicitarAcceso() throws IOException, InterruptedException {
            try (Socket s = new Socket("localhost", 10000);
                 BufferedReader inS = new BufferedReader(new InputStreamReader(s.getInputStream()));
                 PrintWriter outS = new PrintWriter(s.getOutputStream(), true)) {
                outS.println("S");
                String r;
                while (true) {
                    r = inS.readLine();
                    if ("P".equals(r)) return true; else if ("E".equals(r)) Thread.sleep(300);
                }
            } catch (IOException e) {
                return false;
            }
        }

        private void liberarAcceso() {
            try (Socket s = new Socket("localhost", 10000);
                 PrintWriter outS = new PrintWriter(s.getOutputStream(), true)) {
                outS.println("L");
            } catch (IOException e) {
            }
        }

        private int consultarCatalogo(String id) {
            try (Socket s = new Socket("localhost", 8000);
                 BufferedReader inC = new BufferedReader(new InputStreamReader(s.getInputStream()));
                 PrintWriter outC = new PrintWriter(s.getOutputStream(), true)) {
                outC.println("C");
                outC.println(id);
                outC.flush();
                String r = inC.readLine();
                if (r == null) return -1;
                if (r.equals("No existe")) return -1;
                return Integer.parseInt(r);
            } catch (IOException | NumberFormatException e) {
                return -1;
            }
        }

        private boolean actualizarCatalogo(String id, int n) {
            try (Socket s = new Socket("localhost", 8000);
                 PrintWriter outC = new PrintWriter(s.getOutputStream(), true);
                 BufferedReader inC = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                outC.println("U");
                outC.println(id);
                outC.println(n);
                outC.flush();
                String r = inC.readLine();
                return r != null && r.equals("Actualizado");
            } catch (IOException e) {
                return false;
            }
        }
    }
}
