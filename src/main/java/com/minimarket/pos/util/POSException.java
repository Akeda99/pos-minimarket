package com.minimarket.pos.util;

/** Excepcion propia del sistema POS. Responsable: Integrante Ray Cardenas. */
public class POSException extends Exception {
    public POSException(String mensaje) {
        super(mensaje);
    }
    public POSException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
