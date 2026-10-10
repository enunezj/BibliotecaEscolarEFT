
package model;

public class Usuario extends Persona {

    private final String contrasena;
    private final String rol;

    public Usuario(int id, String nombre, String rut, String correo,
                   String contrasena, String rol) {

        super(id, nombre, rut, correo);
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public Usuario(String nombre, String rut, String correo,
                   String contrasena, String rol) {

        super(nombre, rut, correo);
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getRol() {
        return rol;
    }

    @Override
    public String obtenerDescripcion() {
        return "Usuario: " + getNombre() + " - Rol: " + rol;
    }
}
