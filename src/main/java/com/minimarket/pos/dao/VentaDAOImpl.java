package com.minimarket.pos.dao;

import com.minimarket.pos.modelo.DetalleVenta;
import com.minimarket.pos.modelo.MetodoPago;
import com.minimarket.pos.modelo.Venta;
import com.minimarket.pos.util.ConexionBD;
import com.minimarket.pos.util.POSException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAOImpl implements VentaDAO {

    private static final String SQL_BASE = "SELECT * FROM VENTA ";
    private static final String SQL_BUSCAR_ID = SQL_BASE + "WHERE id_venta = ?";
    private static final String SQL_LISTAR = SQL_BASE + "ORDER BY fecha DESC";
    private static final String SQL_POR_USUARIO = SQL_BASE + "WHERE id_usuario = ? ORDER BY fecha DESC";
    private static final String SQL_POR_FECHA =
        SQL_BASE + "WHERE fecha >= TO_TIMESTAMP(?, 'YYYY-MM-DD') " +
        "AND fecha < TO_TIMESTAMP(?, 'YYYY-MM-DD') + 1 ORDER BY fecha DESC";
    private static final String SQL_DETALLES =
        "SELECT d.*, p.nombre AS nombre_producto FROM DETALLE_VENTA d " +
        "JOIN PRODUCTO p ON d.id_producto = p.id_producto WHERE d.id_venta = ?";

    /**
     * Registra la venta usando procedimientos almacenados (PL/SQL):
     *   SP_REGISTRAR_VENTA   -> inserta la cabecera y devuelve el id generado.
     *   SP_REGISTRAR_DETALLE -> por cada item, inserta el detalle, valida y
     *                           descuenta el stock del producto.
     * La transaccion la coordina este metodo: si un detalle falla (por ejemplo,
     * por stock insuficiente), se revierte toda la venta con rollback.
     */
    @Override
    public void insertar(Venta venta) throws POSException {
        Connection con = null;
        try {
            con = ConexionBD.getConexion();
            con.setAutoCommit(false);

            int idVenta;
            try (CallableStatement cs = con.prepareCall("{call SP_REGISTRAR_VENTA(?, ?, ?, ?)}")) {
                cs.setDouble(1, venta.getTotal());
                cs.setString(2, venta.getMetodoPago().name());
                cs.setInt(3, venta.getIdUsuario());
                cs.registerOutParameter(4, Types.INTEGER);
                cs.execute();
                idVenta = cs.getInt(4);
                venta.setIdVenta(idVenta);
            }

            try (CallableStatement cs = con.prepareCall("{call SP_REGISTRAR_DETALLE(?, ?, ?, ?)}")) {
                for (DetalleVenta d : venta.getDetalles()) {
                    cs.setInt(1, idVenta);
                    cs.setInt(2, d.getIdProducto());
                    cs.setInt(3, d.getCantidad());
                    cs.setDouble(4, d.getPrecioUnitario());
                    cs.execute();
                }
            }

            con.commit();

        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ignored) {}
            }
            throw new POSException("Error al registrar venta: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public void actualizar(Venta venta) throws POSException {
        throw new POSException("Las ventas no se pueden modificar.");
    }

    @Override
    public void eliminar(int id) throws POSException {
        throw new POSException("Las ventas no se pueden eliminar.");
    }

    @Override
    public Venta buscarPorId(int id) throws POSException {
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_BUSCAR_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearVenta(rs);
            }
        } catch (SQLException e) {
            throw new POSException("Error al buscar venta id=" + id + ": " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Venta> listar() throws POSException {
        List<Venta> lista = new ArrayList<>();
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapearVenta(rs));
        } catch (SQLException e) {
            throw new POSException("Error al listar ventas: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Venta> listarPorUsuario(int idUsuario) throws POSException {
        List<Venta> lista = new ArrayList<>();
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_POR_USUARIO)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearVenta(rs));
            }
        } catch (SQLException e) {
            throw new POSException("Error al listar ventas por usuario: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Venta> listarPorFecha(String fechaInicio, String fechaFin) throws POSException {
        List<Venta> lista = new ArrayList<>();
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_POR_FECHA)) {
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearVenta(rs));
            }
        } catch (SQLException e) {
            throw new POSException("Error al listar ventas por fecha: " + e.getMessage(), e);
        }
        return lista;
    }

    private Venta mapearVenta(ResultSet rs) throws SQLException {
        Venta v = new Venta();
        v.setIdVenta(rs.getInt("id_venta"));
        v.setIdUsuario(rs.getInt("id_usuario"));
        v.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        v.setMetodoPago(MetodoPago.valueOf(rs.getString("metodo_pago")));
        cargarDetalles(v);
        return v;
    }

    private void cargarDetalles(Venta v) throws SQLException {
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_DETALLES)) {
            ps.setInt(1, v.getIdVenta());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetalleVenta d = new DetalleVenta();
                    d.setIdDetalle(rs.getInt("id_detalle"));
                    d.setIdVenta(v.getIdVenta());
                    d.setIdProducto(rs.getInt("id_producto"));
                    d.setNombreProducto(rs.getString("nombre_producto"));
                    d.setCantidad(rs.getInt("cantidad"));
                    d.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    v.agregarDetalle(d);
                }
            }
        }
    }
}
