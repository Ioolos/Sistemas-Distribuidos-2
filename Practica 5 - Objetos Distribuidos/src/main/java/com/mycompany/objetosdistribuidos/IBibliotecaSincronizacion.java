package com.mycompany.objetosdistribuidos;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBibliotecaSincronizacion extends Remote {
    boolean solicitarAcceso() throws RemoteException;
    void liberarAcceso() throws RemoteException;
}
