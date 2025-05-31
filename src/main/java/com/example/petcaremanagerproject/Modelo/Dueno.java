package com.example.petcaremanagerproject.Modelo;

public class Dueno extends Cliente {
    private String direccion;

    public Dueno(int id, String nombre, String correo, String telefono) {
        super(id, nombre, correo, telefono);
    }

    public Dueno(int id, String nombre, String correo, String telefono, String direccion) {
        super(id, nombre, correo, telefono);
        this.direccion = direccion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return getNombre();
    }
} 