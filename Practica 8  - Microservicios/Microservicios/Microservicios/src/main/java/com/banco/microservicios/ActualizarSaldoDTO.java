package com.banco.microservicios;

public class ActualizarSaldoDTO {
    private String cuenta;
    private double nuevoSaldo;
 
    public ActualizarSaldoDTO(String cuenta, double nuevoSaldo) {
        this.cuenta = cuenta;
        this.nuevoSaldo = nuevoSaldo;
    }

    // Getters y Setters
    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public double getNuevoSaldo() {
        return nuevoSaldo;
    }

    public void setNuevoSaldo(double nuevoSaldo) {
        this.nuevoSaldo = nuevoSaldo;
    }
}
