package main;

import config.DatabaseConnection;

import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Probando conexion a SQL Server...");
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Conexion OK! Base: " + conn.getCatalog());
            } else {
                System.out.println("⚠ Conexion nula o cerrada");
            }
        } catch (Exception e) {
            System.out.println("❌ Error al conectar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
