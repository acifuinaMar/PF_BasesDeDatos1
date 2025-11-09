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
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "PROYECTOFINAL";
    private static final String PASSWORD = "PROYECTOFINAL";
    
    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error cargando driver Oracle", e);
        }
    }
    
    // Constructor privado para prevenir instanciación externa
    private Conexion() {
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
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}