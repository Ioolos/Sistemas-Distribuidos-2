package com.mycompany.objetosdistribuidos;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBibliotecaOperaciones extends Remote {
    String prestar(String usuario, String sucursal, String libroId) throws RemoteException;
    String devolver(String usuario, String sucursal, String libroId) throws RemoteException;
    String transferir(String origenSucursal, String destinoSucursal, String libroId, int cantidad) throws RemoteException;
}
