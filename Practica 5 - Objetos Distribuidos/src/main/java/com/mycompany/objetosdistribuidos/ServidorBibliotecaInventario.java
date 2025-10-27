package com.mycompany.objetosdistribuidos;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServidorBibliotecaInventario {
    public static void main(String[] args) {
        try {
            IBibliotecaInventario inv = new ImpBibliotecaInventario();
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("InventarioService", inv);
            System.out.println("Servidor de Inventario (Biblioteca) RMI listo.");
        } catch (Exception e) {
            System.err.println("Error al iniciar Servidor de Inventario (Biblioteca): " + e.getMessage());
            e.printStackTrace();
        }
    }
}
