package com.minimarket.pos.modelo;

/**
 * Producto vendido por kilogramo (frutas, verduras, granos a granel).
 * precioBase representa el precio por kg (precio de venta al publico).
 * El total de la linea en la venta es calcularPrecioVenta() x cantidadKg.
 *
 * @author Integrante 2
 */
public class ProductoPorPeso extends Producto {

    public ProductoPorPeso(int id, String codigoBarras, String nombre,
                           double precioPorKg, int stock, int idCategoria) {
        super(id, codigoBarras, nombre, precioPorKg, stock, idCategoria);
    }

    public ProductoPorPeso() { super(); }

    @Override
    public double calcularPrecioVenta() {
        return getPrecioBase();
    }

    @Override
    public String getTipo() { return "Por Peso"; }

    /** Precio total para una cantidad en kg. */
    public double calcularTotal(double cantidadKg) {
        return Math.round(calcularPrecioVenta() * cantidadKg * 100.0) / 100.0;
    }
}