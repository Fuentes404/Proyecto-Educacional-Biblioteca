package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// - Funcion: Abre y entrega la conexion con MySQL.
// Patron Singleton: hay una unica instancia y una unica conexion en el programa.
// Es la unica clase que conoce los datos de conexion.
public class DatabaseConnection {

    // — Conexion a la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "casa123";

    // — Instancia unica
    private static DatabaseConnection instancia;
    private Connection conexion;

    // — Constructor
    private DatabaseConnection() {
        abrirConexion();
    }

    // — Metodos de conexion
    public static synchronized DatabaseConnection getInstance() {
        if (instancia == null) {
            instancia = new DatabaseConnection();
        }
        return instancia;
    }

    // Entrega la conexion unica; si se cerro o se cayo, la vuelve a abrir
    public Connection obtenerConexion() {
        if (!conexionActiva()) {
            abrirConexion();
        }
        return conexion;
    }

    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("No se pudo cerrar la conexion: " + e.getMessage());
        }
    }

    // — Metodos Auxiliares
    private void abrirConexion() {
        try {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new DatabaseException("No se pudo conectar a la base de datos", e);
        }
    }

    private boolean conexionActiva() {
        try {
            return conexion != null && !conexion.isClosed() && conexion.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }
}