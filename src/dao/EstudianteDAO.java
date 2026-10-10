
package dao;

import conexion.DatabaseConnection;
import model.Estudiante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAO {

    public List<Estudiante> listar() throws SQLException {
        List<Estudiante> estudiantes = new ArrayList<>();
        String sql = "SELECT * FROM estudiantes ORDER BY id";

        try (Connection conexion = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                estudiantes.add(crearEstudiante(rs));
            }
        }

        return estudiantes;
    }

    public Estudiante buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM estudiantes WHERE id = ?";

        try (Connection conexion = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearEstudiante(rs);
                }
            }
        }

        return null;
    }

    public Estudiante buscarPorRut(String rut) throws SQLException {
        String sql = "SELECT * FROM estudiantes WHERE rut = ?";

        try (Connection conexion = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, rut);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearEstudiante(rs);
                }
            }
        }

        return null;
    }

    public boolean actualizar(Estudiante estudiante) throws SQLException {
        String sql = """
                UPDATE estudiantes
                SET nombre = ?, rut = ?, curso = ?, correo = ?
                WHERE id = ?
                """;

        try (Connection conexion = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getRut());
            ps.setString(3, estudiante.getCurso());
            ps.setString(4, estudiante.getCorreo());
            ps.setInt(5, estudiante.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM estudiantes WHERE id = ?";

        try (Connection conexion = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }

    private Estudiante crearEstudiante(ResultSet rs) throws SQLException {
        return new Estudiante(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("rut"),
                rs.getString("curso"),
                rs.getString("correo")
        );
    }
}
