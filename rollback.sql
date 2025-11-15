use tpi_prog2_pedido_envio
go

SELECT * FROM pedido WHERE numero LIKE 'PED-ROLL-%';
SELECT * FROM envio  WHERE tracking LIKE 'TRK-ROLL-%';
