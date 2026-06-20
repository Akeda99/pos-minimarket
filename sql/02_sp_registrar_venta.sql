-- ============================================================
--  Procedimientos almacenados para el registro de ventas
--  Modulo de Ventas - Integrante 4
-- ============================================================

-- Registra la cabecera de la venta y devuelve el ID generado (parametro OUT).
CREATE OR REPLACE PROCEDURE SP_REGISTRAR_VENTA (
    p_total       IN  NUMBER,
    p_metodo_pago IN  VARCHAR2,
    p_id_usuario  IN  NUMBER,
    p_id_venta    OUT NUMBER
) AS
BEGIN
    INSERT INTO VENTA (fecha, total, metodo_pago, id_usuario)
    VALUES (SYSTIMESTAMP, p_total, p_metodo_pago, p_id_usuario)
    RETURNING id_venta INTO p_id_venta;
END;
/

-- Registra un detalle de venta, valida y descuenta el stock del producto.
-- Si el stock es insuficiente, lanza un error y la venta completa se revierte.
CREATE OR REPLACE PROCEDURE SP_REGISTRAR_DETALLE (
    p_id_venta        IN NUMBER,
    p_id_producto     IN NUMBER,
    p_cantidad        IN NUMBER,
    p_precio_unitario IN NUMBER
) AS
    v_stock NUMBER;
BEGIN
    -- Bloquea la fila del producto y lee su stock actual
    SELECT stock INTO v_stock
    FROM PRODUCTO
    WHERE id_producto = p_id_producto
    FOR UPDATE;

    IF v_stock < p_cantidad THEN
        RAISE_APPLICATION_ERROR(-20001,
            'Stock insuficiente para el producto ' || p_id_producto ||
            ' (disponible: ' || v_stock || ', solicitado: ' || p_cantidad || ').');
    END IF;

    INSERT INTO DETALLE_VENTA (id_venta, id_producto, cantidad, precio_unitario, subtotal)
    VALUES (p_id_venta, p_id_producto, p_cantidad, p_precio_unitario,
            p_cantidad * p_precio_unitario);

    UPDATE PRODUCTO
    SET stock = stock - p_cantidad
    WHERE id_producto = p_id_producto;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20002,
            'El producto ' || p_id_producto || ' no existe.');
END;
/
