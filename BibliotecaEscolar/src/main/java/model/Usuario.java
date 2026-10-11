package model;

public abstract class Usuario {
    // - Atributos
    protected int idUsuario;
    protected String nombre;
    protected String rut;
    protected String correo;
    protected String contrasena;
    protected TipoRol rol;

    // - Constructor
    public Usuario(int idUsuario, String nombre, String rut, String correo, String contrasena, TipoRol rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.rut = rut;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    // - Metodos Getter and Setter
    public int getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getRut() {
        return rut;
    }
    public void setRut(String rut) {
        this.rut = rut;
    }
    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    public String getContrasena() {
        return contrasena;
    }
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    public TipoRol getRol() {
        return rol;
    }
    public void setRol(TipoRol rol) {
        this.rol = rol;
    }

    // - Metodos Funcionales

    // - Metodos Auxiliares

    // - Metodos de representacion

    // Metodo Mostrar Datos Usuario
    public String mostrarUsuario() {
        return "Datos De Usuario: " + getIdUsuario() + "\n" +
                "Nombre: " + getNombre() + "\n" +
                "Rut: " + getRut() + "\n" +
                "Correo: " + getCorreo() + "\n" +
                "Contraseña: " + getContrasena() + "\n" +
                "Rol: " + getRol();
    }

    // Metodo to String
    @Override
    public String toString() {
        return "[" + getRol() + "] Id: " + getIdUsuario() + " - " + getNombre() + " - " + getCorreo();
    }


}
