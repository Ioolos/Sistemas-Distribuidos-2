package com.biblioteca.sistema.model;

public class Libro {
    private String isbn;
    private String titulo;
    private String autor;
    private int copias;

    public Libro() {}

    public Libro(String isbn, String titulo, String autor, int copias) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.copias = copias;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public int getCopias() { return copias; }
    public void setCopias(int copias) { this.copias = copias; }
}
