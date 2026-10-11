package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

// - Funcion: Operaciones sobre la tabla usuario. No aplica reglas de negocio ni muestra mensajes.
public class UsuarioDAO {

    // — Dependencias
    private final DatabaseConnection conexionBD;
    private static final String COLUMNAS = "id_usuario, nombre, rut, correo, contrasena, rol, curso";

    // — Constructor
    public UsuarioDAO(DatabaseConnection conexionBD) {
        this.conexionBD = conexionBD;
    }

    // — Metodos CRUD
    // Crear: guarda el usuario y le asigna el ID que genero MySQL
    public void insertar(Usuario usuario) {
        String sql = "INSERT INTO usuario (nombre, rut, correo, contrasena, rol, curso) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getRut());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getContrasena());
            ps.setString(5, usuario.getRol().name());
            asignarCurso(ps, 6, usuario);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    usuario.setIdUsuario(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al guardar el usuario", e);
        }
    }

    // Editar
    public void actualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET nombre = ?, rut = ?, correo = ?, curso = ? WHERE id_usuario = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getRut());
            ps.setString(3, usuario.getCorreo());
            asignarCurso(ps, 4, usuario);
            ps.setInt(5, usuario.getIdUsuario());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error al actualizar el usuario", e);
        }
    }

    public void actualizarContrasena(int idUsuario, String contrasena) {
        String sql = "UPDATE usuario SET contrasena = ? WHERE id_usuario = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, contrasena);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error al cambiar la contraseña", e);
        }
    }

    // Eliminar
    public void eliminar(int idUsuario) {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error al eliminar el usuario", e);
        }
    }

    // — Consultas
    public ArrayList<Usuario> obtenerTodos() {
        String sql = "SELECT " + COLUMNAS + " FROM usuario ORDER BY id_usuario";
        ArrayList<Usuario> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al consultar los usuarios", e);
        }
        return lista;
    }

    public Usuario buscarPorId(int idUsuario) {
        return buscarUno("id_usuario = ?", idUsuario);
    }

    public Usuario buscarPorCorreo(String correo) {
        // El collation de la tabla ignora mayusculas y minusculas
        return buscarUno("correo = ?", correo);
    }

    public Usuario buscarPorRut(String rutNormalizado) {
        return buscarUno("REPLACE(UPPER(rut), '.', '') = ?", rutNormalizado);
    }

    // — Metodos Auxiliares
    private Usuario buscarUno(String condicion, Object valor) {
        String sql = "SELECT " + COLUMNAS + " FROM usuario WHERE " + condicion;
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setObject(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al buscar el usuario", e);
        }
    }

    // Convierte una fila en Estudiante o Bibliotecario segun el rol guardado
    private Usuario mapear(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_usuario");
        String nombre = rs.getString("nombre");
        String rut = rs.getString("rut");
        String correo = rs.getString("correo");
        String contrasena = rs.getString("contrasena");
        TipoRol rol = TipoRol.valueOf(rs.getString("rol"));

        if (rol == TipoRol.ESTUDIANTE) {
            return new Estudiante(id, nombre, rut, correo, contrasena, rs.getString("curso"));
        }
        return new Bibliotecario(id, nombre, rut, correo, contrasena);
    }

    private void asignarCurso(PreparedStatement ps, int posicion, Usuario usuario) throws SQLException {
        if (usuario instanceof Estudiante) {
            ps.setString(posicion, ((Estudiante) usuario).getCurso());
        } else {
            ps.setNull(posicion, Types.VARCHAR);
        }
    }

    private Connection conexion() {
        return conexionBD.obtenerConexion();
    }
}