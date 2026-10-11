package controller;

import model.DatabaseConnection;
import model.Estudiante;
import model.Libro;
import model.Prestamo;
import model.PrestamoDAO;
import model.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;

public class GestorPrestamos {

    // — Referencias
    private final PrestamoDAO prestamoDAO;
    private final GestorUsuarios gestorUsuarios;
    private final GestorLibros gestorLibros;

    // — Constructor
    public GestorPrestamos(GestorUsuarios gestorUsuarios, GestorLibros gestorLibros) {
        this.prestamoDAO = new PrestamoDAO(DatabaseConnection.getInstance());
        this.gestorUsuarios = gestorUsuarios;
        this.gestorLibros = gestorLibros;
    }

    // — Metodos de Entrada
    public synchronized void crearPrestamo(Prestamo prestamo) {
        if (prestamo == null) {
            throw new IllegalArgumentException("El prestamo no puede ser nulo");
        }
        Estudiante estudiante = validarEstudianteExiste(prestamo.getEstudiante());
        Libro libro = validarLibroExiste(prestamo.getLibro());
        ValidadorDatos.fechas(prestamo.getFechaPrestamo(), prestamo.getFechaDevolucion());
        validarStockDisponible(libro);
        validarPrestamoActivoRepetido(estudiante, libro);
        prestamo.setEstudiante(estudiante);
        prestamo.setLibro(libro);
        prestamoDAO.insertar(prestamo);
    }

    public void editarPrestamo(int idPrestamo, LocalDate fechaDevolucion) {
        Prestamo prestamo = buscarPorId(idPrestamo);
        if (prestamo == null) {
            throw new IllegalArgumentException("El prestamo no existe");
        }
        if (prestamo.isDevuelto()) {
            throw new IllegalArgumentException("No se puede editar un prestamo ya devuelto");
        }
        ValidadorDatos.fechas(prestamo.getFechaPrestamo(), fechaDevolucion);
        prestamoDAO.actualizarFechaDevolucion(idPrestamo, fechaDevolucion);
    }

    public synchronized void registrarDevolucion(int idPrestamo) {
        Prestamo prestamo = buscarPorId(idPrestamo);
        if (prestamo == null) {
            throw new IllegalArgumentException("El prestamo no existe");
        }
        if (prestamo.isDevuelto()) {
            throw new IllegalArgumentException("El prestamo ya fue devuelto");
        }
        prestamoDAO.marcarDevuelto(idPrestamo);
    }

    public synchronized void eliminarPrestamo(int idPrestamo) {
        Prestamo prestamo = buscarPorId(idPrestamo);
        if (prestamo == null) {
            throw new IllegalArgumentException("El prestamo no existe");
        }
        prestamoDAO.eliminar(idPrestamo);
    }

    // — Metodos de control
    private Estudiante validarEstudianteExiste(Estudiante estudiante) {
        if (estudiante == null) {
            throw new IllegalArgumentException("Debe seleccionar un estudiante");
        }
        Usuario existente = gestorUsuarios.buscarPorId(estudiante.getIdUsuario());
        if (!(existente instanceof Estudiante)) {
            throw new IllegalArgumentException("El estudiante no existe");
        }
        return (Estudiante) existente;
    }

    private Libro validarLibroExiste(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("Debe seleccionar un libro");
        }
        Libro existente = gestorLibros.buscarPorIsbn(libro.getIsbn());
        if (existente == null) {
            throw new IllegalArgumentException("El libro no existe");
        }
        return existente;
    }

    private void validarStockDisponible(Libro libro) {
        if (libro.getStock() <= 0) {
            throw new IllegalArgumentException("No hay stock disponible de ese libro");
        }
    }

    private void validarPrestamoActivoRepetido(Estudiante estudiante, Libro libro) {
        if (prestamoDAO.existeActivo(estudiante.getIdUsuario(), libro.getIsbn())) {
            throw new IllegalArgumentException("El estudiante ya tiene ese libro en prestamo");
        }
    }

    // — Metodos de Salida
    public ArrayList<Prestamo> obtenerPrestamos() {
        return prestamoDAO.obtenerTodos();
    }

    public ArrayList<Prestamo> obtenerPrestamosActivos() {
        ArrayList<Prestamo> activos = new ArrayList<>();
        for (Prestamo p : obtenerPrestamos()) {
            if (!p.isDevuelto()) {
                activos.add(p);
            }
        }
        return activos;
    }

    public ArrayList<Prestamo> obtenerPrestamosEstudiante(int idUsuario) {
        return prestamoDAO.obtenerPorEstudiante(idUsuario);
    }

    public ArrayList<Prestamo> buscarPrestamos(ArrayList<Prestamo> base, String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return new ArrayList<>(base);
        }
        String buscado = texto.trim().toLowerCase();

        ArrayList<Prestamo> resultado = new ArrayList<>();
        for (Prestamo p : base) {
            if (p.getEstudiante().getNombre().toLowerCase().contains(buscado)
                    || p.getLibro().getTitulo().toLowerCase().contains(buscado)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public int contarPrestamosLibro(String isbn) {
        return prestamoDAO.contarPrestamosLibro(isbn);
    }

    public int contarActivos(String isbn) {
        return prestamoDAO.contarActivosLibro(isbn);
    }

    public int contarActivos(int idUsuario) {
        return prestamoDAO.contarActivosEstudiante(idUsuario);
    }

    public int contarAtrasos(int idUsuario) {
        int atrasos = 0;
        for (Prestamo p : obtenerPrestamosEstudiante(idUsuario)) {
            if (p.estaAtrasado()) {
                atrasos++;
            }
        }
        return atrasos;
    }

    public Prestamo buscarPorId(int idPrestamo) {
        return prestamoDAO.buscarPorId(idPrestamo);
    }

    public boolean puedeGestionarPrestamos() {
        return hayEstudiantes() && !gestorLibros.obtenerLibros().isEmpty();
    }

    // — Metodos Auxiliares
    private boolean hayEstudiantes() {
        for (Usuario u : gestorUsuarios.obtenerUsuarios()) {
            if (u instanceof Estudiante) {
                return true;
            }
        }
        return false;
    }
}