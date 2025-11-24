package com.banco.microservicios;

public class CuentaBloqueoDTO {
    private String cuenta;

    public CuentaBloqueoDTO() {}

    public CuentaBloqueoDTO(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }
}
