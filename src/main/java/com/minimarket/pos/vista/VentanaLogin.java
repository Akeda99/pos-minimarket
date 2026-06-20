package com.minimarket.pos.vista;

import com.minimarket.pos.dao.UsuarioDAO;
import com.minimarket.pos.dao.UsuarioDAOImpl;
import com.minimarket.pos.modelo.Usuario;
import com.minimarket.pos.util.POSException;
import com.minimarket.pos.util.Validador;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Ventana de inicio de sesion. Es la primera pantalla del sistema.
 * Responsable: Integrante 3.
 *
 * NOTA de integracion para el Integrante 1: cuando se integre este modulo a
 * main, Main.java deberia arrancar con "new VentanaLogin().setVisible(true)"
 * en lugar de abrir VentanaPrincipal directamente (no lo cambio yo porque
 * Main.java es un archivo que administras tu).
 */
public class VentanaLogin extends JFrame {

    private final JTextField txtUsuario = new JTextField(15);
    private final JPasswordField txtPassword = new JPasswordField(15);
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    public VentanaLogin() {
        setTitle("Sistema POS Minimarket - Iniciar sesion");
        setSize(360, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setContentPane(crearPanel());
    }

    private JPanel crearPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        panel.add(txtUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Contrasena:"), gbc);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.addActionListener(e -> intentarLogin());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnIngresar, gbc);

        // Permite enviar el formulario con Enter desde el campo de contrasena.
        txtPassword.addActionListener(e -> intentarLogin());

        return panel;
    }

    private void intentarLogin() {
        String username = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (!Validador.noVacio(username) || !Validador.noVacio(password)) {
            JOptionPane.showMessageDialog(this, "Ingrese usuario y contrasena.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Usuario usuario = usuarioDAO.autenticar(username, password);
            if (usuario == null) {
                JOptionPane.showMessageDialog(this, "Usuario o contrasena incorrectos.",
                        "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
                return;
            }
            abrirSistema(usuario);
        } catch (POSException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error de conexion", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirSistema(Usuario usuario) {
        VentanaPrincipal principal = new VentanaPrincipal(usuario);
        principal.setVisible(true);
        this.dispose();
    }

    /** Permite probar el login de forma aislada, sin pasar por Main.
     * @param args */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }
}