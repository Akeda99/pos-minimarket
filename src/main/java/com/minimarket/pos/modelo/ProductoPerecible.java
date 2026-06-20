package com.minimarket.pos.modelo;

import java.time.LocalDate;

/**
 * Producto con fecha de vencimiento (lacteos, embutidos, pan, etc.).
 * Se vende al precio registrado. La fecha de vencimiento se usa para
 * identificar productos vencidos o proximos a vencer en los reportes.
 *
 * @author Integrante 2
 */
public class ProductoPerecible extends Producto {

    private LocalDate fechaVencimiento;

    private static final int DIAS_PROX_VENC = 3;

    public ProductoPerecible(int id, String codigoBarras, String nombre,
                             double precioBase, int stock, int idCategoria,
                             LocalDate fechaVencimiento) {
        super(id, codigoBarras, nombre, precioBase, stock, idCategoria);
        this.fechaVencimiento = fechaVencimiento;
    }

    public ProductoPerecible() { super(); }

    @Override
    public double calcularPrecioVenta() {
        return getPrecioBase();
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