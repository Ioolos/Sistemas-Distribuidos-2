package com.biblioteca.sistema.model;

public class PrestamoRequest {
    private String usuarioId;
    private String isbn;

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
}
