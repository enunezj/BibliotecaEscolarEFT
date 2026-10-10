
package controlador;

import dao.EstudianteCuentaDAO;
import dao.EstudianteDAO;
import model.Estudiante;
import model.Usuario;

import java.sql.SQLException;
import java.util.List;

public class ControladorEstudiante {

    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private final EstudianteCuentaDAO cuentaDAO = new EstudianteCuentaDAO();
    private final Usuario usuario;

    public ControladorEstudiante(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Estudiante> listar() throws SQLException {
        verificarPermisos();
        return estudianteDAO.listar();
    }

    public Estudiante buscarPorId(int id) throws SQLException {
        verificarPermisos();
        return estudianteDAO.buscarPorId(id);
    }

    public void guardar(String nombre, String rut, String curso,
                        String correo, String contrasena)
            throws SQLException {

        verificarPermisos();
        validarDatos(nombre, rut, curso, correo);

        if (contrasena == null || contrasena.length() < 6) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 6 caracteres."
            );
        }

        Estudiante estudiante = new Estudiante(
                nombre.trim(),
                rut.trim(),
                curso.trim(),
                correo.trim()
        );

        cuentaDAO.registrar(estudiante, contrasena);
    }

    public boolean actualizar(int id, String nombre, String rut,
                              String curso, String correo)
            throws SQLException {

        verificarPermisos();
        validarDatos(nombre, rut, curso, correo);

        Estudiante estudiante = new Estudiante(
                id,
                nombre.trim(),
                rut.trim(),
                curso.trim(),
                correo.trim()
        );

        return cuentaDAO.actualizar(estudiante);
    }

    public boolean eliminar(int id) throws SQLException {
        verificarPermisos();
        return cuentaDAO.eliminar(id);
    }

    private void verificarPermisos() {
        if (!"bibliotecario".equalsIgnoreCase(usuario.getRol())) {
            throw new SecurityException(
                    "No tienes permisos para gestionar estudiantes."
            );
        }
    }

    private void validarDatos(String nombre, String rut,
                              String curso, String correo) {

        validarTexto(nombre, "El nombre", 100);
        validarTexto(rut, "El RUT", 12);
        validarTexto(curso, "El curso", 20);
        validarTexto(correo, "El correo", 100);

        if (!correo.contains("@")) {
            throw new IllegalArgumentException(
                    "Debes ingresar un correo válido."
            );
        }
    }

    private void validarTexto(String texto, String campo, int maximo) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    campo + " es obligatorio."
            );
        }

        if (texto.trim().length() > maximo) {
            throw new IllegalArgumentException(
                    campo + " no puede superar " + maximo + " caracteres."
            );
        }
    }
}
