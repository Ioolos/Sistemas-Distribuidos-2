package com.biblioteca.sistema.controller;

import com.biblioteca.sistema.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/libros")
public class LibroController {

    @Autowired
    private LibroService libroService;

    @GetMapping("/{isbn}")
    public String consultar(@PathVariable String isbn) {
        int copias = libroService.copiasDisponibles(isbn);
        if (copias < 0) return "Libro no encontrado";
        return "Copias disponibles: " + copias;
    }

    @PutMapping("/{isbn}/copias")
    public String actualizar(@PathVariable String isbn, @RequestParam int copias) {
        return libroService.actualizarCopias(isbn, copias);
    }

}
