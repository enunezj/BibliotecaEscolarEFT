
package vista;

import controlador.ControladorEstudiante;
import model.Estudiante;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VentanaEstudiantes extends JFrame {

    private static final Logger LOGGER =
            Logger.getLogger(VentanaEstudiantes.class.getName());

    private final ControladorEstudiante controlador;

    private final JTextField campoNombre = new JTextField();
    private final JTextField campoRut = new JTextField();
    private final JTextField campoCurso = new JTextField();
    private final JTextField campoCorreo = new JTextField();
    private final JPasswordField campoContrasena = new JPasswordField();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "RUT", "Curso", "Correo"}, 0
    ) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private final JTable tabla = new JTable(modeloTabla);

    public VentanaEstudiantes(Usuario usuario) {

        controlador = new ControladorEstudiante(usuario);

        setTitle("Gestión de Estudiantes");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titulo = new JLabel(
                "GESTIÓN DE ESTUDIANTES",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.setRowHeight(25);

        tabla.getSelectionModel().addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting()) {
                seleccionarEstudiante();
            }
        });

        JPanel formulario = new JPanel(new GridLayout(5, 2, 10, 8));

        formulario.add(new JLabel("Nombre:"));
        formulario.add(campoNombre);

        formulario.add(new JLabel("RUT:"));
        formulario.add(campoRut);

        formulario.add(new JLabel("Curso:"));
        formulario.add(campoCurso);

        formulario.add(new JLabel("Correo:"));
        formulario.add(campoCorreo);

        formulario.add(new JLabel("Contraseña (solo para registrar):"));
        formulario.add(campoContrasena);

        JButton botonGuardar = new JButton("Guardar");
        JButton botonModificar = new JButton("Modificar");
        JButton botonEliminar = new JButton("Eliminar");
        JButton botonLimpiar = new JButton("Limpiar");
        JButton botonActualizar = new JButton("Actualizar lista");

        JPanel botones = new JPanel(new FlowLayout());

        botones.add(botonGuardar);
        botones.add(botonModificar);
        botones.add(botonEliminar);
        botones.add(botonLimpiar);
        botones.add(botonActualizar);

        JPanel inferior = new JPanel(new BorderLayout(5, 10));

        inferior.add(formulario, BorderLayout.CENTER);
        inferior.add(botones, BorderLayout.SOUTH);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(inferior, BorderLayout.SOUTH);

        add(panel);

        botonGuardar.addActionListener(evento -> guardar());
        botonModificar.addActionListener(evento -> modificar());
        botonEliminar.addActionListener(evento -> eliminar());
        botonLimpiar.addActionListener(evento -> limpiar());
        botonActualizar.addActionListener(evento -> cargarEstudiantes());

        cargarEstudiantes();
    }

    private void cargarEstudiantes() {

        try {
            List<Estudiante> estudiantes = controlador.listar();

            modeloTabla.setRowCount(0);

            for (Estudiante estudiante : estudiantes) {
                modeloTabla.addRow(new Object[]{
                        estudiante.getId(),
                        estudiante.getNombre(),
                        estudiante.getRut(),
                        estudiante.getCurso(),
                        estudiante.getCorreo()
                });
            }

        } catch (SQLException e) {
            mostrarErrorBaseDatos("cargar los estudiantes", e);

        } catch (SecurityException e) {
            mostrarAviso(e.getMessage());
        }
    }

    private void seleccionarEstudiante() {

        int id = obtenerIdSeleccionado();

        if (id == -1) {
            return;
        }

        try {
            Estudiante estudiante = controlador.buscarPorId(id);

            if (estudiante != null) {

                campoNombre.setText(estudiante.getNombre());
                campoRut.setText(estudiante.getRut());
                campoCurso.setText(estudiante.getCurso());
                campoCorreo.setText(estudiante.getCorreo());
                campoContrasena.setText("");

            } else {

                mostrarAviso(
                        "El estudiante seleccionado ya no existe."
                );

                cargarEstudiantes();
            }

        } catch (SQLException e) {
            mostrarErrorBaseDatos("consultar el estudiante", e);

        } catch (SecurityException e) {
            mostrarAviso(e.getMessage());
        }
    }

    private void guardar() {

        char[] clave = campoContrasena.getPassword();

        try {
            controlador.guardar(
                    campoNombre.getText(),
                    campoRut.getText(),
                    campoCurso.getText(),
                    campoCorreo.getText(),
                    new String(clave)
            );

            mostrarExito(
                    "Estudiante y usuario registrados correctamente."
            );

            cargarEstudiantes();
            limpiar();

        } catch (SQLException e) {

            if (e.getErrorCode() == 1062) {

                LOGGER.log(
                        Level.WARNING,
                        "No se pudo registrar el estudiante por un dato duplicado.",
                        e
                );

                mostrarAviso(
                        "Ya existe un estudiante o una cuenta " +
                                "de usuario con ese RUT."
                );

            } else {
                mostrarErrorBaseDatos("registrar el estudiante", e);
            }

        } catch (IllegalArgumentException | SecurityException e) {
            mostrarAviso(e.getMessage());

        } finally {
            Arrays.fill(clave, '\0');
        }
    }

    private void modificar() {

        int id = obtenerIdSeleccionado();

        if (id == -1) {
            mostrarAviso("Debes seleccionar un estudiante.");
            return;
        }

        try {
            boolean actualizado = controlador.actualizar(
                    id,
                    campoNombre.getText(),
                    campoRut.getText(),
                    campoCurso.getText(),
                    campoCorreo.getText()
            );

            if (actualizado) {

                mostrarExito(
                        "Estudiante modificado correctamente."
                );

                cargarEstudiantes();
                limpiar();

            } else {

                mostrarAviso(
                        "El estudiante seleccionado ya no existe."
                );

                cargarEstudiantes();
            }

        } catch (SQLException e) {

            if (e.getErrorCode() == 1062) {

                LOGGER.log(
                        Level.WARNING,
                        "No se pudo modificar el estudiante por un dato duplicado.",
                        e
                );

                mostrarAviso(
                        "Ya existe otro estudiante o una cuenta " +
                                "de usuario con ese RUT."
                );

            } else {
                mostrarErrorBaseDatos("modificar el estudiante", e);
            }

        } catch (IllegalArgumentException | SecurityException e) {
            mostrarAviso(e.getMessage());
        }
    }

    private void eliminar() {

        int id = obtenerIdSeleccionado();

        if (id == -1) {
            mostrarAviso("Debes seleccionar un estudiante.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas eliminar este estudiante y su cuenta de usuario?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (controlador.eliminar(id)) {

                mostrarExito(
                        "Estudiante eliminado correctamente."
                );

                cargarEstudiantes();
                limpiar();

            } else {

                mostrarAviso(
                        "El estudiante seleccionado ya no existe."
                );

                cargarEstudiantes();
            }

        } catch (SQLException e) {

            if (e.getErrorCode() == 1451) {

                LOGGER.log(
                        Level.WARNING,
                        "No se pudo eliminar el estudiante por una relación existente.",
                        e
                );

                mostrarAviso(
                        "No se puede eliminar el estudiante " +
                                "porque tiene préstamos asociados."
                );

            } else {
                mostrarErrorBaseDatos("eliminar el estudiante", e);
            }

        } catch (SecurityException e) {
            mostrarAviso(e.getMessage());
        }
    }

    private int obtenerIdSeleccionado() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            return -1;
        }

        int filaModelo = tabla.convertRowIndexToModel(fila);

        return (int) modeloTabla.getValueAt(filaModelo, 0);
    }

    private void limpiar() {

        tabla.clearSelection();

        campoNombre.setText("");
        campoRut.setText("");
        campoCurso.setText("");
        campoCorreo.setText("");
        campoContrasena.setText("");

        campoNombre.requestFocusInWindow();
    }

    private void mostrarExito(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Operación exitosa",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void mostrarAviso(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Aviso",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void mostrarErrorBaseDatos(
            String operacion,
            SQLException excepcion
    ) {

        LOGGER.log(
                Level.SEVERE,
                "Error de base de datos al intentar " + operacion,
                excepcion
        );

        JOptionPane.showMessageDialog(
                this,
                "No se pudo " + operacion + ".\n" +
                        "Comprueba la conexión con MySQL y " +
                        "las restricciones de la base de datos.",
                "Error de base de datos",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
