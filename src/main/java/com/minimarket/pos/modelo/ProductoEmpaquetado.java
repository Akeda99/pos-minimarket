package com.minimarket.pos.modelo;

/**
 * Producto envasado que se vende por unidad (gaseosas, conservas, snacks).
 * Se vende al precio registrado: ese precio es el precio de venta al publico
 * (que en el comercio minorista peruano ya incluye el IGV).
 *
 * @author Integrante 2
 */
public class ProductoEmpaquetado extends Producto {

    public ProductoEmpaquetado(int id, String codigoBarras, String nombre,
                               double precioBase, int stock, int idCategoria) {
        super(id, codigoBarras, nombre, precioBase, stock, idCategoria);
    }

    public ProductoEmpaquetado() { super(); }

    @Override
    public double calcularPrecioVenta() {
        return getPrecioBase();
    }

    @Override
    public String getTipo() { return "Empaquetado"; }
}