
package vista;

import controlador.ControladorPrestamo;
import model.Estudiante;
import model.Libro;
import model.Prestamo;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VentanaPrestamos extends JFrame {

    private static final Logger LOGGER =
            Logger.getLogger(VentanaPrestamos.class.getName());

    private final ControladorPrestamo controlador;

    private final JComboBox<String> comboEstudiante = new JComboBox<>();
    private final JComboBox<String> comboLibro = new JComboBox<>();

    private final List<Estudiante> estudiantes = new ArrayList<>();
    private final List<Libro> libros = new ArrayList<>();

    private final JButton botonPrestar =
            new JButton("Registrar préstamo");

    private final JButton botonDevolver =
            new JButton("Registrar devolución");

    private final JButton botonActualizar =
            new JButton("Actualizar lista");

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

        panel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

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

        botonPrestar.addActionListener(
                evento -> registrarPrestamo()
        );

        botonDevolver.addActionListener(
                evento -> registrarDevolucion()
        );

        botonActualizar.addActionListener(
                evento -> cargarDatos()
        );

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
                                        + " - "
                                        + estudiante.getRut()
                        );
                    }


                    for (Libro libro : libros) {

                        comboLibro.addItem(
                                libro.getTitulo()
                                        + " (Stock: "
                                        + libro.getStock()
                                        + ")"
                        );
                    }

// Evitar selecciones automáticas al cargar las listas.
                    comboEstudiante.setSelectedIndex(-1);
                    comboLibro.setSelectedIndex(-1);

                    modeloTabla.setRowCount(0);


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
                                nombreEstudiante(
                                        prestamo.getIdEstudiante()
                                ),
                                tituloLibro(
                                        prestamo.getIdLibro()
                                ),
                                prestamo.getFechaPrestamo(),
                                prestamo.getFechaDevolucion(),
                                estado
                        });
                    }

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    LOGGER.log(
                            Level.WARNING,
                            "Se interrumpió la carga de préstamos.",
                            e
                    );

                    mostrarAviso(
                            "Se interrumpió la consulta de préstamos."
                    );

                } catch (ExecutionException e) {

                    manejarErrorTarea(
                            e,
                            "cargar los datos de préstamos"
                    );

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

            mostrarAviso(
                    "Debes seleccionar un estudiante y un libro."
            );

            return;
        }

        int idEstudiante = estudiantes.get(indiceEstudiante).getId();
        int idLibro = libros.get(indiceLibro).getId();

        ejecutarOperacion(
                () -> {
                    controlador.registrarPrestamo(
                            idEstudiante,
                            idLibro
                    );
                    return true;
                },
                "Préstamo registrado correctamente."
        );
    }

    private void registrarDevolucion() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            mostrarAviso(
                    "Debes seleccionar el préstamo que deseas devolver."
            );

            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(fila);

        int idPrestamo = (int) modeloTabla.getValueAt(
                filaModelo, 0
        );

        String estado = (String) modeloTabla.getValueAt(
                filaModelo, 5
        );

        if ("Devuelto".equals(estado)) {

            mostrarAviso("Este préstamo ya fue devuelto.");

            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Confirmas la devolución del libro?",
                "Confirmar devolución",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        ejecutarOperacion(
                () -> controlador.registrarDevolucion(idPrestamo),
                "Devolución registrada correctamente."
        );
    }

    private void ejecutarOperacion(
            Callable<Boolean> operacion,
            String mensaje
    ) {

        habilitarBotones(false);

        new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() throws Exception {

                return operacion.call();
            }

            @Override
            protected void done() {

                try {
                    boolean completada = Boolean.TRUE.equals(get());

                    if (completada) {

                        mostrarExito(mensaje);

                    } else {

                        mostrarAviso(
                                "No se pudo completar la operación. "
                                        + "El préstamo podría no existir "
                                        + "o haber sido devuelto."
                        );
                    }

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    LOGGER.log(
                            Level.WARNING,
                            "Se interrumpió una operación de préstamos.",
                            e
                    );

                    mostrarAviso(
                            "La operación fue interrumpida."
                    );

                } catch (ExecutionException e) {

                    manejarErrorTarea(
                            e,
                            "registrar el préstamo o la devolución"
                    );

                } finally {

                    cargarDatos();
                }
            }

        }.execute();
    }

    private void manejarErrorTarea(
            ExecutionException excepcion,
            String operacion
    ) {

        Throwable causa = excepcion.getCause();

        if (causa instanceof SQLException errorSQL) {

            manejarErrorSQL(operacion, errorSQL);

        } else if (causa instanceof IllegalArgumentException
                || causa instanceof SecurityException) {

            String mensaje = causa.getMessage();

            if (mensaje == null || mensaje.isBlank()) {

                mostrarAviso(
                        "No se pudo completar la operación solicitada."
                );

            } else {

                mostrarAviso(mensaje);
            }

        } else {

            LOGGER.log(
                    Level.SEVERE,
                    "Error inesperado al intentar " + operacion,
                    causa != null ? causa : excepcion
            );

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo completar la operación "
                            + "debido a un error inesperado.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void manejarErrorSQL(
            String operacion,
            SQLException excepcion
    ) {

        String mensaje = excepcion.getMessage();

        switch (mensaje) {
            case "El libro no existe o no tiene stock disponible." -> {

                LOGGER.log(
                        Level.WARNING,
                        "Intento de préstamo sin stock disponible.",
                        excepcion
                );

                mostrarAviso(
                        "El libro seleccionado no existe o no tiene "
                                + "stock disponible.\n"
                                + "Actualiza la lista y comprueba "
                                + "la disponibilidad."
                );
            }
            case "No se encontró la ficha del estudiante." -> {

                LOGGER.log(
                        Level.WARNING,
                        "No se encontró la ficha del estudiante.",
                        excepcion
                );

                mostrarAviso(
                        "No se encontró la ficha del estudiante "
                                + "asociada a la cuenta."
                );
            }
            case "Las fechas del préstamo no son válidas." -> {

                LOGGER.log(
                        Level.WARNING,
                        "El préstamo contiene fechas inválidas.",
                        excepcion
                );

                mostrarAviso(
                        "Las fechas del préstamo no son válidas."
                );
            }
            case null, default -> mostrarErrorBaseDatos(operacion, excepcion);
        }
    }

    private void habilitarBotones(boolean habilitar) {

        botonPrestar.setEnabled(habilitar);
        botonDevolver.setEnabled(habilitar);
        botonActualizar.setEnabled(habilitar);
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
                "No se pudo " + operacion + ".\n"
                        + "Comprueba la conexión con MySQL "
                        + "y la configuración de la base de datos.",
                "Error de base de datos",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private record Datos(
            List<Estudiante> estudiantes,
            List<Libro> libros,
            List<Prestamo> prestamos
    ) {
    }
}
