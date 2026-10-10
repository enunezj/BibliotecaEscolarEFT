
package controlador;

import dao.UsuarioDAO;
import model.Usuario;

import java.sql.SQLException;

public class ControladorLogin {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario iniciarSesion(String rut, String contrasena)
            throws SQLException {

        if (rut == null || rut.isBlank() ||
                contrasena == null || contrasena.isBlank()) {
            return null;
        }

        return usuarioDAO.autenticar(rut.trim(), contrasena);
    }
}
