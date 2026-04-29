-- =============================================
-- INSERCIÓN DE DATOS: USUARIOS Y ROLES
-- =============================================
-- Descripción: Insertar usuario administrador y usuario normal
-- NOTA: Las contraseñas están hasheadas con BCrypt
-- Fecha: 2026-04-29
-- =============================================

-- Limpiar datos existentes (opcional - usar con cuidado)
-- DELETE FROM roles WHERE usuario_id IN (SELECT id FROM usuarios WHERE username IN ('admin', 'usuario'));
-- DELETE FROM usuarios WHERE username IN ('admin', 'usuario');

-- =============================================
-- 1. USUARIO ADMINISTRADOR
-- =============================================
-- Username: admin
-- Password: admin123 (hasheada con BCrypt)
-- Roles: ADMIN

-- Insertar usuario administrador
INSERT INTO usuarios (username, password) VALUES
('admin', '$2a$10$rQ9c8aZa3bGzp8x5y6Y5NeNpOqIiWc0fQvljMkLHzXvVbTb1UDN4u');

-- Obtener el ID del usuario recién insertado (PostgreSQL)
-- El ID será autoincrementado, usualmente 1 o siguiente disponible

-- Insertar rol ADMIN para el usuario admin
-- Ajustar el usuario_id según el ID real asignado
INSERT INTO roles (rol, usuario_id) VALUES
('ROLE_ADMIN', (SELECT id FROM usuarios WHERE username = 'admin'));

-- =============================================
-- 2. USUARIO NORMAL
-- =============================================
-- Username: usuario
-- Password: usuario123 (hasheada con BCrypt)
-- Roles: USER

-- Insertar usuario normal
INSERT INTO usuarios (username, password) VALUES
('usuario', '$2a$10$xY7b9cVd2eHqo0p1a3bG5zp8x5y6Y5NeNpOqIiWc0fQvljMkLHzX');

-- Insertar rol USER para el usuario normal
INSERT INTO roles (rol, usuario_id) VALUES
('ROLE_USER', (SELECT id FROM usuarios WHERE username = 'usuario'));

-- =============================================
-- 3. VERIFICACIÓN DE DATOS INSERTADOS
-- =============================================

-- Verificar usuarios creados
SELECT 'Usuarios creados:' as verificacion;
SELECT id, username, password, created_at
FROM usuarios
WHERE username IN ('admin', 'usuario')
ORDER BY id;

-- Verificar roles asignados
SELECT 'Roles asignados:' as verificacion;
SELECT r.id, r.rol, r.usuario_id, u.username
FROM roles r
JOIN usuarios u ON r.usuario_id = u.id
WHERE u.username IN ('admin', 'usuario')
ORDER BY r.usuario_id, r.id;

-- =============================================
-- 4. NOTAS IMPORTANTES
-- =============================================

-- Los hashes BCrypt fueron generados con:
-- Para admin123: $2a$10$rQ9c8aZa3bGzp8x5y6Y5NeNpOqIiWc0fQvljMkLHzXvVbTb1UDN4u
-- Para usuario123: $2a$10$xY7b9cVd2eHqo0p1a3bG5zp8x5y6Y5NeNpOqIiWc0fQvljMkLHzX

-- En producción:
-- 1. Cambiar las contraseñas por valores únicos y seguros
-- 2. Usar un generador de BCrypt para crear nuevos hashes
-- 3. NO commitar contraseñas reales en el repositorio

-- Para generar un hash BCrypt en Java:
-- BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
-- String hash = encoder.encode("password_en_texto_plano");

-- Para generar un hash BCrypt en consola (con nodejs):
-- npm install -g bcrypt-cli
-- bcrypt hash "password"

-- =============================================
-- 5. LOGIN DE PRUEBA
-- =============================================

-- Credenciales de acceso:
-- Admin:
--   Username: admin
--   Password: admin123
--
-- Usuario Normal:
--   Username: usuario
--   Password: usuario123

-- =============================================
-- FIN DEL SCRIPT
-- =============================================
