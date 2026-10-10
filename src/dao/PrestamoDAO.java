
package dao;

import conexion.DatabaseConnection;
import model.Prestamo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    public void registrar(Prestamo prestamo) throws SQLException {

        if (prestamo.getFechaPrestamo() == null ||
                prestamo.getFechaDevolucion() == null ||
                prestamo.getFechaDevolucion().isBefore(
                        prestamo.getFechaPrestamo())) {
            throw new SQLException("Las fechas del préstamo no son válidas.");
        }

        String actualizarStock = """
                UPDATE libros
                SET stock = stock - 1
                WHERE id = ? AND stock > 0
                """;

        String insertarPrestamo = """
                INSERT INTO prestamos
                (id_estudiante, id_libro, fecha_prestamo,
                 fecha_devolucion, devuelto)
                VALUES (?, ?, ?, ?, FALSE)
                """;

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection()) {

            conexion.setAutoCommit(false);

            try {
                try (PreparedStatement ps =
                             conexion.prepareStatement(actualizarStock)) {

                    ps.setInt(1, prestamo.getIdLibro());

                    if (ps.executeUpdate() == 0) {
                        throw new SQLException(
                                "El libro no existe o no tiene stock disponible."
                        );
                    }
                }

                int nuevoId;

                try (PreparedStatement ps = conexion.prepareStatement(
                        insertarPrestamo, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, prestamo.getIdEstudiante());
                    ps.setInt(2, prestamo.getIdLibro());
                    ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
                    ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "No se pudo obtener el ID del préstamo."
                            );
                        }

                        nuevoId = rs.getInt(1);
                    }
                }

                conexion.commit();
                prestamo.setId(nuevoId);
                prestamo.setDevuelto(false);

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        }
    }

    public List<Prestamo> listar() throws SQLException {

        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos ORDER BY id";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                prestamos.add(crearPrestamo(rs));
            }
        }

        return prestamos;
    }

    public Prestamo buscarPorId(int id) throws SQLException {

        String sql = "SELECT * FROM prestamos WHERE id = ?";

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearPrestamo(rs);
                }
            }
        }

        return null;
    }

    public boolean devolver(int idPrestamo) throws SQLException {

        String buscar = """
                SELECT id_libro, devuelto
                FROM prestamos
                WHERE id = ?
                FOR UPDATE
                """;

        String actualizarPrestamo = """
                UPDATE prestamos
                SET devuelto = TRUE,
                    fecha_devolucion_real = CURRENT_DATE()
                WHERE id = ? AND devuelto = FALSE
                """;

        String devolverStock = """
                UPDATE libros
                SET stock = stock + 1
                WHERE id = ?
                """;

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection()) {

            conexion.setAutoCommit(false);

            try {
                int idLibro;

                try (PreparedStatement ps =
                             conexion.prepareStatement(buscar)) {

                    ps.setInt(1, idPrestamo);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next() || rs.getBoolean("devuelto")) {
                            conexion.rollback();
                            return false;
                        }

                        idLibro = rs.getInt("id_libro");
                    }
                }

                try (PreparedStatement ps =
                             conexion.prepareStatement(actualizarPrestamo)) {

                    ps.setInt(1, idPrestamo);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudo actualizar el préstamo."
                        );
                    }
                }

                try (PreparedStatement ps =
                             conexion.prepareStatement(devolverStock)) {

                    ps.setInt(1, idLibro);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudo devolver el stock del libro."
                        );
                    }
                }

                conexion.commit();
                return true;

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        }
    }

    private Prestamo crearPrestamo(ResultSet rs) throws SQLException {

        Date fechaPrestamo = rs.getDate("fecha_prestamo");
        Date fechaDevolucion = rs.getDate("fecha_devolucion");

        return new Prestamo(
                rs.getInt("id"),
                rs.getInt("id_estudiante"),
                rs.getInt("id_libro"),
                fechaPrestamo == null
                        ? null : fechaPrestamo.toLocalDate(),
                fechaDevolucion == null
                        ? null : fechaDevolucion.toLocalDate(),
                rs.getBoolean("devuelto")
        );
    }
}
