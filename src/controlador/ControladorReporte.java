
package controlador;

import dao.EstudianteDAO;
import dao.ReporteDAO;
import model.Estudiante;
import model.Usuario;

import java.sql.SQLException;
import java.util.List;

public class ControladorReporte {

    private final ReporteDAO reporteDAO = new ReporteDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private final Usuario usuario;

    public ControladorReporte(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Object[]> librosMasPrestados() throws SQLException {
        verificarBibliotecario();
        return reporteDAO.librosMasPrestados();
    }

    public List<Object[]> prestamosActivos() throws SQLException {
        verificarBibliotecario();
        return reporteDAO.prestamosActivos();
    }

    public List<Estudiante> listarEstudiantes() throws SQLException {

        if (esBibliotecario()) {
            return estudianteDAO.listar();
        }

        return List.of(obtenerEstudianteActual());
    }

    public List<Object[]> historialPorEstudiante(int idEstudiante)
            throws SQLException {

        if (idEstudiante <= 0) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un estudiante."
            );
        }

        if (!esBibliotecario()) {

            Estudiante estudiante = obtenerEstudianteActual();

            if (estudiante.getId() != idEstudiante) {
                throw new SecurityException(
                        "Solo puedes consultar tu propio historial."
                );
            }
        }

        return reporteDAO.historialPorEstudiante(idEstudiante);
    }

    private Estudiante obtenerEstudianteActual()
            throws SQLException {

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

    private void verificarBibliotecario() {
        if (!esBibliotecario()) {
            throw new SecurityException(
                    "No tienes permisos para consultar este reporte."
            );
        }
    }
}
