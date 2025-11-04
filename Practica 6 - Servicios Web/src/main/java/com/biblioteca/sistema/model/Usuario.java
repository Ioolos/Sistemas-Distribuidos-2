package com.biblioteca.sistema.model;

public class Usuario {
    private String id;
    private String nombre;
    private int pin;

    public Usuario() {}

    public Usuario(String id, String nombre, int pin) {
        this.id = id;
        this.nombre = nombre;
        this.pin = pin;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getPin() { return pin; }
    public void setPin(int pin) { this.pin = pin; }
}
