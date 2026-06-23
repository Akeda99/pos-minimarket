package com.minimarket.pos.vista;

import com.minimarket.pos.dao.ProductoDAOImpl;
import com.minimarket.pos.modelo.Producto;
import com.minimarket.pos.util.POSException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Ventana de gestión de inventario.
 * Muestra todos los productos en un JTable con filtro por nombre,
 * y permite agregar, editar y eliminar productos mediante el formulario
 * VentanaProductoForm.
 *
 * Se integra a VentanaPrincipal (I1) como JInternalFrame.
 *
 * @author Integrante 2
 */
public class VentanaInventario extends JInternalFrame {

    // ── DAO ────────────────────────────────────────────────────────────────
    private final ProductoDAOImpl dao = new ProductoDAOImpl();

    // ── Componentes UI ─────────────────────────────────────────────────────
    private JTable          tabla;
    private DefaultTableModel modeloTabla;
    private JTextField      txtBuscar;
    private JButton         btnBuscar;
    private JButton         btnNuevo;
    private JButton         btnEditar;
    private JButton         btnEliminar;
    private JButton         btnRefrescar;

    // ── Columnas de la tabla ───────────────────────────────────────────────
    private static final String[] COLUMNAS = {
        "ID", "Código Barras", "Nombre", "Tipo",
        "Precio Venta (S/)", "Stock", "Categoría"
    };

    // ── Constructor ────────────────────────────────────────────────────────
    public VentanaInventario() {
        this(false);
    }

    /**
     * @param soloLectura si es true, oculta los botones Nuevo/Editar/Eliminar
     *        y muestra el inventario en modo consulta (por ejemplo, para el
     *        rol CAJERO, que puede ver pero no modificar).
     */
    public VentanaInventario(boolean soloLectura) {
        super(soloLectura ? "Consulta de Inventario" : "Gestión de Inventario",
              true, true, true, true);
        setSize(820, 480);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        inicializarUI();
        if (soloLectura) {
            btnNuevo.setVisible(false);
            btnEditar.setVisible(false);
            btnEliminar.setVisible(false);
        }
        cargarTabla(null);
    }

    // ── Inicialización UI ──────────────────────────────────────────────────
    private void inicializarUI() {
        setLayout(new BorderLayout(5, 5));

        // Panel superior: buscador
        JPanel panelSup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSup.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        panelSup.add(new JLabel("Buscar producto:"));
        txtBuscar = new JTextField(20);
        panelSup.add(txtBuscar);

        btnBuscar = new JButton("Buscar");
        panelSup.add(btnBuscar);

        btnRefrescar = new JButton("Mostrar todos");
        panelSup.add(btnRefrescar);

        add(panelSup, BorderLayout.NORTH);

        // Tabla central
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(200);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Panel de botones CRUD
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNuevo    = new JButton("Nuevo");
        btnEditar   = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");

        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        add(panelBotones, BorderLayout.SOUTH);

        // ── Eventos ─────────────────────────────────────────────────────
        btnBuscar.addActionListener(e -> buscar());
        txtBuscar.addActionListener(e -> buscar());

        btnRefrescar.addActionListener(e -> {
            txtBuscar.setText("");
            cargarTabla(null);
        });

        btnNuevo.addActionListener(e -> abrirFormulario(null));

        btnEditar.addActionListener(e -> {
            Producto sel = getProductoSeleccionado();
            if (sel != null) abrirFormulario(sel);
        });

        btnEliminar.addActionListener(e -> eliminarSeleccionado());
    }

    // ── Carga de datos ─────────────────────────────────────────────────────
    /**
     * Carga (o recarga) la tabla.
     * @param filtroNombre null → todos; texto → búsqueda por nombre
     */
    private void cargarTabla(String filtroNombre) {
        modeloTabla.setRowCount(0);
        try {
            List<Producto> lista = (filtroNombre == null || filtroNombre.isBlank())
                    ? dao.listar()
                    : dao.buscarPorNombre(filtroNombre);

            for (Producto p : lista) {
                modeloTabla.addRow(new Object[]{
                    p.getId(),
                    p.getCodigoBarras(),
                    p.getNombre(),
                    p.getTipo(),
                    String.format("%.2f", p.calcularPrecioVenta()),
                    p.getStock(),
                    p.getNombreCategoria()
                });
            }
        } catch (POSException ex) {
            mostrarError("Error al cargar inventario: " + ex.getMessage());
        }
    }

    // ── Búsqueda ───────────────────────────────────────────────────────────
    private void buscar() {
        String texto = txtBuscar.getText().trim();
        cargarTabla(texto.isEmpty() ? null : texto);
    }

    // ── Abrir formulario ───────────────────────────────────────────────────
    /**
     * Abre VentanaProductoForm en modo "nuevo" (producto=null) o "edición".
     * Tras cerrar recarga la tabla si hubo cambios.
     */
    private void abrirFormulario(Producto producto) {
        VentanaProductoForm form = new VentanaProductoForm(
                (JFrame) SwingUtilities.getWindowAncestor(this), producto);
        form.setVisible(true);
        if (form.isGuardado()) {
            cargarTabla(txtBuscar.getText().trim().isEmpty()
                        ? null : txtBuscar.getText().trim());
        }
    }

    // ── Eliminar ───────────────────────────────────────────────────────────
    private void eliminarSeleccionado() {
        Producto p = getProductoSeleccionado();
        if (p == null) return;

        int conf = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el producto \"" + p.getNombre() + "\"?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (conf == JOptionPane.YES_OPTION) {
            try {
                dao.eliminar(p.getId());
                cargarTabla(null);
                JOptionPane.showMessageDialog(this, "Producto eliminado.");
            } catch (POSException ex) {
                mostrarError("No se pudo eliminar: " + ex.getMessage());
            }
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────
    /**
     * Devuelve el Producto correspondiente a la fila seleccionada, o null.
     */
    private Producto getProductoSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un producto de la tabla.", "Aviso",
                JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        try {
            return dao.buscarPorId(id);
        } catch (POSException ex) {
            mostrarError("Error al recuperar producto: " + ex.getMessage());
            return null;
        }
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}