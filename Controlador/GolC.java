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
import Modelo.Jugador;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GolC{
    
    public List<Gol> obtenerTodosGoles() {
        List<Gol> goles = new ArrayList<>();
        String sql = """
            SELECT g.*, 
                   j.nombre1 || ' ' || j.apellido1 as nombre_jugador,
                   e.nombre as nombre_equipo,
                   p.fecha as fecha_partido,
                   ec.nombre as equipo_local,
                   ef.nombre as equipo_visitante
            FROM gol g
            JOIN jugador j ON g.id_jugador = j.id_jugador
            JOIN partido p ON g.id_partido = p.id_partido
            JOIN equipo e ON j.id_equipo = e.id_equipo
            JOIN equipo ec ON p.id_equipo_casa = ec.id_equipo
            JOIN equipo ef ON p.id_equipo_fuera = ef.id_equipo
            ORDER BY p.fecha DESC, g.minuto ASC
            """;

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Gol gol = mapearGolDesdeResultSet(rs);
                goles.add(gol);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener goles: " + e.getMessage());
            e.printStackTrace();
        }
        return goles;
    }
    
    public Gol obtenerGolPorId(int id) {
        Gol gol = null;
        String sql = """
            SELECT g.*, 
                   j.nombre1 || ' ' || j.apellido1 as nombre_jugador,
                   e.nombre as nombre_equipo,
                   p.fecha as fecha_partido,
                   ec.nombre as equipo_local,
                   ef.nombre as equipo_visitante
            FROM gol g
            JOIN jugador j ON g.id_jugador = j.id_jugador
            JOIN partido p ON g.id_partido = p.id_partido
            JOIN equipo e ON j.id_equipo = e.id_equipo
            JOIN equipo ec ON p.id_equipo_casa = ec.id_equipo
            JOIN equipo ef ON p.id_equipo_fuera = ef.id_equipo
            WHERE g.id_gol = ?
            """;

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    gol = mapearGolDesdeResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener gol: " + e.getMessage());
            e.printStackTrace();
        }
        return gol;
    }
    
    public boolean agregarGol(Gol gol) {
        if (!validarDatosGol(gol)) {
            return false;
        }
        
        String sql = "INSERT INTO gol (minuto, descripcion, id_partido, id_jugador) VALUES (?, ?, ?, ?)";
        
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
    
    public boolean actualizarGol(Gol gol) {
        if (!validarDatosGol(gol)) {
            return false;
        }

        String sql = """
            UPDATE gol 
            SET minuto=?, descripcion=?, id_partido=?, id_jugador=?
            WHERE id_gol=?
            """;

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, gol.getMinuto());
            pstmt.setString(2, gol.getDescripcion());
            pstmt.setInt(3, gol.getIdPartido());
            pstmt.setInt(4, gol.getIdJugador());
            pstmt.setInt(5, gol.getIdGol());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar gol: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean eliminarGol(int id) {
        String sql = "DELETE FROM gol WHERE id_gol = ?";

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar gol: " + e.getMessage());
            e.printStackTrace();
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
    
    public List<Gol> buscarGolesPorJugador(String nombreJugador) {
        List<Gol> goles = new ArrayList<>();
        String sql = """
            SELECT g.*, 
                   j.nombre1 || ' ' || j.apellido1 as nombre_jugador,
                   e.nombre as nombre_equipo,
                   p.fecha as fecha_partido,
                   ec.nombre as equipo_local,
                   ef.nombre as equipo_visitante
            FROM gol g
            JOIN jugador j ON g.id_jugador = j.id_jugador
            JOIN partido p ON g.id_partido = p.id_partido
            JOIN equipo e ON j.id_equipo = e.id_equipo
            JOIN equipo ec ON p.id_equipo_casa = ec.id_equipo
            JOIN equipo ef ON p.id_equipo_fuera = ef.id_equipo
            WHERE UPPER(j.nombre1) LIKE UPPER(?) OR UPPER(j.apellido1) LIKE UPPER(?)
            ORDER BY p.fecha DESC, g.minuto ASC
            """;

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + nombreJugador + "%");
            pstmt.setString(2, "%" + nombreJugador + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Gol gol = mapearGolDesdeResultSet(rs);
                    goles.add(gol);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar goles por jugador: " + e.getMessage());
            e.printStackTrace();
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
