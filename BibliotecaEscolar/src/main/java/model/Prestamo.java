package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Prestamo {
    // - Atributos
    private int idPrestamo;
    private Estudiante estudiante;
    private Libro libro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private boolean devuelto;

    // - Constructor
    public Prestamo(int idPrestamo, Estudiante estudiante, Libro libro, LocalDate fechaPrestamo, LocalDate fechaDevolucion, boolean devuelto) {
        this.idPrestamo = idPrestamo;
        this.estudiante = estudiante;
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = devuelto;
    }

    // - Metodos Getter and Setter
    public int getIdPrestamo() {
        return idPrestamo;
    }
    public void setIdPrestamo(int idPrestamo) {
        this.idPrestamo = idPrestamo;
    }
    public Estudiante getEstudiante() {
        return estudiante;
    }
    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }
    public Libro getLibro() {
        return libro;
    }
    public void setLibro(Libro libro) {
        this.libro = libro;
    }
    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }
    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }
    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }
    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }
    public boolean isDevuelto() {
        return devuelto;
    }
    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }

    // - Metodos Funcionales
    public boolean estaAtrasado() {
        return !devuelto && fechaDevolucion.isBefore(LocalDate.now());
    }

    public long diasAtraso() {
        return estaAtrasado() ? ChronoUnit.DAYS.between(fechaDevolucion, LocalDate.now()) : 0;
    }

    // - Metodos Auxiliares

    // - Metodos de Representacion

    // Metodo mostrar informacion Prestamo
    public String mostrarPrestamo() {
        return "Datos del Prestamo: [" + getIdPrestamo() + "]\n" +
                "Estudiante: " + getEstudiante().getNombre() + "\n" +
                "Libro: " + getLibro().getTitulo() + "\n" +
                "Fecha de Prestamo: " + getFechaPrestamo() + "\n" +
                "Fecha de Devolucion: " + getFechaDevolucion() + "\n" +
                "Devuelto: " + (isDevuelto() ? "Si" : "No");
    }

    // Metodo to String
    @Override
    public String toString() {
        return "Id Prestamo: " + getIdPrestamo() + " Estudiante: " + getEstudiante().getNombre() +
                " Libro: " + getLibro().getTitulo() + " Fecha de Prestamo: " + getFechaPrestamo() +
                " Devuelto: " + (isDevuelto() ? "Si" : "No");
    }

}