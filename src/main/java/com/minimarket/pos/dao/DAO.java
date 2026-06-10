package com.minimarket.pos.dao;

import com.minimarket.pos.util.POSException;
import java.util.List;

/**
 * Interfaz generica del patron DAO. Toda implementacion de acceso a datos
 * debe respetar este contrato. Responsable: Integrante Ray Cardenas.
 */
public interface DAO<T> {
    void insertar(T objeto) throws POSException;
    void actualizar(T objeto) throws POSException;
    void eliminar(int id) throws POSException;
    T buscarPorId(int id) throws POSException;
    List<T> listar() throws POSException;
}
