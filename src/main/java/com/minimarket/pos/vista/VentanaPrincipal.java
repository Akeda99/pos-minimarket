package com.minimarket.pos.vista;

import javax.swing.*;
import java.beans.PropertyVetoException;

/**
 * Ventana principal (MDI) que integra todos los modulos del sistema.
 * Responsable: Integrante 1. Cada item de menu abre la ventana de un modulo.
 */
public class VentanaPrincipal extends JFrame {

    private final JDesktopPane desktop = new JDesktopPane();

    public VentanaPrincipal(String rol) {
        setTitle("Sistema POS Minimarket - [" + rol + "]");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(desktop);
        setJMenuBar(crearMenu());
    }

    private JMenuBar crearMenu() {
        JMenuBar barra = new JMenuBar();

        JMenu mInventario = new JMenu("Inventario");
        JMenuItem itProductos = new JMenuItem("Gestionar productos");
        itProductos.addActionListener(e -> abrir(new VentanaInventario()));   // [I2] Boris
        mInventario.add(itProductos);

        JMenu mVentas = new JMenu("Ventas");
        JMenuItem itVender = new JMenuItem("Registrar venta");
        itVender.addActionListener(e -> enDesarrollo("Ventas", "Integrante 4"));   // [I4]
        mVentas.add(itVender);

        JMenu mUsuarios = new JMenu("Usuarios");
        JMenuItem itUsuarios = new JMenuItem("Gestionar usuarios");
        itUsuarios.addActionListener(e -> abrir(new VentanaUsuarios()));   // [I3] Nestor
        mUsuarios.add(itUsuarios);

        JMenu mMaestros = new JMenu("Maestros");
        JMenuItem itCategorias = new JMenuItem("Categorias");
        itCategorias.addActionListener(e -> enDesarrollo("Categorias", "Integrante 5"));   // [I5]
        mMaestros.add(itCategorias);

        JMenu mReportes = new JMenu("Reportes");
        JMenuItem itReportes = new JMenuItem("Ver reportes");
        itReportes.addActionListener(e -> enDesarrollo("Reportes", "Integrante 5"));   // [I5]
        mReportes.add(itReportes);

        barra.add(mInventario);
        barra.add(mVentas);
        barra.add(mUsuarios);
        barra.add(mMaestros);
        barra.add(mReportes);
        return barra;
    }

    /**
     * Abre una ventana interna dentro del escritorio. Si ya esta abierta,
     * la trae al frente en lugar de duplicarla.
     */
    public void abrir(JInternalFrame ventana) {
        for (JInternalFrame abierta : desktop.getAllFrames()) {
            if (abierta.getClass() == ventana.getClass()) {
                traerAlFrente(abierta);
                return;
            }
        }
        desktop.add(ventana);
        ventana.setVisible(true);
        traerAlFrente(ventana);
    }

    private void traerAlFrente(JInternalFrame ventana) {
        ventana.toFront();
        try {
            ventana.setSelected(true);
        } catch (PropertyVetoException ex) {
            // sin accion: si el frame rechaza la seleccion, lo dejamos como esta
        }
    }

    /** Aviso temporal para los modulos que aun no han sido entregados. */
    private void enDesarrollo(String modulo, String integrante) {
        JOptionPane.showMessageDialog(this,
                "El modulo de " + modulo + " esta en desarrollo (" + integrante + ").",
                "Modulo en desarrollo", JOptionPane.INFORMATION_MESSAGE);
    }
}