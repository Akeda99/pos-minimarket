package com.minimarket.pos;

import com.minimarket.pos.vista.VentanaPrincipal;
import javax.swing.SwingUtilities;

/** Punto de entrada de la aplicacion. Responsable: Integrante 1-Ray Cardenas. */
public class Main {
    public static void main(String[] args) {
        // NOTA NO OLVIDAR: cuando el Integrante 3 suba su VentanaLogin, cambiar este arranque O INICIO
        // para que la aplicacion inicie por el login deberia de iniciar por ahi. por ahora, abrimos
        // directamente la ventana principal para probar si es que funciona la base o no.
        SwingUtilities.invokeLater(() -> new VentanaPrincipal("ADMINISTRADOR").setVisible(true));
    }
}
