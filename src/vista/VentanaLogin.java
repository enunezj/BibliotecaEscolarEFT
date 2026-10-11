
package vista;

import controlador.ControladorLogin;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VentanaLogin extends JFrame {

    private static final Logger LOGGER =
            Logger.getLogger(VentanaLogin.class.getName());

    private final JTextField campoRut = new JTextField();
    private final JPasswordField campoContrasena = new JPasswordField();
    private final ControladorLogin controlador = new ControladorLogin();

    public VentanaLogin() {

        setTitle("Biblioteca Escolar - Inicio de sesión");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 15));

        panel.setBorder(
                BorderFactory.createEmptyBorder(25, 35, 25, 35)
        );

        JLabel titulo = new JLabel("BIBLIOTECA ESCOLAR");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel formulario = new JPanel(new GridLayout(4, 1, 5, 5));

        formulario.add(new JLabel("RUT:"));
        formulario.add(campoRut);

        formulario.add(new JLabel("Contraseña:"));
        formulario.add(campoContrasena);

        JButton botonIngresar = new JButton("Iniciar sesión");

        botonIngresar.addActionListener(evento -> iniciarSesion());

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(formulario, BorderLayout.CENTER);
        panel.add(botonIngresar, BorderLayout.SOUTH);

        add(panel);

        getRootPane().setDefaultButton(botonIngresar);
    }

    private void iniciarSesion() {

        char[] clave = campoContrasena.getPassword();

        try {

            if (campoRut.getText().isBlank()) {

                mostrarAviso("Debes ingresar tu RUT.");
                campoRut.requestFocusInWindow();
                return;
            }

            if (clave.length == 0) {

                mostrarAviso("Debes ingresar tu contraseña.");
                campoContrasena.requestFocusInWindow();
                return;
            }

            Usuario usuario = controlador.iniciarSesion(
                    campoRut.getText(),
                    new String(clave)
            );

            if (usuario == null) {

                mostrarAviso("RUT o contraseña incorrectos.");

                campoContrasena.setText("");
                campoContrasena.requestFocusInWindow();

                return;
            }

            VentanaPrincipal principal = new VentanaPrincipal(usuario);

            principal.setVisible(true);

            dispose();

        } catch (SQLException e) {

            mostrarErrorBaseDatos(e);

            campoContrasena.setText("");

        } catch (IllegalArgumentException e) {

            mostrarAviso(e.getMessage());

            campoContrasena.setText("");

        } finally {

            Arrays.fill(clave, '\0');

        }
    }

    private void mostrarAviso(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Aviso",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void mostrarErrorBaseDatos(SQLException excepcion) {

        LOGGER.log(
                Level.SEVERE,
                "Error de base de datos durante el inicio de sesión.",
                excepcion
        );

        JOptionPane.showMessageDialog(
                this,
                "No se pudo completar el inicio de sesión " +
                        "debido a un error de base de datos.\n" +
                        "Comprueba que MySQL esté funcionando y " +
                        "que la configuración de db.properties sea correcta.",
                "Error de base de datos",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
