package com.example.petcaremanagerproject.Modelo;

public class Cuidador extends Usuario {

    public Cuidador(int idUsuario, String nombre, String correo, String contrasena, String telefono, String imagenUrl) {
        super(idUsuario, nombre, correo, contrasena, telefono, imagenUrl);
    }

    // Eliminados campos especialidad y disponibilidad y su lógica
} 