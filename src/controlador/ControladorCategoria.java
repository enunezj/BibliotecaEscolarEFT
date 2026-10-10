
package controlador;

import dao.CategoriaDAO;
import model.Categoria;
import model.Usuario;

import java.sql.SQLException;
import java.util.List;

public class ControladorCategoria {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final Usuario usuario;

    public ControladorCategoria(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Categoria> listar() throws SQLException {
        return categoriaDAO.listar();
    }

    public Categoria buscarPorId(int id) throws SQLException {
        return categoriaDAO.buscarPorId(id);
    }

    public void guardar(String nombre) throws SQLException {
        verificarPermisos();
        validarNombre(nombre);

        Categoria categoria = new Categoria(nombre.trim());
        categoriaDAO.guardar(categoria);
    }

    public boolean actualizar(int id, String nombre) throws SQLException {
        verificarPermisos();
        validarNombre(nombre);

        Categoria categoria = new Categoria(id, nombre.trim());
        return categoriaDAO.actualizar(categoria);
    }

    public boolean eliminar(int id) throws SQLException {
        verificarPermisos();
        return categoriaDAO.eliminar(id);
    }

    private void verificarPermisos() {
        if (!"bibliotecario".equalsIgnoreCase(usuario.getRol())) {
            throw new SecurityException(
                    "No tienes permisos para realizar esta operación."
            );
        }
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre de la categoría es obligatorio."
            );
        }

        if (nombre.trim().length() > 100) {
            throw new IllegalArgumentException(
                    "El nombre no puede superar los 100 caracteres."
            );
        }
    }
}
