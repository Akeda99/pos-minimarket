package com.minimarket.pos.util;

/** Validaciones comunes reutilizables. Responsable: Integrante Ray Cardenas. */
public class Validador {

    public static boolean noVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    public static boolean esEntero(String texto) {
        try { Integer.parseInt(texto); return true; }
        catch (NumberFormatException e) { return false; }
    }

    public static boolean esDecimal(String texto) {
        try { Double.parseDouble(texto); return true; }
        catch (NumberFormatException e) { return false; }
    }

    public static boolean esPositivo(double valor) {
        return valor > 0;
    }
}
