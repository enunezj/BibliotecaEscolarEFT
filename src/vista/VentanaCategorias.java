
package vista;

import controlador.ControladorCategoria;
import model.Categoria;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VentanaCategorias extends JFrame {

    private static final Logger LOGGER =
            Logger.getLogger(VentanaCategorias.class.getName());

    private final ControladorCategoria controlador;
    private final JTextField campoNombre = new JTextField(20);

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Nombre"}, 0
    ) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private final JTable tabla = new JTable(modeloTabla);

    public VentanaCategorias(Usuario usuario) {

        controlador = new ControladorCategoria(usuario);

        setTitle("Gestión de Categorías");
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titulo = new JLabel(
                "GESTIÓN DE CATEGORÍAS",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);

        tabla.getSelectionModel().addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting()) {
                seleccionarCategoria();
            }
        });

        JPanel formulario = new JPanel(new BorderLayout(10, 10));
        formulario.add(new JLabel("Nombre:"), BorderLayout.WEST);
        formulario.add(campoNombre, BorderLayout.CENTER);

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
        inferior.add(formulario, BorderLayout.NORTH);
        inferior.add(botones, BorderLayout.CENTER);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(inferior, BorderLayout.SOUTH);

        add(panel);

        botonGuardar.addActionListener(evento -> guardar());
        botonModificar.addActionListener(evento -> modificar());
        botonEliminar.addActionListener(evento -> eliminar());
        botonLimpiar.addActionListener(evento -> limpiar());
        botonActualizar.addActionListener(evento -> cargarCategorias());

        cargarCategorias();
    }

    private void cargarCategorias() {

        try {
            List<Categoria> categorias = controlador.listar();

            modeloTabla.setRowCount(0);

            for (Categoria categoria : categorias) {
                modeloTabla.addRow(new Object[]{
                        categoria.getId(),
                        categoria.getNombre()
                });
            }

        } catch (SQLException e) {
            mostrarErrorBaseDatos("cargar las categorías", e);
        }
    }

    private void seleccionarCategoria() {

        int fila = tabla.getSelectedRow();

        if (fila >= 0) {

            int filaModelo = tabla.convertRowIndexToModel(fila);
            int id = (int) modeloTabla.getValueAt(filaModelo, 0);

            try {
                Categoria categoria = controlador.buscarPorId(id);

                if (categoria != null) {
                    campoNombre.setText(categoria.getNombre());
                } else {
                    mostrarAviso(
                            "La categoría seleccionada ya no existe."
                    );
                    cargarCategorias();
                }

            } catch (SQLException e) {
                mostrarErrorBaseDatos("consultar la categoría", e);
            }
        }
    }

    private void guardar() {

        try {
            controlador.guardar(campoNombre.getText());

            mostrarExito("Categoría registrada correctamente.");

            cargarCategorias();
            limpiar();

        } catch (SQLException e) {
            mostrarErrorBaseDatos("registrar la categoría", e);

        } catch (IllegalArgumentException | SecurityException e) {
            mostrarAviso(e.getMessage());
        }
    }

    private void modificar() {

        int id = obtenerIdSeleccionado();

        if (id == -1) {
            mostrarAviso("Debes seleccionar una categoría.");
            return;
        }

        try {
            if (controlador.actualizar(id, campoNombre.getText())) {

                mostrarExito("Categoría modificada correctamente.");

                cargarCategorias();
                limpiar();

            } else {
                mostrarAviso("La categoría seleccionada ya no existe.");
                cargarCategorias();
            }

        } catch (SQLException e) {
            mostrarErrorBaseDatos("modificar la categoría", e);

        } catch (IllegalArgumentException | SecurityException e) {
            mostrarAviso(e.getMessage());
        }
    }

    private void eliminar() {

        int id = obtenerIdSeleccionado();

        if (id == -1) {
            mostrarAviso("Debes seleccionar una categoría.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas eliminar esta categoría?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (controlador.eliminar(id)) {

                mostrarExito("Categoría eliminada correctamente.");

                cargarCategorias();
                limpiar();

            } else {
                mostrarAviso("La categoría seleccionada ya no existe.");
                cargarCategorias();
            }

        } catch (SQLException e) {

            String estadoSQL = e.getSQLState();

            if (estadoSQL != null && estadoSQL.startsWith("23")) {

                LOGGER.log(
                        Level.WARNING,
                        "No se pudo eliminar la categoría por una restricción de integridad.",
                        e
                );

                mostrarAviso(
                        "No se puede eliminar la categoría porque " +
                                "tiene registros asociados, como libros."
                );

            } else {
                mostrarErrorBaseDatos("eliminar la categoría", e);
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
