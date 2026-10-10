
package controlador;

import dao.EstudianteDAO;
import dao.LibroDAO;
import dao.PrestamoDAO;
import model.Estudiante;
import model.Libro;
import model.Prestamo;
import model.Usuario;
import servicio.ServicioPrestamos;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ControladorPrestamo {

    private final Usuario usuario;
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private final LibroDAO libroDAO = new LibroDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    private final ServicioPrestamos servicio =
            ServicioPrestamos.getInstance();

    public ControladorPrestamo(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Libro> listarLibros() throws SQLException {
        return libroDAO.listar();
    }

    public List<Estudiante> listarEstudiantes() throws SQLException {

        if (esBibliotecario()) {
            return estudianteDAO.listar();
        }

        return List.of(obtenerEstudianteActual());
    }

    public List<Prestamo> listarPrestamos() throws SQLException {

        List<Prestamo> prestamos = prestamoDAO.listar();

        if (esBibliotecario()) {
            return prestamos;
        }

        int idEstudiante = obtenerEstudianteActual().getId();
        List<Prestamo> resultado = new ArrayList<>();

        for (Prestamo prestamo : prestamos) {
            if (prestamo.getIdEstudiante() == idEstudiante) {
                resultado.add(prestamo);
            }
        }

        return resultado;
    }

    public void registrarPrestamo(int idEstudiante, int idLibro)
            throws SQLException {

        if (idEstudiante <= 0 || idLibro <= 0) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un estudiante y un libro."
            );
        }

        if (!esBibliotecario() &&
                obtenerEstudianteActual().getId() != idEstudiante) {

            throw new SecurityException(
                    "Solo puedes registrar tus propios préstamos."
            );
        }

        LocalDate fecha = LocalDate.now();

        Prestamo prestamo = new Prestamo(
                idEstudiante,
                idLibro,
                fecha,
                fecha.plusDays(7)
        );

        servicio.registrarPrestamo(prestamo);
    }

    public boolean registrarDevolucion(int idPrestamo)
            throws SQLException {

        Prestamo prestamo = prestamoDAO.buscarPorId(idPrestamo);

        if (prestamo == null) {
            return false;
        }

        if (!esBibliotecario() &&
                prestamo.getIdEstudiante() !=
                        obtenerEstudianteActual().getId()) {

            throw new SecurityException(
                    "No puedes devolver préstamos de otros estudiantes."
            );
        }

        return servicio.registrarDevolucion(idPrestamo);
    }

    private Estudiante obtenerEstudianteActual() throws SQLException {

        Estudiante estudiante =
                estudianteDAO.buscarPorRut(usuario.getRut());

        if (estudiante == null) {
            throw new SQLException(
                    "No se encontró la ficha del estudiante."
            );
        }

        return estudiante;
    }

    private boolean esBibliotecario() {
        return "bibliotecario".equalsIgnoreCase(usuario.getRol());
    }
}
