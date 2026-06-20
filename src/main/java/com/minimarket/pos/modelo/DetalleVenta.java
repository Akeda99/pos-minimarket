package com.minimarket.pos.modelo;

public class DetalleVenta {

    private int idDetalle;
    private int idVenta;
    private int idProducto;
    private String nombreProducto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleVenta() {}

    public DetalleVenta(int idProducto, String nombreProducto, int cantidad, double precioUnitario) {
        this.idProducto     = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad       = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal       = cantidad * precioUnitario;
    }

    public int getIdDetalle()               { return idDetalle; }
    public void setIdDetalle(int v)         { this.idDetalle = v; }

    public int getIdVenta()                 { return idVenta; }
    public void setIdVenta(int v)           { this.idVenta = v; }

    public int getIdProducto()              { return idProducto; }
    public void setIdProducto(int v)        { this.idProducto = v; }

    public String getNombreProducto()       { return nombreProducto; }
    public void setNombreProducto(String v) { this.nombreProducto = v; }

    public int getCantidad()                { return cantidad; }
    public void setCantidad(int v)          { this.cantidad = v; recalcular(); }

    public double getPrecioUnitario()       { return precioUnitario; }
    public void setPrecioUnitario(double v) { this.precioUnitario = v; recalcular(); }

    public double getSubtotal()             { return subtotal; }

    private void recalcular()               { this.subtotal = this.cantidad * this.precioUnitario; }

    @Override
    public String toString() {
        return nombreProducto + " x" + cantidad + " = S/ " + String.format("%.2f", subtotal);
    }
}