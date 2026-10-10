
package dao;

import conexion.DatabaseConnection;
import model.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    public void guardar(Libro libro) throws SQLException {
        String sql = """
                INSERT INTO libros
                (titulo, autor, isbn, editorial, stock, id_categoria)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexion = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            colocarDatosLibro(ps, libro);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    libro.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Libro> listar() throws SQLException {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros ORDER BY id";

        try (Connection conexion = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                libros.add(crearLibro(rs));
            }
        }

        return libros;
    }

    public Libro buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM libros WHERE id = ?";

        try (Connection conexion = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearLibro(rs);
                }
            }
        }

        return null;
    }

    public boolean actualizar(Libro libro) throws SQLException {
        String sql = """
                UPDATE libros
                SET titulo = ?, autor = ?, isbn = ?,
                    editorial = ?, stock = ?, id_categoria = ?
                WHERE id = ?
                """;

        try (Connection conexion = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            colocarDatosLibro(ps, libro);
            ps.setInt(7, libro.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM libros WHERE id = ?";

        try (Connection conexion = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }

    private static void colocarDatosLibro(PreparedStatement ps, Libro libro)
            throws SQLException {

        ps.setString(1, libro.getTitulo());
        ps.setString(2, libro.getAutor());
        ps.setString(3, libro.getIsbn());
        ps.setString(4, libro.getEditorial());
        ps.setInt(5, libro.getStock());
        ps.setInt(6, libro.getIdCategoria());
    }

    private Libro crearLibro(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("isbn"),
                rs.getString("editorial"),
                rs.getInt("stock"),
                rs.getInt("id_categoria")
        );
    }
}
