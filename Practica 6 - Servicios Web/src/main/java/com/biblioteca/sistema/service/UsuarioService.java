package com.biblioteca.sistema.service;

import com.biblioteca.sistema.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UsuarioService {

    private final Map<String, Usuario> usuarios = new HashMap<>();

    public UsuarioService() {
        usuarios.put("u123", new Usuario("u123", "Ana Perez", 1111));
        usuarios.put("u246", new Usuario("u246", "Juan López", 2222));
    }

    public String autenticar(String id, int pin) {
        Usuario u = usuarios.get(id);
        if (u != null && u.getPin() == pin) return "Autenticación exitosa";
        return "Error en autenticación";
    }

    public Usuario obtenerUsuario(String id) {
        return usuarios.get(id);
    }
}
