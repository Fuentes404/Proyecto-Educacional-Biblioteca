package model;

public class Libro {
    // - Atributos
    private String isbn;
    private String titulo;
    private String autor;
    private String editorial;
    private Categoria categoria;
    private int stock;

    // - Constructor
    public Libro(String isbn, String titulo, String autor, String editorial, Categoria categoria, int stock) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.categoria = categoria;
        this.stock = stock;
    }

    // - Metodos Getter and Setter
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }
    public String getEditorial() {
        return editorial;
    }
    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }
    public Categoria getCategoria() {
        return categoria;
    }
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }

    // - Metodos Funcionales

    // - Metodos Auxiliares

    // - Metodos de Representacion

    // Metodo mostrar Datos Libro
    public String mostrarLibro() {
        return "Datos del Libro: " + "[" + getIsbn() + "]\n" +
                "Titulo: " + getTitulo() + "\n" +
                "Autor: " + getAutor() + "\n" +
                "Editorial: " + getEditorial() + "\n" +
                "Categoria: " + getCategoria() + "\n" +
                "Stock: " + getStock();
    }

    // Metodo To String
    @Override
    public String toString() {
        return "ISBN: " + getIsbn() + " Titulo: " + getTitulo() + " Autor: " + getAutor();
    }
}