package app;

import controller.GestorLibros;
import controller.GestorPrestamos;
import controller.GestorUsuarios;
import model.DatabaseConnection;
import model.DatabaseException;
import view.VentanaLogin;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        // - Conexion - si falla, se informa -  no se abre la aplicacion
        try {
            DatabaseConnection.getInstance();
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error de conexion",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cierra la conexion al terminar el programa
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> DatabaseConnection.getInstance().cerrarConexion()));

        // - Gestores
        GestorUsuarios gestorUsuarios = new GestorUsuarios();
        GestorLibros gestorLibros = new GestorLibros();
        GestorPrestamos gestorPrestamos = new GestorPrestamos(gestorUsuarios, gestorLibros);

        SwingUtilities.invokeLater(() -> new VentanaLogin(gestorUsuarios, gestorLibros, gestorPrestamos).setVisible(true));
    }
}