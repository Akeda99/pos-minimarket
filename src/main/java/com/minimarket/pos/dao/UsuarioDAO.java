package com.minimarket.pos.dao;

import com.minimarket.pos.modelo.Usuario;
import com.minimarket.pos.util.POSException;

/**
 * Contrato de acceso a datos para Usuario. Respeta la interfaz generica
 * DAO&lt;T&gt; definida por el Integrante 1 y agrega los metodos propios
 * del modulo de seguridad.
 * Responsable: Integrante 3.
 */
public interface UsuarioDAO extends DAO<Usuario> {

    /**
     * Busca un usuario por username y valida la contrasena.
     * @return el Usuario (Administrador o Cajero) si las credenciales son correctas,
     *         o null si no coinciden.
     */
    Usuario autenticar(String username, String password) throws POSException;

    /** Permite validar duplicados antes de insertar un usuario nuevo. */
    boolean existeUsername(String username) throws POSException;
}
