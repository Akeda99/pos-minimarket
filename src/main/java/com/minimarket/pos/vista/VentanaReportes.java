package com.minimarket.pos.vista;

import com.minimarket.pos.dao.ReporteDAO;
import com.minimarket.pos.util.POSException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Ventana de reportes del sistema:
 *   - Resumen por categoria (nro de productos, stock total y valor)
 *   - Stock bajo (alerta de reposicion)
 *   - Ventas por usuario (cuanto vendio cada cajero/admin)
 * Modulo de Reportes - Integrante 5.
 */
public class VentanaReportes extends JInternalFrame {

    private final ReporteDAO dao = new ReporteDAO();
    private JTable tabla;
    private DefaultTableModel modelo;

    public VentanaReportes() {
        super("Reportes", true, true, true, true);
        setSize(720, 470);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        construirUI();
    }

    private void construirUI() {
        setLayout(new BorderLayout(5, 5));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnResumen   = new JButton("Resumen por categoria");
        JButton btnStockBajo = new JButton("Stock bajo");
        JButton btnVentas    = new JButton("Ventas por usuario");
        panelBotones.add(new JLabel("Generar reporte:"));
        panelBotones.add(btnResumen);
        panelBotones.add(btnStockBajo);
        panelBotones.add(btnVentas);
        add(panelBotones, BorderLayout.NORTH);

        modelo = new DefaultTableModel();
        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnResumen.addActionListener(e -> generarResumen());
        btnStockBajo.addActionListener(e -> generarStockBajo());
        btnVentas.addActionListener(e -> generarVentas());
    }

    private void generarResumen() {
        try {
            List<Object[]> filas = dao.resumenPorCategoria();
            for (Object[] f : filas) {
                f[3] = String.format("S/ %.2f", ((Number) f[3]).doubleValue());
            }
            mostrar(new String[]{"Categoria", "N. productos", "Stock total", "Valor inventario"}, filas);
        } catch (POSException e) {
            error(e.getMessage());
        }
    }

    private void generarStockBajo() {
        String entrada = JOptionPane.showInputDialog(this,
                "Mostrar productos con stock igual o menor a:", "10");
        if (entrada == null) return;
        int umbral;
        try {
            umbral = Integer.parseInt(entrada.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingresa un numero valido.");
            return;
        }
        try {
            List<Object[]> filas = dao.stockBajo(umbral);
            if (filas.isEmpty()) {
                modelo.setDataVector(new Object[0][], new String[]{"Producto", "Categoria", "Stock"});
                JOptionPane.showMessageDialog(this,
                        "No hay productos con stock igual o menor a " + umbral + ". Todo en orden.");
                return;
            }
            mostrar(new String[]{"Producto", "Categoria", "Stock"}, filas);
        } catch (POSException e) {
            error(e.getMessage());
        }
    }

    private void generarVentas() {
        try {
            List<Object[]> filas = dao.ventasPorUsuario();
            for (Object[] f : filas) {
                f[3] = String.format("S/ %.2f", ((Number) f[3]).doubleValue());
            }
            mostrar(new String[]{"Usuario", "Rol", "N. ventas", "Total vendido"}, filas);
        } catch (POSException e) {
            error(e.getMessage());
        }
    }

    private void mostrar(String[] columnas, List<Object[]> filas) {
        modelo.setDataVector(filas.toArray(new Object[0][]), columnas);
        if (filas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay datos para este reporte todavia.");
        }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}