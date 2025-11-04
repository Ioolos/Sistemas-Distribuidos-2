package com.biblioteca.sistema.controller;

import com.biblioteca.sistema.model.PrestamoRequest;
import com.biblioteca.sistema.service.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @PostMapping("/pedir")
    public String pedir(@RequestBody PrestamoRequest request) {
        return prestamoService.pedir(request.getUsuarioId(), request.getIsbn());
    }

    @PostMapping("/devolver")
    public String devolver(@RequestBody PrestamoRequest request) {
        return prestamoService.devolver(request.getUsuarioId(), request.getIsbn());
    }

}
