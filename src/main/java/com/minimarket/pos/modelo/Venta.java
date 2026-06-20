package com.minimarket.pos.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venta {

    private int idVenta;
    private LocalDateTime fecha;
    private int idUsuario;
    private MetodoPago metodoPago;
    private List<DetalleVenta> detalles;
    private double total;

    public Venta() {
        this.fecha    = LocalDateTime.now();
        this.detalles = new ArrayList<>();
    }

    public void agregarDetalle(DetalleVenta d) {
        detalles.add(d);
        recalcularTotal();
    }

    public void quitarDetalle(int indice) {
        if (indice >= 0 && indice < detalles.size()) {
            detalles.remove(indice);
            recalcularTotal();
        }
    }

    public void vaciarCarrito() {
        detalles.clear();
        total = 0;
    }

    private void recalcularTotal() {
        total = detalles.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
    }

    public int getIdVenta()                 { return idVenta; }
    public void setIdVenta(int v)           { this.idVenta = v; }

    public LocalDateTime getFecha()         { return fecha; }
    public void setFecha(LocalDateTime v)   { this.fecha = v; }

    public int getIdUsuario()               { return idUsuario; }
    public void setIdUsuario(int v)         { this.idUsuario = v; }

    public MetodoPago getMetodoPago()       { return metodoPago; }
    public void setMetodoPago(MetodoPago v) { this.metodoPago = v; }

    public List<DetalleVenta> getDetalles() { return detalles; }

    public double getTotal()                { return total; }
}