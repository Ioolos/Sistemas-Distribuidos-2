package com.mycompany.objetosdistribuidos;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServidorBibliotecaAutenticacion {
    public static void main(String[] args) {
        try {
            IBibliotecaAutenticacion obj = new ImpBibliotecaAutenticacion();
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("BibliotecaAuthService", obj);
            System.out.println("Servidor de Autenticación (Biblioteca) RMI listo.");
        } catch (Exception e) {
            System.err.println("Error al iniciar Servidor de Autenticación (Biblioteca): " + e.getMessage());
            e.printStackTrace();
        }
    }
}
