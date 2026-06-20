-- ============================================================
--  Datos de prueba: categorias y productos de un minimarket
--  Para probar el inventario y el registro de ventas.
--  Ejecutar en SQL Developer con F5 (Ejecutar como script).
-- ============================================================

-- ---------- CATEGORIAS ----------
INSERT INTO CATEGORIA (nombre, descripcion) VALUES ('Bebidas',   'Gaseosas, aguas y refrescos');
INSERT INTO CATEGORIA (nombre, descripcion) VALUES ('Snacks',    'Piqueos, galletas y golosinas');
INSERT INTO CATEGORIA (nombre, descripcion) VALUES ('Abarrotes', 'Productos basicos de despensa');
INSERT INTO CATEGORIA (nombre, descripcion) VALUES ('Lacteos',   'Leche, yogurt y derivados');
INSERT INTO CATEGORIA (nombre, descripcion) VALUES ('Limpieza',  'Articulos de aseo y limpieza');

-- ---------- PRODUCTOS ----------
-- Empaquetados (precio fijo)
INSERT INTO PRODUCTO (codigo_barras, nombre, precio, stock, tipo_producto, fecha_vencimiento, precio_por_kg, id_categoria)
VALUES ('7750182001234', 'Inca Kola 500ml', 2.50, 50, 'EMPAQUETADO', NULL, NULL,
        (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'Bebidas'));

INSERT INTO PRODUCTO (codigo_barras, nombre, precio, stock, tipo_producto, fecha_vencimiento, precio_por_kg, id_categoria)
VALUES ('7894900011517', 'Coca Cola 500ml', 2.50, 40, 'EMPAQUETADO', NULL, NULL,
        (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'Bebidas'));

INSERT INTO PRODUCTO (codigo_barras, nombre, precio, stock, tipo_producto, fecha_vencimiento, precio_por_kg, id_categoria)
VALUES ('7750182002345', 'Agua San Luis 625ml', 1.50, 60, 'EMPAQUETADO', NULL, NULL,
        (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'Bebidas'));

INSERT INTO PRODUCTO (codigo_barras, nombre, precio, stock, tipo_producto, fecha_vencimiento, precio_por_kg, id_categoria)
VALUES ('7750182003456', 'Papas Lays Clasicas', 3.50, 30, 'EMPAQUETADO', NULL, NULL,
        (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'Snacks'));

INSERT INTO PRODUCTO (codigo_barras, nombre, precio, stock, tipo_producto, fecha_vencimiento, precio_por_kg, id_categoria)
VALUES ('7750182004567', 'Galletas Soda Field', 1.20, 45, 'EMPAQUETADO', NULL, NULL,
        (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'Snacks'));

INSERT INTO PRODUCTO (codigo_barras, nombre, precio, stock, tipo_producto, fecha_vencimiento, precio_por_kg, id_categoria)
VALUES ('7750182005678', 'Chocolate Sublime', 1.80, 35, 'EMPAQUETADO', NULL, NULL,
        (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'Snacks'));

INSERT INTO PRODUCTO (codigo_barras, nombre, precio, stock, tipo_producto, fecha_vencimiento, precio_por_kg, id_categoria)
VALUES ('7750182006789', 'Arroz Costeno 1kg', 4.80, 25, 'EMPAQUETADO', NULL, NULL,
        (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'Abarrotes'));

INSERT INTO PRODUCTO (codigo_barras, nombre, precio, stock, tipo_producto, fecha_vencimiento, precio_por_kg, id_categoria)
VALUES ('7750182007890', 'Aceite Primor 1L', 9.90, 20, 'EMPAQUETADO', NULL, NULL,
        (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'Abarrotes'));

INSERT INTO PRODUCTO (codigo_barras, nombre, precio, stock, tipo_producto, fecha_vencimiento, precio_por_kg, id_categoria)
VALUES ('7750182008901', 'Atun Florida lata', 5.50, 28, 'EMPAQUETADO', NULL, NULL,
        (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'Abarrotes'));

INSERT INTO PRODUCTO (codigo_barras, nombre, precio, stock, tipo_producto, fecha_vencimiento, precio_por_kg, id_categoria)
VALUES ('7750182009012', 'Fideos Don Vittorio 500g', 3.20, 32, 'EMPAQUETADO', NULL, NULL,
        (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'Abarrotes'));

INSERT INTO PRODUCTO (codigo_barras, nombre, precio, stock, tipo_producto, fecha_vencimiento, precio_por_kg, id_categoria)
VALUES ('7750182010123', 'Detergente Bolivar 800g', 7.50, 18, 'EMPAQUETADO', NULL, NULL,
        (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'Limpieza'));

INSERT INTO PRODUCTO (codigo_barras, nombre, precio, stock, tipo_producto, fecha_vencimiento, precio_por_kg, id_categoria)
VALUES ('7750182011234', 'Jabon Bolivar barra', 2.30, 40, 'EMPAQUETADO', NULL, NULL,
        (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'Limpieza'));

-- Perecibles (con fecha de vencimiento)
INSERT INTO PRODUCTO (codigo_barras, nombre, precio, stock, tipo_producto, fecha_vencimiento, precio_por_kg, id_categoria)
VALUES ('7750182012345', 'Leche Gloria 1L', 4.50, 24, 'PERECIBLE', DATE '2026-12-31', NULL,
        (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'Lacteos'));

INSERT INTO PRODUCTO (codigo_barras, nombre, precio, stock, tipo_producto, fecha_vencimiento, precio_por_kg, id_categoria)
VALUES ('7750182013456', 'Yogurt Gloria 1L', 6.80, 15, 'PERECIBLE', DATE '2026-10-15', NULL,
        (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'Lacteos'));

-- Por peso (precio por kilo)
INSERT INTO PRODUCTO (codigo_barras, nombre, precio, stock, tipo_producto, fecha_vencimiento, precio_por_kg, id_categoria)
VALUES ('7750182014567', 'Azucar rubia granel', 4.20, 100, 'POR_PESO', NULL, 4.20,
        (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'Abarrotes'));

COMMIT;
