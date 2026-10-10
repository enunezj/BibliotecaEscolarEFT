
package vista;

import controlador.ControladorPrestamo;
import model.Estudiante;
import model.Libro;
import model.Prestamo;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class VentanaPrestamos extends JFrame {

    private final ControladorPrestamo controlador;

    private final JComboBox<String> comboEstudiante = new JComboBox<>();
    private final JComboBox<String> comboLibro = new JComboBox<>();

    private final List<Estudiante> estudiantes = new ArrayList<>();
    private final List<Libro> libros = new ArrayList<>();

    private final JButton botonPrestar = new JButton("Registrar préstamo");
    private final JButton botonDevolver = new JButton("Registrar devolución");
    private final JButton botonActualizar = new JButton("Actualizar lista");

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{
                    "ID", "Estudiante", "Libro", "Préstamo",
                    "Vencimiento", "Estado"
            }, 0
    ) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private final JTable tabla = new JTable(modeloTabla);

    public VentanaPrestamos(Usuario usuario) {

        controlador = new ControladorPrestamo(usuario);

        setTitle("Préstamos y Devoluciones");
        setSize(1000, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel(
                "PRÉSTAMOS Y DEVOLUCIONES",
                SwingConstants.CENTER
        );
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel formulario = new JPanel(new GridLayout(2, 2, 10, 10));

        formulario.add(new JLabel("Estudiante:"));
        formulario.add(comboEstudiante);
        formulario.add(new JLabel("Libro:"));
        formulario.add(comboLibro);

        JPanel botones = new JPanel(new FlowLayout());
        botones.add(botonPrestar);
        botones.add(botonDevolver);
        botones.add(botonActualizar);

        JPanel inferior = new JPanel(new BorderLayout(10, 10));
        inferior.add(formulario, BorderLayout.NORTH);
        inferior.add(botones, BorderLayout.SOUTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.setRowHeight(25);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(inferior, BorderLayout.SOUTH);

        add(panel);

        botonPrestar.addActionListener(_ -> registrarPrestamo());
        botonDevolver.addActionListener(_ -> registrarDevolucion());
        botonActualizar.addActionListener(_ -> cargarDatos());

        cargarDatos();
    }

    private void cargarDatos() {

        habilitarBotones(false);

        new SwingWorker<Datos, Void>() {

            @Override
            protected Datos doInBackground() throws Exception {
                return new Datos(
                        controlador.listarEstudiantes(),
                        controlador.listarLibros(),
                        controlador.listarPrestamos()
                );
            }

            @Override
            protected void done() {
                try {
                    Datos datos = get();

                    estudiantes.clear();
                    estudiantes.addAll(datos.estudiantes());

                    libros.clear();
                    libros.addAll(datos.libros());

                    comboEstudiante.removeAllItems();
                    comboLibro.removeAllItems();

                    for (Estudiante estudiante : estudiantes) {
                        comboEstudiante.addItem(
                                estudiante.getNombre()
                                        + " - " + estudiante.getRut()
                        );
                    }

                    for (Libro libro : libros) {
                        comboLibro.addItem(
                                libro.getTitulo()
                                        + " (Stock: " + libro.getStock() + ")"
                        );
                    }

                    modeloTabla.setRowCount(0);

                    for (Prestamo prestamo : datos.prestamos()) {

                        String estado;

                        if (prestamo.isDevuelto()) {
                            estado = "Devuelto";
                        } else if (prestamo.estaAtrasado()) {
                            estado = "Atrasado";
                        } else {
                            estado = "En plazo";
                        }

                        modeloTabla.addRow(new Object[]{
                                prestamo.getId(),
                                nombreEstudiante(prestamo.getIdEstudiante()),
                                tituloLibro(prestamo.getIdLibro()),
                                prestamo.getFechaPrestamo(),
                                prestamo.getFechaDevolucion(),
                                estado
                        });
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    mostrarError("Se interrumpió la consulta.");

                } catch (ExecutionException e) {
                    mostrarError(e.getCause().getMessage());

                } finally {
                    habilitarBotones(true);
                }
            }

        }.execute();
    }

    private String nombreEstudiante(int id) {

        for (Estudiante estudiante : estudiantes) {
            if (estudiante.getId() == id) {
                return estudiante.getNombre();
            }
        }

        return "Estudiante ID: " + id;
    }

    private String tituloLibro(int id) {

        for (Libro libro : libros) {
            if (libro.getId() == id) {
                return libro.getTitulo();
            }
        }

        return "Libro ID: " + id;
    }

    private void registrarPrestamo() {

        int indiceEstudiante = comboEstudiante.getSelectedIndex();
        int indiceLibro = comboLibro.getSelectedIndex();

        if (indiceEstudiante < 0 || indiceLibro < 0) {
            mostrarError("Debes seleccionar un estudiante y un libro.");
            return;
        }

        int idEstudiante = estudiantes.get(indiceEstudiante).getId();
        int idLibro = libros.get(indiceLibro).getId();

        ejecutarOperacion(
                () -> {
                    controlador.registrarPrestamo(idEstudiante, idLibro);
                    return true;
                },
                "Préstamo registrado correctamente."
        );
    }

    private void registrarDevolucion() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            mostrarError("Selecciona el préstamo que deseas devolver.");
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(fila);
        int idPrestamo = (int) modeloTabla.getValueAt(filaModelo, 0);

        String estado = (String) modeloTabla.getValueAt(filaModelo, 5);

        if ("Devuelto".equals(estado)) {
            mostrarError("Este préstamo ya fue devuelto.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Confirmas la devolución del libro?",
                "Confirmar devolución",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        ejecutarOperacion(
                () -> controlador.registrarDevolucion(idPrestamo),
                "Devolución registrada correctamente."
        );
    }

    private void ejecutarOperacion(Callable<Boolean> operacion,
                                   String mensaje) {

        habilitarBotones(false);

        new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() throws Exception {
                return operacion.call();
            }

            @Override
            protected void done() {
                try {
                    if (Boolean.TRUE.equals(get())) {
                        JOptionPane.showMessageDialog(
                                VentanaPrestamos.this, mensaje
                        );
                    } else {
                        mostrarError("No se pudo completar la operación.");
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    mostrarError("La operación fue interrumpida.");

                } catch (ExecutionException e) {
                    mostrarError(e.getCause().getMessage());

                } finally {
                    cargarDatos();
                }
            }

        }.execute();
    }

    private void habilitarBotones(boolean habilitar) {
        botonPrestar.setEnabled(habilitar);
        botonDevolver.setEnabled(habilitar);
        botonActualizar.setEnabled(habilitar);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Aviso",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private record Datos(
            List<Estudiante> estudiantes,
            List<Libro> libros,
            List<Prestamo> prestamos
    ) {
    }
}
