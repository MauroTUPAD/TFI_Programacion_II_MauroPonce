IF DB_ID('TPI_Prog2_PedidoEnvio') IS NULL
BEGIN
    PRINT 'BD NO EXISTE';
END
ELSE
BEGIN
    PRINT 'BD EXISTE';
END;
GO

USE TPI_Prog2_PedidoEnvio;
GO

SELECT TOP 1 * FROM Envio;
SELECT TOP 1 * FROM Pedido;
