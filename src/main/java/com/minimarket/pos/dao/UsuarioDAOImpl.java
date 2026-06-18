package com.minimarket.pos.dao;

import com.minimarket.pos.modelo.Administrador;
import com.minimarket.pos.modelo.Cajero;
import com.minimarket.pos.modelo.Usuario;
import com.minimarket.pos.util.ConexionBD;
import com.minimarket.pos.util.POSException;

import java.security.MessageDigest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion JDBC del acceso a datos de Usuario sobre la tabla USUARIO.
 * Usa siempre ConexionBD.getConexion(), como acordamos en los contratos del equipo.
 * Responsable: Integrante 3.
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public void insertar(Usuario usuario) throws POSException {
        String sql = "INSERT INTO USUARIO (username, password, nombre, rol) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getPassword());
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getRol());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new POSException("No se pudo registrar el usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizar(Usuario usuario) throws POSException {
        String sql = "UPDATE USUARIO SET username = ?, password = ?, nombre = ?, rol = ? WHERE id_usuario = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getPassword());
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getRol());
            ps.setInt(5, usuario.getIdUsuario());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new POSException("No se pudo actualizar el usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(int id) throws POSException {
        String sql = "DELETE FROM USUARIO WHERE id_usuario = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new POSException("No se pudo eliminar el usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public Usuario buscarPorId(int id) throws POSException {
        String sql = "SELECT * FROM USUARIO WHERE id_usuario = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new POSException("No se pudo buscar el usuario: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Usuario> listar() throws POSException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM USUARIO ORDER BY nombre";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            throw new POSException("No se pudo listar los usuarios: " + e.getMessage(), e);
        }
        return usuarios;
    }

    @Override
    public Usuario autenticar(String username, String password) throws POSException {
        String sql = "SELECT * FROM USUARIO WHERE username = ? AND password = ?";
        String passwordHash = hashear(password);
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new POSException("Error al autenticar: " + e.getMessage(), e);
        }
        return null; // credenciales incorrectas
    }

    @Override
    public boolean existeUsername(String username) throws POSException {
        String sql = "SELECT 1 FROM USUARIO WHERE username = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new POSException("Error al validar el username: " + e.getMessage(), e);
        }
    }

    /**
     * Construye el objeto concreto (Administrador o Cajero) segun la columna ROL.
     * Aqui se aplica el polimorfismo: cada fila de la BD se convierte en su subtipo real.
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_usuario");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String nombre = rs.getString("nombre");
        String rol = rs.getString("rol");

        if ("ADMINISTRADOR".equalsIgnoreCase(rol)) {
            return new Administrador(id, username, password, nombre);
        } else {
            return new Cajero(id, username, password, nombre);
        }
    }

    /**
     * Devuelve el hash SHA-256 (64 caracteres hex) del texto recibido.
     * Se usa para no guardar ni comparar contrasenas en texto plano.
     */
    public static String hashear(String texto) throws POSException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(texto.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new POSException("Error al cifrar la contrasena: " + e.getMessage(), e);
        }
    }
}
