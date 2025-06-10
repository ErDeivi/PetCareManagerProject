package com.example.petcaremanagerproject.Modelo;

/**
 * Clase que representa a un usuario en el sistema.
 * Almacena la información básica de un usuario como su identificador,
 * nombre, correo electrónico, contraseña, teléfono y URL de imagen de perfil.
 */
public class Usuario {
    private int idUsuario;
    private String nombre,correo,contrasena,telefono,imagenUrl;

    /**
     * Constructor para crear un nuevo usuario.
     * @param idUsuario Identificador único del usuario
     * @param nombre Nombre completo del usuario
     * @param correo Correo electrónico del usuario
     * @param contrasena Contraseña del usuario
     * @param telefono Número de teléfono del usuario
     * @param imagenUrl URL de la imagen de perfil del usuario
     */
    public Usuario(int idUsuario, String nombre, String correo, String contrasena, String telefono, String imagenUrl) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.telefono = telefono;
        this.imagenUrl = imagenUrl;
    }

    /**
     * Obtiene el identificador del usuario.
     * @return El ID del usuario
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el identificador del usuario.
     * @param idUsuario Nuevo ID para el usuario
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Obtiene el nombre del usuario.
     * @return El nombre del usuario
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del usuario.
     * @param nombre Nuevo nombre para el usuario
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     * @return El correo electrónico del usuario
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Establece el correo electrónico del usuario.
     * @param correo Nuevo correo electrónico para el usuario
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Obtiene la contraseña del usuario.
     * @return La contraseña del usuario
     */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * Establece la contraseña del usuario.
     * @param contrasena Nueva contraseña para el usuario
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * Obtiene el número de teléfono del usuario.
     * @return El número de teléfono del usuario
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el número de teléfono del usuario.
     * @param telefono Nuevo número de teléfono para el usuario
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Obtiene la URL de la imagen de perfil del usuario.
     * @return La URL de la imagen de perfil
     */
    public String getImagenUrl() {
        return imagenUrl;
    }

    /**
     * Establece la URL de la imagen de perfil del usuario.
     * @param imagenUrl Nueva URL para la imagen de perfil
     */
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    /**
     * Devuelve una representación en cadena del usuario.
     * @return El nombre del usuario como cadena
     */
    @Override
    public String toString() {
        return nombre;
    }
}