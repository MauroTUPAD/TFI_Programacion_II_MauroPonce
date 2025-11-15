-- 1) Crear login a nivel servidor
USE master;
GO

CREATE LOGIN tpi_user
WITH PASSWORD = 'MiClaveSegura123',
     CHECK_POLICY = OFF;  -- para que no moleste con complejidad de clave
GO

-- 2) Ir a tu base del TPI
USE TPI_Prog2_PedidoEnvio;
GO

-- 3) Crear el usuario atado al login
CREATE USER tpi_user FOR LOGIN tpi_user;
GO

-- 4) Darle permisos (db_owner es cómodo para desarrollo)
ALTER ROLE db_owner ADD MEMBER tpi_user;
GO
