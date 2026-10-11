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
    ('1111111111', 'Rayuela',  'Cortazar', 'Editorial A', 1, 5),
    ('2222222222', 'Quijote',  'Cervantes', 'Editorial B', 2, 5),
    ('3333333333', 'Dracula',  'Stoker',   'Editorial C', 3, 5),
    ('4444444444', 'Cosmos',   'Sagan',    'Editorial D', 4, 5),
    ('5555555555', 'Matilda',  'Dahl',     'Editorial E', 5, 5);


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


-- Consulta de tablas
SELECT * FROM categoria;
SELECT * FROM usuario;
SELECT * FROM libro;
SELECT * FROM prestamo;