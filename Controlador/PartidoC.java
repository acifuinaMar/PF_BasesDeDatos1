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
import Modelo.Partido;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PartidoC{
    
    public List<Partido> obtenerTodosPartidos() {
        List<Partido> partidos = new ArrayList<>();
        String sql = "SELECT p.*, ec.nombre as equipo_casa, ef.nombre as equipo_fuera FROM partido p JOIN equipo ec ON p.id_equipo_casa = ec.id_equipo " +
                    "JOIN equipo ef ON p.id_equipo_fuera = ef.id_equipo ORDER BY p.fecha DESC";
        
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Partido partido = mapearPartidoDesdeResultSet(rs);
                partidos.add(partido);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener partidos: " + e.getMessage());
        }
        return partidos;
    }
    
    public boolean agregarPartido(Partido partido) {
        if (!validarDatosPartido(partido)) {
            return false;
        }
        
        String sql = "INSERT INTO partido (fecha, goles_casa, goles_fuera, id_equipo_casa, id_equipo_fuera) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id_partido"})) {
            
            pstmt.setDate(1, new java.sql.Date(partido.getFecha().getTime()));
            pstmt.setInt(2, partido.getGolesCasa());
            pstmt.setInt(3, partido.getGolesFuera());
            pstmt.setInt(4, partido.getIdEquipoCasa());
            pstmt.setInt(5, partido.getIdEquipoFuera());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        partido.setIdPartido(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar partido: " + e.getMessage());
        }
        return false;
    }
    
    public Partido obtenerPartidoPorId(int id) {
        Partido partido = null;
        String sql = """
            SELECT p.*, 
                   ec.nombre as equipo_casa_nombre,
                   ef.nombre as equipo_fuera_nombre
            FROM partido p
            LEFT JOIN equipo ec ON p.id_equipo_casa = ec.id_equipo
            LEFT JOIN equipo ef ON p.id_equipo_fuera = ef.id_equipo
            WHERE p.id_partido = ?
            """;

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    partido = mapearPartidoDesdeResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener partido: " + e.getMessage());
            e.printStackTrace();
        }
        return partido;
    }

    public boolean actualizarPartido(Partido partido) {
        if (!validarDatosPartido(partido)) {
            return false;
        }

        String sql = """
            UPDATE partido 
            SET fecha=?, goles_casa=?, goles_fuera=?, id_equipo_casa=?, id_equipo_fuera=?
            WHERE id_partido=?
            """;

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, new java.sql.Timestamp(partido.getFecha().getTime()));
            pstmt.setInt(2, partido.getGolesCasa());
            pstmt.setInt(3, partido.getGolesFuera());
            pstmt.setInt(4, partido.getIdEquipoCasa());
            pstmt.setInt(5, partido.getIdEquipoFuera());
            pstmt.setInt(6, partido.getIdPartido());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar partido: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminarPartido(int id) {
        String sql = "DELETE FROM partido WHERE id_partido = ?";

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar partido: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Partido> buscarPartidosPorEquipo(String nombreEquipo) {
        List<Partido> partidos = new ArrayList<>();
        String sql = "SELECT p.id_partido, p.fecha, p.goles_casa, p.goles_fuera, " +
                 "p.id_equipo_casa, p.id_equipo_fuera, " +
                 "ec.nombre as equipo_casa, " +
                 "ef.nombre as equipo_fuera " +
                 "FROM partido p " +
                 "JOIN equipo ec ON p.id_equipo_casa = ec.id_equipo " +
                 "JOIN equipo ef ON p.id_equipo_fuera = ef.id_equipo " +
                 "WHERE UPPER(ec.nombre) LIKE UPPER(?) OR UPPER(ef.nombre) LIKE UPPER(?) " +
                 "ORDER BY p.fecha DESC";

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + nombreEquipo + "%");
            pstmt.setString(2, "%" + nombreEquipo + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Partido partido = mapearPartidoDesdeResultSet(rs);
                    partidos.add(partido);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar partidos: " + e.getMessage());
            e.printStackTrace();
        }
        return partidos;
    }

    public List<Partido> buscarPartidosPorFecha(Date fecha) {
        List<Partido> partidos = new ArrayList<>();
        
        String sql = "SELECT p.id_partido, p.fecha, p.goles_casa, p.goles_fuera, " +
                 "p.id_equipo_casa, p.id_equipo_fuera, " +
                 "ec.nombre as equipo_casa, " +
                 "ef.nombre as equipo_fuera " +
                 "FROM partido p " +
                 "JOIN equipo ec ON p.id_equipo_casa = ec.id_equipo " +
                 "JOIN equipo ef ON p.id_equipo_fuera = ef.id_equipo " +
                 "WHERE TRUNC(p.fecha) = TRUNC(?) " +
                 "ORDER BY p.fecha DESC";

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, new java.sql.Date(fecha.getTime()));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Partido partido = mapearPartidoDesdeResultSet(rs);
                    partidos.add(partido);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar partidos por fecha: " + e.getMessage());
            e.printStackTrace();
        }
        return partidos;
    }
    
    // Métodos auxiliares privados
    private Partido mapearPartidoDesdeResultSet(ResultSet rs) throws SQLException {
        Partido partido = new Partido();
        partido.setIdPartido(rs.getInt("id_partido"));
        partido.setFecha(rs.getDate("fecha"));
        partido.setGolesCasa(rs.getInt("goles_casa"));
        partido.setGolesFuera(rs.getInt("goles_fuera"));
        partido.setIdEquipoCasa(rs.getInt("id_equipo_casa"));
        partido.setIdEquipoFuera(rs.getInt("id_equipo_fuera"));
        partido.setNombreEquipoCasa(rs.getString("equipo_casa"));
        partido.setNombreEquipoFuera(rs.getString("equipo_fuera"));
        return partido;
    }
    
    private boolean validarDatosPartido(Partido partido) {
        if (partido.getIdEquipoCasa() == partido.getIdEquipoFuera()) {
            System.err.println("Error: Un equipo no puede jugar contra sí mismo");
            return false;
        }
        
        if (partido.getGolesCasa() < 0 || partido.getGolesFuera() < 0) {
            System.err.println("Error: Los goles no pueden ser negativos");
            return false;
        }
        
        return true;
    }

    public List<Partido> obtenerPartidosParaCombo() {
        List<Partido> partidos = new ArrayList<>();
        String sql = """
            SELECT p.id_partido, p.fecha, 
                   ec.nombre as equipo_casa, 
                   ef.nombre as equipo_fuera
            FROM partido p
            JOIN equipo ec ON p.id_equipo_casa = ec.id_equipo
            JOIN equipo ef ON p.id_equipo_fuera = ef.id_equipo
            ORDER BY p.fecha DESC
            """;

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Partido partido = new Partido();
                partido.setIdPartido(rs.getInt("id_partido"));
                partido.setFecha(rs.getTimestamp("fecha"));
                partido.setNombreEquipoCasa(rs.getString("equipo_casa"));
                partido.setNombreEquipoFuera(rs.getString("equipo_fuera"));
                partidos.add(partido);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener partidos para combo: " + e.getMessage());
        }
        return partidos;
    }
}
