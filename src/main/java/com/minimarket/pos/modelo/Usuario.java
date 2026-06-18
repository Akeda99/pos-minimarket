package com.minimarket.pos.modelo;

/**
 * Clase abstracta que representa a un usuario del sistema POS.
 * Jerarquia: Usuario -> Administrador / Cajero.
 * Responsable: Integrante 3.
 */
public abstract class Usuario {

    protected int idUsuario;
    protected String username;
    protected String password;
    protected String nombre;

    protected Usuario() {
    }

    protected Usuario(int idUsuario, String username, String password, String nombre) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.password = password;
        this.nombre = nombre;
    }

    /**
     * Cada subtipo de usuario decide si puede ejercer un permiso determinado.
     * Polimorfismo: Administrador y Cajero responden distinto a esta pregunta.
     */
    public abstract boolean tienePermiso(String permiso);

    /** Texto del rol tal como se guarda en la columna ROL de la BD. */
    public abstract String getRol();

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre + " (" + username + ") - " + getRol();
    }
}
