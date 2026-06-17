package com.minimarket.pos.dao;

import com.minimarket.pos.modelo.Producto;
import com.minimarket.pos.util.POSException;
import java.util.List;

/**
 * Contrato de acceso a datos para la entidad Producto.
 * Extiende la interfaz genérica DAO<T> definida por el Integrante 1,
 * y agrega operaciones específicas del módulo de inventario.
 *
 * @author Integrante 2
 */
public interface ProductoDAO extends DAO<Producto> {

    /**
     * Busca un producto por su código de barras EAN-13.
     * Usado en la VentanaVenta para identificar el producto al momento del cobro.
     *
     * @param codigoBarras código EAN del producto
     * @return el Producto encontrado, o null si no existe
     */
    Producto buscarPorCodigoBarras(String codigoBarras) throws POSException;

    /**
     * Devuelve todos los productos cuyo nombre contenga el texto dado
     * (búsqueda insensible a mayúsculas).
     *
     * @param nombre texto a buscar (parcial o completo)
     * @return lista de productos que coinciden
     */
    List<Producto> buscarPorNombre(String nombre) throws POSException;

    /**
     * Devuelve todos los productos que pertenecen a una categoría.
     *
     * @param idCategoria ID de la categoría
     * @return lista de productos de esa categoría
     */
    List<Producto> listarPorCategoria(int idCategoria) throws POSException;

    /**
     * Devuelve los productos con stock igual o menor al mínimo indicado.
     * Útil para el reporte de "stock bajo" que genera el Integrante 5.
     *
     * @param stockMinimo umbral de alerta
     * @return lista de productos con stock crítico
     */
    List<Producto> listarStockBajo(int stockMinimo) throws POSException;
}
