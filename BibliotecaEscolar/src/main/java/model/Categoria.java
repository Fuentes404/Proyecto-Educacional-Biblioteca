package model;

public class Categoria {
    // - Atributos
    private int idCategoria;
    private String nombre;

    // - Constructor
    public Categoria(int idCategoria, String nombre) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
    }

    // - Metodos Getter and Setter
    public int getIdCategoria() {
        return idCategoria;
    }
    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // - Metodos Funcionales

    // - Metodos Auxiliares

    // - Metodos Representativos

    // Metodo Mostrar Datos de Categoria
    public String mostrarCategoria() {
        return "Datos Categoria: " + getIdCategoria() + "\n" +
                "Nombre: " + getNombre();
    }

    // Metodo to String
    @Override
    public String toString() {
        return "id Categoria " + getIdCategoria() + " Nombre: " + getNombre();
    }
}
