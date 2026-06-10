# Sistema POS Minimarket

Proyecto final del curso de Programacion Orientada a Objetos (Java) - TECSUP.
Aplicacion de escritorio: Java Swing + JDBC + patron DAO + Oracle.

## Requisitos
- JDK 17 (o 21)
- Apache NetBeans
- Oracle Database (XE sirve)
- Maven (incluido en NetBeans)

## Como arrancar
1. Clonar el repositorio.
2. Crear el esquema: ejecutar en orden los scripts de la carpeta `sql/`.
3. Configurar usuario/clave de Oracle en `util/ConexionBD.java`.
4. Abrir el proyecto en NetBeans y ejecutar `Main`.

## Estructura y responsables
Ver `docs/EQUIPO.md`. Cada archivo indica su responsable con `// TODO [I2]` ... `[I5]`.

## Flujo Git
- `main`: solo codigo que compila. No se trabaja directo aqui.
- Cada integrante en su rama: `feat/productos`, `feat/usuarios`, `feat/ventas`, `feat/reportes`.
- Al terminar una pieza: Pull Request -> el Integrante 1 integra a `main`.
