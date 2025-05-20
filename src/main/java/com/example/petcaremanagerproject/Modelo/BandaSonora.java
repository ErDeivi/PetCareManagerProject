package com.example.petcaremanagerproject.Modelo;

public class BandaSonora {

    private int id;
    private String nombre;
    private String compositor;
    private String url;

    public BandaSonora(int id, String nombre, String compositor, String url) {
        this.id = id;
        this.nombre = nombre;
        this.compositor = compositor;
        this.url = url;
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

    public String getCompositor() {
        return compositor;
    }

    public void setCompositor(String compositor) {
        this.compositor = compositor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
