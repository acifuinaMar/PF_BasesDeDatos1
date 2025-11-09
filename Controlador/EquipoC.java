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
import Modelo.Equipo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoC {
    
    public List<Equipo> obtenerTodosEquipos() {
        List<Equipo> equipos = new ArrayList<>();
        String sql = "SELECT e.*, p.nombre1 || ' ' || p.apellido1 as nombre_presidente FROM equipo e LEFT JOIN presidente p ON e.id_presidente = p.id_presidente ORDER BY e.nombre";
        
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Equipo equipo = mapearEquipoDesdeResultSet(rs);
                equipos.add(equipo);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener equipos: " + e.getMessage());
        }
        return equipos;
    }
    
    public boolean agregarEquipo(Equipo equipo) {
        if (!validarDatosEquipo(equipo)) {
            return false;
        }
        
        String sql = "INSERT INTO equipo (id_equipo, nombre, estadio, aforo, fundacion, departamento, id_presidente) VALUES (seq_equipo.NEXTVAL, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id_equipo"})) {
            
            pstmt.setString(1, equipo.getNombre());
            pstmt.setString(2, equipo.getEstadio());
            pstmt.setInt(3, equipo.getAforo());
            pstmt.setInt(4, equipo.getFundacion());
            pstmt.setString(5, equipo.getDepartamento());
            pstmt.setInt(6, equipo.getIdPresidente());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        equipo.setIdEquipo(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar equipo: " + e.getMessage());
        }
        return false;
    }
    
    public List<Equipo> obtenerEquiposDisponibles() {
        List<Equipo> equipos = new ArrayList<>();
        String sql = "SELECT id_equipo, nombre FROM equipo ORDER BY nombre";
        
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Equipo equipo = new Equipo();
                equipo.setIdEquipo(rs.getInt("id_equipo"));
                equipo.setNombre(rs.getString("nombre"));
                equipos.add(equipo);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener equipos disponibles: " + e.getMessage());
        }
        return equipos;
    }
    
    // Métodos auxiliares privados
    private Equipo mapearEquipoDesdeResultSet(ResultSet rs) throws SQLException {
        Equipo equipo = new Equipo();
        equipo.setIdEquipo(rs.getInt("id_equipo"));
        equipo.setNombre(rs.getString("nombre"));
        equipo.setEstadio(rs.getString("estadio"));
        equipo.setAforo(rs.getInt("aforo"));
        equipo.setFundacion(rs.getInt("fundacion"));
        equipo.setDepartamento(rs.getString("departamento"));
        equipo.setIdPresidente(rs.getInt("id_presidente"));
        equipo.setNombrePresidente(rs.getString("nombre_presidente"));
        return equipo;
    }
    
    private boolean validarDatosEquipo(Equipo equipo) {
        if (equipo.getNombre() == null || equipo.getNombre().trim().isEmpty()) {
            System.err.println("Error: El nombre del equipo es obligatorio");
            return false;
        }
        
        if (equipo.getEstadio() == null || equipo.getEstadio().trim().isEmpty()) {
            System.err.println("Error: El nombre del estadio es obligatorio");
            return false;
        }
        
        if (equipo.getAforo() <= 0) {
            System.err.println("Error: El aforo debe ser mayor a 0");
            return false;
        }
        
        if (equipo.getFundacion() <= 0) {
            System.err.println("Error: El año de fundación debe ser válido");
            return false;
        }
        
        return true;
    }
}
