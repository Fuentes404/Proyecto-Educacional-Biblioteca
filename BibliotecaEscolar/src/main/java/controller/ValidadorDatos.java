package controller;

import java.time.LocalDate;

// Validadcion de los datos en los distintos campos , si no, lanza IllegalArgumentException con un mensaje.
public class ValidadorDatos {

    // - Constructor
    private ValidadorDatos() {
    }

    // - Textos

    // Campo Vacio
    public static void noVacio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo " + campo + " no puede estar vacio");
        }
    }

    // Campo Nombre : limpia espacios - valida cantidad de caracteres - limpia caracteres especiales
    public static void nombre(String valor) {
        noVacio(valor, "nombre");
        String limpio = valor.trim();
        if (limpio.length() < 2 || limpio.length() > 50) {
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 50 caracteres");
        }
        if (!limpio.matches("\\p{L}+([ '\\-]\\p{L}+)*")) {
            throw new IllegalArgumentException("El nombre solo puede contener letras y espacios");
        }
    }

    // Formato Correo
    public static void correo(String valor) {
        noVacio(valor, "correo");
        if (!valor.trim().matches("[\\w.+\\-]+@[\\w\\-]+(\\.[\\w\\-]+)+")) {
            throw new IllegalArgumentException("El formato del correo no es valido");
        }
    }

    // Campo vacio curso
    public static void curso(String valor) {
        noVacio(valor, "curso");
    }

    // - Rut chileno - formato 12345678-5, con o sin puntos
    public static void rut(String valor) {
        noVacio(valor, "RUT");
        String rut = valor.replace(".", "").trim().toUpperCase();

        if (!rut.matches("\\d{7,8}-[\\dK]")) {
            throw new IllegalArgumentException("El RUT debe tener el formato 12345678-5");
        }

    }

    // - Contraseña
    public static void contrasena(String valor) {
        noVacio(valor, "contraseña");
        if (valor.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
    }

    // - Libros
    public static void isbn(String valor) {
        noVacio(valor, "ISBN");
        String soloDigitos = valor.replace("-", "").trim();
        if (!soloDigitos.matches("\\d{10}|\\d{13}")) {
            throw new IllegalArgumentException("El ISBN debe tener 10 o 13 digitos");
        }
    }

    public static void stock(int valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }

    // - Numeros y fechas
    public static void numeroPositivo(int valor, String campo) {
        if (valor <= 0) {
            throw new IllegalArgumentException("El campo " + campo + " debe ser mayor a 0");
        }
    }

    public static void fechas(LocalDate prestamo, LocalDate devolucion) {
        if (prestamo == null || devolucion == null) {
            throw new IllegalArgumentException("Las fechas no pueden estar vacias");
        }
        if (devolucion.isBefore(prestamo)) {
            throw new IllegalArgumentException("La devolucion no puede ser anterior al prestamo");
        }
    }

}