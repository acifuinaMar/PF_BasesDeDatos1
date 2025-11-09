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
        
        String sql = "INSERT INTO jugador ( nombre1, nombre2, nombre3, apellido1, apellido2, municipio, fecha_nac, posicion, id_equipo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
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
    
    public Jugador obtenerJugadorPorId(int id) {
        Jugador jugador = null;
        String sql = """
            SELECT j.*, e.nombre as nombre_equipo,
                   j.nombre1 || ' ' || j.apellido1 as nombre_completo
            FROM jugador j 
            LEFT JOIN equipo e ON j.id_equipo = e.id_equipo 
            WHERE j.id_jugador = ?
            """;

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    jugador = mapearJugadorDesdeResultSet(rs);
                    jugador.setCorreos(obtenerCorreosJugador(conn, id));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener jugador: " + e.getMessage());
            e.printStackTrace();
        }
        return jugador;
    }
    
    // En JugadorC.java, agrega este método:
public List<Jugador> buscarJugadoresPorEquipo(int idEquipo) {
    List<Jugador> jugadoresFiltrados = new ArrayList<>();
    String sql = "SELECT * FROM jugador WHERE id_equipo = ? ORDER BY apellido1, nombre1";
    
    try (Connection conn = Conexion.getInstance().getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, idEquipo);
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Jugador jugador = mapearJugadorDesdeResultSet(rs);
                jugadoresFiltrados.add(jugador);
            }
        }
    } catch (SQLException e) {
        System.err.println("Error al buscar jugadores por equipo: " + e.getMessage());
        e.printStackTrace();
    }
    return jugadoresFiltrados;
}
    
    public boolean actualizarJugador(Jugador jugador) {
        if (!validarDatosJugador(jugador)) {
            return false;
        }

        String sql = """
            UPDATE jugador 
            SET nombre1=?, nombre2=?, nombre3=?, apellido1=?, apellido2=?, 
                municipio=?, fecha_nac=?, posicion=?, id_equipo=?
            WHERE id_jugador=?
            """;

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            establecerParametrosJugador(pstmt, jugador);
            pstmt.setInt(10, jugador.getIdJugador());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                actualizarCorreosJugador(conn, jugador.getIdJugador(), jugador.getCorreos());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar jugador: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminarJugador(int id) {
        String sql = "DELETE FROM jugador WHERE id_jugador = ?";

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar jugador: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Jugador> buscarJugadoresPorNombre(String nombre) {
        List<Jugador> jugadores = new ArrayList<>();
        String sql = """
            SELECT j.*, e.nombre as nombre_equipo,
                   j.nombre1 || ' ' || j.apellido1 as nombre_completo
            FROM jugador j 
            LEFT JOIN equipo e ON j.id_equipo = e.id_equipo 
            WHERE UPPER(j.nombre1) LIKE UPPER(?) OR UPPER(j.apellido1) LIKE UPPER(?)
            ORDER BY j.apellido1, j.nombre1
            """;

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + nombre + "%");
            pstmt.setString(2, "%" + nombre + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Jugador jugador = mapearJugadorDesdeResultSet(rs);
                    jugador.setCorreos(obtenerCorreosJugador(conn, jugador.getIdJugador()));
                    jugadores.add(jugador);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar jugadores: " + e.getMessage());
            e.printStackTrace();
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
        String sql = "SELECT correo FROM CORREOJUGADOR WHERE id_jugador = ?";
        
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
        String sql = "INSERT INTO CORREOJUGADOR (id_jugador, correo) " +
                    "VALUES (?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String correo : correos) {
                if (correo != null && !correo.trim().isEmpty()) {
                    pstmt.setInt(1, idJugador);
                    pstmt.setString(2, correo.trim());
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        }
    }
    
    private void actualizarCorreosJugador(Connection conn, int idJugador, List<String> correos) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM CORREOJUGADOR WHERE id_jugador = ?")) {
            pstmt.setInt(1, idJugador);
            pstmt.executeUpdate();
        }
        if (correos != null && !correos.isEmpty()) {
            insertarCorreosJugador(conn, idJugador, correos);
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
    
    public List<Jugador> obtenerJugadoresParaCombo() {
        List<Jugador> jugadores = new ArrayList<>();
        String sql = """
            SELECT j.id_jugador, j.nombre1, j.apellido1, e.nombre as equipo
            FROM jugador j
            JOIN equipo e ON j.id_equipo = e.id_equipo
            ORDER BY j.apellido1, j.nombre1
            """;

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Jugador jugador = new Jugador();
                jugador.setIdJugador(rs.getInt("id_jugador"));
                jugador.setNombre1(rs.getString("nombre1"));
                jugador.setApellido1(rs.getString("apellido1"));
                jugador.setNombreEquipo(rs.getString("equipo"));
                jugadores.add(jugador);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener jugadores para combo: " + e.getMessage());
        }
        return jugadores;
    }
}