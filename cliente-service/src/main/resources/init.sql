
-- ============================================
-- Script de Inicialización - Base de Datos Clientes
-- Sistema Financiero - Microservicios
-- ============================================

-- Eliminar tabla si existe (para desarrollo, comentar en producción)
-- DROP TABLE IF EXISTS clientes CASCADE;

-- Crear tabla de clientes
CREATE TABLE IF NOT EXISTS clientes (
    id BIGSERIAL PRIMARY KEY,
    codigo_unico VARCHAR(50) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    tipo_documento VARCHAR(20) NOT NULL,
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100),
    telefono VARCHAR(20),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT true,

    -- Constraints
    CONSTRAINT chk_tipo_documento CHECK (tipo_documento IN ('DNI', 'CE', 'PASAPORTE', 'RUC')),
    CONSTRAINT chk_numero_documento CHECK (LENGTH(numero_documento) >= 8)
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_clientes_codigo_unico ON clientes(codigo_unico) WHERE activo = true;
CREATE INDEX IF NOT EXISTS idx_clientes_numero_documento ON clientes(numero_documento);
CREATE INDEX IF NOT EXISTS idx_clientes_activo ON clientes(activo);
CREATE INDEX IF NOT EXISTS idx_clientes_tipo_documento ON clientes(tipo_documento);
CREATE INDEX IF NOT EXISTS idx_clientes_fecha_creacion ON clientes(fecha_creacion);

-- Comentarios en la tabla
COMMENT ON TABLE clientes IS 'Tabla principal de clientes del sistema financiero';
COMMENT ON COLUMN clientes.codigo_unico IS 'Código único del cliente, usado para consultas en la API';
COMMENT ON COLUMN clientes.tipo_documento IS 'Tipo de documento: DNI, CE, PASAPORTE, RUC';
COMMENT ON COLUMN clientes.activo IS 'Estado del cliente: true = activo, false = inactivo';

-- Insertar datos de prueba
INSERT INTO clientes (codigo_unico, nombres, apellidos, tipo_documento, numero_documento, email, telefono, fecha_creacion, activo)
VALUES
    ('CLI001', 'Juan Carlos', 'Pérez García', 'DNI', '12345678', 'juan.perez@email.com', '+51987654321', CURRENT_TIMESTAMP, true),
    ('CLI002', 'María Elena', 'Rodríguez López', 'DNI', '87654321', 'maria.rodriguez@email.com', '+51987654322', CURRENT_TIMESTAMP, true),
    ('CLI003', 'Pedro José', 'Sánchez Martínez', 'DNI', '11223344', 'pedro.sanchez@email.com', '+51987654323', CURRENT_TIMESTAMP, true),
    ('CLI004', 'Ana Lucía', 'Torres Fernández', 'CE', '99887766', 'ana.torres@email.com', '+51987654324', CURRENT_TIMESTAMP, true),
    ('CLI005', 'Roberto Carlos', 'Gómez Díaz', 'DNI', '55667788', 'roberto.gomez@email.com', '+51987654325', CURRENT_TIMESTAMP, true),
    ('CLI006', 'Carmen Rosa', 'Villanueva Castro', 'DNI', '22334455', 'carmen.villanueva@email.com', '+51987654326', CURRENT_TIMESTAMP, true),
    ('CLI007', 'Luis Alberto', 'Mendoza Ríos', 'PASAPORTE', 'P12345678', 'luis.mendoza@email.com', '+51987654327', CURRENT_TIMESTAMP, true),
    ('CLI008', 'Patricia Isabel', 'Huamán Flores', 'DNI', '33445566', 'patricia.huaman@email.com', '+51987654328', CURRENT_TIMESTAMP, false),
    ('CLI009', 'Jorge Enrique', 'Carrillo Morales', 'DNI', '44556677', 'jorge.carrillo@email.com', '+51987654329', CURRENT_TIMESTAMP, true),
    ('CLI010', 'Silvia Beatriz', 'Ramírez Gutiérrez', 'CE', '88776655', 'silvia.ramirez@email.com', '+51987654330', CURRENT_TIMESTAMP, true)
ON CONFLICT (codigo_unico) DO NOTHING;

-- Verificar inserción
DO $
DECLARE
    total_count INTEGER;
    activos_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO total_count FROM clientes;
    SELECT COUNT(*) INTO activos_count FROM clientes WHERE activo = true;

    RAISE NOTICE '============================================';
    RAISE NOTICE 'Base de Datos Clientes - Inicialización Completa';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Total de clientes insertados: %', total_count;
    RAISE NOTICE 'Clientes activos: %', activos_count;
    RAISE NOTICE 'Clientes inactivos: %', (total_count - activos_count);
    RAISE NOTICE '============================================';
END $;

-- Query de verificación (para logs)
SELECT
    codigo_unico,
    CONCAT(nombres, ' ', apellidos) as nombre_completo,
    tipo_documento,
    numero_documento,
    activo,
    fecha_creacion
FROM clientes
ORDER BY codigo_unico;

-- Estadísticas finales
SELECT
    'CLIENTES' as tabla,
    COUNT(*) as total_registros,
    COUNT(*) FILTER (WHERE activo = true) as activos,
    COUNT(*) FILTER (WHERE activo = false) as inactivos,
    COUNT(DISTINCT tipo_documento) as tipos_documento
FROM clientes;