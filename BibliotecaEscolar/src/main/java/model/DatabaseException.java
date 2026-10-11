package model;

import java.sql.SQLException;

// - Funcion: Error al acceder a la base de datos.
// Es una RuntimeException para no obligar a los controladores a declarar throws
public class DatabaseException extends RuntimeException {

    public DatabaseException(String mensaje, SQLException causa) {
        super(mensaje + ": " + causa.getMessage(), causa);
    }
}