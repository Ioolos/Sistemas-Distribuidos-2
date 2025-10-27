package com.mycompany.objetosdistribuidos;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalTime;

public class ImpBibliotecaAutenticacion extends UnicastRemoteObject implements IBibliotecaAutenticacion {

    private Map<String, Integer> usuarios;

    public ImpBibliotecaAutenticacion() throws RemoteException {
        super();
    usuarios = new HashMap<>();

    usuarios.put("user1", 1001);
    usuarios.put("user2", 1002);
    usuarios.put("user3", 1003);
        System.out.println("[" + LocalTime.now() + "] Servidor de Autenticación (Biblioteca) inicializado con usuarios de prueba.");
    }

    @Override
    public String autenticarUsuario(String usuario, int pin) throws RemoteException {
        System.out.println("[" + LocalTime.now() + "] Solicitud de autenticación para usuario: " + usuario);
        if (usuarios.containsKey(usuario) && usuarios.get(usuario).equals(pin)) {
            System.out.println("[" + LocalTime.now() + "] Autenticación exitosa para usuario: " + usuario);
            return "Autenticacion Exitosa";
        } else {
            System.out.println("[" + LocalTime.now() + "] Fallo en autenticación para usuario: " + usuario);
            return "Error en autenticación";
        }
    }
}
