package com.example.petcaremanagerproject.Modelo;

import java.time.LocalDateTime;

/**
 * Clase que representa un servicio en el sistema.
 * Almacena la información de un servicio incluyendo sus fechas, estado,
 * observaciones y las referencias a las entidades relacionadas.
 */
public class Servicio {
    private int idServicio,idCategoria,idMascota,idCuidador,idDueno;
    private LocalDateTime fechaSolicitud,fechaProgramada,fechaRealizacion;
    private String estado,observaciones, nombreCategoria,nombreMascota, nombreCuidador,nombreDueno;

    /**
     * Constructor para crear un nuevo servicio con información básica.
     * @param idServicio Identificador único del servicio
     * @param idCategoria Identificador de la categoría del servicio
     * @param fechaSolicitud Fecha y hora de solicitud del servicio
     * @param fechaProgramada Fecha y hora programada para el servicio
     * @param fechaRealizacion Fecha y hora de realización del servicio
     * @param estado Estado actual del servicio
     * @param observaciones Observaciones adicionales del servicio
     * @param idMascota Identificador de la mascota asociada
     * @param idCuidador Identificador del cuidador asignado
     * @param idDueno Identificador del dueño que solicita el servicio
     */
    public Servicio(int idServicio, int idCategoria, LocalDateTime fechaSolicitud, LocalDateTime fechaProgramada,
                    LocalDateTime fechaRealizacion, String estado, String observaciones,
                    int idMascota, int idCuidador, int idDueno) {
        this.idServicio = idServicio;
        this.idCategoria = idCategoria;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaProgramada = fechaProgramada;
        this.fechaRealizacion = fechaRealizacion;
        this.estado = estado;
        this.observaciones = observaciones;
        this.idMascota = idMascota;
        this.idCuidador = idCuidador;
        this.idDueno = idDueno;
    }

    /**
     * Constructor para crear un nuevo servicio con información completa incluyendo nombres.
     * @param idServicio Identificador único del servicio
     * @param idCategoria Identificador de la categoría del servicio
     * @param fechaSolicitud Fecha y hora de solicitud del servicio
     * @param fechaProgramada Fecha y hora programada para el servicio
     * @param fechaRealizacion Fecha y hora de realización del servicio
     * @param estado Estado actual del servicio
     * @param observaciones Observaciones adicionales del servicio
     * @param idMascota Identificador de la mascota asociada
     * @param idCuidador Identificador del cuidador asignado
     * @param idDueno Identificador del dueño que solicita el servicio
     * @param nombreCategoria Nombre de la categoría del servicio
     * @param nombreMascota Nombre de la mascota asociada
     * @param nombreCuidador Nombre del cuidador asignado
     * @param nombreDueno Nombre del dueño que solicita el servicio
     */
    public Servicio(int idServicio, int idCategoria, LocalDateTime fechaSolicitud, LocalDateTime fechaProgramada,
                    LocalDateTime fechaRealizacion, String estado, String observaciones,
                    int idMascota, int idCuidador, int idDueno,
                    String nombreCategoria, String nombreMascota, String nombreCuidador, String nombreDueno) {
        this.idServicio = idServicio;
        this.idCategoria = idCategoria;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaProgramada = fechaProgramada;
        this.fechaRealizacion = fechaRealizacion;
        this.estado = estado;
        this.observaciones = observaciones;
        this.idMascota = idMascota;
        this.idCuidador = idCuidador;
        this.idDueno = idDueno;
        this.nombreCategoria = nombreCategoria;
        this.nombreMascota = nombreMascota;
        this.nombreCuidador = nombreCuidador;
        this.nombreDueno = nombreDueno;
    }

    /**
     * Obtiene el identificador del servicio.
     * @return El ID del servicio
     */
    public int getIdServicio() {
        return idServicio;
    }

    /**
     * Establece el identificador del servicio.
     * @param idServicio Nuevo ID para el servicio
     */
    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    /**
     * Obtiene el identificador de la categoría.
     * @return El ID de la categoría
     */
    public int getIdCategoria() {
        return idCategoria;
    }

    /**
     * Establece el identificador de la categoría.
     * @param idCategoria Nuevo ID para la categoría
     */
    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    /**
     * Obtiene la fecha de solicitud del servicio.
     * @return La fecha y hora de solicitud
     */
    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    /**
     * Establece la fecha de solicitud del servicio.
     * @param fechaSolicitud Nueva fecha y hora de solicitud
     */
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    /**
     * Obtiene la fecha programada del servicio.
     * @return La fecha y hora programada
     */
    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    /**
     * Establece la fecha programada del servicio.
     * @param fechaProgramada Nueva fecha y hora programada
     */
    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    /**
     * Obtiene la fecha de realización del servicio.
     * @return La fecha y hora de realización
     */
    public LocalDateTime getFechaRealizacion() {
        return fechaRealizacion;
    }

    /**
     * Establece la fecha de realización del servicio.
     * @param fechaRealizacion Nueva fecha y hora de realización
     */
    public void setFechaRealizacion(LocalDateTime fechaRealizacion) {
        this.fechaRealizacion = fechaRealizacion;
    }

    /**
     * Obtiene el estado actual del servicio.
     * @return El estado del servicio
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado del servicio.
     * @param estado Nuevo estado para el servicio
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene las observaciones del servicio.
     * @return Las observaciones del servicio
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Establece las observaciones del servicio.
     * @param observaciones Nuevas observaciones para el servicio
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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
     * Obtiene el identificador del cuidador.
     * @return El ID del cuidador
     */
    public int getIdCuidador() {
        return idCuidador;
    }

    /**
     * Establece el identificador del cuidador.
     * @param idCuidador Nuevo ID para el cuidador
     */
    public void setIdCuidador(int idCuidador) {
        this.idCuidador = idCuidador;
    }

    /**
     * Obtiene el identificador del dueño.
     * @return El ID del dueño
     */
    public int getIdDueno() {
        return idDueno;
    }

    /**
     * Establece el identificador del dueño.
     * @param idDueno Nuevo ID para el dueño
     */
    public void setIdDueno(int idDueno) {
        this.idDueno = idDueno;
    }

    /**
     * Obtiene el nombre de la categoría.
     * @return El nombre de la categoría
     */
    public String getNombreCategoria() {
        return nombreCategoria;
    }

    /**
     * Establece el nombre de la categoría.
     * @param nombreCategoria Nuevo nombre para la categoría
     */
    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    /**
     * Obtiene el nombre de la mascota.
     * @return El nombre de la mascota
     */
    public String getNombreMascota() {
        return nombreMascota;
    }

    /**
     * Establece el nombre de la mascota.
     * @param nombreMascota Nuevo nombre para la mascota
     */
    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    /**
     * Obtiene el nombre del cuidador.
     * @return El nombre del cuidador
     */
    public String getNombreCuidador() {
        return nombreCuidador;
    }

    /**
     * Establece el nombre del cuidador.
     * @param nombreCuidador Nuevo nombre para el cuidador
     */
    public void setNombreCuidador(String nombreCuidador) {
        this.nombreCuidador = nombreCuidador;
    }

    /**
     * Obtiene el nombre del dueño.
     * @return El nombre del dueño
     */
    public String getNombreDueno() {
        return nombreDueno;
    }

    /**
     * Establece el nombre del dueño.
     * @param nombreDueno Nuevo nombre para el dueño
     */
    public void setNombreDueno(String nombreDueno) {
        this.nombreDueno = nombreDueno;
    }
} 