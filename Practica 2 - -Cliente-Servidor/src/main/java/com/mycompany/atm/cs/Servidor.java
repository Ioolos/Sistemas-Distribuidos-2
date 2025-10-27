package com.mycompany.atm.cs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Servidor {
    private static List<Nota> notas = new ArrayList<>();
    private static AtomicInteger contadorId = new AtomicInteger(1);
    
    public static void main(String[] args) {
        notas.add(new Nota(contadorId.getAndIncrement(), "Bienvenida", "Bienvenido al gestor de notas"));
        notas.add(new Nota(contadorId.getAndIncrement(), "Recordatorio", "Revisar correos importantes"));
        notas.add(new Nota(contadorId.getAndIncrement(), "Lista de compras", "Leche, Pan, Huevos"));
        
        try {
            ServerSocket ss = new ServerSocket(3000);
            System.out.println("Servidor de Gestor de Notas iniciado en el puerto 3000...");
            
            while (true) {
                Socket cliente = ss.accept();
                System.out.println("Cliente conectado: " + cliente.getInetAddress());
                new Thread(() -> manejarCliente(cliente)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void manejarCliente(Socket cliente) {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            PrintWriter salida = new PrintWriter(cliente.getOutputStream(), true);
            
            String operacion;
            while ((operacion = entrada.readLine()) != null) {
                System.out.println("Operación recibida: " + operacion);
                
                switch (operacion) {
                    case "LISTAR":
                        listarNotas(salida);
                        break;
                    case "CREAR":
                        crearNota(entrada, salida);
                        break;
                    case "VER":
                        verNota(entrada, salida);
                        break;
                    case "BORRAR":
                        borrarNota(entrada, salida);
                        break;
                    case "DESCONECTAR":
                        salida.println("OK|Desconectado correctamente");
                        cliente.close();
                        System.out.println("Cliente desconectado.");
                        return;
                    default:
                        salida.println("ERROR|Operación no reconocida");
                }
            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado inesperadamente.");
        } finally {
            try {
                cliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void listarNotas(PrintWriter salida) {
        synchronized (notas) {
            if (notas.isEmpty()) {
                salida.println("OK|0");
            } else {
                salida.println("OK|" + notas.size());
                for (Nota nota : notas) {
                    salida.println(nota.getId() + "|" + nota.getTitulo() + "|" + nota.getFechaCreacion());
                }
            }
        }
    }
    
    private static void crearNota(BufferedReader entrada, PrintWriter salida) {
        try {
            String titulo = entrada.readLine();
            String contenido = entrada.readLine();
            
            synchronized (notas) {
                Nota nuevaNota = new Nota(contadorId.getAndIncrement(), titulo, contenido);
                notas.add(nuevaNota);
                salida.println("OK|Nota creada exitosamente con ID: " + nuevaNota.getId());
                System.out.println("Nota creada: " + titulo);
            }
        } catch (IOException e) {
            salida.println("ERROR|Error al crear la nota");
        }
    }
    
    private static void verNota(BufferedReader entrada, PrintWriter salida) {
        try {
            int id = Integer.parseInt(entrada.readLine());
            
            synchronized (notas) {
                Nota nota = buscarNotaPorId(id);
                if (nota != null) {
                    salida.println("OK|" + nota.getId() + "|" + nota.getTitulo() + "|" + nota.getContenido() + "|" + nota.getFechaCreacion());
                } else {
                    salida.println("ERROR|Nota no encontrada");
                }
            }
        } catch (IOException | NumberFormatException e) {
            salida.println("ERROR|Error al obtener la nota");
        }
    }
    
    private static void borrarNota(BufferedReader entrada, PrintWriter salida) {
        try {
            int id = Integer.parseInt(entrada.readLine());
            
            synchronized (notas) {
                Nota nota = buscarNotaPorId(id);
                if (nota != null) {
                    notas.remove(nota);
                    salida.println("OK|Nota borrada exitosamente");
                    System.out.println("Nota borrada: ID " + id);
                } else {
                    salida.println("ERROR|Nota no encontrada");
                }
            }
        } catch (IOException | NumberFormatException e) {
            salida.println("ERROR|Error al borrar la nota");
        }
    }
    
    private static Nota buscarNotaPorId(int id) {
        for (Nota nota : notas) {
            if (nota.getId() == id) {
                return nota;
            }
        }
        return null;
    }
}