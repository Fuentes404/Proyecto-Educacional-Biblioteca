
# Biblioteca Escolar - Sistema de Gestión con Java, Swing y MySQL

Aplicación de escritorio para gestionar una **biblioteca escolar**. Aplica **Programación Orientada a Objetos** (herencia, clase abstracta, enumeraciones, polimorfismo y sobreescritura), una arquitectura **Modelo - Vista - Controlador**, el patrón **DAO**, el patrón **Singleton** para la conexión, **concurrencia** con hilos y sincronización, e interfaz gráfica con **Java Swing**. Los datos se guardan en una base de datos **MySQL** mediante **JDBC**.

## 📋 Descripción

Biblioteca Escolar permite a un colegio administrar sus **libros**, **estudiantes** y **préstamos** desde una sola ventana. Existen dos tipos de usuario con permisos distintos:

- **Bibliotecario (ADMIN):** gestiona libros, usuarios y préstamos, y registra las devoluciones.
- **Estudiante:** consulta el catálogo de libros y revisa sus propios préstamos.

El sistema controla el stock de cada libro (baja al prestar y se repone al devolver), evita préstamos duplicados, detecta los préstamos atrasados y valida todos los datos antes de guardarlos.

## 🛠️ Tecnologías

| Tecnología | Uso |
|------------|-----|
| Java (JDK 8 o superior) | Lenguaje de la aplicación. |
| Java Swing | Interfaz gráfica. |
| MySQL 8.0.16 o superior | Base de datos. |
| MySQL Connector/J | Driver JDBC para conectar Java con MySQL. |
| IntelliJ IDEA | Entorno de desarrollo. |

## 🧩 Estructuras utilizadas

| Tipo | Sintaxis | Descripción |
|------|----------|-------------|
| Clase abstracta | `abstract class Usuario` | Define los datos y métodos comunes a todos los usuarios. |
| Herencia | `class Estudiante extends Usuario` | `Estudiante` y `Bibliotecario` especializan a la clase base. |
| Enumeraciones | `enum TipoRol` | Define los roles disponibles: `ADMIN` y `ESTUDIANTE`. |
| Sobreescritura | `@Override mostrarUsuario()` | `Estudiante` amplía la información del usuario con su curso. |
| Polimorfismo | `usuario instanceof Estudiante` | Las vistas y controladores deciden según el tipo real del usuario. |
| Patrón MVC | `model` / `view` / `controller` | Separa los datos, la interfaz y la lógica de negocio. |
| Patrón DAO | `LibroDAO`, `UsuarioDAO`... | Cada DAO se encarga solo de hablar con la base de datos. |
| Patrón Singleton | `DatabaseConnection.getInstance()` | Existe una única instancia y una única conexión a MySQL. |
| JDBC | `PreparedStatement` | Consultas parametrizadas contra la base de datos. |
| Transacciones | `setAutoCommit(false)`, `commit`, `rollback` | El préstamo y el ajuste de stock se guardan juntos o no se guardan. |
| Hilos | `SwingWorker` | El registro de un préstamo corre en segundo plano sin congelar la interfaz. |
| Sincronización | `synchronized` | Evita condiciones de carrera al modificar el stock. |
| Excepciones | `IllegalArgumentException`, `DatabaseException` | Los controladores lanzan el error con un mensaje y la vista lo muestra. |
| Validación | `ValidadorDatos.rut(valor)` | Clase de utilidad con métodos estáticos que validan cada campo. |
| Expresiones regulares | `valor.matches("\\d{10}\|\\d{13}")` | Validan nombre, correo, RUT e ISBN. |
| Interfaz gráfica | `JFrame`, `JPanel`, `JTable`, `JDialog` | Ventanas, paneles, tablas y formularios emergentes. |
| Ventanas modales | `ModalityType.APPLICATION_MODAL` | Los formularios bloquean la ventana principal hasta cerrarse. |
| Cambio de pantalla | `CardLayout` | La ventana principal alterna entre Inicio, Libros, Estudiantes y Préstamos. |
| Modelo de tabla | `DefaultTableModel` | Los paneles llenan la tabla con los datos de los gestores. |
| Búsqueda en vivo | `DocumentListener` | Filtra la tabla con cada letra escrita en el buscador. |
| Renderizado personalizado | `DefaultListCellRenderer`, `DefaultTableCellRenderer` | Muestra el nombre en los combos y pinta en rojo las fechas atrasadas. |
| Expresiones lambda | `btn.addActionListener(e -> guardar())` | Asocian cada botón con su acción. |
| Fecha | `LocalDate` | Registra y valida las fechas de préstamo y devolución. |

## 📂 Estructura del proyecto

```text
BibliotecaGestion/
├── sql/
│   ├── crear_tablas.sql                 # DROP, CREATE y poblado de datos de ejemplo
│   └── consultas.sql                    # Consultas para revisar las 4 tablas
└── src/
    ├── app/
    │   └── Main.java                    # Punto de entrada: abre la conexión y el login
    ├── model/
    │   ├── Usuario.java                 # Clase abstracta base
    │   ├── Bibliotecario.java           # Usuario con rol ADMIN
    │   ├── Estudiante.java              # Usuario con rol ESTUDIANTE y curso
    │   ├── TipoRol.java                 # ADMIN, ESTUDIANTE
    │   ├── Libro.java                   # ISBN, título, autor, editorial, categoría y stock
    │   ├── Categoria.java               # Categoría de un libro
    │   ├── Prestamo.java                # Estudiante - libro, con fechas, estado y atraso
    │   ├── DatabaseConnection.java      # Singleton de la conexión JDBC
    │   ├── DatabaseException.java       # Error de acceso a la base de datos
    │   ├── UsuarioDAO.java              # Acceso a la tabla usuario
    │   ├── LibroDAO.java                # Acceso a la tabla libro
    │   ├── CategoriaDAO.java            # Acceso a la tabla categoria
    │   └── PrestamoDAO.java             # Acceso a la tabla prestamo (con transacciones)
    ├── controller/
    │   ├── GestorUsuarios.java          # Lógica de usuarios y autenticación
    │   ├── GestorLibros.java            # Lógica de libros y categorías
    │   ├── GestorPrestamos.java         # Lógica de préstamos, devoluciones y stock
    │   └── ValidadorDatos.java          # Validaciones de campos
    └── view/
        ├── VentanaLogin.java            # Inicio de sesión
        ├── VentanaMenuPrincipal.java    # Ventana con menú lateral
        ├── PanelPerfil.java             # Inicio: datos del usuario
        ├── PanelLibros.java             # Catálogo y gestión de libros
        ├── PanelEstudiantes.java        # Gestión de usuarios (solo ADMIN)
        ├── PanelPrestamos.java          # Gestión y consulta de préstamos
        ├── VentanaNuevoLibro.java       # Formulario: crear libro
        ├── VentanaEditarLibro.java      # Formulario: editar libro
        ├── VentanaNuevoUsuario.java     # Formulario: crear usuario
        ├── VentanaEditarUsuario.java    # Formulario: editar usuario
        ├── VentanaNuevoPrestamo.java    # Formulario: crear préstamo (en segundo plano)
        ├── VentanaEditarPrestamo.java   # Formulario: editar préstamo
        ├── VentanaEditarContrasena.java # Formulario: cambiar contraseña
        └── VentanaHistorial.java        # Historial de préstamos de un estudiante
```

## 🏗️ Capas

| Capa | Responsabilidad |
|------|-----------------|
| `app` | Arranca el sistema: abre la conexión, crea los gestores y abre el login. |
| `model` | Entidades del sistema y acceso a la base de datos (DAO y conexión). |
| `controller` | Lógica de negocio: crear, editar, eliminar, buscar y validar. |
| `view` | Interfaz gráfica: muestra tablas y botones, pide datos con formularios, llama a los gestores y muestra los mensajes. |

Cada vista recibe los gestores por constructor, de modo que **toda la aplicación comparte una única instancia** de cada uno. Los DAO solo ejecutan consultas: no aplican reglas de negocio ni muestran mensajes.

## ⚙️ Patrones y concurrencia

**Singleton.** `DatabaseConnection` tiene un constructor privado y un método estático `getInstance()`, así que toda la aplicación usa una única conexión. Si la conexión se cae, `obtenerConexion()` la vuelve a abrir.

**DAO.** Cada tabla tiene su clase (`UsuarioDAO`, `LibroDAO`, `CategoriaDAO`, `PrestamoDAO`) con las operaciones de crear, leer, actualizar y eliminar mediante `PreparedStatement`.

**Hilo independiente.** Al guardar un préstamo, `VentanaNuevoPrestamo` usa un `SwingWorker`: `doInBackground()` registra el préstamo en segundo plano y `done()` vuelve al hilo de la interfaz para mostrar el resultado. Mientras tanto se bloquean los controles, así la ventana no se congela.

**Consistencia del stock.** Tres mecanismos trabajan juntos:

| Mecanismo | Dónde | Qué evita |
|-----------|-------|-----------|
| `synchronized` | `crearPrestamo`, `registrarDevolucion` y `eliminarPrestamo` en `GestorPrestamos` | Que dos hilos validen el último ejemplar a la vez (condición de carrera). |
| `synchronized` | `enTransaccion` en `PrestamoDAO` | Que dos hilos mezclen transacciones sobre la conexión única. |
| `UPDATE ... AND stock > 0` | `PrestamoDAO.insertar` | Que el stock quede negativo aunque falle la sincronización. |

Además, el préstamo, el descuento de stock y el registro en el historial se guardan en **una misma transacción**: si algo falla se hace `rollback` y no queda nada a medias.

## 💾 Base de datos

Base de datos `biblioteca_db` con 4 tablas (el historial de cada estudiante se obtiene desde `prestamo`):

| Tabla | Descripción |
|-------|-------------|
| `categoria` | Categorías de los libros. |
| `usuario` | Bibliotecarios y estudiantes, diferenciados por la columna `rol`. El `curso` solo aplica a estudiantes. |
| `libro` | Libros con ISBN, título, autor, editorial, categoría y stock. |
| `prestamo` | Préstamos con estudiante, libro, fechas y estado de devolución. |

### Script de creación de tablas y poblado (`sql/crear_tablas.sql`)

Elimina las tablas si existen, las crea y carga datos de ejemplo. Se puede ejecutar las veces que se quiera: deja la base siempre en el estado inicial.

```sql
CREATE DATABASE IF NOT EXISTS biblioteca_db;
USE biblioteca_db;

-- DROP 
DROP TABLE IF EXISTS prestamo;
DROP TABLE IF EXISTS libro;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS categoria;

-- CREACION DE TABLAS
CREATE TABLE categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(50)  NOT NULL,
    rut        VARCHAR(12)  NOT NULL UNIQUE,
    correo     VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(100) NOT NULL,
    rol        ENUM('ADMIN', 'ESTUDIANTE') NOT NULL,
    curso      VARCHAR(30) NULL
) ENGINE=InnoDB;

CREATE TABLE libro (
    isbn         VARCHAR(20)  NOT NULL PRIMARY KEY,
    titulo       VARCHAR(150) NOT NULL,
    autor        VARCHAR(100) NOT NULL,
    editorial    VARCHAR(100) NOT NULL,
    id_categoria INT          NOT NULL,
    stock        INT          NOT NULL DEFAULT 0,
    CONSTRAINT chk_libro_stock CHECK (stock >= 0),
    CONSTRAINT fk_libro_categoria FOREIGN KEY (id_categoria)
        REFERENCES categoria (id_categoria)
) ENGINE=InnoDB;

CREATE TABLE prestamo (
    id_prestamo      INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario       INT         NOT NULL,
    isbn             VARCHAR(20) NOT NULL,
    fecha_prestamo   DATE        NOT NULL,
    fecha_devolucion DATE        NOT NULL,
    devuelto         BOOLEAN     NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_prestamo_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_prestamo_libro FOREIGN KEY (isbn)
        REFERENCES libro (isbn) ON DELETE CASCADE
) ENGINE=InnoDB;

-- POBLADO

INSERT INTO categoria (nombre) VALUES
    ('Novela'),
    ('Clasicos'),
    ('Terror'),
    ('Ciencia'),
    ('Infantil');

INSERT INTO usuario (nombre, rut, correo, contrasena, rol, curso) VALUES
    ('Admin',  '111111111-1', 'admin@correo.com',  '123456', 'ADMIN',      NULL),
    ('Ana',    '123456789-0', 'ana@correo.com',    '123456', 'ESTUDIANTE', '1A'),
    ('Luis',   '123456789-1', 'luis@correo.com',   '123456', 'ESTUDIANTE', '1B'),
    ('Camila', '123456789-2', 'camila@correo.com', '123456', 'ESTUDIANTE', '2A'),
    ('Diego',  '123456789-3', 'diego@correo.com',  '123456', 'ESTUDIANTE', '2B');

INSERT INTO libro (isbn, titulo, autor, editorial, id_categoria, stock) VALUES
    ('1111111111', 'Rayuela',  'Cortazar',  'Editorial A', 1, 5),
    ('2222222222', 'Quijote',  'Cervantes', 'Editorial B', 2, 5),
    ('3333333333', 'Dracula',  'Stoker',    'Editorial C', 3, 5),
    ('4444444444', 'Cosmos',   'Sagan',     'Editorial D', 4, 5),
    ('5555555555', 'Matilda',  'Dahl',      'Editorial E', 5, 5);

INSERT INTO prestamo (id_usuario, isbn, fecha_prestamo, fecha_devolucion, devuelto) VALUES
    -- Devueltos
    (2, '3333333333', DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 13 DAY), TRUE),
    (3, '2222222222', DATE_SUB(CURDATE(), INTERVAL 15 DAY), DATE_SUB(CURDATE(), INTERVAL 8 DAY),  TRUE),
    -- Activos atrasados
    (4, '4444444444', DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_SUB(CURDATE(), INTERVAL 3 DAY),  FALSE),
    (5, '5555555555', DATE_SUB(CURDATE(), INTERVAL 12 DAY), DATE_SUB(CURDATE(), INTERVAL 5 DAY),  FALSE),
    -- Activos al dia
    (2, '1111111111', DATE_SUB(CURDATE(), INTERVAL 2 DAY),  DATE_ADD(CURDATE(), INTERVAL 5 DAY),  FALSE),
    (3, '4444444444', DATE_SUB(CURDATE(), INTERVAL 1 DAY),  DATE_ADD(CURDATE(), INTERVAL 6 DAY),  FALSE);
```

### Script de consultas (`sql/consultas.sql`)

Sirve para revisar el contenido de las 4 tablas y comprobar los cambios que hace la aplicación (por ejemplo, el stock después de un préstamo).

```sql
USE biblioteca_db;

SELECT * FROM categoria;
SELECT * FROM usuario;
SELECT * FROM libro;
SELECT * FROM prestamo;

SELECT p.id_prestamo,
       u.nombre AS estudiante,
       l.titulo AS libro,
       c.nombre AS categoria,
       p.fecha_prestamo,
       p.fecha_devolucion,
       p.devuelto,
       (p.devuelto = FALSE AND p.fecha_devolucion < CURDATE()) AS atrasado
FROM prestamo p
JOIN usuario u   ON p.id_usuario = u.id_usuario
JOIN libro l     ON p.isbn = l.isbn
JOIN categoria c ON l.id_categoria = c.id_categoria
ORDER BY p.id_prestamo;
```

### Datos de ejemplo

**Usuarios** (la contraseña de todos es `123456`):

| Rol | Nombre | Correo | Curso |
|-----|--------|--------|-------|
| Bibliotecario (ADMIN) | Admin | admin@correo.com | - |
| Estudiante | Ana | ana@correo.com | 1A |
| Estudiante | Luis | luis@correo.com | 1B |
| Estudiante | Camila | camila@correo.com | 2A |
| Estudiante | Diego | diego@correo.com | 2B |

**Categorías:** Novela, Clasicos, Terror, Ciencia e Infantil.

**Libros:** 5 libros (Rayuela, Quijote, Dracula, Cosmos y Matilda), cada uno con stock 5.

**Préstamos:** 6 en total: 2 devueltos, 2 activos atrasados y 2 activos al día. Las fechas se calculan respecto a la fecha actual, así siempre hay préstamos atrasados para probar el color rojo.

## 🚀 Cómo clonar y reproducir el proyecto

### Requisitos

- **Java JDK 8 o superior.**
- **MySQL 8.0.16 o superior** (con el servidor en ejecución).
- **MySQL Connector/J** (driver JDBC).
- Un IDE con soporte para Java (se desarrolló con IntelliJ IDEA).

### Paso 1: Clonar el repositorio

```bash
git clone https://github.com/<usuario>/<repositorio>.git
cd <repositorio>
```

### Paso 2: Crear la base de datos

Con la terminal (pide la contraseña de tu usuario de MySQL):

```bash
mysql -u root -p < sql/crear_tablas.sql
```

O desde MySQL Workbench: abrir `sql/crear_tablas.sql` y ejecutarlo completo.

Para comprobar que todo se creó bien, ejecutar `sql/consultas.sql`: deben aparecer 5 categorías, 5 usuarios, 5 libros y 6 préstamos.

### Paso 3: Configurar la conexión

Abrir `src/model/DatabaseConnection.java` y ajustar las constantes con los datos de tu MySQL:

```java
private static final String URL = "jdbc:mysql://localhost:3306/biblioteca_db?useSSL=false&allowPublicKeyRetrieval=true";
private static final String USER = "root";
private static final String PASSWORD = "tu_clave";
```

> **Nota:** No subas tu clave real a un repositorio público.

### Paso 4: Agregar el driver de MySQL

En IntelliJ IDEA: `File > Project Structure > Libraries > + > Java` y seleccionar el archivo `.jar` de **MySQL Connector/J**.

### Paso 5: Ejecutar

Ejecutar la clase `app.Main`. Se abre la ventana de **Login**. Si la conexión falla, la aplicación muestra el error y no se abre: revisar que MySQL esté encendido y que la clave sea correcta.

### Cómo probar que funciona

1. Ingresar con `admin@correo.com` / `123456`.
2. En **Prestamos**, crear un préstamo y comprobar que la ventana no se congela mientras guarda.
3. Ejecutar `sql/consultas.sql` y verificar que el stock del libro bajó en 1.
4. En **Prestamos**, los préstamos atrasados muestran la fecha de devolución en rojo.
5. En **Estudiantes > Ver historial**, comprobar la columna *Atrasado* y el contador de préstamos atrasados.
6. Para reiniciar los datos, volver a ejecutar `sql/crear_tablas.sql`.

## ▶️ Funcionamiento

Al ejecutar `Main.java` se realizan las siguientes acciones en orden:

1. `Main` abre la conexión con `DatabaseConnection.getInstance()`. Si falla, muestra el error y termina.
2. Se registra un `ShutdownHook` que cierra la conexión al terminar el programa.
3. Se crean los gestores (usuarios, libros y préstamos).
4. `SwingUtilities.invokeLater()` crea y muestra la `VentanaLogin`.
5. Al ingresar un correo y contraseña válidos, el sistema abre la `VentanaMenuPrincipal` según el rol del usuario. Si los datos son incorrectos, se muestra un error y se limpia la contraseña.
6. La ventana principal alterna los paneles con `CardLayout`. Siempre abre en **Inicio** (perfil del usuario). Los paneles se recargan cada vez que se visitan.
7. Cada formulario valida los datos y llama al gestor correspondiente. Si el gestor lanza un error, se muestra el mensaje y el formulario permanece abierto para corregirlo.

**Reglas de negocio de los préstamos:**

| Acción | Efecto sobre el stock | Reglas |
|--------|-----------------------|--------|
| Crear préstamo | Baja 1 | El libro debe tener stock disponible y el estudiante no puede tener ese mismo libro en préstamo activo. Se ejecuta en segundo plano. |
| Registrar devolución | Sube 1 | Solo si el préstamo sigue activo. |
| Eliminar préstamo activo | Sube 1 | Se repone el stock al deshacer el préstamo. |
| Eliminar préstamo devuelto | Sin cambios | - |
| Editar préstamo | Sin cambios | Solo se modifica la fecha de devolución, y solo si el préstamo no fue devuelto. |

**Atrasos:** un préstamo está atrasado si **no fue devuelto** y su fecha de devolución es **anterior a hoy**. Se marca con la fecha en rojo en la tabla de préstamos y con una columna *Atrasado* (con los días de atraso) en el historial del estudiante, junto con el total de atrasos.

**Permisos por rol:**

| Sección | Bibliotecario (ADMIN) | Estudiante |
|---------|:---------------------:|:----------:|
| Inicio (perfil y cambio de contraseña) | ✔ | ✔ |
| Libros: consultar y buscar | ✔ | ✔ |
| Libros: crear, editar y eliminar | ✔ | |
| Estudiantes (usuarios) | ✔ | |
| Préstamos: consultar y buscar | Todos | Solo los propios |
| Préstamos: crear, editar, eliminar y devolver | ✔ | |

## 📖 Manual de usuario

### Inicio de sesión

Ingrese el correo y la contraseña de un usuario (por ejemplo `admin@correo.com` / `123456` para el bibliotecario, o `ana@correo.com` / `123456` para un estudiante) y presione **Ingresar**. El botón **Salir** cierra la aplicación.

### Ventana principal

Al iniciar sesión aparece la ventana **Biblioteca Escolar - Sistema de Gestion**, con un menú lateral y el panel de Inicio abierto:

| Botón | Qué hace |
|-------|----------|
| Inicio | Muestra los datos del usuario que inició sesión. |
| Libros | Abre el catálogo de libros. |
| Estudiantes | Abre la gestión de usuarios (solo el bibliotecario). |
| Prestamos | Abre la gestión de préstamos (el estudiante ve solo los suyos). |
| Cerrar Sesion | Pide confirmación y vuelve al login. |

Los botones **Editar**, **Eliminar**, **Ver historial** y **Registrar devolucion** permanecen deshabilitados hasta que se selecciona una fila. El botón **Actualizar** vuelve a cargar la tabla.

**Reglas de los campos** (se validan antes de guardar):

- **Nombre:** entre 2 y 50 caracteres; solo letras (con tildes y ñ), espacios, apóstrofes y guiones.
- **Correo:** formato válido (`usuario@dominio.com`) y no repetido.
- **RUT:** formato `12345678-5`, con o sin puntos, y no repetido.
- **Contraseña:** mínimo 6 caracteres.
- **Curso:** obligatorio para estudiantes.
- **ISBN:** 10 o 13 dígitos (se aceptan guiones) y no repetido.
- **Título, Autor y Editorial:** obligatorios.
- **Categoría:** debe seleccionarse una.
- **Stock:** número entero igual o mayor que 0.
- **Fecha de devolución:** formato `AAAA-MM-DD`, no anterior a la fecha del préstamo.

### 1. Inicio y perfil

Muestra el ID, nombre, RUT, correo, rol y (si es estudiante) el curso del usuario.

- **Cambiar Contraseña:** pide la contraseña antigua, la nueva y su confirmación.
- **Editar datos:** funcionalidad pendiente; muestra el aviso *Próximamente*.

### 2. Gestionar libros

1. Presione **Libros** en el menú lateral.
2. Use **Buscar** para filtrar en vivo por ISBN, título, autor, editorial o categoría.
3. Use **Ver** para ordenar el catálogo: *Todos*, *Mas prestados* o *Menos prestados* (reporte de libros más prestados).
4. Solo el bibliotecario ve los botones de gestión:
   - **Nuevo:** ingrese ISBN, título, autor, editorial, categoría y stock.
   - **Editar:** seleccione la fila. El ISBN no se puede modificar.
   - **Eliminar:** pide confirmación. No se puede eliminar un libro con préstamos activos.

### 3. Gestionar estudiantes (solo bibliotecario)

1. Presione **Estudiantes** en el menú lateral. La tabla lista todos los usuarios con su rol y curso.
2. **Nuevo:** elija el rol (Estudiante por defecto) e ingrese nombre, RUT, correo, contraseña y curso. El curso solo aplica a estudiantes.
3. **Editar:** seleccione la fila. El ID y el rol no se pueden modificar.
4. **Ver historial:** abre una ventana con todos los préstamos del usuario seleccionado, la columna *Atrasado* y el total de atrasos (reporte de historial por estudiante).
5. **Eliminar:** pide confirmación. No se puede eliminar la cuenta con la que se inició sesión ni un usuario con préstamos activos.

### 4. Gestionar préstamos

1. Presione **Prestamos** en el menú lateral. El bibliotecario ve todos los préstamos; el estudiante, solo los suyos.
2. Use **Buscar** para filtrar por nombre del estudiante o título del libro, y **Ver** para mostrar *Todos* o solo los *En prestamo actualmente* (reporte de libros actualmente en préstamo).
3. Las fechas de devolución de los préstamos atrasados se muestran en rojo.
4. El bibliotecario cuenta además con estos botones:
   - **Nuevo:** elija estudiante y libro. La fecha de préstamo es la de hoy y la de devolución sugerida es a 7 días (editable). El préstamo se guarda en segundo plano. Requiere que exista al menos un estudiante y un libro.
   - **Editar:** permite cambiar la fecha de devolución de un préstamo activo.
   - **Registrar devolucion:** disponible solo para préstamos activos; repone el stock del libro.
   - **Eliminar:** pide confirmación; si el préstamo estaba activo, repone el stock.

## ⚠️ Limitaciones

- Las contraseñas se almacenan en texto plano.
- El RUT se valida por formato, no por dígito verificador.
- Los RUT de los datos de ejemplo tienen 9 dígitos antes del guion, pero el validador acepta 7 u 8. Si se edita un usuario de ejemplo, hay que ingresar un RUT con formato válido (por ejemplo `12345678-5`).
- La base de datos no guarda la fecha real de devolución, así que un préstamo devuelto tarde deja de contarse como atrasado una vez devuelto.
- La edición de datos desde el perfil aún no está implementada.
- La clave de la base de datos está escrita en `DatabaseConnection.java`.
