package com.example.petcaremanagerproject.Modelo;

public class Categoria {
    private int idCategoria;
    private String tipo, descripcion;

    public Categoria(int idCategoria, String tipo, String descripcion) {
        this.idCategoria = idCategoria;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return tipo;
    }
} 