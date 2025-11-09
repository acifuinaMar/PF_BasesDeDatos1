/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author maryori
 */

public class Gol {
    private int idGol;
    private int minuto;
    private String descripcion;
    private int idPartido;
    private int idJugador;
    private String nombreJugador;
    private String nombreEquipo;
    
    public Gol() {}
    
    public Gol(int idGol, int minuto, String descripcion, int idPartido, int idJugador) {
        this.idGol = idGol;
        this.minuto = minuto;
        this.descripcion = descripcion;
        this.idPartido = idPartido;
        this.idJugador = idJugador;
    }
    
    // Getters y Setters
    public int getIdGol() { 
        return idGol; 
    }
    public void setIdGol(int idGol) { 
        this.idGol = idGol; 
    }
    
    public int getMinuto() { 
        return minuto; 
    }
    public void setMinuto(int minuto) { 
        this.minuto = minuto; 
    }
    
    public String getDescripcion() { 
        return descripcion; 
    }
    public void setDescripcion(String descripcion) { 
        this.descripcion = descripcion; 
    }
    
    public int getIdPartido() { 
        return idPartido; 
    }
    public void setIdPartido(int idPartido) {
        this.idPartido = idPartido;
    }
    
    public int getIdJugador() { 
        return idJugador; 
    }
    public void setIdJugador(int idJugador) { 
        this.idJugador = idJugador; 
    }
    
    public String getNombreJugador() { 
        return nombreJugador; 
    }
    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador; 
    }
    
    public String getNombreEquipo() {
        return nombreEquipo;
    }
    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo; 
    }
    
    @Override
    public String toString() {
        return String.format("Gol [Minuto: %d', Jugador: %s, Descripción: %s]", 
            minuto, nombreJugador, descripcion);
    }
}
