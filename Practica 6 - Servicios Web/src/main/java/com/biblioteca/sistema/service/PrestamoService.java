package com.biblioteca.sistema.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrestamoService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private LibroService libroService;

    public String pedir(String usuarioId, String isbn) {
        if (usuarioService.obtenerUsuario(usuarioId) == null) return "Usuario no encontrado.";

        // sincronizar por ISBN para evitar condiciones de carrera
        synchronized (isbn.intern()) {
            int copias = libroService.copiasDisponibles(isbn);
            if (copias <= 0) return "No hay copias disponibles.";
            boolean ok = libroService.ajustarCopias(isbn, -1);
            return ok ? "Préstamo exitoso. Copias restantes: " + libroService.copiasDisponibles(isbn) : "Error al procesar préstamo.";
        }
    }

    public String devolver(String usuarioId, String isbn) {
        if (usuarioService.obtenerUsuario(usuarioId) == null) return "Usuario no encontrado.";

        synchronized (isbn.intern()) {
            boolean ok = libroService.ajustarCopias(isbn, +1);
            return ok ? "Devolución exitosa. Copias disponibles: " + libroService.copiasDisponibles(isbn) : "Error al procesar devolución.";
        }
    }
}
