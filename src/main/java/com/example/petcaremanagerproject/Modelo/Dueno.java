package com.example.petcaremanagerproject.Modelo;

public class Dueno extends Usuario {
    private String direccion;

    public Dueno(int idUsuario, String nombre, String correo, String contrasena, String telefono, String imagenUrl) {
        super(idUsuario, nombre, correo, contrasena, telefono, imagenUrl);
    }

    // Constructor sin contrasena e imagenUrl, si es necesario para consultas existentes
    public Dueno(int idUsuario, String nombre, String correo, String telefono) {
        super(idUsuario, nombre, correo, null, telefono, null);
    }

    // Constructor para mostrar en la tabla
    public Dueno(int idUsuario, String nombre, String correo, String telefono, String imagenUrl) {
        super(idUsuario, nombre, correo, null, telefono, imagenUrl);
    }

    public Dueno(int idUsuario, String nombre, String correo, String contrasena, String telefono, String imagenUrl, String direccion) {
        super(idUsuario, nombre, correo, contrasena, telefono, imagenUrl);
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