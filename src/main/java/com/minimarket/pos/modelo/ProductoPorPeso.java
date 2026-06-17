package com.minimarket.pos.modelo;

/**
 * Producto vendido por kilogramo (frutas, verduras, granos a granel).
 * precioBase representa el precio por kg; se aplica un margen del 25 %.
 * El total de la línea en la venta es calcularPrecioVenta() x cantidadKg.
 *
 * @author Integrante 2
 */
public class ProductoPorPeso extends Producto {

    private static final double MARGEN = 0.25;

    public ProductoPorPeso(int id, String codigoBarras, String nombre,
                           double precioPorKg, int stock, int idCategoria) {
        super(id, codigoBarras, nombre, precioPorKg, stock, idCategoria);
    }

    public ProductoPorPeso() { super(); }

    @Override
    public double calcularPrecioVenta() {
        double precio = getPrecioBase() * (1 + MARGEN);
        return Math.round(precio * 100.0) / 100.0;
    }

    @Override
    public String getTipo() { return "Por Peso"; }

    /** Precio total para una cantidad en kg. */
    public double calcularTotal(double cantidadKg) {
        return Math.round(calcularPrecioVenta() * cantidadKg * 100.0) / 100.0;
    }
}
