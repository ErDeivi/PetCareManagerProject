package com.example.petcaremanagerproject.Modelo;

/**
 * Clase que representa a un dueño en el sistema.
 * Extiende de la clase Usuario y hereda sus atributos y funcionalidades básicas.
 * Un dueño es un tipo específico de usuario que puede tener mascotas registradas.
 */
public class Dueno extends Usuario {

    /**
     * Constructor para crear un nuevo dueño.
     * Este constructor se utiliza principalmente para mostrar la información en tablas.
     * @param idUsuario Identificador único del dueño
     * @param nombre Nombre completo del dueño
     * @param correo Correo electrónico del dueño
     * @param telefono Número de teléfono del dueño
     * @param imagenUrl URL de la imagen de perfil del dueño
     */
    public Dueno(int idUsuario, String nombre, String correo, String telefono, String imagenUrl) {
        super(idUsuario, nombre, correo, null, telefono, imagenUrl);
    }
    
    /**
     * Devuelve una representación en cadena del dueño.
     * @return El nombre del dueño como cadena
     */
    @Override
    public String toString() {
        return getNombre();
    }
} 