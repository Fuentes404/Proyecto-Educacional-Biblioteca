package controller;

import model.Categoria;
import model.CategoriaDAO;
import model.DatabaseConnection;
import model.Libro;
import model.LibroDAO;

import java.util.ArrayList;

public class GestorLibros {

    // — Referencias
    private final LibroDAO libroDAO;
    private final CategoriaDAO categoriaDAO;

    // — Constructor
    public GestorLibros() {
        DatabaseConnection conexion = DatabaseConnection.getInstance();
        this.libroDAO = new LibroDAO(conexion);
        this.categoriaDAO = new CategoriaDAO(conexion);
    }

    // — Metodos de Entrada
    public void agregarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoria no puede ser nula");
        }
        categoriaDAO.insertar(categoria);
    }

    public void crearLibro(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("El libro no puede ser nulo");
        }
        validarDatos(libro.getIsbn(), libro.getTitulo(), libro.getAutor(), libro.getEditorial(),
                libro.getCategoria(), libro.getStock());
        validarIsbnRepetido(libro.getIsbn());
        libroDAO.insertar(libro);
    }

    public void eliminarLibro(String isbn) {
        Libro libro = buscarPorIsbn(isbn);
        if (libro == null) {
            throw new IllegalArgumentException("El libro no existe");
        }
        libroDAO.eliminar(libro.getIsbn());
    }

    public void editarLibro(String isbn, String titulo, String autor, String editorial,
                            Categoria categoria, int stock) {
        Libro libro = buscarPorIsbn(isbn);
        if (libro == null) {
            throw new IllegalArgumentException("El libro no existe");
        }
        validarDatos(isbn, titulo, autor, editorial, categoria, stock);
        libro.setTitulo(titulo.trim());
        libro.setAutor(autor.trim());
        libro.setEditorial(editorial.trim());
        libro.setCategoria(categoria);
        libro.setStock(stock);
        libroDAO.actualizar(libro);
    }

    // — Metodos de control
    private void validarDatos(String isbn, String titulo, String autor, String editorial,
                              Categoria categoria, int stock) {
        ValidadorDatos.isbn(isbn);
        ValidadorDatos.noVacio(titulo, "titulo");
        ValidadorDatos.noVacio(autor, "autor");
        ValidadorDatos.noVacio(editorial, "editorial");
        if (categoria == null) {
            throw new IllegalArgumentException("Debe seleccionar una categoria");
        }
        ValidadorDatos.stock(stock);
    }

    private void validarIsbnRepetido(String isbn) {
        if (buscarPorIsbn(isbn) != null) {
            throw new IllegalArgumentException("Ya existe un libro con ese ISBN");
        }
    }

    // — Metodos de Salida
    public ArrayList<Libro> obtenerLibros() {
        return libroDAO.obtenerTodos();
    }

    public ArrayList<Categoria> obtenerCategorias() {
        return categoriaDAO.obtenerTodos();
    }

    public Libro buscarPorIsbn(String isbn) {
        if (isbn == null) {
            return null;
        }
        return libroDAO.buscarPorIsbn(normalizarIsbn(isbn));
    }

    public ArrayList<Libro> buscarLibros(String texto) {
        ArrayList<Libro> libros = obtenerLibros();
        if (texto == null || texto.trim().isEmpty()) {
            return libros;
        }
        String buscado = texto.trim().toLowerCase();
        String isbnBuscado = normalizarIsbn(buscado);

        ArrayList<Libro> resultado = new ArrayList<>();
        for (Libro l : libros) {
            boolean porIsbn = !isbnBuscado.isEmpty() && normalizarIsbn(l.getIsbn()).contains(isbnBuscado);
            if (porIsbn
                    || l.getTitulo().toLowerCase().contains(buscado)
                    || l.getAutor().toLowerCase().contains(buscado)
                    || l.getEditorial().toLowerCase().contains(buscado)
                    || l.getCategoria().getNombre().toLowerCase().contains(buscado)) {
                resultado.add(l);
            }
        }
        return resultado;
    }

    // — Metodos Auxiliares
    private String normalizarIsbn(String isbn) {
        return isbn.replace("-", "").trim();
    }
}