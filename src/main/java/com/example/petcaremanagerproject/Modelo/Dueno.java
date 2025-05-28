package com.example.petcaremanagerproject.Modelo;

public class Dueno extends Cliente {
    private String direccion;

    public Dueno(int id, String nombre, String apellidos, String telefono, String email, String direccion) {
        super(id, nombre, apellidos, telefono, email);
        this.direccion = direccion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
} 