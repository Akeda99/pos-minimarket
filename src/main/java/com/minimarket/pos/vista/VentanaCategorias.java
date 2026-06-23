package com.minimarket.pos.vista;

import com.minimarket.pos.dao.CategoriaDAOImpl;
import com.minimarket.pos.modelo.Categoria;
import com.minimarket.pos.util.POSException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Gestion de categorias (alta, edicion y baja).
 * Modulo de Categorias - Integrante 5.
 */
public class VentanaCategorias extends JInternalFrame {

    private final CategoriaDAOImpl dao = new CategoriaDAOImpl();

    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private Categoria seleccionada;

    public VentanaCategorias() {
        super("Gestion de Categorias", true, true, true, true);
        setSize(640, 440);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        construirUI();
        cargar();
    }

    private void construirUI() {
        setLayout(new BorderLayout(5, 5));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        form.setBorder(BorderFactory.createTitledBorder("Datos de la categoria"));
        txtNombre = new JTextField(14);
        txtDescripcion = new JTextField(22);
        form.add(new JLabel("Nombre:"));      form.add(txtNombre);
        form.add(new JLabel("Descripcion:")); form.add(txtDescripcion);
        add(form, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new Object[]{"ID", "Nombre", "Descripcion"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLimpiar  = new JButton("Limpiar");
        JButton btnGuardar  = new JButton("Guardar");
        JButton btnEliminar = new JButton("Eliminar");
        botones.add(btnLimpiar);
        botones.add(btnGuardar);
        botones.add(btnEliminar);
        add(botones, BorderLayout.SOUTH);

        btnLimpiar.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
    }

    private void cargar() {
        modelo.setRowCount(0);
        try {
            List<Categoria> lista = dao.listar();
            for (Categoria c : lista) {
                modelo.addRow(new Object[]{c.getIdCategoria(), c.getNombre(), c.getDescripcion()});
            }
        } catch (POSException e) {
            error(e.getMessage());
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        int id = (int) modelo.getValueAt(fila, 0);
        try {
            seleccionada = dao.buscarPorId(id);
            if (seleccionada != null) {
                txtNombre.setText(seleccionada.getNombre());
                txtDescripcion.setText(seleccionada.getDescripcion());
            }
        } catch (POSException e) {
            error(e.getMessage());
        }
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.");
            return;
        }
        String descripcion = txtDescripcion.getText().trim();
        try {
            if (seleccionada == null) {
                dao.insertar(new Categoria(0, nombre, descripcion));
            } else {
                seleccionada.setNombre(nombre);
                seleccionada.setDescripcion(descripcion);
                dao.actualizar(seleccionada);
            }
            limpiar();
            cargar();
        } catch (POSException e) {
            error(e.getMessage());
        }
    }

    private void eliminar() {
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una categoria de la tabla.");
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this,
                "Eliminar la categoria \"" + seleccionada.getNombre() + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (conf == JOptionPane.YES_OPTION) {
            try {
                dao.eliminar(seleccionada.getIdCategoria());
                limpiar();
                cargar();
            } catch (POSException e) {
                error(e.getMessage());
            }
        }
    }

    private void limpiar() {
        seleccionada = null;
        tabla.clearSelection();
        txtNombre.setText("");
        txtDescripcion.setText("");
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}