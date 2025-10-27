package com.mycompany.objetosdistribuidos;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServidorBibliotecaSincronizacion {
    public static void main(String[] args) {
        try {
            IBibliotecaSincronizacion sync = new ImpBibliotecaSincronizacion();
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("SyncBibliotecaService", sync);
            System.out.println("Servidor de Sincronización (Biblioteca) RMI listo.");
        } catch (Exception e) {
            System.err.println("Error al iniciar Servidor de Sincronización (Biblioteca): " + e.getMessage());
            e.printStackTrace();
        }
    }
}
