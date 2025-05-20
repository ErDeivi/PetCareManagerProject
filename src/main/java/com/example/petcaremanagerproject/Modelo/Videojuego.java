package com.example.petcaremanagerproject.Modelo;

import java.util.Date;

public class Videojuego {
    private int id;
    private String nombre;
    private String plataforma;
    private String genero;
    private Date publicado;
    private Double precio;
    private int ventas;
    private BandaSonora bandaSonora;

    // Constructor
    public Videojuego(int id, String nombre, String plataforma, String genero, 
                      Date publicado, Double precio, int ventas) {
        this.id = id;
        this.nombre = nombre;
        this.plataforma = plataforma;
        this.genero = genero;
        this.publicado = publicado;
        this.precio = precio;
        this.ventas = ventas;
    }

    public Videojuego(int int1, String string) {
        //TODO Auto-generated constructor stub
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Date getPublicado() {
        return publicado;
    }

    public void setPublicado(Date publicado) {
        this.publicado = publicado;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public int getVentas() {
        return ventas;
    }

    public void setVentas(int ventas) {
        this.ventas = ventas;
    }

    public BandaSonora getBandaSonora() {
        return bandaSonora;
    }

    public void setBandaSonora(BandaSonora bandaSonora) {
        this.bandaSonora = bandaSonora;
    }
}
