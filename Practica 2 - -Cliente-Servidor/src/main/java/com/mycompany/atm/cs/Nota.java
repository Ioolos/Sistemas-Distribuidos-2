package com.mycompany.atm.cs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Nota {
    private int id;
    private String titulo;
    private String contenido;
    private String fechaCreacion;
    
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
        return id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public String getContenido() {
        return contenido;
    }
    
    public String getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
    
    @Override
    public String toString() {
        return "ID: " + id + " | " + titulo + " | " + fechaCreacion;
    }
}
