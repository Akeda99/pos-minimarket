package com.minimarket.pos.dao;

import com.minimarket.pos.modelo.Categoria;
import com.minimarket.pos.util.ConexionBD;
import com.minimarket.pos.util.POSException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion JDBC del DAO de categorias (tabla CATEGORIA).
 * Modulo de Categorias - Integrante 5.
 */
public class CategoriaDAOImpl implements CategoriaDAO {

    @Override
    public void insertar(Categoria c) throws POSException {
        String sql = "INSERT INTO CATEGORIA (nombre, descripcion) VALUES (?, ?)";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new POSException("Error al insertar categoria: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizar(Categoria c) throws POSException {
        String sql = "UPDATE CATEGORIA SET nombre = ?, descripcion = ? WHERE id_categoria = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setInt(3, c.getIdCategoria());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new POSException("Error al actualizar categoria: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(int id) throws POSException {
        String sql = "DELETE FROM CATEGORIA WHERE id_categoria = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new POSException("No se pudo eliminar la categoria (puede tener productos asociados): " + e.getMessage(), e);
        }
    }

    @Override
    public Categoria buscarPorId(int id) throws POSException {
        String sql = "SELECT * FROM CATEGORIA WHERE id_categoria = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new POSException("Error al buscar categoria: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Categoria> listar() throws POSException {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM CATEGORIA ORDER BY nombre";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new POSException("Error al listar categorias: " + e.getMessage(), e);
        }
        return lista;
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(
            rs.getInt("id_categoria"),
            rs.getString("nombre"),
            rs.getString("descripcion"));
    }
}
