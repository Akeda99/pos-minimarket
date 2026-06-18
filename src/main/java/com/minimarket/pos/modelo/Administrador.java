package com.minimarket.pos.modelo;

/**
 * Usuario con rol Administrador: tiene acceso a todos los modulos del sistema
 * (inventario, usuarios, ventas, categorias y reportes).
 * Responsable: Integrante 3.
 */
public class Administrador extends Usuario {

    public Administrador() {
        super();
    }

    public Administrador(int idUsuario, String username, String password, String nombre) {
        super(idUsuario, username, password, nombre);
    }

    @Override
    public boolean tienePermiso(String permiso) {
        // El administrador puede hacer cualquier operacion del sistema.
        return true;
    }

    @Override
    public String getRol() {
        return "ADMINISTRADOR";
    }
}
