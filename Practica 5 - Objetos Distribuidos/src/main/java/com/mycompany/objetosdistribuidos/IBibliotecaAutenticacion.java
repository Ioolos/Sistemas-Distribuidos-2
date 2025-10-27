package com.mycompany.objetosdistribuidos;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBibliotecaAutenticacion extends Remote {
    String autenticarUsuario(String usuario, int pin) throws RemoteException;
}
