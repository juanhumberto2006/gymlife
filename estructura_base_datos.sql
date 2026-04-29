-- =============================================
-- BASE DE DATOS: gymlife
-- =============================================
-- Descripción: Sistema de gestión de gimnasio
-- Motor: PostgreSQL
-- Fecha: 2026-04-29
-- =============================================

-- Eliminar tablas si existen (orden correcto por dependencias)
DROP TABLE IF EXISTS reservas CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS clases CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

-- =============================================
-- TABLA: usuarios
-- =============================================
-- Descripción: Almacena la información de los usuarios del sistema
-- Relaciones: Uno a muchos con roles, uno a muchos con reservas
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para usuarios
CREATE INDEX idx_usuarios_username ON usuarios(username);

-- =============================================
-- TABLA: roles
-- =============================================
-- Descripción: Roles asignados a cada usuario (ej: USER, ADMIN)
-- Relaciones: Pertenece a un usuario (muchos roles por usuario)
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    rol VARCHAR(50) NOT NULL,
    usuario_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Índices para roles
CREATE INDEX idx_roles_usuario_id ON roles(usuario_id);
CREATE INDEX idx_roles_rol ON roles(rol);

-- =============================================
-- TABLA: clases
-- =============================================
-- Descripción: Clases o sesiones fitness ofrecidas por el gimnasio
-- Relaciones: Muchas reservas pueden estar asociadas a una clase
CREATE TABLE clases (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT NOT NULL,
    capacidad INTEGER NOT NULL CHECK (capacidad > 0),
    fecha_hora TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para clases
CREATE INDEX idx_clases_fecha_hora ON clases(fecha_hora);
CREATE INDEX idx_clases_nombre ON clases(nombre);

-- =============================================
-- TABLA: reservas
-- =============================================
-- Descripción: Reservas de clases realizadas por los usuarios
-- Relaciones: Pertenece a un usuario y a una clase
CREATE TABLE reservas (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    clase_id BIGINT NOT NULL,
    creada_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (clase_id) REFERENCES clases(id) ON DELETE CASCADE,
    CONSTRAINT uk_reserva_usuario_clase UNIQUE (usuario_id, clase_id)
);

-- Índices para reservas
CREATE INDEX idx_reservas_usuario_id ON reservas(usuario_id);
CREATE INDEX idx_reservas_clase_id ON reservas(clase_id);
CREATE INDEX idx_reservas_creada_en ON reservas(creada_en);

-- =============================================
-- TRIGGERS PARA actualizar timestamps automáticamente
-- =============================================

-- Trigger para actualizar updated_at en usuarios
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_usuarios_updated_at
    BEFORE UPDATE ON usuarios
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_clases_updated_at
    BEFORE UPDATE ON clases
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =============================================
-- DATOS DE PRUEBA (OPCIONAL)
-- =============================================

-- Insertar usuario de prueba (username: admin, password: admin123)
-- NOTA: En producción las contraseñas deben estar encriptadas
INSERT INTO usuarios (username, password) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mr...'),
('usuario1', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mr...');

-- Insertar roles para los usuarios
INSERT INTO roles (rol, usuario_id) VALUES
('ADMIN', 1),
('USER', 2);

-- Insertar algunas clases de ejemplo
INSERT INTO clases (nombre, descripcion, capacidad, fecha_hora) VALUES
('Yoga Matutino', 'Clase de yoga para empezar el día con energía', 20, '2026-04-30 08:00:00'),
('CrossFit Intenso', 'Entrenamiento funcional de alta intensidad', 15, '2026-04-30 10:00:00'),
('Spinning Nocturno', 'Clase de ciclismo indoor con música electrónica', 25, '2026-04-30 18:00:00'),
('Pilates Reformador', 'Ejercicios de pilates con equipo especializado', 12, '2026-05-01 09:00:00');

-- Insertar algunas reservas de ejemplo
INSERT INTO reservas (usuario_id, clase_id) VALUES
(2, 1),
(2, 3);

-- =============================================
-- CONSULTAS ÚTILES
-- =============================================

-- Ver todos los usuarios con sus roles
-- SELECT u.id, u.username, r.rol
-- FROM usuarios u
-- JOIN roles r ON r.usuario_id = u.id
-- ORDER BY u.id, r.id;

-- Ver todas las clases con número de reservas
-- SELECT c.id, c.nombre, c.capacidad,
--        COUNT(r.id) as reservas_actuales
-- FROM clases c
-- LEFT JOIN reservas r ON r.clase_id = c.id
-- GROUP BY c.id
-- ORDER BY c.fecha_hora;

-- Ver reservas detalladas
-- SELECT r.id, u.username, c.nombre, c.fecha_hora, r.creada_en
-- FROM reservas r
-- JOIN usuarios u ON r.usuario_id = u.id
-- JOIN clases c ON r.clase_id = c.id
-- ORDER BY r.creada_en DESC;

-- =============================================
-- FIN DEL SCRIPT
-- =============================================
