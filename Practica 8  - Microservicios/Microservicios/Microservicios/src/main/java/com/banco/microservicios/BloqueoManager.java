package com.banco.microservicios;

import java.util.HashSet;
import java.util.Set;

public class BloqueoManager {
    private static final Set<String> cuentasBloqueadas = new HashSet<>();

    // Método sincronizado para obtener acceso
    public static synchronized boolean obtenerAcceso(String cuenta) {
        if (cuentasBloqueadas.contains(cuenta)) {
            return false; // Ya está en uso
        } else {
            cuentasBloqueadas.add(cuenta);
            return true;
        }
    }

    // Método sincronizado para liberar el acceso
    public static synchronized void liberarAcceso(String cuenta) {
        cuentasBloqueadas.remove(cuenta);
    }
}
