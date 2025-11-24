package com.banco.microservicios;

public class OperacionDTO {
    private String cuenta;
    private double monto;

    // Constructor vacío (necesario para Gson)
    public OperacionDTO() {}

    // Constructor útil para enviar solicitudes internas
    public OperacionDTO(String cuenta, double monto) {
        this.cuenta = cuenta;
        this.monto = monto;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}
