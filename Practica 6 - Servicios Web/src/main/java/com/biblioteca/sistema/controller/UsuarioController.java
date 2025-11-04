package com.biblioteca.sistema.controller;

import com.biblioteca.sistema.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{id}/{pin}")
    public String autenticar(@PathVariable String id, @PathVariable int pin) {
        return usuarioService.autenticar(id, pin);
    }

}
