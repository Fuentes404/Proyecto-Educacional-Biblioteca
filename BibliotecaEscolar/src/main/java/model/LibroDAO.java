package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// - Funcion: Operaciones sobre la tabla libro. No aplica reglas de negocio ni muestra mensajes.
public class LibroDAO {

    // — Dependencias
    private final DatabaseConnection conexionBD;
    private static final String SELECT_LIBRO =
            "SELECT l.isbn, l.titulo, l.autor, l.editorial, l.stock, c.id_categoria, c.nombre AS categoria " +
                    "FROM libro l JOIN categoria c ON l.id_categoria = c.id_categoria ";

    // — Constructor
    public LibroDAO(DatabaseConnection conexionBD) {
        this.conexionBD = conexionBD;
    }

    // — Metodos CRUD
    // Crear
    public void insertar(Libro libro) {
        String sql = "INSERT INTO libro (isbn, titulo, autor, editorial, id_categoria, stock) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, libro.getIsbn());
            ps.setString(2, libro.getTitulo());
            ps.setString(3, libro.getAutor());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getCategoria().getIdCategoria());
            ps.setInt(6, libro.getStock());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error al guardar el libro", e);
        }
    }

    // Editar
    public void actualizar(Libro libro) {
        String sql = "UPDATE libro SET titulo = ?, autor = ?, editorial = ?, id_categoria = ?, stock = ? WHERE isbn = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getEditorial());
            ps.setInt(4, libro.getCategoria().getIdCategoria());
            ps.setInt(5, libro.getStock());
            ps.setString(6, libro.getIsbn());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error al actualizar el libro", e);
        }
    }

    // Eliminar
    public void eliminar(String isbn) {
        String sql = "DELETE FROM libro WHERE isbn = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, isbn);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error al eliminar el libro", e);
        }
    }

    // — Consultas
    public ArrayList<Libro> obtenerTodos() {
        ArrayList<Libro> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion().prepareStatement(SELECT_LIBRO + "ORDER BY l.titulo");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al consultar los libros", e);
        }
        return lista;
    }

    public Libro buscarPorIsbn(String isbnNormalizado) {
        String sql = SELECT_LIBRO + "WHERE REPLACE(l.isbn, '-', '') = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, isbnNormalizado);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al buscar el libro", e);
        }
    }

    // — Metodos Auxiliares
    private Libro mapear(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(rs.getInt("id_categoria"), rs.getString("categoria"));
        return new Libro(
                rs.getString("isbn"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("editorial"),
                categoria,
                rs.getInt("stock")
        );
    }

    private Connection conexion() {
        return conexionBD.obtenerConexion();
    }
}