package com.mycompany.objetosdistribuidos;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;

public class ImpBibliotecaOperaciones extends UnicastRemoteObject implements IBibliotecaOperaciones {

    private IBibliotecaInventario inventario;
    private IBibliotecaSincronizacion sync;

    public ImpBibliotecaOperaciones() throws RemoteException {
        super();
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            inventario = (IBibliotecaInventario) registry.lookup("InventarioService");
            sync = (IBibliotecaSincronizacion) registry.lookup("SyncBibliotecaService");
            System.out.println("[" + LocalTime.now() + "] Servidor de Operaciones (Biblioteca) conectado correctamente a los servicios remotos.");
        } catch (Exception e) {
            System.err.println("[" + LocalTime.now() + "] Error al conectar con servicios de inventario o sincronización: " + e.getMessage());
            throw new RemoteException("Servicios no disponibles.");
        }
    }

    @Override
    public String prestar(String usuario, String sucursal, String libroId) throws RemoteException {
        System.out.println("[" + LocalTime.now() + "] Usuario " + usuario + " solicitó préstamo de libro " + libroId + " en " + sucursal);
        if (!sync.solicitarAcceso()) {
            return "Error: No se pudo obtener acceso a la operación.";
        }
        try {
            int disponible = inventario.consultarDisponibilidad(sucursal, libroId);
            if (disponible <= 0) return "Libro no disponible.";
            disponible -= 1;
            String res = inventario.actualizarStock(sucursal, libroId, disponible);
            System.out.println("[" + LocalTime.now() + "] Préstamo completado. Nuevo stock: " + disponible);
            return res;
        } finally {
            sync.liberarAcceso();
        }
    }

    @Override
    public String devolver(String usuario, String sucursal, String libroId) throws RemoteException {
        System.out.println("[" + LocalTime.now() + "] Usuario " + usuario + " devolvió libro " + libroId + " en " + sucursal);
        if (!sync.solicitarAcceso()) {
            return "Error: No se pudo obtener acceso a la operación.";
        }
        try {
            int disponible = inventario.consultarDisponibilidad(sucursal, libroId);
            disponible += 1;
            String res = inventario.actualizarStock(sucursal, libroId, disponible);
            System.out.println("[" + LocalTime.now() + "] Devolución procesada. Nuevo stock: " + disponible);
            return res;
        } finally {
            sync.liberarAcceso();
        }
    }

    @Override
    public String transferir(String origenSucursal, String destinoSucursal, String libroId, int cantidad) throws RemoteException {
        System.out.println("[" + LocalTime.now() + "] Solicitud de transferencia de " + cantidad + " del libro " + libroId + " desde " + origenSucursal + " a " + destinoSucursal);
        if (!sync.solicitarAcceso()) {
            return "Error: No se pudo obtener acceso a la operación.";
        }
        try {
            int stockOrigen = inventario.consultarDisponibilidad(origenSucursal, libroId);
            int stockDestino = inventario.consultarDisponibilidad(destinoSucursal, libroId);
            if (stockOrigen < cantidad) return "Error: Stock insuficiente en origen.";
            stockOrigen -= cantidad;
            stockDestino += cantidad;
            inventario.actualizarStock(origenSucursal, libroId, stockOrigen);
            inventario.actualizarStock(destinoSucursal, libroId, stockDestino);
            System.out.println("[" + LocalTime.now() + "] Transferencia completada. Nuevo stock en origen: " + stockOrigen);
            return "Transferencia exitosa. Nuevo stock en origen: " + stockOrigen;
        } finally {
            sync.liberarAcceso();
        }
    }
}
