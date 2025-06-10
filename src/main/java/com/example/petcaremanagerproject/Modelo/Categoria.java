package com.example.petcaremanagerproject.Modelo;

/**
 * Clase que representa una categoría de servicio en el sistema.
 * Almacena la información básica de una categoría como su identificador,
 * tipo y descripción.
 */
public class Categoria {
    private int idCategoria;
    private String tipo, descripcion;

    /**
     * Constructor para crear una nueva categoría.
     * @param idCategoria Identificador único de la categoría
     * @param tipo Nombre o tipo de la categoría
     * @param descripcion Descripción detallada de la categoría
     */
    public Categoria(int idCategoria, String tipo, String descripcion) {
        this.idCategoria = idCategoria;
        this.tipo = tipo;
        this.descripcion = descripcion;
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
     * Obtiene el tipo de la categoría.
     * @return El tipo de categoría
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de la categoría.
     * @param tipo Nuevo tipo para la categoría
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene la descripción de la categoría.
     * @return La descripción de la categoría
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción de la categoría.
     * @param descripcion Nueva descripción para la categoría
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Devuelve una representación en cadena de la categoría.
     * @return El tipo de la categoría como cadena
     */
    @Override
    public String toString() {
        return tipo;
    }
}