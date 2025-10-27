package com.mycompany.objetosdistribuidos;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.time.LocalTime;

public class ImpBibliotecaSincronizacion extends UnicastRemoteObject implements IBibliotecaSincronizacion {
    private boolean enUso = false;

    public ImpBibliotecaSincronizacion() throws RemoteException {
        super();
    }

    @Override
    public synchronized boolean solicitarAcceso() throws RemoteException {
        System.out.println("[" + LocalTime.now() + "] Cliente remoto solicitó acceso a la sección crítica (Biblioteca).");
        try {
            while (enUso) {
                System.out.println("[" + LocalTime.now() + "] Sección crítica ocupada. Cliente en espera...");
                wait();
            }
            enUso = true;
            System.out.println("[" + LocalTime.now() + "] Acceso concedido. Cliente ingresó a la sección crítica (Biblioteca).");
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RemoteException("Error al solicitar acceso (interrumpido)", e);
        }
    }

    @Override
    public synchronized void liberarAcceso() throws RemoteException {
        System.out.println("[" + LocalTime.now() + "] Cliente liberó la sección crítica (Biblioteca). Acceso disponible.");
        enUso = false;
        notifyAll();
    }
}
