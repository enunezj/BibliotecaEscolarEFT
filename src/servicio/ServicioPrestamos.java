
package servicio;

import dao.PrestamoDAO;
import model.Prestamo;

import java.sql.SQLException;

public final class ServicioPrestamos {

    private static final ServicioPrestamos INSTANCE =
            new ServicioPrestamos();

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    private ServicioPrestamos() {
    }

    public static ServicioPrestamos getInstance() {
        return INSTANCE;
    }

    public synchronized void registrarPrestamo(Prestamo prestamo)
            throws SQLException {

        if (prestamo == null) {
            throw new IllegalArgumentException(
                    "El préstamo no puede estar vacío."
            );
        }

        prestamoDAO.registrar(prestamo);
    }

    public synchronized boolean registrarDevolucion(int idPrestamo)
            throws SQLException {

        if (idPrestamo <= 0) {
            throw new IllegalArgumentException(
                    "El ID del préstamo no es válido."
            );
        }

        return prestamoDAO.devolver(idPrestamo);
    }
}
