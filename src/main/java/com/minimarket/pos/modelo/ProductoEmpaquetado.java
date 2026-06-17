package com.minimarket.pos.modelo;

/**
 * Producto envasado que se vende por unidad (gaseosas, conservas, snacks).
 * Aplica un margen del 30 % sobre el precio base.
 *
 * @author Integrante 2
 */
public class ProductoEmpaquetado extends Producto {

    private static final double MARGEN = 0.30;

    public ProductoEmpaquetado(int id, String codigoBarras, String nombre,
                               double precioBase, int stock, int idCategoria) {
        super(id, codigoBarras, nombre, precioBase, stock, idCategoria);
    }

    public ProductoEmpaquetado() { super(); }

    @Override
    public double calcularPrecioVenta() {
        double precio = getPrecioBase() * (1 + MARGEN);
        return Math.round(precio * 100.0) / 100.0;
    }

    @Override
    public String getTipo() { return "Empaquetado"; }
}
