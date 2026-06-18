package com.minimarket.pos;
import com.minimarket.pos.vista.VentanaLogin;
import javax.swing.SwingUtilities;

public class Main {
    //Ahora con este cambio Iniciara desde la ventana login, pero se necesita
    // que la base de datos ya este conectada con oracle, sino cuando se le de a login
    // va a aparecer error porque no esta conectada a la base de datos y no hay usuario creados.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }
}