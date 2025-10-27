package com.mycompany.atm.cs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class Nota {
    private int id;
    private String titulo;
    private String contenido;
    private String fechaCreacion;
    
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    public Nota(int id, String titulo, String contenido) {
        this.id = id;
        this.titulo = titulo;
        this.contenido = contenido;
        this.fechaCreacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
    
    public Nota(int id, String titulo, String contenido, String fechaCreacion) {
        this.id = id;
        this.titulo = titulo;
        this.contenido = contenido;
        this.fechaCreacion = fechaCreacion;
    }
    
    public int getId() {
        lock.readLock().lock();
        try {
            return id;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public String getTitulo() {
        lock.readLock().lock();
        try {
            return titulo;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public String getContenido() {
        lock.readLock().lock();
        try {
            return contenido;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public String getFechaCreacion() {
        lock.readLock().lock();
        try {
            return fechaCreacion;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public void setTitulo(String titulo) {
        lock.writeLock().lock();
        try {
            this.titulo = titulo;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public void setContenido(String contenido) {
        lock.writeLock().lock();
        try {
            this.contenido = contenido;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public String toString() {
        lock.readLock().lock();
        try {
            return id + "|" + titulo + "|" + fechaCreacion;
        } finally {
            lock.readLock().unlock();
        }
    }
}
