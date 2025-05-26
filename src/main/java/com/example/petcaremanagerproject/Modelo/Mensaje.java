package com.example.petcaremanagerproject.Modelo;

import java.time.LocalDateTime;

public class Mensaje {
    private int idMensaje;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private int idEmisor;
    private int idReceptor;

    public Mensaje(int idMensaje, String contenido, LocalDateTime fechaEnvio, int idEmisor, int idReceptor) {
        this.idMensaje = idMensaje;
        this.contenido = contenido;
        this.fechaEnvio = fechaEnvio;
        this.idEmisor = idEmisor;
        this.idReceptor = idReceptor;
    }

    public int getIdMensaje() { return idMensaje; }
    public void setIdMensaje(int idMensaje) { this.idMensaje = idMensaje; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
    public int getIdEmisor() { return idEmisor; }
    public void setIdEmisor(int idEmisor) { this.idEmisor = idEmisor; }
    public int getIdReceptor() { return idReceptor; }
    public void setIdReceptor(int idReceptor) { this.idReceptor = idReceptor; }
} 