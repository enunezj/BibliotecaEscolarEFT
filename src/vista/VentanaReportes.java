
package vista;

import controlador.ControladorReporte;
import model.Estudiante;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class VentanaReportes extends JFrame {

    private final ControladorReporte controlador;
    private final boolean esBibliotecario;

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


    private final JComboBox<Estudiante> comboEstudiante = new JComboBox<>();

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

        JPanel panelHistorial = new JPanel(new BorderLayout(10, 10));

        JPanel selector = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selector.add(new JLabel("Estudiante:"));

        comboEstudiante.setPreferredSize(new Dimension(300, 28));
        comboEstudiante.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean seleccionado, boolean tieneFoco) {

                JLabel etiqueta = (JLabel) super.getListCellRendererComponent(
                        list, value, index, seleccionado, tieneFoco
                );

                if (value instanceof Estudiante estudiante) {
                    etiqueta.setText(
                            estudiante.getNombre() + " - " + estudiante.getRut()
                    );
                }

                return etiqueta;
            }
        });

        selector.add(comboEstudiante);

        if (esBibliotecario) {
            panelHistorial.add(selector, BorderLayout.NORTH);
        }

        panelHistorial.add(
                new JScrollPane(crearTabla(modeloHistorial)),
                BorderLayout.CENTER
        );

        pestanas.addTab("Historial por estudiante", panelHistorial);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(pestanas, BorderLayout.CENTER);
        JButton botonActualizar = new JButton("Actualizar reportes");
        panel.add(botonActualizar, BorderLayout.SOUTH);

        add(panel);

        comboEstudiante.addActionListener(_ -> cargarHistorial());
        botonActualizar.addActionListener(_ -> actualizarReportes());

        actualizarReportes();
        cargarEstudiantes();
    }

    private static DefaultTableModel crearModelo(String... columnas) {

        return new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private JTable crearTabla(DefaultTableModel modelo) {

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(25);
        tabla.setAutoCreateRowSorter(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return tabla;
    }

    private void actualizarReportes() {

        if (esBibliotecario) {

            cargarTabla(
                    controlador::librosMasPrestados,
                    modeloPopulares
            );

            cargarTabla(
                    controlador::prestamosActivos,
                    modeloActivos
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

                    for (Estudiante estudiante : estudiantes) {
                        comboEstudiante.addItem(estudiante);
                    }

                    if (estudiantes.isEmpty()) {
                        mostrarError("No hay estudiantes registrados.");
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    mostrarError("La consulta fue interrumpida.");

                } catch (ExecutionException e) {
                    mostrarError(obtenerMensajeError(e));
                }
            }
        }.execute();
    }

    private void cargarHistorial() {

        Estudiante estudiante =
                (Estudiante) comboEstudiante.getSelectedItem();

        if (estudiante == null) {
            return;
        }

        int idEstudiante = estudiante.getId();

        cargarTabla(
                () -> controlador.historialPorEstudiante(idEstudiante),
                modeloHistorial
        );
    }

    private void cargarTabla(Callable<List<Object[]>> consulta,
                             DefaultTableModel modelo) {

        new SwingWorker<List<Object[]>, Void>() {

            @Override
            protected List<Object[]> doInBackground()
                    throws Exception {

                return consulta.call();
            }

            @Override
            protected void done() {

                try {
                    List<Object[]> filas = get();

                    modelo.setRowCount(0);

                    for (Object[] fila : filas) {
                        modelo.addRow(fila);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    mostrarError("La consulta fue interrumpida.");

                } catch (ExecutionException e) {
                    mostrarError(obtenerMensajeError(e));
                }
            }
        }.execute();
    }

    private String obtenerMensajeError(ExecutionException e) {

        Throwable causa = e.getCause();

        return causa == null
                ? e.getMessage()
                : causa.getMessage();
    }

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
