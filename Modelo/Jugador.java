/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author maryori
 */

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Jugador {
    private int idJugador;
    private String nombre1;
    private String nombre2;
    private String nombre3;
    private String apellido1;
    private String apellido2;
    private String municipio;
    private Date fechaNac;
    private String posicion;
    private int idEquipo;
    private List<String> correos;
    private String nombreEquipo;
    
    public Jugador() {
        this.correos = new ArrayList<>();
    }
    
    public Jugador(int idJugador, String nombre1, String apellido1, String apellido2,String municipio, Date fechaNac, String posicion, int idEquipo) {
        this();
        this.idJugador = idJugador;
        this.nombre1 = nombre1;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.municipio = municipio;
        this.fechaNac = fechaNac;
        this.posicion = posicion;
        this.idEquipo = idEquipo;
    }
    
    // Getters y Setters
    public int getIdJugador() { 
        return idJugador; 
    }
    public void setIdJugador(int idJugador) { 
        this.idJugador = idJugador; 
    }
    
    public String getNombre1() { 
        return nombre1; 
    }
    public void setNombre1(String nombre1) { 
        this.nombre1 = nombre1; 
    }
    
    public String getNombre2() { 
        return nombre2; 
    }
    public void setNombre2(String nombre2) { 
        this.nombre2 = nombre2; 
    }
    
    public String getNombre3() { 
        return nombre3; 
    }
    public void setNombre3(String nombre3) { 
        this.nombre3 = nombre3; 
    }
    
    public String getApellido1() { 
        return apellido1; 
    }
    public void setApellido1(String apellido1) { 
        this.apellido1 = apellido1; 
    }
    
    public String getApellido2() { 
        return apellido2; 
    }
    public void setApellido2(String apellido2) { 
        this.apellido2 = apellido2; 
    }
    
    public String getMunicipio() {
        return municipio; 
    }
    public void setMunicipio(String municipio) { 
        this.municipio = municipio; 
    }
    
    public Date getFechaNac() { 
        return fechaNac; 
    }
    public void setFechaNac(Date fechaNac) { 
        this.fechaNac = fechaNac; 
    }
    
    public String getPosicion() { 
        return posicion; 
    }
    public void setPosicion(String posicion) { 
        this.posicion = posicion; 
    }
    
    public int getIdEquipo() { 
        return idEquipo; 
    }
    public void setIdEquipo(int idEquipo) { 
        this.idEquipo = idEquipo; 
    }
    
    public List<String> getCorreos() { 
        return correos; 
    }
    public void setCorreos(List<String> correos) { 
        this.correos = correos; 
    }
    
    public String getNombreEquipo() { 
        return nombreEquipo; 
    }
    public void setNombreEquipo(String nombreEquipo) { 
        this.nombreEquipo = nombreEquipo; 
    }
    
    public void agregarCorreo(String correo) {
        if (this.correos == null) {
            this.correos = new ArrayList<>();
        }
        this.correos.add(correo);
    }
    
    @Override
    public String toString() {
        return String.format("Jugador [ID: %d, Nombre: %s %s, Posición: %s, Equipo: %s]", 
            idJugador, nombre1, apellido1, posicion, nombreEquipo != null ? nombreEquipo : "Sin equipo");
    }
}