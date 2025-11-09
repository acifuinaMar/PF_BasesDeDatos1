/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author maryori
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static Conexion instance;
    private Connection connection;
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:umg";
    private static final String USER = "system";
    private static final String PASSWORD = "Umg$2025";
    
    // Constructor privado para prevenir instanciación externa
    private Conexion() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión establecida con la base de datos");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }
    
    // Método estático para obtener la instancia única
    public static Conexion getInstance() {
        if (instance == null) {
            synchronized (Conexion.class) {
                if (instance == null) {
                    instance = new Conexion();
                }
            }
        }
        return instance;
    }
    
    // Método para obtener la conexión
    public Connection getConnection() {
        try {
            if (this.connection == null || this.connection.isClosed()) {
                // Reconectar si la conexión está cerrada
                this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener conexión: " + e.getMessage());
        }
        return this.connection;
    }
    
    // Método para cerrar la conexión
    public void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
                System.out.println("Conexión cerrada");
            } catch (SQLException e) {
                System.err.println("Error cerrando conexión: " + e.getMessage());
            }
        }
    }
}