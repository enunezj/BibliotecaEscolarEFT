
package dao;

import conexion.DatabaseConnection;
import model.Estudiante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EstudianteCuentaDAO {

    public void registrar(Estudiante estudiante, String contrasena)
            throws SQLException {

        String sqlEstudiante = """
                INSERT INTO estudiantes (nombre, rut, curso, correo)
                VALUES (?, ?, ?, ?)
                """;

        String sqlUsuario = """
                INSERT INTO usuarios (nombre, rut, correo, `contraseña`, rol)
                VALUES (?, ?, ?, ?, 'estudiante')
                """;

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection()) {

            conexion.setAutoCommit(false);

            try {
                int nuevoId;

                try (PreparedStatement ps = conexion.prepareStatement(
                        sqlEstudiante, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, estudiante.getNombre());
                    ps.setString(2, estudiante.getRut());
                    ps.setString(3, estudiante.getCurso());
                    ps.setString(4, estudiante.getCorreo());
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "No se pudo obtener el ID del estudiante."
                            );
                        }

                        nuevoId = rs.getInt(1);
                    }
                }

                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlUsuario)) {

                    prepararDatosUsuario(ps, estudiante, contrasena);
                    ps.executeUpdate();
                }

                conexion.commit();
                estudiante.setId(nuevoId);

            } catch (SQLException | RuntimeException e) {
                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }

                throw e;
            }
        }
    }

    public boolean actualizar(Estudiante estudiante) throws SQLException {

        String sqlEstudiante = """
                UPDATE estudiantes
                SET nombre = ?, rut = ?, curso = ?, correo = ?
                WHERE id = ?
                """;

        String sqlUsuario = """
                UPDATE usuarios
                SET nombre = ?, rut = ?, correo = ?
                WHERE rut = ? AND rol = 'estudiante'
                """;

        return ejecutarTransaccion(conexion -> {

            String rutAnterior = buscarRut(
                    conexion, estudiante.getId()
            );

            if (rutAnterior == null) {
                return false;
            }

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlEstudiante)) {

                ps.setString(1, estudiante.getNombre());
                ps.setString(2, estudiante.getRut());
                ps.setString(3, estudiante.getCurso());
                ps.setString(4, estudiante.getCorreo());
                ps.setInt(5, estudiante.getId());

                if (ps.executeUpdate() != 1) {
                    throw new SQLException(
                            "No se pudo actualizar el estudiante."
                    );
                }
            }

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlUsuario)) {

                prepararDatosUsuario(ps, estudiante, rutAnterior);
                ps.executeUpdate();
            }

            return true;
        });
    }

    public boolean eliminar(int id) throws SQLException {

        String sqlEstudiante =
                "DELETE FROM estudiantes WHERE id = ?";

        String sqlUsuario = """
                DELETE FROM usuarios
                WHERE rut = ? AND rol = 'estudiante'
                """;

        return ejecutarTransaccion(conexion -> {

            String rut = buscarRut(conexion, id);

            if (rut == null) {
                return false;
            }

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlEstudiante)) {

                ps.setInt(1, id);

                if (ps.executeUpdate() != 1) {
                    throw new SQLException(
                            "No se pudo eliminar el estudiante."
                    );
                }
            }

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlUsuario)) {

                ps.setString(1, rut);
                ps.executeUpdate();
            }

            return true;
        });
    }

    private static void prepararDatosUsuario(
            PreparedStatement ps,
            Estudiante estudiante,
            String ultimoDato) throws SQLException {

        ps.setString(1, estudiante.getNombre());
        ps.setString(2, estudiante.getRut());
        ps.setString(3, estudiante.getCorreo());
        ps.setString(4, ultimoDato);
    }

    private String buscarRut(Connection conexion, int id)
            throws SQLException {

        String sql = """
                SELECT rut FROM estudiantes
                WHERE id = ? FOR UPDATE
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getString("rut");
                }
            }
        }

        return null;
    }

    private boolean ejecutarTransaccion(OperacionBD operacion)
            throws SQLException {

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection()) {

            conexion.setAutoCommit(false);

            try {
                boolean resultado = operacion.ejecutar(conexion);

                if (resultado) {
                    conexion.commit();
                } else {
                    conexion.rollback();
                }

                return resultado;

            } catch (SQLException | RuntimeException e) {

                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }

                throw e;
            }
        }
    }

    @FunctionalInterface
    private interface OperacionBD {

        boolean ejecutar(Connection conexion) throws SQLException;
    }
}
