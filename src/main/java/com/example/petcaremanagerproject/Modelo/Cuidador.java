package com.example.petcaremanagerproject.Modelo;

public class Cuidador extends Cliente {
    private String especialidad;
    private String disponibilidad;

    public Cuidador(int id, String nombre, String apellidos, String telefono, String email, String especialidad, String disponibilidad) {
        super(id, nombre, apellidos, telefono, email);
        this.especialidad = especialidad;
        this.disponibilidad = disponibilidad;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
} 