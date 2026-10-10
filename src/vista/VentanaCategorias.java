
package vista;

import controlador.ControladorCategoria;
import model.Categoria;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class VentanaCategorias extends JFrame {

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
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel(
                "GESTIÓN DE CATEGORÍAS",
                SwingConstants.CENTER
        );
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
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

        botonGuardar.addActionListener(_ -> guardar());
        botonModificar.addActionListener(_ -> modificar());
        botonEliminar.addActionListener(_ -> eliminar());
        botonLimpiar.addActionListener(_ -> limpiar());
        botonActualizar.addActionListener(_ -> cargarCategorias());

        cargarCategorias();
    }

    private void cargarCategorias() {
        try {
            modeloTabla.setRowCount(0);

            for (Categoria categoria : controlador.listar()) {
                modeloTabla.addRow(new Object[]{
                        categoria.getId(),
                        categoria.getNombre()
                });
            }

        } catch (SQLException e) {
            mostrarError(e.getMessage());
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
                }

            } catch (SQLException e) {
                mostrarError(e.getMessage());
            }
        }
    }

    private void guardar() {
        try {
            controlador.guardar(campoNombre.getText());

            JOptionPane.showMessageDialog(
                    this, "Categoría registrada correctamente."
            );

            cargarCategorias();
            limpiar();

        } catch (SQLException | IllegalArgumentException |
                 SecurityException e) {
            mostrarError(e.getMessage());
        }
    }

    private void modificar() {
        int id = obtenerIdSeleccionado();

        if (id == -1) {
            mostrarError("Selecciona una categoría.");
            return;
        }

        try {
            if (controlador.actualizar(id, campoNombre.getText())) {
                JOptionPane.showMessageDialog(
                        this, "Categoría modificada correctamente."
                );

                cargarCategorias();
                limpiar();
            } else {
                mostrarError("La categoría ya no existe.");
            }

        } catch (SQLException | IllegalArgumentException |
                 SecurityException e) {
            mostrarError(e.getMessage());
        }
    }

    private void eliminar() {
        int id = obtenerIdSeleccionado();

        if (id == -1) {
            mostrarError("Selecciona una categoría.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas eliminar esta categoría?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (controlador.eliminar(id)) {
                JOptionPane.showMessageDialog(
                        this, "Categoría eliminada correctamente."
                );

                cargarCategorias();
                limpiar();
            } else {
                mostrarError("La categoría ya no existe.");
            }

        } catch (SQLException | SecurityException e) {
            mostrarError(
                    "No se pudo eliminar la categoría. " +
                            "Puede tener libros asociados.\n" + e.getMessage()
            );
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

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Aviso",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
