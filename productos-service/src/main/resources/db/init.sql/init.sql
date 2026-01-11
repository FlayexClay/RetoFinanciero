CREATE TABLE IF NOT EXISTS productos_financieros (
    id BIGSERIAL PRIMARY KEY,
    codigo_cliente VARCHAR(50) NOT NULL,
    tipo_producto VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    numero_producto VARCHAR(50) NOT NULL UNIQUE,
    saldo DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    moneda VARCHAR(3) NOT NULL DEFAULT 'PEN',
    fecha_apertura TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT true
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_productos_codigo_cliente ON productos_financieros(codigo_cliente);
CREATE INDEX IF NOT EXISTS idx_productos_tipo ON productos_financieros(tipo_producto);
CREATE INDEX IF NOT EXISTS idx_productos_activo ON productos_financieros(activo);
CREATE INDEX IF NOT EXISTS idx_productos_cliente_activo ON productos_financieros(codigo_cliente, activo);

-- Insertar datos de prueba para CLI001
INSERT INTO productos_financieros
(codigo_cliente, tipo_producto, nombre, numero_producto, saldo, moneda, fecha_apertura, activo)
VALUES
    ('CLI001', 'CUENTA_AHORRO', 'Cuenta Ahorro Premium', 'CA-0001-123456', 15000.50, 'PEN', CURRENT_TIMESTAMP, true),
    ('CLI001', 'TARJETA_CREDITO', 'Visa Platinum', 'TC-4532-****-1234', -2500.00, 'USD', CURRENT_TIMESTAMP, true),
    ('CLI001', 'CUENTA_CORRIENTE', 'Cuenta Corriente Empresarial', 'CC-0001-789012', 8750.25, 'PEN', CURRENT_TIMESTAMP, true),
    ('CLI001', 'PRESTAMO', 'Préstamo Personal', 'PR-2023-001234', -25000.00, 'PEN', CURRENT_TIMESTAMP, true),

    -- Productos para CLI002
    ('CLI002', 'CUENTA_AHORRO', 'Cuenta Ahorro Joven', 'CA-0002-234567', 5500.75, 'PEN', CURRENT_TIMESTAMP, true),
    ('CLI002', 'TARJETA_DEBITO', 'Mastercard Débito', 'TD-5421-****-5678', 5500.75, 'PEN', CURRENT_TIMESTAMP, true),
    ('CLI002', 'INVERSION', 'Fondo Mutuo Agresivo', 'IN-2024-001', 12000.00, 'USD', CURRENT_TIMESTAMP, true),

    -- Productos para CLI003
    ('CLI003', 'CUENTA_AHORRO', 'Cuenta Ahorro Digital', 'CA-0003-345678', 3200.00, 'PEN', CURRENT_TIMESTAMP, true),
    ('CLI003', 'TARJETA_CREDITO', 'Visa Gold', 'TC-4532-****-9012', -1200.50, 'PEN', CURRENT_TIMESTAMP, true),
    ('CLI003', 'SEGURO', 'Seguro de Vida', 'SG-2024-001', 0.00, 'PEN', CURRENT_TIMESTAMP, true),

    -- Productos para CLI004
    ('CLI004', 'CUENTA_CORRIENTE', 'Cuenta Corriente Premium', 'CC-0004-456789', 45000.00, 'USD', CURRENT_TIMESTAMP, true),
    ('CLI004', 'TARJETA_CREDITO', 'American Express Gold', 'TC-3782-****-3456', -8500.00, 'USD', CURRENT_TIMESTAMP, true),
    ('CLI004', 'INVERSION', 'Depósito a Plazo Fijo', 'IN-2024-002', 50000.00, 'USD', CURRENT_TIMESTAMP, true),
    ('CLI004', 'PRESTAMO', 'Préstamo Hipotecario', 'PR-2023-002345', -180000.00, 'USD', CURRENT_TIMESTAMP, true),

    -- Productos para CLI005
    ('CLI005', 'CUENTA_AHORRO', 'Cuenta Ahorro Simple', 'CA-0005-567890', 1850.30, 'PEN', CURRENT_TIMESTAMP, true),
    ('CLI005', 'TARJETA_DEBITO', 'Visa Débito Clásica', 'TD-4532-****-7890', 1850.30, 'PEN', CURRENT_TIMESTAMP, true)
ON CONFLICT (numero_producto) DO NOTHING;

-- Verificar inserción
SELECT
    codigo_cliente,
    COUNT(*) as total_productos,
    SUM(CASE WHEN tipo_producto = 'CUENTA_AHORRO' THEN 1 ELSE 0 END) as cuentas_ahorro,
    SUM(CASE WHEN tipo_producto = 'TARJETA_CREDITO' THEN 1 ELSE 0 END) as tarjetas_credito
FROM productos_financieros
GROUP BY codigo_cliente
ORDER BY codigo_cliente;