package com.mycompany.objetosdistribuidos;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServidorBibliotecaOperaciones {
    public static void main(String[] args) {
        try {
            IBibliotecaOperaciones ops = new ImpBibliotecaOperaciones();
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("OperacionesBibliotecaService", ops);
            System.out.println("Servidor de Operaciones (Biblioteca) RMI listo.");
        } catch (Exception e) {
            System.err.println("Error al iniciar Servidor de Operaciones (Biblioteca): " + e.getMessage());
            e.printStackTrace();
        }
    }
}
