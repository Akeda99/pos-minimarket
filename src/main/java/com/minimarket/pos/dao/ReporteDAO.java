package com.minimarket.pos.dao;

import com.minimarket.pos.util.ConexionBD;
import com.minimarket.pos.util.POSException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Consultas de reportes del sistema. No es un CRUD, por eso no extiende DAO<T>.
 * Modulo de Reportes - Integrante 5.
 */
public class ReporteDAO {

    /**
     * Resumen por categoria: numero de productos, stock total y valor del
     * inventario (precio x stock) de cada categoria. Usa LEFT JOIN para incluir
     * tambien las categorias que aun no tienen productos.
     * Filas: {categoria, num_productos, stock_total, valor_inventario}.
     */
    public List<Object[]> resumenPorCategoria() throws POSException {
        List<Object[]> filas = new ArrayList<>();
        String sql =
            "SELECT c.nombre AS categoria, COUNT(p.id_producto) AS num_productos, " +
            "NVL(SUM(p.stock), 0) AS stock_total, " +
            "NVL(SUM(p.stock * p.precio), 0) AS valor_inventario " +
            "FROM CATEGORIA c LEFT JOIN PRODUCTO p ON p.id_categoria = c.id_categoria " +
            "GROUP BY c.nombre ORDER BY c.nombre";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                filas.add(new Object[]{
                    rs.getString("categoria"),
                    rs.getInt("num_productos"),
                    rs.getInt("stock_total"),
                    rs.getDouble("valor_inventario")
                });
            }
        } catch (SQLException e) {
            throw new POSException("Error al generar el resumen por categoria: " + e.getMessage(), e);
        }
        return filas;
    }

    /**
     * Productos con stock igual o menor al umbral indicado (alerta de
     * reposicion). Filas: {producto, categoria, stock}.
     */
    public List<Object[]> stockBajo(int umbral) throws POSException {
        List<Object[]> filas = new ArrayList<>();
        String sql =
            "SELECT p.nombre AS producto, c.nombre AS categoria, p.stock " +
            "FROM PRODUCTO p INNER JOIN CATEGORIA c ON p.id_categoria = c.id_categoria " +
            "WHERE p.stock <= ? ORDER BY p.stock ASC";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, umbral);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Object[]{
                        rs.getString("producto"),
                        rs.getString("categoria"),
                        rs.getInt("stock")
                    });
                }
            }
        } catch (SQLException e) {
            throw new POSException("Error al generar el reporte de stock bajo: " + e.getMessage(), e);
        }
        return filas;
    }

    /**
     * Total vendido y numero de ventas por usuario.
     * Filas: {usuario, rol, num_ventas, total_vendido}.
     */
    public List<Object[]> ventasPorUsuario() throws POSException {
        List<Object[]> filas = new ArrayList<>();
        String sql =
            "SELECT u.nombre AS usuario, u.rol, COUNT(v.id_venta) AS num_ventas, " +
            "NVL(SUM(v.total), 0) AS total_vendido " +
            "FROM USUARIO u LEFT JOIN VENTA v ON u.id_usuario = v.id_usuario " +
            "GROUP BY u.nombre, u.rol ORDER BY total_vendido DESC";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                filas.add(new Object[]{
                    rs.getString("usuario"),
                    rs.getString("rol"),
                    rs.getInt("num_ventas"),
                    rs.getDouble("total_vendido")
                });
            }
        } catch (SQLException e) {
            throw new POSException("Error al generar el reporte de ventas: " + e.getMessage(), e);
        }
        return filas;
    }
}