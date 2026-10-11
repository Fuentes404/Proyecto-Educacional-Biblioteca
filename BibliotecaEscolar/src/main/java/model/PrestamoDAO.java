package model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

// - Funcion: Operaciones sobre la tabla prestamo. No muestra mensajes.
// El ajuste de stock va dentro de la misma transaccion que el prestamo para que los datos no queden descuadrados.
public class PrestamoDAO {

    // — Dependencias
    private final DatabaseConnection conexionBD;

    // Un solo SELECT con JOIN: trae el prestamo con su estudiante y su libro
    private static final String SELECT_PRESTAMO =
            "SELECT p.id_prestamo, p.fecha_prestamo, p.fecha_devolucion, p.devuelto, " +
                    "u.id_usuario, u.nombre, u.rut, u.correo, u.contrasena, u.curso, " +
                    "l.isbn, l.titulo, l.autor, l.editorial, l.stock, c.id_categoria, c.nombre AS categoria " +
                    "FROM prestamo p " +
                    "JOIN usuario u ON p.id_usuario = u.id_usuario " +
                    "JOIN libro l ON p.isbn = l.isbn " +
                    "JOIN categoria c ON l.id_categoria = c.id_categoria ";

    // Operacion que se ejecuta dentro de una transaccion
    @FunctionalInterface
    private interface Operacion {
        void ejecutar(Connection con) throws SQLException;
    }

    // — Constructor
    public PrestamoDAO(DatabaseConnection conexionBD) {
        this.conexionBD = conexionBD;
    }

    // — Metodos CRUD
    // Crear
    public void insertar(Prestamo prestamo) {
        enTransaccion("Error al registrar el prestamo", con -> {
            int filas;
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE libro SET stock = stock - 1 WHERE isbn = ? AND stock > 0")) {
                ps.setString(1, prestamo.getLibro().getIsbn());
                filas = ps.executeUpdate();
            }
            if (filas == 0) {
                throw new IllegalArgumentException("No hay stock disponible de ese libro");
            }

            String sql = "INSERT INTO prestamo (id_usuario, isbn, fecha_prestamo, fecha_devolucion, devuelto) " +
                    "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, prestamo.getEstudiante().getIdUsuario());
                ps.setString(2, prestamo.getLibro().getIsbn());
                ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
                ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
                ps.setBoolean(5, prestamo.isDevuelto());
                ps.executeUpdate();
                try (ResultSet claves = ps.getGeneratedKeys()) {
                    if (claves.next()) {
                        prestamo.setIdPrestamo(claves.getInt(1));
                    }
                }
            }
        });
    }

    // Editar
    public void actualizarFechaDevolucion(int idPrestamo, LocalDate fechaDevolucion) {
        String sql = "UPDATE prestamo SET fecha_devolucion = ? WHERE id_prestamo = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fechaDevolucion));
            ps.setInt(2, idPrestamo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error al actualizar el prestamo", e);
        }
    }

    // Marca el prestamo como devuelto y repone 1 de stock
    public void marcarDevuelto(int idPrestamo) {
        enTransaccion("Error al registrar la devolucion", con -> {
            int filas;
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE prestamo SET devuelto = TRUE WHERE id_prestamo = ? AND devuelto = FALSE")) {
                ps.setInt(1, idPrestamo);
                filas = ps.executeUpdate();
            }
            if (filas > 0) {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE libro SET stock = stock + 1 " +
                                "WHERE isbn = (SELECT isbn FROM prestamo WHERE id_prestamo = ?)")) {
                    ps.setInt(1, idPrestamo);
                    ps.executeUpdate();
                }
            }
        });
    }

    // Eliminar
    public void eliminar(int idPrestamo) {
        enTransaccion("Error al eliminar el prestamo", con -> {
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE libro SET stock = stock + 1 " +
                            "WHERE isbn = (SELECT isbn FROM prestamo WHERE id_prestamo = ? AND devuelto = FALSE)")) {
                ps.setInt(1, idPrestamo);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM prestamo WHERE id_prestamo = ?")) {
                ps.setInt(1, idPrestamo);
                ps.executeUpdate();
            }
        });
    }

    // — Consultas
    public ArrayList<Prestamo> obtenerTodos() {
        return consultar("");
    }

    public ArrayList<Prestamo> obtenerPorEstudiante(int idUsuario) {
        return consultar("WHERE p.id_usuario = ?", idUsuario);
    }

    public Prestamo buscarPorId(int idPrestamo) {
        ArrayList<Prestamo> resultado = consultar("WHERE p.id_prestamo = ?", idPrestamo);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    public int contarPrestamosLibro(String isbn) {
        return contar("SELECT COUNT(*) FROM prestamo WHERE isbn = ?", isbn);
    }

    public int contarActivosLibro(String isbn) {
        return contar("SELECT COUNT(*) FROM prestamo WHERE devuelto = FALSE AND isbn = ?", isbn);
    }

    public int contarActivosEstudiante(int idUsuario) {
        return contar("SELECT COUNT(*) FROM prestamo WHERE devuelto = FALSE AND id_usuario = ?", idUsuario);
    }

    public boolean existeActivo(int idUsuario, String isbn) {
        return contar("SELECT COUNT(*) FROM prestamo WHERE devuelto = FALSE AND id_usuario = ? AND isbn = ?",
                idUsuario, isbn) > 0;
    }

    // — Metodos Auxiliares
    private ArrayList<Prestamo> consultar(String filtro, Object... parametros) {
        String sql = SELECT_PRESTAMO + filtro + " ORDER BY p.id_prestamo";
        ArrayList<Prestamo> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            asignar(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al consultar los prestamos", e);
        }
        return lista;
    }

    private int contar(String sql, Object... parametros) {
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            asignar(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al contar los prestamos", e);
        }
    }

    private void asignar(PreparedStatement ps, Object[] parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            ps.setObject(i + 1, parametros[i]);
        }
    }

    private synchronized void enTransaccion(String mensajeError, Operacion operacion) {
        Connection con = conexion();
        try {
            con.setAutoCommit(false);
            operacion.ejecutar(con);
            con.commit();
        } catch (SQLException e) {
            revertir(con);
            throw new DatabaseException(mensajeError, e);
        } catch (RuntimeException e) {
            // Regla de negocio violada dentro de la transaccion (ej: sin stock): se deshace y se propaga
            revertir(con);
            throw e;
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ignorada) {
                // La proxima llamada a obtenerConexion() revisa el estado de la conexion
            }
        }
    }

    private void revertir(Connection con) {
        try {
            con.rollback();
        } catch (SQLException ignorada) {
            // Si el rollback falla, se informa el error original
        }
    }

    private Prestamo mapear(ResultSet rs) throws SQLException {
        Estudiante estudiante = new Estudiante(
                rs.getInt("id_usuario"),
                rs.getString("nombre"),
                rs.getString("rut"),
                rs.getString("correo"),
                rs.getString("contrasena"),
                rs.getString("curso")
        );
        Categoria categoria = new Categoria(rs.getInt("id_categoria"), rs.getString("categoria"));
        Libro libro = new Libro(
                rs.getString("isbn"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("editorial"),
                categoria,
                rs.getInt("stock")
        );
        return new Prestamo(
                rs.getInt("id_prestamo"),
                estudiante,
                libro,
                rs.getDate("fecha_prestamo").toLocalDate(),
                rs.getDate("fecha_devolucion").toLocalDate(),
                rs.getBoolean("devuelto")
        );
    }

    private Connection conexion() {
        return conexionBD.obtenerConexion();
    }
}