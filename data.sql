-- Usar la base de datos
USE tpi_prog2_pedido_envio;
GO

-- Insertar datos en la tabla de envios
-- Esta sintaxis es compatible con SQL Server
INSERT INTO envio (tracking, empresa, tipo, costo, fechaDespacho, fechaEstimada, estado)
VALUES
  ('TRK-0001', 'ANDREANI', 'ESTANDAR', 1500.00, '2025-11-10', '2025-11-15', 'EN_PREPARACION'),
  ('TRK-0002', 'OCA', 'EXPRES', 3200.00, '2025-11-11', '2025-11-13', 'EN_TRANSITO');
GO

-- Insertar datos en la tabla de pedidos
-- Esto funciona porque la tabla 'envio' usa IDENTITY(1,1),
-- por lo que el primer envío ('TRK-0001') tendrá id = 1.
INSERT INTO pedido (numero, fecha, clienteNombre, total, estado, id_envio)
VALUES
  ('PED-1001', '2025-11-10', 'Cliente Uno', 25000.00, 'NUEVO', 1);
GO

-- Nota: el PED-1002 se crea luego desde la app para probar transacciones
