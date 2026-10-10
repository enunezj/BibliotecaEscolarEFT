
package vista;

import controlador.ControladorLogin;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;

public class VentanaLogin extends JFrame {

    private final JTextField campoRut = new JTextField();
    private final JPasswordField campoContrasena = new JPasswordField();
    private final ControladorLogin controlador = new ControladorLogin();

    public VentanaLogin() {
        setTitle("Biblioteca Escolar - Inicio de Sesion");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel titulo = new JLabel("BIBLIOTECA ESCOLAR");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel formulario = new JPanel(new GridLayout(4, 1, 5, 5));

        formulario.add(new JLabel("RUT:"));
        formulario.add(campoRut);

        formulario.add(new JLabel("Contraseña:"));
        formulario.add(campoContrasena);

        JButton botonIngresar = new JButton("Iniciar sesion");
        botonIngresar.addActionListener(_ -> iniciarSesion());

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(formulario, BorderLayout.CENTER);
        panel.add(botonIngresar, BorderLayout.SOUTH);

        add(panel);
        getRootPane().setDefaultButton(botonIngresar);
    }

    private void iniciarSesion() {

        char[] clave = campoContrasena.getPassword();

        try {
            Usuario usuario = controlador.iniciarSesion(
                    campoRut.getText(),
                    new String(clave)
            );

            if (usuario == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "RUT o contraseña incorrectos.",
                        "Error de acceso",
                        JOptionPane.WARNING_MESSAGE
                );
                campoContrasena.setText("");
                return;
            }

            VentanaPrincipal principal = new VentanaPrincipal(usuario);
            principal.setVisible(true);
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo conectar con la base de datos:\n"
                            + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            Arrays.fill(clave, '\0');
        }
    }
}
