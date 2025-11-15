USE TPI_Prog2_PedidoEnvio;
SELECT id, numero, eliminado FROM Pedido;

/**********************************************/
USE TPI_Prog2_PedidoEnvio;

SELECT * FROM Envio WHERE tracking LIKE 'TRK-ROLL-%';
SELECT * FROM Pedido WHERE numero LIKE 'PED-ROLL-%';
