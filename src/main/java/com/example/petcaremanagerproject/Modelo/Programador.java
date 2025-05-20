package com.example.petcaremanagerproject.Modelo;

public class Programador {

    private int id;
    private String nombre;
    private String apellido;
    private String localidad;
    private int salario;
    private String dni;
   
    public Programador(int id, String nombre,String apellido,String localidad,int salario,String dni){

        this.id= id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.localidad = localidad;
        this.salario = salario;
        this.dni = dni;

    }

    public Programador(String nombre,String apellido,String localidad,int salario,String dni){

        this.nombre = nombre;
        this.apellido = apellido;
        this.localidad = localidad;
        this.salario = salario;
        this.dni = dni;

    }

    public Programador(int int1, String string, String string2) {
        //TODO Auto-generated constructor stub
    }

    public int getId(){
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public String getLocalidad() {
        return localidad;
    }
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
    public int getSalario() {
        return salario;
    }
    public void setSalario(int salario) {
        this.salario = salario;
    }
    public String getDni() {
        return dni;
    }
    public void setDni(String dni) {
        this.dni = dni;
    }


    
}