
package vista;

import controlador.ControladorReporte;
import model.Estudiante;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VentanaReportes extends JFrame {

    private static final Logger LOGGER =
            Logger.getLogger(VentanaReportes.class.getName());

    private final ControladorReporte controlador;
    private final boolean esBibliotecario;

    private long versionHistorial = 0;

    private final DefaultTableModel modeloPopulares = crearModelo(
            "ID", "Título", "Autor", "Total préstamos"
    );

    private final DefaultTableModel modeloActivos = crearModelo(
            "ID", "Estudiante", "Libro",
            "Fecha préstamo", "Vencimiento",
            "Devolución real", "Estado"
    );

    private final DefaultTableModel modeloHistorial = crearModelo(
            "ID", "Estudiante", "Libro",
            "Fecha préstamo", "Vencimiento",
            "Devolución real", "Estado"
    );

    private final JComboBox<Estudiante> comboEstudiante =
            new JComboBox<>();

    public VentanaReportes(Usuario usuario) {

        controlador = new ControladorReporte(usuario);

        esBibliotecario =
                "bibliotecario".equalsIgnoreCase(usuario.getRol());

        setTitle("Reportes - Biblioteca Escolar");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titulo = new JLabel(
                "REPORTES DE BIBLIOTECA",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JTabbedPane pestanas = new JTabbedPane();

        if (esBibliotecario) {

            pestanas.addTab(
                    "Libros más prestados",
                    new JScrollPane(crearTabla(modeloPopulares))
            );

            pestanas.addTab(
                    "Préstamos activos",
                    new JScrollPane(crearTabla(modeloActivos))
            );
        }

        JPanel panelHistorial =
                new JPanel(new BorderLayout(10, 10));

        JPanel selector =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        selector.add(new JLabel("Estudiante:"));

        comboEstudiante.setPreferredSize(
                new Dimension(300, 28)
        );

        comboEstudiante.setRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean seleccionado,
                            boolean tieneFoco
                    ) {

                        JLabel etiqueta = (JLabel)
                                super.getListCellRendererComponent(
                                        list,
                                        value,
                                        index,
                                        seleccionado,
                                        tieneFoco
                                );

                        if (value instanceof Estudiante estudiante) {

                            etiqueta.setText(
                                    estudiante.getNombre()
                                            + " - "
                                            + estudiante.getRut()
                            );
                        }

                        return etiqueta;
                    }
                }
        );

        selector.add(comboEstudiante);

        if (esBibliotecario) {
            panelHistorial.add(
                    selector,
                    BorderLayout.NORTH
            );
        }

        panelHistorial.add(
                new JScrollPane(crearTabla(modeloHistorial)),
                BorderLayout.CENTER
        );

        pestanas.addTab(
                "Historial por estudiante",
                panelHistorial
        );

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(pestanas, BorderLayout.CENTER);

        JButton botonActualizar =
                new JButton("Actualizar reportes");

        panel.add(
                botonActualizar,
                BorderLayout.SOUTH
        );

        add(panel);

        comboEstudiante.addActionListener(
                evento -> cargarHistorial()
        );

        botonActualizar.addActionListener(
                evento -> actualizarReportes()
        );

        actualizarReportes();
        cargarEstudiantes();
    }

    private static DefaultTableModel crearModelo(
            String... columnas
    ) {

        return new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(
                    int fila,
                    int columna
            ) {
                return false;
            }
        };
    }

    private JTable crearTabla(DefaultTableModel modelo) {

        JTable tabla = new JTable(modelo);

        tabla.setRowHeight(25);
        tabla.setAutoCreateRowSorter(true);

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        return tabla;
    }

    private void actualizarReportes() {

        if (esBibliotecario) {

            cargarTabla(
                    controlador::librosMasPrestados,
                    modeloPopulares,
                    "cargar el reporte de libros más prestados",
                    -1
            );

            cargarTabla(
                    controlador::prestamosActivos,
                    modeloActivos,
                    "cargar el reporte de préstamos activos",
                    -1
            );
        }

        cargarHistorial();
    }

    private void cargarEstudiantes() {

        new SwingWorker<List<Estudiante>, Void>() {

            @Override
            protected List<Estudiante> doInBackground()
                    throws Exception {

                return controlador.listarEstudiantes();
            }

            @Override
            protected void done() {

                try {

                    List<Estudiante> estudiantes = get();

                    comboEstudiante.removeAllItems();

                    for (Estudiante estudiante : estudiantes) {

                        comboEstudiante.addItem(estudiante);
                    }

                    if (estudiantes.isEmpty()) {

                        mostrarAviso(
                                "No hay estudiantes disponibles "
                                        + "para consultar el historial."
                        );
                    }

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    LOGGER.log(
                            Level.WARNING,
                            "Se interrumpió la consulta de estudiantes.",
                            e
                    );

                    mostrarAviso(
                            "La consulta de estudiantes "
                                    + "fue interrumpida."
                    );

                } catch (ExecutionException e) {

                    manejarErrorTarea(
                            e,
                            "cargar la lista de estudiantes"
                    );
                }
            }

        }.execute();
    }

    private void cargarHistorial() {

        long versionActual = ++versionHistorial;

        Estudiante estudiante =
                (Estudiante) comboEstudiante.getSelectedItem();

        if (estudiante == null) {

            modeloHistorial.setRowCount(0);
            return;
        }

        int idEstudiante = estudiante.getId();

        cargarTabla(
                () -> controlador.historialPorEstudiante(
                        idEstudiante
                ),
                modeloHistorial,
                "cargar el historial del estudiante",
                versionActual
        );
    }

    private void cargarTabla(
            Callable<List<Object[]>> consulta,
            DefaultTableModel modelo,
            String operacion,
            long versionSolicitada
    ) {

        new SwingWorker<List<Object[]>, Void>() {

            @Override
            protected List<Object[]> doInBackground()
                    throws Exception {

                return consulta.call();
            }

            @Override
            protected void done() {

                // Evita mostrar un historial anterior si el
                // usuario cambió de estudiante rápidamente.
                if (versionSolicitada >= 0
                        && versionSolicitada != versionHistorial) {

                    return;
                }

                try {

                    List<Object[]> filas = get();

                    // La tabla se modifica solamente cuando
                    // la consulta terminó correctamente.
                    modelo.setRowCount(0);

                    for (Object[] fila : filas) {
                        modelo.addRow(fila);
                    }

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    LOGGER.log(
                            Level.WARNING,
                            "Se interrumpió la operación: "
                                    + operacion,
                            e
                    );

                    mostrarAviso(
                            "La consulta fue interrumpida."
                    );

                } catch (ExecutionException e) {

                    manejarErrorTarea(e, operacion);
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

            mostrarErrorBaseDatos(
                    operacion,
                    errorSQL
            );

        } else if (causa instanceof IllegalArgumentException
                || causa instanceof SecurityException) {

            LOGGER.log(
                    Level.WARNING,
                    "No se pudo completar la operación: "
                            + operacion,
                    causa
            );

            String mensaje = causa.getMessage();

            if (mensaje == null || mensaje.isBlank()) {

                mostrarAviso(
                        "No se pudo completar la consulta solicitada."
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
                    "No se pudo completar la consulta "
                            + "debido a un error inesperado.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
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
                        + "Comprueba que MySQL esté funcionando "
                        + "y que la base de datos esté configurada "
                        + "correctamente.",
                "Error de base de datos",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
