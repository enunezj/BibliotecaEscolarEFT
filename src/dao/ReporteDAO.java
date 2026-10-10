
package dao;

import conexion.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    public List<Object[]> librosMasPrestados() throws SQLException {

        List<Object[]> resultados = new ArrayList<>();

        String sql = """
                SELECT l.id, l.titulo, l.autor,
                       COUNT(p.id) AS total_prestamos
                FROM libros l
                LEFT JOIN prestamos p ON l.id = p.id_libro
                GROUP BY l.id, l.titulo, l.autor
                ORDER BY total_prestamos DESC, l.titulo
                """;

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                resultados.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getInt("total_prestamos")
                });
            }
        }

        return resultados;
    }

    public List<Object[]> prestamosActivos() throws SQLException {

        String sql = """
                SELECT p.id, e.nombre AS estudiante,
                       l.titulo AS libro,
                       p.fecha_prestamo,
                       p.fecha_devolucion,
                       p.fecha_devolucion_real,
                       CASE
                           WHEN p.fecha_devolucion < CURDATE()
                           THEN 'Atrasado'
                           ELSE 'En plazo'
                       END AS estado
                FROM prestamos p
                INNER JOIN estudiantes e
                    ON p.id_estudiante = e.id
                INNER JOIN libros l
                    ON p.id_libro = l.id
                WHERE p.devuelto = FALSE
                ORDER BY p.fecha_devolucion, p.id
                """;

        return consultarPrestamos(sql, null);
    }

    public List<Object[]> historialPorEstudiante(int idEstudiante)
            throws SQLException {

        String sql = """
                SELECT p.id, e.nombre AS estudiante,
                       l.titulo AS libro,
                       p.fecha_prestamo,
                       p.fecha_devolucion,
                       p.fecha_devolucion_real,
                       CASE
                           WHEN p.devuelto = TRUE
                                AND p.fecha_devolucion_real IS NULL
                           THEN 'Devuelto (sin fecha)'

                           WHEN p.devuelto = TRUE
                                AND p.fecha_devolucion_real >
                                    p.fecha_devolucion
                           THEN 'Devuelto con atraso'

                           WHEN p.devuelto = TRUE
                           THEN 'Devuelto'

                           WHEN p.fecha_devolucion < CURDATE()
                           THEN 'Atrasado'

                           ELSE 'En plazo'
                       END AS estado
                FROM prestamos p
                INNER JOIN estudiantes e
                    ON p.id_estudiante = e.id
                INNER JOIN libros l
                    ON p.id_libro = l.id
                WHERE p.id_estudiante = ?
                ORDER BY p.fecha_prestamo DESC, p.id DESC
                """;

        return consultarPrestamos(sql, idEstudiante);
    }

    private List<Object[]> consultarPrestamos(
            String sql, Integer idEstudiante) throws SQLException {

        List<Object[]> resultados = new ArrayList<>();

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            if (idEstudiante != null) {
                ps.setInt(1, idEstudiante);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    resultados.add(new Object[]{
                            rs.getInt("id"),
                            rs.getString("estudiante"),
                            rs.getString("libro"),
                            convertirFecha(rs.getDate("fecha_prestamo")),
                            convertirFecha(rs.getDate("fecha_devolucion")),
                            convertirFecha(rs.getDate("fecha_devolucion_real")),
                            rs.getString("estado")
                    });
                }
            }
        }

        return resultados;
    }

    private String convertirFecha(Date fecha) {
        return fecha == null ? "Sin registro" : fecha.toString();
    }
}
