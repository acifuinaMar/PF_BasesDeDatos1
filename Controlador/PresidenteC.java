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
import Modelo.Presidente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PresidenteC {
    
    public List<Presidente> obtenerTodosPresidentes() {
        List<Presidente> presidentes = new ArrayList<>();
        String sql = "SELECT p.* FROM presidente p ORDER BY p.apellido1, p.nombre1";
        
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Presidente presidente = mapearPresidenteDesdeResultSet(rs);
                presidente.setCorreos(obtenerCorreosPresidente(conn, presidente.getIdPresidente()));
                presidentes.add(presidente);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener presidentes: " + e.getMessage());
        }
        return presidentes;
    }
    
    public Presidente obtenerPresidentePorId(int id) {
        Presidente presidente = null;
        String sql = "SELECT * FROM presidente WHERE id_presidente = ?";
        
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    presidente = mapearPresidenteDesdeResultSet(rs);
                    presidente.setCorreos(obtenerCorreosPresidente(conn, id));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener presidente: " + e.getMessage());
        }
        return presidente;
    }
    
    public boolean agregarPresidente(Presidente presidente) {
        // Validaciones de negocio
        if (!validarDatosPresidente(presidente)) {
            return false;
        }
        
        String sql = "INSERT INTO presidente (id_presidente, dpi, nombre1, nombre2, nombre3, " +
                    "apellido1, apellido2, fecha_nac, municipio, año_eleccion) " +
                    "VALUES (seq_presidente.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id_presidente"})) {
            
            establecerParametrosPresidente(pstmt, presidente);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idGenerado = generatedKeys.getInt(1);
                        presidente.setIdPresidente(idGenerado);
                        
                        // Insertar correos
                        if (presidente.getCorreos() != null && !presidente.getCorreos().isEmpty()) {
                            insertarCorreosPresidente(conn, idGenerado, presidente.getCorreos());
                        }
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar presidente: " + e.getMessage());
        }
        return false;
    }
    
    public boolean actualizarPresidente(Presidente presidente) {
        if (!validarDatosPresidente(presidente)) {
            return false;
        }
        
        String sql = "UPDATE presidente SET dpi=?, nombre1=?, nombre2=?, nombre3=?, " +
                    "apellido1=?, apellido2=?, fecha_nac=?, municipio=?, año_eleccion=? " +
                    "WHERE id_presidente=?";
        
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            establecerParametrosPresidente(pstmt, presidente);
            pstmt.setInt(10, presidente.getIdPresidente());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Actualizar correos
                actualizarCorreosPresidente(conn, presidente.getIdPresidente(), presidente.getCorreos());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar presidente: " + e.getMessage());
        }
        return false;
    }
    
    public boolean eliminarPresidente(int id) {
        String sql = "DELETE FROM presidente WHERE id_presidente = ?";
        
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar presidente: " + e.getMessage());
        }
        return false;
    }
    
    // Métodos auxiliares privados
    private Presidente mapearPresidenteDesdeResultSet(ResultSet rs) throws SQLException {
        Presidente presidente = new Presidente();
        presidente.setIdPresidente(rs.getInt("id_presidente"));
        presidente.setDpi(rs.getString("dpi"));
        presidente.setNombre1(rs.getString("nombre1"));
        presidente.setNombre2(rs.getString("nombre2"));
        presidente.setNombre3(rs.getString("nombre3"));
        presidente.setApellido1(rs.getString("apellido1"));
        presidente.setApellido2(rs.getString("apellido2"));
        presidente.setFechaNac(rs.getDate("fecha_nac"));
        presidente.setMunicipio(rs.getString("municipio"));
        presidente.setAñoEleccion(rs.getInt("año_eleccion"));
        return presidente;
    }
    
    private void establecerParametrosPresidente(PreparedStatement pstmt, Presidente presidente) throws SQLException {
        pstmt.setString(1, presidente.getDpi());
        pstmt.setString(2, presidente.getNombre1());
        pstmt.setString(3, presidente.getNombre2() != null ? presidente.getNombre2() : "");
        pstmt.setString(4, presidente.getNombre3() != null ? presidente.getNombre3() : "");
        pstmt.setString(5, presidente.getApellido1());
        pstmt.setString(6, presidente.getApellido2());
        pstmt.setDate(7, new java.sql.Date(presidente.getFechaNac().getTime()));
        pstmt.setString(8, presidente.getMunicipio());
        pstmt.setInt(9, presidente.getAñoEleccion());
    }
    
    private List<String> obtenerCorreosPresidente(Connection conn, int idPresidente) throws SQLException {
        List<String> correos = new ArrayList<>();
        String sql = "SELECT correo FROM correo_presidente WHERE id_presidente = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPresidente);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    correos.add(rs.getString("correo"));
                }
            }
        }
        return correos;
    }
    
    private void insertarCorreosPresidente(Connection conn, int idPresidente, List<String> correos) throws SQLException {
        String sql = "INSERT INTO correo_presidente (id_correo_pres, correo, id_presidente) VALUES (seq_correo_presidente.NEXTVAL, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String correo : correos) {
                if (correo != null && !correo.trim().isEmpty()) {
                    pstmt.setString(1, correo.trim());
                    pstmt.setInt(2, idPresidente);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        }
    }
    
    private void actualizarCorreosPresidente(Connection conn, int idPresidente, List<String> correos) throws SQLException {
        // Eliminar correos existentes
        String deleteSql = "DELETE FROM correo_presidente WHERE id_presidente = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, idPresidente);
            pstmt.executeUpdate();
        }
        
        // Insertar nuevos correos
        if (correos != null && !correos.isEmpty()) {
            insertarCorreosPresidente(conn, idPresidente, correos);
        }
    }
    
    private boolean validarDatosPresidente(Presidente presidente) {
        if (presidente.getNombre1() == null || presidente.getNombre1().trim().isEmpty()) {
            System.err.println("Error: El primer nombre es obligatorio");
            return false;
        }
        
        if (presidente.getApellido1() == null || presidente.getApellido1().trim().isEmpty()) {
            System.err.println("Error: El primer apellido es obligatorio");
            return false;
        }
        
        if (presidente.getDpi() == null || presidente.getDpi().trim().isEmpty()) {
            System.err.println("Error: El DPI es obligatorio");
            return false;
        }
        
        if (!validarEdadPresidente(presidente.getFechaNac())) {
            System.err.println("Error: El presidente debe tener al menos 25 años");
            return false;
        }
        
        return true;
    }
    
    public boolean validarEdadPresidente(java.util.Date fechaNac) {
        if (fechaNac == null) return false;
        
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(new java.util.Date());
        int añoActual = cal.get(java.util.Calendar.YEAR);
        
        cal.setTime(fechaNac);
        int añoNacimiento = cal.get(java.util.Calendar.YEAR);
        
        return (añoActual - añoNacimiento) >= 25;
    }
}
