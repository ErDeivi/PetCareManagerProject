package com.example.petcaremanagerproject.Modelo;

import java.time.LocalDateTime;

public class Servicio {
    private int idServicio,idCategoria,idMascota,idCuidador,idDueno;
    private LocalDateTime fechaSolicitud,fechaProgramada,fechaRealizacion;
    private String estado,observaciones, nombreCategoria,nombreMascota, nombreCuidador,nombreDueno;

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

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public LocalDateTime getFechaRealizacion() {
        return fechaRealizacion;
    }

    public void setFechaRealizacion(LocalDateTime fechaRealizacion) {
        this.fechaRealizacion = fechaRealizacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public int getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

    public int getIdCuidador() {
        return idCuidador;
    }

    public void setIdCuidador(int idCuidador) {
        this.idCuidador = idCuidador;
    }

    public int getIdDueno() {
        return idDueno;
    }

    public void setIdDueno(int idDueno) {
        this.idDueno = idDueno;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public String getNombreCuidador() {
        return nombreCuidador;
    }

    public void setNombreCuidador(String nombreCuidador) {
        this.nombreCuidador = nombreCuidador;
    }

    public String getNombreDueno() {
        return nombreDueno;
    }

    public void setNombreDueno(String nombreDueno) {
        this.nombreDueno = nombreDueno;
    }
} 