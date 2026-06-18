package com.minimarket.pos.modelo;

import java.util.Set;

/**
 * Usuario con rol Cajero: solo puede registrar ventas y consultar productos.
 * No tiene acceso a la gestion de usuarios, inventario, categorias ni reportes.
 * Responsable: Integrante 3.
 */
public class Cajero extends Usuario {

    // Lista de permisos concretos que puede ejercer un cajero.
    private static final Set<String> PERMISOS_CAJERO = Set.of("VENTA", "CONSULTA_PRODUCTO");

    public Cajero() {
        super();
    }

    public Cajero(int idUsuario, String username, String password, String nombre) {
        super(idUsuario, username, password, nombre);
    }

    @Override
    public boolean tienePermiso(String permiso) {
        return PERMISOS_CAJERO.contains(permiso);
    }

    @Override
    public String getRol() {
        return "CAJERO";
    }
}
