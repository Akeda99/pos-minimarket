package com.minimarket.pos.dao;

import com.minimarket.pos.modelo.Producto;
import com.minimarket.pos.modelo.ProductoEmpaquetado;
import com.minimarket.pos.modelo.ProductoPerecible;
import com.minimarket.pos.modelo.ProductoPorPeso;
import com.minimarket.pos.util.ConexionBD;
import com.minimarket.pos.util.POSException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de ProductoDAO contra Oracle Database.
 * Toda conexión se obtiene de ConexionBD.getConexion() (Singleton de I1).
 * Las excepciones SQL se envuelven en POSException (de I1).
 *
 * Mapeo con la tabla PRODUCTO real del repositorio (sql/01_tablas.sql):
 *   id_producto (IDENTITY), codigo_barras, nombre, precio, stock,
 *   tipo_producto ('PERECIBLE' | 'POR_PESO' | 'EMPAQUETADO'),
 *   fecha_vencimiento (solo perecibles), precio_por_kg (solo por peso),
 *   id_categoria (FK a CATEGORIA).
 *
 * Nota: el ID lo genera la columna IDENTITY de Oracle, por eso el INSERT
 * no lo incluye.
 *
 * @author Integrante 2 (ajustado al esquema del equipo)
 */
public class ProductoDAOImpl implements DAO<Producto> {

    // Valores de la columna tipo_producto
    private static final String T_PERECIBLE   = "PERECIBLE";
    private static final String T_POR_PESO    = "POR_PESO";
    private static final String T_EMPAQUETADO = "EMPAQUETADO";

    // ── SQL ────────────────────────────────────────────────────────────────
    private static final String SQL_INSERT =
        "INSERT INTO PRODUCTO (codigo_barras, nombre, precio, stock, tipo_producto, " +
        "fecha_vencimiento, precio_por_kg, id_categoria) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
        "UPDATE PRODUCTO SET codigo_barras=?, nombre=?, precio=?, stock=?, " +
        "tipo_producto=?, fecha_vencimiento=?, precio_por_kg=?, id_categoria=? " +
        "WHERE id_producto=?";

    private static final String SQL_DELETE =
        "DELETE FROM PRODUCTO WHERE id_producto=?";

    private static final String SQL_BASE =
        "SELECT p.id_producto, p.codigo_barras, p.nombre, p.precio, p.stock, " +
        "p.tipo_producto, p.fecha_vencimiento, p.precio_por_kg, p.id_categoria, " +
        "c.nombre AS nom_cat " +
        "FROM PRODUCTO p LEFT JOIN CATEGORIA c ON p.id_categoria = c.id_categoria ";

    private static final String SQL_BUSCAR_ID    = SQL_BASE + "WHERE p.id_producto = ?";
    private static final String SQL_LISTAR       = SQL_BASE + "ORDER BY p.nombre";
    private static final String SQL_POR_CODIGO   = SQL_BASE + "WHERE p.codigo_barras = ?";
    private static final String SQL_POR_NOMBRE   = SQL_BASE + "WHERE UPPER(p.nombre) LIKE UPPER(?) ORDER BY p.nombre";
    private static final String SQL_POR_CATEGORIA= SQL_BASE + "WHERE p.id_categoria = ? ORDER BY p.nombre";
    private static final String SQL_STOCK_BAJO   = SQL_BASE + "WHERE p.stock <= ? ORDER BY p.stock";

    // ── CRUD: insertar ─────────────────────────────────────────────────────
    @Override
    public void insertar(Producto p) throws POSException {
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERT)) {

            mapearCampos(ps, p);            // posiciones 1..8
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new POSException("Error al insertar producto: " + e.getMessage(), e);
        }
    }

    // ── CRUD: actualizar ───────────────────────────────────────────────────
    @Override
    public void actualizar(Producto p) throws POSException {
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_UPDATE)) {

            mapearCampos(ps, p);            // posiciones 1..8
            ps.setInt(9, p.getId());        // WHERE id_producto = ?
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new POSException("Error al actualizar producto: " + e.getMessage(), e);
        }
    }

    // ── CRUD: eliminar ─────────────────────────────────────────────────────
    @Override
    public void eliminar(int id) throws POSException {
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new POSException("Error al eliminar producto id=" + id + ": " + e.getMessage(), e);
        }
    }

    // ── CRUD: buscar por ID ────────────────────────────────────────────────
    @Override
    public Producto buscarPorId(int id) throws POSException {
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_BUSCAR_ID)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearProducto(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new POSException("Error al buscar producto id=" + id + ": " + e.getMessage(), e);
        }
    }

    // ── CRUD: listar todos ─────────────────────────────────────────────────
    @Override
    public List<Producto> listar() throws POSException {
        List<Producto> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapearProducto(rs));
            return lista;

        } catch (SQLException e) {
            throw new POSException("Error al listar productos: " + e.getMessage(), e);
        }
    }

    // ── Búsqueda por código de barras ──────────────────────────────────────
    public Producto buscarPorCodigoBarras(String codigoBarras) throws POSException {
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_POR_CODIGO)) {

            ps.setString(1, codigoBarras);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearProducto(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new POSException("Error al buscar por código de barras: " + e.getMessage(), e);
        }
    }

    // ── Búsqueda por nombre ────────────────────────────────────────────────
    public List<Producto> buscarPorNombre(String nombre) throws POSException {
        List<Producto> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_POR_NOMBRE)) {

            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearProducto(rs));
            }
            return lista;

        } catch (SQLException e) {
            throw new POSException("Error al buscar productos por nombre: " + e.getMessage(), e);
        }
    }

    // ── Listar por categoría ───────────────────────────────────────────────
    public List<Producto> listarPorCategoria(int idCategoria) throws POSException {
        List<Producto> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_POR_CATEGORIA)) {

            ps.setInt(1, idCategoria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearProducto(rs));
            }
            return lista;

        } catch (SQLException e) {
            throw new POSException("Error al listar por categoría: " + e.getMessage(), e);
        }
    }

    // ── Stock bajo ─────────────────────────────────────────────────────────
    public List<Producto> listarStockBajo(int stockMinimo) throws POSException {
        List<Producto> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_STOCK_BAJO)) {

            ps.setInt(1, stockMinimo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearProducto(rs));
            }
            return lista;

        } catch (SQLException e) {
            throw new POSException("Error al obtener stock bajo: " + e.getMessage(), e);
        }
    }

    // ── Helpers privados ───────────────────────────────────────────────────

    /**
     * Construye el Producto concreto a partir de un ResultSet.
     * Usa la columna tipo_producto para instanciar la subclase correcta (polimorfismo).
     */
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        int    id         = rs.getInt("id_producto");
        String codigo     = rs.getString("codigo_barras");
        String nombre     = rs.getString("nombre");
        double precio     = rs.getDouble("precio");
        int    stock      = rs.getInt("stock");
        int    idCat       = rs.getInt("id_categoria");
        String nomCat     = rs.getString("nom_cat");
        String tipo       = rs.getString("tipo_producto");

        Producto p;
        switch (tipo == null ? "" : tipo.trim().toUpperCase()) {
            case T_PERECIBLE:
                Date f = rs.getDate("fecha_vencimiento");
                LocalDate fecha = (f != null) ? f.toLocalDate() : null;
                p = new ProductoPerecible(id, codigo, nombre, precio, stock, idCat, fecha);
                break;

            case T_POR_PESO:
                // precio_por_kg existe en la tabla; si viene nulo usamos precio
                double ppk = rs.getDouble("precio_por_kg");
                if (rs.wasNull()) ppk = precio;
                p = new ProductoPorPeso(id, codigo, nombre, ppk, stock, idCat);
                break;

            case T_EMPAQUETADO:
            default:
                p = new ProductoEmpaquetado(id, codigo, nombre, precio, stock, idCat);
                break;
        }
        p.setNombreCategoria(nomCat);
        return p;
    }

    /**
     * Coloca las posiciones 1..8 del INSERT/UPDATE a partir del producto.
     * Determina tipo_producto, fecha_vencimiento y precio_por_kg según el subtipo.
     */
    private void mapearCampos(PreparedStatement ps, Producto p) throws SQLException {
        ps.setString(1, p.getCodigoBarras());
        ps.setString(2, p.getNombre());
        ps.setDouble(3, p.getPrecioBase());
        ps.setInt(4, p.getStock());

        String tipo;
        Date   fecha = null;
        Double ppk   = null;

        if (p instanceof ProductoPerecible) {
            tipo = T_PERECIBLE;
            LocalDate fv = ((ProductoPerecible) p).getFechaVencimiento();
            if (fv != null) fecha = Date.valueOf(fv);
        } else if (p instanceof ProductoPorPeso) {
            tipo = T_POR_PESO;
            ppk = p.getPrecioBase();            // el precio base ES el precio por kg
        } else {
            tipo = T_EMPAQUETADO;
        }

        ps.setString(5, tipo);
        if (fecha != null) ps.setDate(6, fecha); else ps.setNull(6, Types.DATE);
        if (ppk != null)   ps.setDouble(7, ppk); else ps.setNull(7, Types.NUMERIC);
        ps.setInt(8, p.getIdCategoria());
    }
}