package com.minimarket.pos.vista;

import com.minimarket.pos.dao.ProductoDAO;
import com.minimarket.pos.dao.ProductoDAOImpl;
import com.minimarket.pos.dao.VentaDAOImpl;
import com.minimarket.pos.modelo.DetalleVenta;
import com.minimarket.pos.modelo.MetodoPago;
import com.minimarket.pos.modelo.Producto;
import com.minimarket.pos.modelo.Venta;
import com.minimarket.pos.util.POSException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaVenta extends JInternalFrame {

    private final ProductoDAO productoDAO = new ProductoDAOImpl();
    private final VentaDAOImpl ventaDAO   = new VentaDAOImpl();

    private JTextField txtCodigo, txtNombre, txtPrecio, txtCantidad;
    private JLabel lblTotal;
    private JTable tablaCarrito;
    private DefaultTableModel modeloTabla;
    private JComboBox<MetodoPago> cbMetodoPago;
    private JButton btnAgregar, btnQuitar, btnCobrar, btnNuevaVenta;

    private Venta ventaActual;
    private Producto productoSeleccionado;
    private int idUsuarioActual = 1;

    public VentanaVenta() {
        super("Registrar Venta", true, true, true, true);
        setSize(750, 520);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        iniciarNuevaVenta();
        construirUI();
    }

    private void construirUI() {
        add(panelBusqueda(), BorderLayout.NORTH);
        add(panelCarrito(),  BorderLayout.CENTER);
        add(panelCobro(),    BorderLayout.SOUTH);
    }

    private JPanel panelBusqueda() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        p.setBorder(BorderFactory.createTitledBorder("Agregar producto"));

        txtCodigo   = new JTextField(10);
        txtNombre   = new JTextField(16); txtNombre.setEditable(false);
        txtPrecio   = new JTextField(7);  txtPrecio.setEditable(false);
        txtCantidad = new JTextField(4);  txtCantidad.setText("1");
        btnAgregar  = new JButton("Agregar");
        JButton btnBuscarNombre = new JButton("Buscar x nombre");

        p.add(new JLabel("Codigo barras:")); p.add(txtCodigo);
        p.add(btnBuscarNombre);
        p.add(new JLabel("Producto:"));      p.add(txtNombre);
        p.add(new JLabel("Precio S/:"));     p.add(txtPrecio);
        p.add(new JLabel("Cant.:"));         p.add(txtCantidad);
        p.add(btnAgregar);

        txtCodigo.addActionListener(e -> buscarProducto());
        btnBuscarNombre.addActionListener(e -> buscarPorNombre());
        btnAgregar.addActionListener(e -> agregarAlCarrito());

        return p;
    }

    private JPanel panelCarrito() {
        String[] columnas = {"ID", "Producto", "Cant.", "P. Unit.", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaCarrito = new JTable(modeloTabla);
        tablaCarrito.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCarrito.getColumnModel().getColumn(0).setMaxWidth(50);

        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Carrito"));
        p.add(new JScrollPane(tablaCarrito), BorderLayout.CENTER);

        btnQuitar = new JButton("Quitar seleccionado");
        btnQuitar.addActionListener(e -> quitarItem());
        p.add(btnQuitar, BorderLayout.SOUTH);

        return p;
    }

    private JPanel panelCobro() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));

        cbMetodoPago = new JComboBox<>(MetodoPago.values());
        lblTotal = new JLabel("TOTAL: S/ 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(new Color(0, 100, 0));

        btnNuevaVenta = new JButton("Nueva venta");
        btnNuevaVenta.addActionListener(e -> nuevaVenta());

        btnCobrar = new JButton("Cobrar");
        btnCobrar.setBackground(new Color(0, 150, 0));
        btnCobrar.setForeground(Color.WHITE);
        btnCobrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCobrar.addActionListener(e -> cobrar());

        p.add(new JLabel("Metodo de pago:"));
        p.add(cbMetodoPago);
        p.add(btnNuevaVenta);
        p.add(lblTotal);
        p.add(btnCobrar);

        return p;
    }

    private void buscarProducto() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) return;
        try {
            Producto producto = productoDAO.buscarPorCodigoBarras(codigo);
            if (producto == null) {
                JOptionPane.showMessageDialog(this, "Producto no encontrado.");
                txtCodigo.selectAll();
                return;
            }
            productoSeleccionado = producto;
            txtNombre.setText(producto.getNombre());
            txtPrecio.setText(String.valueOf(producto.calcularPrecioVenta()));
            txtCantidad.selectAll();
            txtCantidad.requestFocus();
        } catch (POSException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Busca productos por nombre (coincidencia parcial) y deja elegir de una lista. */
    private void buscarPorNombre() {
        String texto = JOptionPane.showInputDialog(this, "Escribe parte del nombre del producto:");
        if (texto == null || texto.trim().isEmpty()) return;
        try {
            java.util.List<Producto> encontrados = productoDAO.buscarPorNombre(texto.trim());
            if (encontrados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron productos con ese nombre.");
                return;
            }
            Producto elegido = (Producto) JOptionPane.showInputDialog(
                    this, "Selecciona el producto:", "Resultados de la busqueda",
                    JOptionPane.QUESTION_MESSAGE, null,
                    encontrados.toArray(), encontrados.get(0));
            if (elegido == null) return;   // el usuario cancelo

            productoSeleccionado = elegido;
            txtCodigo.setText(elegido.getCodigoBarras());
            txtNombre.setText(elegido.getNombre());
            txtPrecio.setText(String.valueOf(elegido.calcularPrecioVenta()));
            txtCantidad.selectAll();
            txtCantidad.requestFocus();
        } catch (POSException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarAlCarrito() {
        if (productoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Primero busca un producto (presiona Enter en el codigo).");
            return;
        }
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();

            ventaActual.agregarDetalle(new DetalleVenta(
                    productoSeleccionado.getId(),
                    productoSeleccionado.getNombre(),
                    cantidad,
                    productoSeleccionado.calcularPrecioVenta()));
            refrescarTabla();

            productoSeleccionado = null;
            txtCodigo.setText("");
            txtNombre.setText("");
            txtPrecio.setText("");
            txtCantidad.setText("1");
            txtCodigo.requestFocus();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cantidad debe ser un numero entero mayor a 0.");
        }
    }

    private void quitarItem() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un item de la tabla.");
            return;
        }
        ventaActual.quitarDetalle(fila);
        refrescarTabla();
    }

    private void cobrar() {
        if (ventaActual.getDetalles().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito esta vacio.");
            return;
        }
        ventaActual.setMetodoPago((MetodoPago) cbMetodoPago.getSelectedItem());
        ventaActual.setIdUsuario(idUsuarioActual);

        try {
            ventaDAO.insertar(ventaActual);
            JOptionPane.showMessageDialog(this,
                    "Venta #" + ventaActual.getIdVenta() + " registrada correctamente.\n"
                    + "Total cobrado: S/ " + String.format("%.2f", ventaActual.getTotal()),
                    "Venta exitosa", JOptionPane.INFORMATION_MESSAGE);
            nuevaVenta();
        } catch (POSException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void nuevaVenta() {
        iniciarNuevaVenta();
        modeloTabla.setRowCount(0);
        txtCodigo.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtCantidad.setText("1");
        lblTotal.setText("TOTAL: S/ 0.00");
        txtCodigo.requestFocus();
    }

    private void iniciarNuevaVenta() {
        ventaActual = new Venta();
    }

    private void refrescarTabla() {
        modeloTabla.setRowCount(0);
        for (DetalleVenta d : ventaActual.getDetalles()) {
            modeloTabla.addRow(new Object[]{
                d.getIdProducto(),
                d.getNombreProducto(),
                d.getCantidad(),
                String.format("S/ %.2f", d.getPrecioUnitario()),
                String.format("S/ %.2f", d.getSubtotal())
            });
        }
        lblTotal.setText("TOTAL: S/ " + String.format("%.2f", ventaActual.getTotal()));
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuarioActual = idUsuario;
    }
}