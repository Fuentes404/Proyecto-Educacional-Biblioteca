package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

// - Funcion: Operaciones sobre la tabla categoria. No aplica reglas de negocio ni muestra mensajes.
public class CategoriaDAO {

    // — Dependencias
    private final DatabaseConnection conexionBD;

    // — Constructor
    public CategoriaDAO(DatabaseConnection conexionBD) {
        this.conexionBD = conexionBD;
    }

    // — Metodos CRUD
    public void insertar(Categoria categoria) {
        String sql = "INSERT INTO categoria (nombre) VALUES (?)";
        try (PreparedStatement ps = conexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categoria.getNombre());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    categoria.setIdCategoria(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al guardar la categoria", e);
        }
    }

    // — Consultas
    public ArrayList<Categoria> obtenerTodos() {
        String sql = "SELECT id_categoria, nombre FROM categoria ORDER BY id_categoria";
        ArrayList<Categoria> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Categoria(rs.getInt("id_categoria"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al consultar las categorias", e);
        }
        return lista;
    }

    // — Metodos Auxiliares
    private Connection conexion() {
        return conexionBD.obtenerConexion();
    }
}