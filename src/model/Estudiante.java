
package model;

public class Estudiante extends Persona {

    private final String curso;

    public Estudiante(int id, String nombre, String rut,
                      String curso, String correo) {

        super(id, nombre, rut, correo);
        this.curso = curso;
    }

    public Estudiante(String nombre, String rut,
                      String curso, String correo) {

        super(nombre, rut, correo);
        this.curso = curso;
    }

    public String getCurso() {
        return curso;
    }

    @Override
    public String obtenerDescripcion() {
        return "Estudiante: " + getNombre() + " - Curso: " + curso;
    }
}
