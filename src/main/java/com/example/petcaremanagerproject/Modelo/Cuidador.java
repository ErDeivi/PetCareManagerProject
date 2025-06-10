package com.example.petcaremanagerproject.Modelo;

/**
 * Clase que representa a un cuidador en el sistema.
 * Extiende de la clase Usuario y hereda sus atributos y funcionalidades básicas.
 * Un cuidador es un tipo específico de usuario que puede realizar servicios de cuidado de mascotas.
 */
public class Cuidador extends Usuario {

    /**
     * Constructor para crear un nuevo cuidador.
     * @param idUsuario Identificador único del cuidador
     * @param nombre Nombre completo del cuidador
     * @param correo Correo electrónico del cuidador
     * @param contrasena Contraseña del cuidador
     * @param telefono Número de teléfono del cuidador
     * @param imagenUrl URL de la imagen de perfil del cuidador
     */
    public Cuidador(int idUsuario, String nombre, String correo, String contrasena, String telefono, String imagenUrl) {
        super(idUsuario, nombre, correo, contrasena, telefono, imagenUrl);
    }

} 