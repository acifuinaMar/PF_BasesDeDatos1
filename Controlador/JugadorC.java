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
import Modelo.Jugador;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JugadorC{
    
    public List<Jugador> obtenerTodosJugadores() {
        List<Jugador> jugadores = new ArrayList<>();
        String sql = "SELECT j.*, e.nombre as nombre_equipo " +
                    "FROM jugador j LEFT JOIN equipo e ON j.id_equipo = e.id_equipo " +
                    "ORDER BY j.apellido1, j.nombre1";
        
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Jugador jugador = mapearJugadorDesdeResultSet(rs);
                jugador.setCorreos(obtenerCorreosJugador(conn, jugador.getIdJugador()));
                jugadores.add(jugador);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener jugadores: " + e.getMessage());
        }
        return jugadores;
    }
    
    public boolean agregarJugador(Jugador jugador) {
        if (!validarDatosJugador(jugador)) {
            return false;
        }
        
        String sql = "INSERT INTO jugador (id_jugador, nombre1, nombre2, nombre3, apellido1, apellido2, municipio, fecha_nac, posicion, id_equipo) VALUES (seq_jugador.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id_jugador"})) {
            
            establecerParametrosJugador(pstmt, jugador);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idGenerado = generatedKeys.getInt(1);
                        jugador.setIdJugador(idGenerado);
                        
                        // Insertar correos
                        if (jugador.getCorreos() != null && !jugador.getCorreos().isEmpty()) {
                            insertarCorreosJugador(conn, idGenerado, jugador.getCorreos());
                        }
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar jugador: " + e.getMessage());
        }
        return false;
    }
    
    public List<Jugador> obtenerJugadoresPorEquipo(int idEquipo) {
        List<Jugador> jugadores = new ArrayList<>();
        String sql = "SELECT j.*, e.nombre as nombre_equipo FROM jugador j " +
                    "LEFT JOIN equipo e ON j.id_equipo = e.id_equipo " +
                    "WHERE j.id_equipo = ? ORDER BY j.apellido1, j.nombre1";
        
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idEquipo);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Jugador jugador = mapearJugadorDesdeResultSet(rs);
                    jugador.setCorreos(obtenerCorreosJugador(conn, jugador.getIdJugador()));
                    jugadores.add(jugador);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener jugadores por equipo: " + e.getMessage());
        }
        return jugadores;
    }
    
    // Métodos auxiliares privados
    private Jugador mapearJugadorDesdeResultSet(ResultSet rs) throws SQLException {
        Jugador jugador = new Jugador();
        jugador.setIdJugador(rs.getInt("id_jugador"));
        jugador.setNombre1(rs.getString("nombre1"));
        jugador.setNombre2(rs.getString("nombre2"));
        jugador.setNombre3(rs.getString("nombre3"));
        jugador.setApellido1(rs.getString("apellido1"));
        jugador.setApellido2(rs.getString("apellido2"));
        jugador.setMunicipio(rs.getString("municipio"));
        jugador.setFechaNac(rs.getDate("fecha_nac"));
        jugador.setPosicion(rs.getString("posicion"));
        jugador.setIdEquipo(rs.getInt("id_equipo"));
        jugador.setNombreEquipo(rs.getString("nombre_equipo"));
        return jugador;
    }
    
    private void establecerParametrosJugador(PreparedStatement pstmt, Jugador jugador) throws SQLException {
        pstmt.setString(1, jugador.getNombre1());
        pstmt.setString(2, jugador.getNombre2() != null ? jugador.getNombre2() : "");
        pstmt.setString(3, jugador.getNombre3() != null ? jugador.getNombre3() : "");
        pstmt.setString(4, jugador.getApellido1());
        pstmt.setString(5, jugador.getApellido2());
        pstmt.setString(6, jugador.getMunicipio());
        pstmt.setDate(7, new java.sql.Date(jugador.getFechaNac().getTime()));
        pstmt.setString(8, jugador.getPosicion());
        pstmt.setInt(9, jugador.getIdEquipo());
    }
    
    private List<String> obtenerCorreosJugador(Connection conn, int idJugador) throws SQLException {
        List<String> correos = new ArrayList<>();
        String sql = "SELECT correo FROM correo_jugador WHERE id_jugador = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idJugador);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    correos.add(rs.getString("correo"));
                }
            }
        }
        return correos;
    }
    
    private void insertarCorreosJugador(Connection conn, int idJugador, List<String> correos) throws SQLException {
        String sql = "INSERT INTO correo_jugador (id_correo_jug, correo, id_jugador) " +
                    "VALUES (seq_correo_jugador.NEXTVAL, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String correo : correos) {
                if (correo != null && !correo.trim().isEmpty()) {
                    pstmt.setString(1, correo.trim());
                    pstmt.setInt(2, idJugador);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        }
    }
    
    private boolean validarDatosJugador(Jugador jugador) {
        if (jugador.getNombre1() == null || jugador.getNombre1().trim().isEmpty()) {
            System.err.println("Error: El primer nombre es obligatorio");
            return false;
        }
        
        if (jugador.getApellido1() == null || jugador.getApellido1().trim().isEmpty()) {
            System.err.println("Error: El primer apellido es obligatorio");
            return false;
        }
        
        if (jugador.getPosicion() == null || jugador.getPosicion().trim().isEmpty()) {
            System.err.println("Error: La posición es obligatoria");
            return false;
        }
        
        if (!validarEdadJugador(jugador.getFechaNac())) {
            System.err.println("Error: El jugador debe tener al menos 16 años");
            return false;
        }
        
        return true;
    }
    
    public boolean validarEdadJugador(java.util.Date fechaNac) {
        if (fechaNac == null) return false;
        
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(new java.util.Date());
        int añoActual = cal.get(java.util.Calendar.YEAR);
        
        cal.setTime(fechaNac);
        int añoNacimiento = cal.get(java.util.Calendar.YEAR);
        
        return (añoActual - añoNacimiento) >= 16;
    }
}