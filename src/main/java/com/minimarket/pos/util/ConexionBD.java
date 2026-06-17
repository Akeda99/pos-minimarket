package com.minimarket.pos.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Conexion unica a la base de datos Oracle (patron Singleton).
 *
 * Las credenciales NO van en el codigo: se leen de un archivo
 * "config.properties" que cada integrante crea localmente a partir de
 * "config.properties.example". Ese archivo esta en .gitignore y NO se sube
 * al repositorio, para no exponer usuarios ni contrasenas.
 *
 * Responsable: Integrante 1.
 */
public class ConexionBD {

    private static final String URL;
    private static final String USER;
    private static final String PASS;

    private static Connection instancia;

    // Carga las credenciales desde config.properties (en el classpath) al iniciar.
    static {
        Properties props = new Properties();
        try (InputStream in = ConexionBD.class.getResourceAsStream("/config.properties")) {
            if (in == null) {
                throw new RuntimeException(
                    "No se encontro 'config.properties'. Copia 'config.properties.example' " +
                    "como 'config.properties' y coloca tus credenciales de Oracle.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer config.properties: " + e.getMessage(), e);
        }
        URL  = props.getProperty("db.url");
        USER = props.getProperty("db.user");
        PASS = props.getProperty("db.password");
    }

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
