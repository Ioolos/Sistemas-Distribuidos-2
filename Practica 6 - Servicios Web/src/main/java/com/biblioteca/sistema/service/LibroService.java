package com.biblioteca.sistema.service;

import com.biblioteca.sistema.model.Libro;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LibroService {

    private final Map<String, Libro> libros = new ConcurrentHashMap<>();

    public LibroService() {
        libros.put("978-0-13-110362-7", new Libro("978-0-13-110362-7", "The C Programming Language", "Kernighan & Ritchie", 3));
        libros.put("978-0-201-03801-8", new Libro("978-0-201-03801-8", "The Art of Computer Programming", "Knuth", 1));
    }

    public int copiasDisponibles(String isbn) {
        Libro l = libros.get(isbn);
        return l == null ? -1 : l.getCopias();
    }

    public String actualizarCopias(String isbn, int copias) {
        Libro l = libros.get(isbn);
        if (l == null) return "Libro no encontrado.";
        l.setCopias(copias);
        return "Copias actualizadas a " + copias;
    }

    public boolean ajustarCopias(String isbn, int delta) {
        Libro l = libros.get(isbn);
        if (l == null) return false;
        int nuevas = l.getCopias() + delta;
        if (nuevas < 0) return false;
        l.setCopias(nuevas);
        return true;
    }
}
