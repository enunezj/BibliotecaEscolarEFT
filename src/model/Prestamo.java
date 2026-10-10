
package model;

import java.time.LocalDate;

public class Prestamo {

    private int id;
    private final int idEstudiante;
    private final int idLibro;
    private final LocalDate fechaPrestamo;
    private final LocalDate fechaDevolucion;
    private boolean devuelto;

    public Prestamo(int id, int idEstudiante, int idLibro,
                    LocalDate fechaPrestamo, LocalDate fechaDevolucion,
                    boolean devuelto) {

        this.id = id;
        this.idEstudiante = idEstudiante;
        this.idLibro = idLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = devuelto;
    }

    public Prestamo(int idEstudiante, int idLibro,
                    LocalDate fechaPrestamo, LocalDate fechaDevolucion) {

        this.idEstudiante = idEstudiante;
        this.idLibro = idLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }

    public boolean estaAtrasado() {
        return !devuelto
                && fechaDevolucion != null
                && LocalDate.now().isAfter(fechaDevolucion);
    }
}
