package Controlador;

import Modelo.Conexion;
import Modelo.Presidente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PresidenteC {

    public List<Presidente> obtenerTodosPresidentes() {
        List<Presidente> presidentes = new ArrayList<>();
        String sql = "SELECT * FROM PRESIDENTE ORDER BY APELLIDO1, NOMBRE1";
        
        
        try (Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()){
           

                while (rs.next()) {
                    Presidente presidente = mapearPresidenteDesdeResultSet(rs);
                    presidente.setCorreos(obtenerCorreosPresidente(conn, presidente.getIdPresidente()));
                    presidentes.add(presidente);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener presidentes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // NO cierres la conexión aquí si usas un pool de conexiones
            // Si Conexion.getInstance() maneja un pool, déjalo que maneje el cierre
        }
        return presidentes;
    }
    
    public Presidente obtenerPresidentePorId(int id) {
        Presidente presidente = null;
        String sql = "SELECT * FROM PRESIDENTE WHERE ID_PRESIDENTE = ?";

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
        if (!validarDatosPresidente(presidente)) {
            return false;
        }

        // Cambia el nombre de la secuencia a la que realmente tienes en tu BD
        String sql = """
            INSERT INTO PRESIDENTE
            (DPI, NOMBRE1, NOMBRE2, NOMBRE3, APELLIDO1, APELLIDO2, FECHA_NAC, MUNICIPIO, ANIO_ELECCION)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"ID_PRESIDENTE"})) {

            establecerParametrosPresidente(pstmt, presidente);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idGenerado = generatedKeys.getInt(1);
                        presidente.setIdPresidente(idGenerado);

                        if (presidente.getCorreos() != null && !presidente.getCorreos().isEmpty()) {
                            insertarCorreosPresidente(conn, idGenerado, presidente.getCorreos());
                        }
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar presidente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizarPresidente(Presidente presidente) {
        if (!validarDatosPresidente(presidente)) return false;

        String sql = """
            UPDATE PRESIDENTE
            SET DPI=?, NOMBRE1=?, NOMBRE2=?, NOMBRE3=?, APELLIDO1=?, APELLIDO2=?, FECHA_NAC=?, MUNICIPIO=?, ANIO_ELECCION=?
            WHERE ID_PRESIDENTE=?
            """;

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            establecerParametrosPresidente(pstmt, presidente);
            pstmt.setInt(10, presidente.getIdPresidente());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                actualizarCorreosPresidente(conn, presidente.getIdPresidente(), presidente.getCorreos());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar presidente: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminarPresidente(int id) {
        String sql = "DELETE FROM PRESIDENTE WHERE ID_PRESIDENTE = ?";

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar presidente: " + e.getMessage());
        }
        return false;
    }

    // ------------------------ MÉTODOS AUXILIARES ------------------------

    private Presidente mapearPresidenteDesdeResultSet(ResultSet rs) throws SQLException {
        Presidente presidente = new Presidente();
        presidente.setIdPresidente(rs.getInt("ID_PRESIDENTE"));
        presidente.setDpi(rs.getString("DPI"));
        presidente.setNombre1(rs.getString("NOMBRE1"));
        presidente.setNombre2(rs.getString("NOMBRE2"));
        presidente.setNombre3(rs.getString("NOMBRE3"));
        presidente.setApellido1(rs.getString("APELLIDO1"));
        presidente.setApellido2(rs.getString("APELLIDO2"));
        presidente.setFechaNac(rs.getDate("FECHA_NAC"));
        presidente.setMunicipio(rs.getString("MUNICIPIO"));
        presidente.setAñoEleccion(rs.getInt("ANIO_ELECCION"));
        return presidente;
    }

    private void establecerParametrosPresidente(PreparedStatement pstmt, Presidente presidente) throws SQLException {
        pstmt.setString(1, presidente.getDpi());
        pstmt.setString(2, presidente.getNombre1());
        pstmt.setString(3, presidente.getNombre2());
        pstmt.setString(4, presidente.getNombre3());
        pstmt.setString(5, presidente.getApellido1());
        pstmt.setString(6, presidente.getApellido2());
        pstmt.setDate(7, new java.sql.Date(presidente.getFechaNac().getTime()));
        pstmt.setString(8, presidente.getMunicipio());
        pstmt.setInt(9, presidente.getAñoEleccion());
    }

    private List<String> obtenerCorreosPresidente(Connection conn, int idPresidente) throws SQLException {
        List<String> correos = new ArrayList<>();
        String sql = "SELECT CORREO FROM CORREOPRESIDENTE WHERE ID_PRESIDENTE = ?";

        try (Connection connCorreos = Conexion.getInstance().getConnection();
            PreparedStatement pstmt = connCorreos.prepareStatement(sql)) {

           pstmt.setInt(1, idPresidente);
           try (ResultSet rs = pstmt.executeQuery()) {
               while (rs.next()) {
                   correos.add(rs.getString("CORREO"));
               }
           }
       } catch (SQLException e) {
           System.err.println("Error al obtener correos: " + e.getMessage());
       }
       return correos;
   }

    private void insertarCorreosPresidente(Connection conn, int idPresidente, List<String> correos) throws SQLException {
        String sql = "INSERT INTO CORREOPRESIDENTE (CORREO, ID_PRESIDENTE) VALUES (?, ?)";

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
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM CORREOPRESIDENTE WHERE ID_PRESIDENTE = ?")) {
            pstmt.setInt(1, idPresidente);
            pstmt.executeUpdate();
        }
        if (correos != null && !correos.isEmpty()) {
            insertarCorreosPresidente(conn, idPresidente, correos);
        }
    }

    private boolean validarDatosPresidente(Presidente presidente) {
        if (presidente.getNombre1() == null || presidente.getNombre1().isEmpty()) return false;
        if (presidente.getApellido1() == null || presidente.getApellido1().isEmpty()) return false;
        if (presidente.getDpi() == null || presidente.getDpi().isEmpty()) return false;
        return validarEdadPresidente(presidente.getFechaNac());
    }

    private boolean validarEdadPresidente(java.util.Date fechaNac) {
        if (fechaNac == null) return false;
        java.util.Calendar hoy = java.util.Calendar.getInstance();
        java.util.Calendar nac = java.util.Calendar.getInstance();
        nac.setTime(fechaNac);
        int edad = hoy.get(java.util.Calendar.YEAR) - nac.get(java.util.Calendar.YEAR);
        return edad >= 25;
    }
}
