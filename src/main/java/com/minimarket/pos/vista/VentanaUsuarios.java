package com.minimarket.pos.vista;

import com.minimarket.pos.dao.UsuarioDAO;
import com.minimarket.pos.dao.UsuarioDAOImpl;
import com.minimarket.pos.modelo.Administrador;
import com.minimarket.pos.modelo.Cajero;
import com.minimarket.pos.modelo.Usuario;
import com.minimarket.pos.util.POSException;
import com.minimarket.pos.util.Validador;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

/**
 * CRUD de usuarios: alta, baja, modificacion y listado.
 * Se abre como ventana interna dentro de VentanaPrincipal (MDI), invocada
 * desde el menu "Usuarios" que administra el Integrante 1.
 * Responsable: Integrante 3.
 */
public class VentanaUsuarios extends JInternalFrame {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    private final DefaultTableModel modeloTabla =
            new DefaultTableModel(new Object[]{"ID", "Usuario", "Nombre", "Rol"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // la tabla es solo para ver/seleccionar; se edita en el formulario
                }
            };
    private final JTable tabla = new JTable(modeloTabla);

    private final JTextField txtUsername = new JTextField(15);
    private final JPasswordField txtPassword = new JPasswordField(15);
    private final JTextField txtNombre = new JTextField(15);
    private final JComboBox<String> cmbRol = new JComboBox<>(new String[]{"ADMINISTRADOR", "CAJERO"});

    private Usuario usuarioSeleccionado; // null = se va a crear un usuario nuevo

    public VentanaUsuarios() {
        super("Gestion de usuarios", true, true, true, true);
        setSize(560, 420);
        setContentPane(crearPanel());
        cargarUsuarios();
    }

    private JPanel crearPanel() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(8, 8));

        tabla.getSelectionModel().addListSelectionListener(e -> cargarSeleccionEnFormulario());
        panelPrincipal.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panelPrincipal.add(crearFormulario(), BorderLayout.SOUTH);

        return panelPrincipal;
    }

    private JPanel crearFormulario() {
        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        agregarCampo(formulario, gbc, 0, "Usuario:", txtUsername);
        agregarCampo(formulario, gbc, 1, "Contrasena:", txtPassword);
        agregarCampo(formulario, gbc, 2, "Nombre:", txtNombre);
        agregarCampo(formulario, gbc, 3, "Rol:", cmbRol);

        JButton btnNuevo = new JButton("Nuevo");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefrescar = new JButton("Refrescar");

        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnRefrescar.addActionListener(e -> cargarUsuarios());

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnNuevo);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnRefrescar);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formulario.add(panelBotones, gbc);

        return formulario;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JComponent campo) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 1;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        panel.add(campo, gbc);
    }

    private void cargarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.listar();
            modeloTabla.setRowCount(0);
            for (Usuario u : usuarios) {
                modeloTabla.addRow(new Object[]{u.getIdUsuario(), u.getUsername(), u.getNombre(), u.getRol()});
            }
        } catch (POSException e) {
            mostrarError(e);
        }
    }

    private void cargarSeleccionEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);
        try {
            usuarioSeleccionado = usuarioDAO.buscarPorId(id);
            if (usuarioSeleccionado != null) {
                txtUsername.setText(usuarioSeleccionado.getUsername());
                // La contrasena esta hasheada: no se muestra. Dejarla vacia mantiene la actual.
                txtPassword.setText("");
                txtNombre.setText(usuarioSeleccionado.getNombre());
                cmbRol.setSelectedItem(usuarioSeleccionado.getRol());
            }
        } catch (POSException e) {
            mostrarError(e);
        }
    }

    private void guardar() {
        String username = txtUsername.getText().trim();
        String passwordPlano = new String(txtPassword.getPassword());
        String nombre = txtNombre.getText().trim();
        String rol = (String) cmbRol.getSelectedItem();

        boolean esNuevo = (usuarioSeleccionado == null);

        // username y nombre siempre obligatorios; la contrasena solo es obligatoria al crear
        if (!Validador.noVacio(username) || !Validador.noVacio(nombre)
                || (esNuevo && !Validador.noVacio(passwordPlano))) {
            JOptionPane.showMessageDialog(this,
                    esNuevo ? "Complete usuario, contrasena y nombre."
                            : "Complete usuario y nombre.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // La contrasena se guarda HASHEADA. En edicion, si se deja vacia,
            // se conserva la contrasena actual del usuario.
            String passwordHash;
            if (!esNuevo && passwordPlano.isEmpty()) {
                passwordHash = usuarioSeleccionado.getPassword();
            } else {
                passwordHash = UsuarioDAOImpl.hashear(passwordPlano);
            }

            Usuario usuario = "ADMINISTRADOR".equals(rol)
                    ? new Administrador(0, username, passwordHash, nombre)
                    : new Cajero(0, username, passwordHash, nombre);

            if (esNuevo) {
                if (usuarioDAO.existeUsername(username)) {
                    JOptionPane.showMessageDialog(this, "Ese nombre de usuario ya existe.",
                            "Usuario duplicado", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                usuarioDAO.insertar(usuario);
            } else {
                usuario.setIdUsuario(usuarioSeleccionado.getIdUsuario());
                usuarioDAO.actualizar(usuario);
            }
            limpiarFormulario();
            cargarUsuarios();
        } catch (POSException e) {
            mostrarError(e);
        }
    }

    private void eliminar() {
        if (usuarioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.",
                    "Nada seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "Eliminar a " + usuarioSeleccionado.getNombre() + "?",
                "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            usuarioDAO.eliminar(usuarioSeleccionado.getIdUsuario());
            limpiarFormulario();
            cargarUsuarios();
        } catch (POSException e) {
            mostrarError(e);
        }
    }

    private void limpiarFormulario() {
        usuarioSeleccionado = null;
        txtUsername.setText("");
        txtPassword.setText("");
        txtNombre.setText("");
        cmbRol.setSelectedIndex(0);
        tabla.clearSelection();
    }

    private void mostrarError(POSException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
