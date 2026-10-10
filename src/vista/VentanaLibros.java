
package vista;

import controlador.ControladorLibro;
import model.Categoria;
import model.Libro;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class VentanaLibros extends JFrame {

    private final ControladorLibro controlador;
    private final List<Categoria> categorias = new ArrayList<>();

    private final JTextField campoTitulo = new JTextField();
    private final JTextField campoAutor = new JTextField();
    private final JTextField campoIsbn = new JTextField();
    private final JTextField campoEditorial = new JTextField();

    private final JSpinner campoStock = new JSpinner(
            new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1)
    );

    private final JComboBox<String> comboCategoria = new JComboBox<>();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{
                    "ID", "Título", "Autor", "ISBN",
                    "Editorial", "Stock", "Categoría"
            }, 0
    ) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private final JTable tabla = new JTable(modeloTabla);

    public VentanaLibros(Usuario usuario) {

        controlador = new ControladorLibro(usuario);

        boolean esBibliotecario =
                "bibliotecario".equalsIgnoreCase(usuario.getRol());

        setTitle(esBibliotecario
                ? "Gestión de Libros"
                : "Consulta de Libros");

        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titulo = new JLabel(
                "BIBLIOTECA ESCOLAR - LIBROS",
                SwingConstants.CENTER
        );
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.setRowHeight(25);

        if (esBibliotecario) {
            tabla.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    seleccionarLibro();
                }
            });
        }

        JPanel formulario = new JPanel(new GridLayout(3, 4, 10, 8));

        formulario.add(new JLabel("Título:"));
        formulario.add(campoTitulo);

        formulario.add(new JLabel("Autor:"));
        formulario.add(campoAutor);

        formulario.add(new JLabel("ISBN:"));
        formulario.add(campoIsbn);

        formulario.add(new JLabel("Editorial:"));
        formulario.add(campoEditorial);

        formulario.add(new JLabel("Stock:"));
        formulario.add(campoStock);

        formulario.add(new JLabel("Categoría:"));
        formulario.add(comboCategoria);

        JButton botonGuardar = new JButton("Guardar");
        JButton botonModificar = new JButton("Modificar");
        JButton botonEliminar = new JButton("Eliminar");
        JButton botonLimpiar = new JButton("Limpiar");
        JButton botonActualizar = new JButton("Actualizar lista");

        JPanel botones = new JPanel(new FlowLayout());

        if (esBibliotecario) {
            botones.add(botonGuardar);
            botones.add(botonModificar);
            botones.add(botonEliminar);
            botones.add(botonLimpiar);
        }

        botones.add(botonActualizar);

        JPanel inferior = new JPanel(new BorderLayout(5, 10));

        if (esBibliotecario) {
            inferior.add(formulario, BorderLayout.CENTER);
        }

        inferior.add(botones, BorderLayout.SOUTH);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(inferior, BorderLayout.SOUTH);

        add(panel);

        botonGuardar.addActionListener(_ -> guardar());
        botonModificar.addActionListener(_ -> modificar());
        botonEliminar.addActionListener(_ -> eliminar());
        botonLimpiar.addActionListener(_ -> limpiar());
        botonActualizar.addActionListener(_ -> cargarDatos());

        cargarDatos();
    }

    private void cargarDatos() {
        try {
            categorias.clear();
            categorias.addAll(controlador.listarCategorias());

            comboCategoria.removeAllItems();

            for (Categoria categoria : categorias) {
                comboCategoria.addItem(categoria.getNombre());
            }

            modeloTabla.setRowCount(0);

            for (Libro libro : controlador.listar()) {
                modeloTabla.addRow(new Object[]{
                        libro.getId(),
                        libro.getTitulo(),
                        libro.getAutor(),
                        libro.getIsbn(),
                        libro.getEditorial(),
                        libro.getStock(),
                        obtenerNombreCategoria(libro.getIdCategoria())
                });
            }

        } catch (SQLException e) {
            mostrarError(e.getMessage());
        }
    }

    private String obtenerNombreCategoria(int idCategoria) {
        for (Categoria categoria : categorias) {
            if (categoria.getId() == idCategoria) {
                return categoria.getNombre();
            }
        }

        return "Sin categoría";
    }

    private void seleccionarLibro() {
        int id = obtenerIdSeleccionado();

        if (id == -1) {
            return;
        }

        try {
            Libro libro = controlador.buscarPorId(id);

            if (libro == null) {
                mostrarError("El libro ya no existe.");
                return;
            }

            campoTitulo.setText(libro.getTitulo());
            campoAutor.setText(libro.getAutor());
            campoIsbn.setText(libro.getIsbn());
            campoEditorial.setText(libro.getEditorial());
            campoStock.setValue(libro.getStock());

            for (int i = 0; i < categorias.size(); i++) {
                if (categorias.get(i).getId() ==
                        libro.getIdCategoria()) {

                    comboCategoria.setSelectedIndex(i);
                    break;
                }
            }

        } catch (SQLException e) {
            mostrarError(e.getMessage());
        }
    }

    private int obtenerIdCategoria() {
        int indice = comboCategoria.getSelectedIndex();

        if (indice < 0 || indice >= categorias.size()) {
            throw new IllegalArgumentException(
                    "Debes seleccionar una categoría."
            );
        }

        return categorias.get(indice).getId();
    }

    private int obtenerStock() {
        try {
            campoStock.commitEdit();
        } catch (ParseException e) {
            throw new IllegalArgumentException(
                    "Debes ingresar un stock válido."
            );
        }

        return (Integer) campoStock.getValue();
    }

    private void guardar() {
        try {
            controlador.guardar(
                    campoTitulo.getText(),
                    campoAutor.getText(),
                    campoIsbn.getText(),
                    campoEditorial.getText(),
                    obtenerStock(),
                    obtenerIdCategoria()
            );

            JOptionPane.showMessageDialog(
                    this, "Libro registrado correctamente."
            );

            cargarDatos();
            limpiar();

        } catch (SQLException | IllegalArgumentException |
                 SecurityException e) {
            mostrarError(e.getMessage());
        }
    }

    private void modificar() {
        int id = obtenerIdSeleccionado();

        if (id == -1) {
            mostrarError("Selecciona un libro.");
            return;
        }

        try {
            boolean actualizado = controlador.actualizar(
                    id,
                    campoTitulo.getText(),
                    campoAutor.getText(),
                    campoIsbn.getText(),
                    campoEditorial.getText(),
                    obtenerStock(),
                    obtenerIdCategoria()
            );

            if (actualizado) {
                JOptionPane.showMessageDialog(
                        this, "Libro modificado correctamente."
                );

                cargarDatos();
                limpiar();
            } else {
                mostrarError("El libro ya no existe.");
            }

        } catch (SQLException | IllegalArgumentException |
                 SecurityException e) {
            mostrarError(e.getMessage());
        }
    }

    private void eliminar() {
        int id = obtenerIdSeleccionado();

        if (id == -1) {
            mostrarError("Selecciona un libro.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas eliminar este libro?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (controlador.eliminar(id)) {
                JOptionPane.showMessageDialog(
                        this, "Libro eliminado correctamente."
                );

                cargarDatos();
                limpiar();
            } else {
                mostrarError("El libro ya no existe.");
            }

        } catch (SQLException | SecurityException e) {
            mostrarError(
                    "No se pudo eliminar el libro. " +
                            "Puede tener préstamos asociados.\n" +
                            e.getMessage()
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
        campoTitulo.setText("");
        campoAutor.setText("");
        campoIsbn.setText("");
        campoEditorial.setText("");
        campoStock.setValue(0);

        if (comboCategoria.getItemCount() > 0) {
            comboCategoria.setSelectedIndex(0);
        }

        campoTitulo.requestFocusInWindow();
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
