package com.example.petcaremanagerproject.Modelo;

/**
 * Clase que representa una mascota en el sistema.
 * Almacena la información básica de una mascota como su identificador,
 * nombre, especie, raza, edad, peso y datos de su dueño.
 */
public class Mascota {
    private int idMascota,idDueno,edad;
    private String nombre,especie,raza, imagenUrl,nombreDueno;
    private double peso;

    /**
     * Constructor para crear una nueva mascota.
     * @param idMascota Identificador único de la mascota
     * @param nombre Nombre de la mascota
     * @param especie Especie de la mascota
     * @param raza Raza de la mascota
     * @param edad Edad de la mascota en años
     * @param peso Peso de la mascota en kilogramos
     * @param idDueno Identificador del dueño de la mascota
     * @param imagenUrl URL de la imagen de la mascota
     */
    public Mascota(int idMascota, String nombre, String especie, String raza, int edad, double peso, int idDueno, String imagenUrl) {
        this.idMascota = idMascota;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.peso = peso;
        this.idDueno = idDueno;
        this.imagenUrl = imagenUrl;
    }

    /**
     * Constructor para crear una nueva mascota incluyendo el nombre del dueño.
     * @param idMascota Identificador único de la mascota
     * @param nombre Nombre de la mascota
     * @param especie Especie de la mascota
     * @param raza Raza de la mascota
     * @param edad Edad de la mascota en años
     * @param peso Peso de la mascota en kilogramos
     * @param idDueno Identificador del dueño de la mascota
     * @param imagenUrl URL de la imagen de la mascota
     * @param nombreDueno Nombre del dueño de la mascota
     */
    public Mascota(int idMascota, String nombre, String especie, String raza, int edad, double peso, int idDueno, String imagenUrl, String nombreDueno) {
        this.idMascota = idMascota;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.peso = peso;
        this.idDueno = idDueno;
        this.imagenUrl = imagenUrl;
        this.nombreDueno = nombreDueno;
    }

    /**
     * Obtiene el identificador de la mascota.
     * @return El ID de la mascota
     */
    public int getIdMascota() {
        return idMascota;
    }

    /**
     * Establece el identificador de la mascota.
     * @param idMascota Nuevo ID para la mascota
     */
    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

    /**
     * Obtiene el nombre de la mascota.
     * @return El nombre de la mascota
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la mascota.
     * @param nombre Nuevo nombre para la mascota
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la especie de la mascota.
     * @return La especie de la mascota
     */
    public String getEspecie() {
        return especie;
    }

    /**
     * Establece la especie de la mascota.
     * @param especie Nueva especie para la mascota
     */
    public void setEspecie(String especie) {
        this.especie = especie;
    }

    /**
     * Obtiene la raza de la mascota.
     * @return La raza de la mascota
     */
    public String getRaza() {
        return raza;
    }

    /**
     * Establece la raza de la mascota.
     * @param raza Nueva raza para la mascota
     */
    public void setRaza(String raza) {
        this.raza = raza;
    }

    /**
     * Obtiene la edad de la mascota.
     * @return La edad de la mascota en años
     */
    public int getEdad() {
        return edad;
    }

    /**
     * Establece la edad de la mascota.
     * @param edad Nueva edad para la mascota en años
     */
    public void setEdad(int edad) {
        this.edad = edad;
    }

    /**
     * Obtiene el peso de la mascota.
     * @return El peso de la mascota en kilogramos
     */
    public double getPeso() {
        return peso;
    }

    /**
     * Establece el peso de la mascota.
     * @param peso Nuevo peso para la mascota en kilogramos
     */
    public void setPeso(double peso) {
        this.peso = peso;
    }

    /**
     * Obtiene el identificador del dueño de la mascota.
     * @return El ID del dueño
     */
    public int getIdDueno() {
        return idDueno;
    }

    /**
     * Establece el identificador del dueño de la mascota.
     * @param idDueno Nuevo ID del dueño
     */
    public void setIdDueno(int idDueno) {
        this.idDueno = idDueno;
    }

    /**
     * Obtiene la URL de la imagen de la mascota.
     * @return La URL de la imagen
     */
    public String getImagenUrl() {
        return imagenUrl;
    }

    /**
     * Establece la URL de la imagen de la mascota.
     * @param imagenUrl Nueva URL de la imagen
     */
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    /**
     * Obtiene el nombre del dueño de la mascota.
     * @return El nombre del dueño
     */
    public String getNombreDueno() {
        return nombreDueno;
    }

    /**
     * Establece el nombre del dueño de la mascota.
     * @param nombreDueno Nuevo nombre del dueño
     */
    public void setNombreDueno(String nombreDueno) {
        this.nombreDueno = nombreDueno;
    }

    /**
     * Devuelve una representación en cadena de la mascota.
     * @return El nombre de la mascota como cadena
     */
    @Override
    public String toString() {
        return nombre;
    }
}