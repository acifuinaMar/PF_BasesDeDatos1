/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

/**
 *
 * @author maryori
 */

import Modelo.Conexion;
import Modelo.Gol;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GolC{
    
    public List<Gol> obtenerTodosGoles() {
        List<Gol> goles = new ArrayList<>();
        String sql = "SELECT g.*, j.nombre1 || ' ' || j.apellido1 as nombre_jugador, e.nombre as nombre_equipo " +
                    "FROM gol g " +
                    "JOIN jugador j ON g.id_jugador = j.id_jugador " +
                    "JOIN equipo e ON j.id_equipo = e.id_equipo " +
                    "ORDER BY g.id_partido, g.minuto";
        
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Gol gol = mapearGolDesdeResultSet(rs);
                goles.add(gol);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener goles: " + e.getMessage());
        }
        return goles;
    }
    
    public boolean agregarGol(Gol gol) {
        if (!validarDatosGol(gol)) {
            return false;
        }
        
        String sql = "INSERT INTO gol (id_gol, minuto, descripcion, id_partido, id_jugador) VALUES (seq_gol.NEXTVAL, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id_gol"})) {
            
            pstmt.setInt(1, gol.getMinuto());
            pstmt.setString(2, gol.getDescripcion());
            pstmt.setInt(3, gol.getIdPartido());
            pstmt.setInt(4, gol.getIdJugador());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        gol.setIdGol(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar gol: " + e.getMessage());
        }
        return false;
    }
    
    public List<Gol> obtenerGolesPorPartido(int idPartido) {
        List<Gol> goles = new ArrayList<>();
        String sql = "SELECT g.*, j.nombre1 || ' ' || j.apellido1 as nombre_jugador, e.nombre as nombre_equipo FROM gol g " +
                    "JOIN jugador j ON g.id_jugador = j.id_jugador JOIN equipo e ON j.id_equipo = e.id_equipo " +
                    "WHERE g.id_partido = ? ORDER BY g.minuto";
        
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPartido);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Gol gol = mapearGolDesdeResultSet(rs);
                    goles.add(gol);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener goles por partido: " + e.getMessage());
        }
        return goles;
    }
    
    public List<Gol> obtenerGolesPorJugador(int idJugador) {
        List<Gol> goles = new ArrayList<>();
        String sql = "SELECT g.*, j.nombre1 || ' ' || j.apellido1 as nombre_jugador, e.nombre as nombre_equipo FROM gol g " +
                    "JOIN jugador j ON g.id_jugador = j.id_jugador JOIN equipo e ON j.id_equipo = e.id_equipo " +
                    "WHERE g.id_jugador = ? ORDER BY g.id_partido, g.minuto";
        
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idJugador);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Gol gol = mapearGolDesdeResultSet(rs);
                    goles.add(gol);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener goles por jugador: " + e.getMessage());
        }
        return goles;
    }
    
    // Métodos auxiliares privados
    private Gol mapearGolDesdeResultSet(ResultSet rs) throws SQLException {
        Gol gol = new Gol();
        gol.setIdGol(rs.getInt("id_gol"));
        gol.setMinuto(rs.getInt("minuto"));
        gol.setDescripcion(rs.getString("descripcion"));
        gol.setIdPartido(rs.getInt("id_partido"));
        gol.setIdJugador(rs.getInt("id_jugador"));
        gol.setNombreJugador(rs.getString("nombre_jugador"));
        gol.setNombreEquipo(rs.getString("nombre_equipo"));
        return gol;
    }
    
    private boolean validarDatosGol(Gol gol) {
        if (gol.getMinuto() < 1 || gol.getMinuto() > 120) {
            System.err.println("Error: El minuto debe estar entre 1 y 120");
            return false;
        }
        
        if (gol.getDescripcion() == null || gol.getDescripcion().trim().isEmpty()) {
            System.err.println("Error: La descripción del gol es obligatoria");
            return false;
        }
        
        return true;
    }
}
