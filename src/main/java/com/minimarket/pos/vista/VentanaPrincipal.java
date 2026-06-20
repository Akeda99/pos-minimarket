package com.minimarket.pos.vista;

import javax.swing.*;
import java.beans.PropertyVetoException;

/**
 * Ventana principal (MDI) que integra todos los modulos del sistema.
 * Responsable: Integrante 1.
 *
 * El menu se construye segun el rol del usuario que inicio sesion:
 *   - ADMINISTRADOR: ve todos los modulos (gestion de inventario, ventas,
 *     usuarios, categorias y reportes).
 *   - CAJERO: ve solo Ventas y la consulta de Inventario (sin poder editar).
 */
public class VentanaPrincipal extends JFrame {

    private final JDesktopPane desktop = new JDesktopPane();
    private final String rol;

    public VentanaPrincipal(String rol) {
        this.rol = rol;
        setTitle("Sistema POS Minimarket - [" + rol + "]");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(desktop);
        setJMenuBar(crearMenu());
    }

    private JMenuBar crearMenu() {
        JMenuBar barra = new JMenuBar();
        boolean esAdmin = "ADMINISTRADOR".equalsIgnoreCase(rol);

        // --- Inventario: el administrador lo gestiona; el cajero solo lo consulta ---
        JMenu mInventario = new JMenu("Inventario");
        if (esAdmin) {
            JMenuItem itProductos = new JMenuItem("Gestionar productos");
            itProductos.addActionListener(e -> abrir(new VentanaInventario()));            // [I2] Boris
            mInventario.add(itProductos);
        } else {
            JMenuItem itConsultar = new JMenuItem("Consultar productos");
            itConsultar.addActionListener(e -> abrir(new VentanaInventario(true)));         // [I2] solo lectura
            mInventario.add(itConsultar);
        }

        // --- Ventas: disponible para ambos roles ---
        JMenu mVentas = new JMenu("Ventas");
        JMenuItem itVender = new JMenuItem("Registrar venta");
        itVender.addActionListener(e -> abrir(new VentanaVenta()));                         // [I4] Leydi
        mVentas.add(itVender);

        barra.add(mInventario);
        barra.add(mVentas);

        // --- Opciones exclusivas del ADMINISTRADOR ---
        if (esAdmin) {
            JMenu mUsuarios = new JMenu("Usuarios");
            JMenuItem itUsuarios = new JMenuItem("Gestionar usuarios");
            itUsuarios.addActionListener(e -> abrir(new VentanaUsuarios()));                // [I3] Nestor
            mUsuarios.add(itUsuarios);

            JMenu mMaestros = new JMenu("Categorias");
            JMenuItem itCategorias = new JMenuItem("Ver Categorias");
            itCategorias.addActionListener(e -> enDesarrollo("Categorias", "Integrante 5")); // [I5]
            mMaestros.add(itCategorias);

            JMenu mReportes = new JMenu("Reportes");
            JMenuItem itReportes = new JMenuItem("Ver reportes");
            itReportes.addActionListener(e -> enDesarrollo("Reportes", "Integrante 5"));     // [I5]
            mReportes.add(itReportes);

            barra.add(mUsuarios);
            barra.add(mMaestros);
            barra.add(mReportes);
        }

        return barra;
    }

    /**
     * Abre una ventana interna dentro del escritorio. Si ya esta abierta y
     * visible, la trae al frente; si estaba oculta o cerrada, la elimina y
     * crea una nueva limpia para que siempre se pueda volver a abrir.
     */
    public void abrir(JInternalFrame ventana) {
        for (JInternalFrame abierta : desktop.getAllFrames()) {
            if (abierta.getClass() == ventana.getClass()) {
                if (abierta.isVisible() && !abierta.isClosed()) {
                    traerAlFrente(abierta);
                    return;
                }
                abierta.dispose();
                desktop.remove(abierta);
            }
        }
        desktop.add(ventana);
        ventana.setVisible(true);
        desktop.repaint();
        traerAlFrente(ventana);
    }

    private void traerAlFrente(JInternalFrame ventana) {
        try {
            if (ventana.isIcon()) {
                ventana.setIcon(false);   // restaura la ventana si estaba minimizada
            }
            ventana.setSelected(true);
        } catch (PropertyVetoException ex) {
            // sin accion: si el frame rechaza la operacion, lo dejamos como esta
        }
        ventana.toFront();
    }

    /** Aviso temporal para los modulos que aun no han sido entregados. */
    private void enDesarrollo(String modulo, String integrante) {
        JOptionPane.showMessageDialog(this,
                "El modulo de " + modulo + " esta en desarrollo (" + integrante + ").",
                "Modulo en desarrollo", JOptionPane.INFORMATION_MESSAGE);
    }
}