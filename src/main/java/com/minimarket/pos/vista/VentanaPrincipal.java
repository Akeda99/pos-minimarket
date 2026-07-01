package com.minimarket.pos.vista;

import com.minimarket.pos.modelo.Usuario;
import javax.swing.*;
import java.beans.PropertyVetoException;

/**
 * Ventana principal (MDI) que integra todos los modulos del sistema.
 * Responsable: Integrante 1.
 *
 * El menu se arma segun el rol del usuario que inicio sesion:
 *   - ADMINISTRADOR: ve todos los modulos.
 *   - CAJERO: ve solo Ventas y la consulta de Inventario.
 * Ademas, cada venta se registra a nombre del usuario conectado.
 */
public class VentanaPrincipal extends JFrame {

    private final JDesktopPane desktop = new JDesktopPane();
    private final Usuario usuario;

    public VentanaPrincipal(Usuario usuario) {
        this.usuario = usuario;
        setTitle("Sistema POS Minimarket - " + usuario.getNombre() + " [" + usuario.getRol() + "]");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(desktop);
        setJMenuBar(crearMenu());
    }

    private JMenuBar crearMenu() {
        JMenuBar barra = new JMenuBar();
        boolean esAdmin = "ADMINISTRADOR".equalsIgnoreCase(usuario.getRol());

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

        // --- Ventas: para ambos roles; la venta queda a nombre del usuario ---
        JMenu mVentas = new JMenu("Ventas");
        JMenuItem itVender = new JMenuItem("Registrar venta");
        itVender.addActionListener(e -> {
            VentanaVenta ventaVenta = new VentanaVenta();
            ventaVenta.setIdUsuario(usuario.getIdUsuario());                                // [I4] Leydi
            abrir(ventaVenta);
        });
        mVentas.add(itVender);

        barra.add(mInventario);
        barra.add(mVentas);

        // --- Opciones exclusivas del ADMINISTRADOR ---
        if (esAdmin) {
            JMenu mUsuarios = new JMenu("Usuarios");
            JMenuItem itUsuarios = new JMenuItem("Gestionar usuarios");
            itUsuarios.addActionListener(e -> abrir(new VentanaUsuarios()));                // [I3] Nestor
            mUsuarios.add(itUsuarios);

            JMenu mCategorias = new JMenu("Categorias");
            JMenuItem itCategorias = new JMenuItem("Ver Categorias");
            itCategorias.addActionListener(e -> abrir(new VentanaCategorias()));            // [I5] Eddy
            mCategorias.add(itCategorias);

            JMenu mReportes = new JMenu("Reportes");
            JMenuItem itReportes = new JMenuItem("Ver reportes");
            itReportes.addActionListener(e -> abrir(new VentanaReportes()));                // [I5] Eddy
            mReportes.add(itReportes);

            barra.add(mUsuarios);
            barra.add(mCategorias);
            barra.add(mReportes);
        }

        // --- Sesion: cerrar sesion (volver al login) o salir; para ambos roles ---
        JMenu mSesion = new JMenu("Sesion");
        JMenuItem itCerrar = new JMenuItem("Cerrar sesion");
        itCerrar.addActionListener(e -> cerrarSesion());                                // [I1] Ray
        JMenuItem itSalir = new JMenuItem("Salir");
        itSalir.addActionListener(e -> System.exit(0));
        mSesion.add(itCerrar);
        mSesion.add(itSalir);
        barra.add(mSesion);

        return barra;
    }

    /** Cierra la sesion actual y regresa a la ventana de acceso. */
    private void cerrarSesion() {
        int r = JOptionPane.showConfirmDialog(this,
                "Deseas cerrar la sesion actual?", "Cerrar sesion",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            dispose();
            new VentanaLogin().setVisible(true);
        }
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
                ventana.setIcon(false);
            }
            ventana.setSelected(true);
        } catch (PropertyVetoException ex) {
            // sin accion
        }
        ventana.toFront();
    }
}