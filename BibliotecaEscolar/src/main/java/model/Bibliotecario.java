package model;

public class Bibliotecario extends Usuario {
    // - Atributos
    // Los Hereda de Usurio

    // - Constructor
    public Bibliotecario(int idUsuario, String nombre, String rut, String correo, String contrasena) {
        super(idUsuario, nombre, rut, correo, contrasena, TipoRol.ADMIN);
    }

    // - Metodos Getter and Setter
    // Los Hereda de Usuario

    // - Metodos Funcionales

    // - Metodos Auxiliares

    // - Metodos de Representacion

}
