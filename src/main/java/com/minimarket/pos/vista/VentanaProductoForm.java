package com.minimarket.pos.vista;

import com.minimarket.pos.dao.ProductoDAOImpl;
import com.minimarket.pos.dao.CategoriaDAOImpl;
import com.minimarket.pos.modelo.Producto;
import com.minimarket.pos.modelo.Categoria;
import com.minimarket.pos.modelo.ProductoEmpaquetado;
import com.minimarket.pos.modelo.ProductoPerecible;
import com.minimarket.pos.modelo.ProductoPorPeso;
import com.minimarket.pos.util.POSException;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Formulario modal para crear o editar un Producto.
 * Lo abre VentanaInventario: si recibe producto=null trabaja en modo "Nuevo",
 * y si recibe un Producto trabaja en modo "Editar".
 *
 * Tras cerrar, VentanaInventario consulta isGuardado() para saber si debe
 * recargar la tabla.
 *
 * La categoría se elige desde un JComboBox que se carga con las categorías
 * registradas en la base de datos (módulo del Integrante 5).
 *
 * @author Equipo POS Minimarket (módulo Inventario)
 */
public class VentanaProductoForm extends JDialog {

    private final ProductoDAOImpl dao = new ProductoDAOImpl();
    private final CategoriaDAOImpl categoriaDAO = new CategoriaDAOImpl();
    private final Producto productoEditar;   // null = nuevo
    private boolean guardado = false;

    // Tipos (coinciden con getTipo() de cada subclase)
    private static final String PERECEDERO  = "Perecedero";
    private static final String POR_PESO    = "Por Peso";
    private static final String EMPAQUETADO = "Empaquetado";

    // Componentes
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JComboBox<String> cmbTipo;
    private JLabel     lblPrecio;
    private JTextField txtPrecio;
    private JTextField txtStock;
    private JLabel     lblFecha;
    private JTextField txtFecha;     // yyyy-MM-dd
    private JComboBox<Categoria> cmbCategoria;

    public VentanaProductoForm(JFrame parent, Producto producto) {
        super(parent, producto == null ? "Nuevo producto" : "Editar producto", true);
        this.productoEditar = producto;
        construirUI();
        if (producto != null) precargar(producto);
        pack();
        setLocationRelativeTo(parent);
    }

    public boolean isGuardado() {
        return guardado;
    }

    // ── UI ─────────────────────────────────────────────────────────────────
    private void construirUI() {
        JPanel campos = new JPanel(new GridLayout(0, 2, 8, 8));
        campos.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        campos.add(new JLabel("Código de barras:"));
        txtCodigo = new JTextField(15);
        campos.add(txtCodigo);

        campos.add(new JLabel("Nombre:"));
        txtNombre = new JTextField(15);
        campos.add(txtNombre);

        campos.add(new JLabel("Tipo:"));
        cmbTipo = new JComboBox<>(new String[]{ PERECEDERO, POR_PESO, EMPAQUETADO });
        campos.add(cmbTipo);

        lblPrecio = new JLabel("Precio (S/):");
        campos.add(lblPrecio);
        txtPrecio = new JTextField(15);
        campos.add(txtPrecio);

        campos.add(new JLabel("Stock:"));
        txtStock = new JTextField(15);
        campos.add(txtStock);

        lblFecha = new JLabel("Vence (yyyy-MM-dd):");
        campos.add(lblFecha);
        txtFecha = new JTextField(15);
        campos.add(txtFecha);

        campos.add(new JLabel("Categoría:"));
        cmbCategoria = new JComboBox<>();
        cargarCategorias();
        campos.add(cmbCategoria);

        // El campo fecha y la etiqueta de precio dependen del tipo
        cmbTipo.addActionListener(e -> actualizarCamposSegunTipo());
        actualizarCamposSegunTipo();

        // Botones
        JButton btnGuardar  = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        setLayout(new BorderLayout());
        add(campos, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /** Habilita la fecha solo para perecederos y ajusta la etiqueta del precio. */
    private void actualizarCamposSegunTipo() {
        String tipo = (String) cmbTipo.getSelectedItem();
        boolean esPerecedero = PERECEDERO.equals(tipo);
        boolean esPorPeso    = POR_PESO.equals(tipo);

        txtFecha.setEnabled(esPerecedero);
        if (!esPerecedero) txtFecha.setText("");
        lblPrecio.setText(esPorPeso ? "Precio x kg (S/):" : "Precio (S/):");
    }

    private void precargar(Producto p) {
        txtCodigo.setText(p.getCodigoBarras());
        txtNombre.setText(p.getNombre());
        cmbTipo.setSelectedItem(p.getTipo());
        txtPrecio.setText(String.valueOf(p.getPrecioBase()));
        txtStock.setText(String.valueOf(p.getStock()));
        seleccionarCategoria(p.getIdCategoria());
        if (p instanceof ProductoPerecible) {
            LocalDate fv = ((ProductoPerecible) p).getFechaVencimiento();
            if (fv != null) txtFecha.setText(fv.toString());
        }
        actualizarCamposSegunTipo();
    }

    /** Carga en el combo todas las categorías registradas en la base de datos. */
    private void cargarCategorias() {
        try {
            cmbCategoria.removeAllItems();
            for (Categoria c : categoriaDAO.listar()) {
                cmbCategoria.addItem(c);
            }
        } catch (POSException ex) {
            error("No se pudieron cargar las categorías: " + ex.getMessage());
        }
    }

    /** Deja seleccionada en el combo la categoría cuyo id coincide (modo edición). */
    private void seleccionarCategoria(int idCategoria) {
        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
            if (cmbCategoria.getItemAt(i).getIdCategoria() == idCategoria) {
                cmbCategoria.setSelectedIndex(i);
                return;
            }
        }
    }

    // ── Guardar ──────────────────────────────────────────────────────────────
    private void guardar() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty()) {
            error("El código de barras y el nombre son obligatorios.");
            return;
        }

        double precio;
        int stock;
        try {
            precio = Double.parseDouble(txtPrecio.getText().trim());
            stock  = Integer.parseInt(txtStock.getText().trim());
        } catch (NumberFormatException ex) {
            error("El precio y el stock deben ser numéricos.");
            return;
        }
        if (precio < 0 || stock < 0) {
            error("El precio y el stock no pueden ser negativos.");
            return;
        }

        Categoria catSel = (Categoria) cmbCategoria.getSelectedItem();
        if (catSel == null) {
            error("Debe seleccionar una categoría. Si no hay ninguna, créela primero en el módulo de Categorías.");
            return;
        }
        int idCategoria = catSel.getIdCategoria();

        int id = (productoEditar == null) ? 0 : productoEditar.getId();
        String tipo = (String) cmbTipo.getSelectedItem();
        Producto p;

        try {
            if (PERECEDERO.equals(tipo)) {
                LocalDate fv = null;
                String f = txtFecha.getText().trim();
                if (!f.isEmpty()) fv = LocalDate.parse(f);   // yyyy-MM-dd
                p = new ProductoPerecible(id, codigo, nombre, precio, stock, idCategoria, fv);
            } else if (POR_PESO.equals(tipo)) {
                p = new ProductoPorPeso(id, codigo, nombre, precio, stock, idCategoria);
            } else {
                p = new ProductoEmpaquetado(id, codigo, nombre, precio, stock, idCategoria);
            }
        } catch (DateTimeParseException ex) {
            error("La fecha debe tener el formato yyyy-MM-dd (ej. 2026-12-31).");
            return;
        }

        try {
            if (productoEditar == null) dao.insertar(p);
            else                        dao.actualizar(p);
            guardado = true;
            dispose();
        } catch (POSException ex) {
            error("No se pudo guardar: " + ex.getMessage());
        }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validación", JOptionPane.WARNING_MESSAGE);
    }
}