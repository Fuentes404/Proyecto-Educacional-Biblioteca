package model;

public class Estudiante extends Usuario{
    // - Atributos
    private String curso;

    // - Constructor
    public Estudiante(int idUsuario,String nombre, String rut, String correo, String contrasena, String curso) {
        super(idUsuario, nombre, rut, correo, contrasena, TipoRol.ESTUDIANTE);
        this.curso = curso;
    }

    // - Metodos Getter and Setter
    public String getCurso() {
        return curso;
    }
    public void setCurso(String curso) {
        this.curso = curso;
    }

    // - Metodos Funcionales


    // - Metodos Auxiliares


    // - Metodos Representativos

    // Metodo Mostrar Datos de Usuario
    @Override
    public String mostrarUsuario() {
        return super.mostrarUsuario() + "\n" +
                "Curso: " + getCurso();
    }

}
