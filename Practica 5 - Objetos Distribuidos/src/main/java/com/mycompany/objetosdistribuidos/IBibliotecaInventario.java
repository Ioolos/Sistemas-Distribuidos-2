package com.mycompany.objetosdistribuidos;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBibliotecaInventario extends Remote {
    int consultarDisponibilidad(String sucursal, String libroId) throws RemoteException;
    String actualizarStock(String sucursal, String libroId, int nuevoStock) throws RemoteException;
    String[] listarSucursales() throws RemoteException;
    String[] listarLibros(String sucursal) throws RemoteException;
}
