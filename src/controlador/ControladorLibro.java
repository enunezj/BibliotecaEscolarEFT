
package controlador;

import dao.CategoriaDAO;
import dao.LibroDAO;
import model.Categoria;
import model.Libro;
import model.Usuario;

import java.sql.SQLException;
import java.util.List;

public class ControladorLibro {

    private final LibroDAO libroDAO = new LibroDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final Usuario usuario;

    public ControladorLibro(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Libro> listar() throws SQLException {
        return libroDAO.listar();
    }

    public List<Categoria> listarCategorias() throws SQLException {
        return categoriaDAO.listar();
    }

    public Libro buscarPorId(int id) throws SQLException {
        return libroDAO.buscarPorId(id);
    }

    public void guardar(String titulo, String autor, String isbn,
                        String editorial, int stock, int idCategoria)
            throws SQLException {

        verificarPermisos();
        validarDatos(titulo, autor, isbn, editorial, stock, idCategoria);

        Libro libro = new Libro(
                titulo.trim(),
                autor.trim(),
                isbn.trim(),
                editorial.trim(),
                stock,
                idCategoria
        );

        libroDAO.guardar(libro);
    }

    public boolean actualizar(int id, String titulo, String autor,
                              String isbn, String editorial,
                              int stock, int idCategoria)
            throws SQLException {

        verificarPermisos();
        validarDatos(titulo, autor, isbn, editorial, stock, idCategoria);

        Libro libro = new Libro(
                id,
                titulo.trim(),
                autor.trim(),
                isbn.trim(),
                editorial.trim(),
                stock,
                idCategoria
        );

        return libroDAO.actualizar(libro);
    }

    public boolean eliminar(int id) throws SQLException {
        verificarPermisos();
        return libroDAO.eliminar(id);
    }

    private void verificarPermisos() {
        if (!"bibliotecario".equalsIgnoreCase(usuario.getRol())) {
            throw new SecurityException(
                    "No tienes permisos para modificar libros."
            );
        }
    }

    private void validarDatos(String titulo, String autor, String isbn,
                              String editorial, int stock, int idCategoria) {

        validarTexto(titulo, "El título", 200);
        validarTexto(autor, "El autor", 100);
        validarTexto(isbn, "El ISBN", 20);
        validarTexto(editorial, "La editorial", 100);

        if (stock < 0) {
            throw new IllegalArgumentException(
                    "El stock no puede ser negativo."
            );
        }

        if (idCategoria <= 0) {
            throw new IllegalArgumentException(
                    "Debes seleccionar una categoría."
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
