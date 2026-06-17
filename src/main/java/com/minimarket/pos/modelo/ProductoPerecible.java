package com.minimarket.pos.modelo;

import java.time.LocalDate;

/**
 * Producto con fecha de vencimiento (lácteos, embutidos, pan, etc.).
 * Aplica un margen del 20 %; si vence en <= 3 días se descuenta un 30 %.
 *
 * @author Integrante 2
 */
public class ProductoPerecible extends Producto {

    private LocalDate fechaVencimiento;

    private static final double MARGEN         = 0.20;
    private static final double DESCUENTO_PROX = 0.30;
    private static final int    DIAS_PROX_VENC = 3;

    public ProductoPerecible(int id, String codigoBarras, String nombre,
                             double precioBase, int stock, int idCategoria,
                             LocalDate fechaVencimiento) {
        super(id, codigoBarras, nombre, precioBase, stock, idCategoria);
        this.fechaVencimiento = fechaVencimiento;
    }

    public ProductoPerecible() { super(); }

    @Override
    public double calcularPrecioVenta() {
        double precio = getPrecioBase() * (1 + MARGEN);
        if (estaProximoAVencer()) {
            precio = precio * (1 - DESCUENTO_PROX);
        }
        return Math.round(precio * 100.0) / 100.0;
    }

    @Override
    public String getTipo() { return "Perecedero"; }

    public boolean estaProximoAVencer() {
        if (fechaVencimiento == null) return false;
        LocalDate hoy = LocalDate.now();
        return !fechaVencimiento.isBefore(hoy) &&
               fechaVencimiento.isBefore(hoy.plusDays(DIAS_PROX_VENC + 1));
    }

    public boolean estaVencido() {
        return fechaVencimiento != null && fechaVencimiento.isBefore(LocalDate.now());
    }

    public LocalDate getFechaVencimiento()           { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fecha) { this.fechaVencimiento = fecha; }
}
