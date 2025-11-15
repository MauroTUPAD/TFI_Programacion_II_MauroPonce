package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
        "jdbc:sqlserver://localhost:1433;" +
        "databaseName=TPI_Prog2_PedidoEnvio;" +
        "encrypt=false;";

    // USUARIO NUEVO DEL TPI
    private static final String USER = "tpi_user";
    private static final String PASS = "MiClaveSegura123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
