package com.mycompany.objetosdistribuidos;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalTime;
import java.util.Set;
import java.util.HashSet;

public class ImpBibliotecaInventario extends UnicastRemoteObject implements IBibliotecaInventario {
    private ConcurrentHashMap<String, Integer> stock;

    public ImpBibliotecaInventario() throws RemoteException {
        super();
        stock = new ConcurrentHashMap<>();
    stock.put("sucursalA:LB001", 3);
    stock.put("sucursalA:LB002", 1);
    stock.put("sucursalB:LB001", 2);
    stock.put("sucursalB:LB003", 5);

    stock.put("sucursalA:B001", 4);
    stock.put("sucursalA:B002", 2);
    stock.put("sucursalA:B003", 5);
    stock.put("sucursalA:B004", 1);
    stock.put("sucursalA:B005", 0);
    stock.put("sucursalB:B006", 3);
    stock.put("sucursalB:B007", 6);
    stock.put("sucursalB:B008", 2);
    stock.put("sucursalB:B009", 7);
    stock.put("sucursalB:B010", 1);
        System.out.println("[" + LocalTime.now() + "] Servidor de Inventario (Biblioteca) inicializado con datos de prueba.");
    }

    private String key(String sucursal, String libroId) {
        return sucursal + ":" + libroId;
    }

    @Override
    public int consultarDisponibilidad(String sucursal, String libroId) throws RemoteException {
        String k = key(sucursal, libroId);
        System.out.println("[" + LocalTime.now() + "] Consulta de disponibilidad para: " + k);
        return stock.getOrDefault(k, 0);
    }

    @Override
    public String actualizarStock(String sucursal, String libroId, int nuevoStock) throws RemoteException {
        String k = key(sucursal, libroId);
        System.out.println("[" + LocalTime.now() + "] Actualización de stock para " + k + " -> " + nuevoStock);
        stock.put(k, nuevoStock);
        return "Stock actualizado: " + nuevoStock;
    }

    @Override
    public String[] listarSucursales() throws RemoteException {
        Set<String> sucursales = new HashSet<>();
        for (String k : stock.keySet()) {
            int idx = k.indexOf(':');
            if (idx > 0) sucursales.add(k.substring(0, idx));
        }
        return sucursales.toArray(new String[0]);
    }

    @Override
    public String[] listarLibros(String sucursal) throws RemoteException {
        String prefix = sucursal + ":";
        Set<String> libros = new HashSet<>();
        for (String k : stock.keySet()) {
            if (k.startsWith(prefix)) {
                libros.add(k.substring(prefix.length()));
            }
        }
        return libros.toArray(new String[0]);
    }
}
