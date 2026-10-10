
package model;

public abstract class Persona implements Describible {

    private int id;
    private String nombre;
    private final String rut;
    private final String correo;

    public Persona(int id, String nombre, String rut, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.rut = rut;
        this.correo = correo;
    }

    public Persona(String nombre, String rut, String correo) {
        this.nombre = nombre;
        this.rut = rut;
        this.correo = correo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRut() {
        return rut;
    }

    public String getCorreo() {
        return correo;
    }

    @Override
    public abstract String obtenerDescripcion();
}
