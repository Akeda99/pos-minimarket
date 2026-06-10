package com.minimarket.pos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexion unica a la base de datos Oracle- Patron Singleton.
 * Responsable: Integrante Ray Cardenas.
 */
public class ConexionBD {

    // TODO [I1]: ajustar segun tu instalacion de Oracle
    private static final String URL  = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "pos_minimarket";
    private static final String PASS = "la_clave_xd";

    private static Connection instancia;

    private ConexionBD() { }

    public static Connection getConexion() throws SQLException {
        if (instancia == null || instancia.isClosed()) {
            instancia = DriverManager.getConnection(URL, USER, PASS);
        }
        return instancia;
    }

    public static void cerrar() {
        try {
            if (instancia != null && !instancia.isClosed()) {
                instancia.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexion: " + e.getMessage());
        }
    }
}
