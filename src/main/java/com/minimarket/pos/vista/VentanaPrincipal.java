package com.minimarket.pos.vista;

import javax.swing.*;

/**
 * Ventana principal (MDI) que integra todos los modulos del sistema.
 * Responsable: Integrante Ray Cardenas. Cada item de menu abre la ventana de un modulo.
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
        // El integrante 2 debe abrir VentanaInventario dentro del desktop despues de apretar
        // itProductos.addActionListener(e -> abrir(new VentanaInventario()));
        mInventario.add(itProductos);

        JMenu mVentas = new JMenu("Ventas");
        JMenuItem itVender = new JMenuItem("Registrar venta");
        // El integrante 4 debe de abrir VentanaVenta:
        mVentas.add(itVender);

        JMenu mUsuarios = new JMenu("Usuarios");
        JMenuItem itUsuarios = new JMenuItem("Gestionar usuarios");
        // El integrante 3: abrir VentanaUsuarios
        mUsuarios.add(itUsuarios);

        JMenu mMaestros = new JMenu("Categorias");
        JMenuItem itCategorias = new JMenuItem("Gestionar Categorias");
        // Integrante 5: abrir VentanaCategorias
        mMaestros.add(itCategorias);

        JMenu mReportes = new JMenu("Reportes");
        JMenuItem itReportes = new JMenuItem("Ver reportes");
        // Integrante 5:  abrir VentanaReportes
        mReportes.add(itReportes);

        barra.add(mInventario);
        barra.add(mVentas);
        barra.add(mUsuarios);
        barra.add(mMaestros);
        barra.add(mReportes);
        return barra;
    }

    /** Utilidad para abrir una ventana interna dentro del escritorio. */
    public void abrir(JInternalFrame ventana) {
        desktop.add(ventana);
        ventana.setVisible(true);
    }
}
