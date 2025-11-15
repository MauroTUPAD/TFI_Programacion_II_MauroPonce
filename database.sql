DROP DATABASE IF EXISTS TPI_Prog2_PedidoEnvio;
GO

CREATE DATABASE TPI_Prog2_PedidoEnvio;
GO

USE TPI_Prog2_PedidoEnvio;
GO

-- Tabla Envio (B)
CREATE TABLE Envio (
    id INT IDENTITY(1,1) PRIMARY KEY,
    eliminado BIT NOT NULL DEFAULT 0,
    tracking VARCHAR(40) UNIQUE,
    empresa VARCHAR(40),
    tipo VARCHAR(40),
    costo DECIMAL(10,2),
    fechaDespacho DATE,
    fechaEstimada DATE,
    estado VARCHAR(40)
);

-- Tabla Pedido (A)
CREATE TABLE Pedido (
    id INT IDENTITY(1,1) PRIMARY KEY,
    eliminado BIT NOT NULL DEFAULT 0,
    numero VARCHAR(20) NOT NULL UNIQUE,
    fecha DATE NOT NULL,
    clienteNombre VARCHAR(120) NOT NULL,
    total DECIMAL(12,2) NOT NULL,
    estado VARCHAR(40) NOT NULL,
    id_envio INT UNIQUE,
    CONSTRAINT FK_Pedido_Envio FOREIGN KEY (id_envio) REFERENCES Envio(id)
);
