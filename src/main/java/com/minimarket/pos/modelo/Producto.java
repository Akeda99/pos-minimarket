package com.minimarket.pos.modelo;

/**
 * Clase abstracta que representa un producto del minimarket.
 * Demuestra abstracción y herencia: cada subclase concreta implementa
 * calcularPrecioVenta() de forma distinta (polimorfismo).
 *
 * @author Integrante 2
 */
public abstract class Producto {

    private int    id;
    private String codigoBarras;
    private String nombre;
    private double precioBase;
    private int    stock;
    private int    idCategoria;
    private String nombreCategoria; // se rellena en consultas JOIN, no viene de la tabla

    public Producto(int id, String codigoBarras, String nombre,
                    double precioBase, int stock, int idCategoria) {
        this.id           = id;
        this.codigoBarras = codigoBarras;
        this.nombre       = nombre;
        this.precioBase   = precioBase;
        this.stock        = stock;
        this.idCategoria  = idCategoria;
    }

    public Producto() {}

    /** Precio de venta al público. Cada subtipo aplica su propia lógica. */
    public abstract double calcularPrecioVenta();

    /** Tipo legible para la JTable: "Perecedero", "Por Peso", "Empaquetado". */
    public abstract String getTipo();

    public boolean hayStock(int cantidad) {
        return this.stock >= cantidad;
    }

    public void descontarStock(int cantidad) {
        if (!hayStock(cantidad)) {
            throw new IllegalArgumentException(
                "Stock insuficiente para el producto: " + nombre);
        }
        this.stock -= cantidad;
    }

    public int getId()                        { return id; }
    public void setId(int id)                 { this.id = id; }
    public String getCodigoBarras()           { return codigoBarras; }
    public void setCodigoBarras(String cb)    { this.codigoBarras = cb; }
    public String getNombre()                 { return nombre; }
    public void setNombre(String nombre)      { this.nombre = nombre; }
    public double getPrecioBase()             { return precioBase; }
    public void setPrecioBase(double p)       { this.precioBase = p; }
    public int getStock()                     { return stock; }
    public void setStock(int stock)           { this.stock = stock; }
    public int getIdCategoria()               { return idCategoria; }
    public void setIdCategoria(int id)        { this.idCategoria = id; }
    public String getNombreCategoria()        { return nombreCategoria; }
    public void setNombreCategoria(String nc) { this.nombreCategoria = nc; }

    @Override
    public String toString() {
        return String.format("[%s] %s - S/ %.2f (stock: %d)",
                getTipo(), nombre, calcularPrecioVenta(), stock);
    }
}
