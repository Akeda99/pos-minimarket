package com.minimarket.pos.dao;

import com.minimarket.pos.modelo.Venta;
import com.minimarket.pos.util.POSException;
import java.util.List;

public interface VentaDAO extends DAO<Venta> {

    List<Venta> listarPorUsuario(int idUsuario) throws POSException;

    List<Venta> listarPorFecha(String fechaInicio, String fechaFin) throws POSException;
}