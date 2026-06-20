package com.minimarket.pos.modelo;

/**
 * Categoria de productos del minimarket.
 * Modulo de Categorias y Reportes - Integrante 5.
 */
public class Categoria {
    private int idCategoria;
    private String nombre;
    private String descripcion;

    public Categoria() {}

    public Categoria(int idCategoria, String nombre, String descripcion) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getIdCategoria()              { return idCategoria; }
    public void setIdCategoria(int id)       { this.idCategoria = id; }

    public String getNombre()                { return nombre; }
    public void setNombre(String nombre)     { this.nombre = nombre; }

    public String getDescripcion()           { return descripcion; }
    public void setDescripcion(String desc)  { this.descripcion = desc; }

    @Override
    public String toString() { return nombre; }
}
