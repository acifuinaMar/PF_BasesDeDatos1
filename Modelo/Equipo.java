/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author maryori
 */

public class Equipo {
    private int idEquipo;
    private String nombre;
    private String estadio;
    private int aforo;
    private int fundacion;
    private String departamento;
    private int idPresidente;
    private String nombrePresidente;
    
    public Equipo() {
    }
    
    public Equipo(int idEquipo, String nombre, String estadio, int aforo, int fundacion, String departamento, int idPresidente) {
        this.idEquipo = idEquipo;
        this.nombre = nombre;
        this.estadio = estadio;
        this.aforo = aforo;
        this.fundacion = fundacion;
        this.departamento = departamento;
        this.idPresidente = idPresidente;
    }
    
    // Getters y Setters
    public int getIdEquipo() { 
        return idEquipo; 
    }
    public void setIdEquipo(int idEquipo) { 
        this.idEquipo = idEquipo; 
    }
    
    public String getNombre() { 
        return nombre; 
    }
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }
    
    public String getEstadio() { 
        return estadio; 
    }
    public void setEstadio(String estadio) { 
        this.estadio = estadio; 
    }
    
    public int getAforo() { 
        return aforo; 
    }
    public void setAforo(int aforo) { 
        this.aforo = aforo; 
    }
    
    public int getFundacion() { 
        return fundacion; 
    }
    public void setFundacion(int fundacion) { 
        this.fundacion = fundacion; 
    }
    
    public String getDepartamento() { 
        return departamento; 
    }
    public void setDepartamento(String departamento) { 
        this.departamento = departamento; 
    }
    
    public int getIdPresidente() { 
        return idPresidente; 
    }
    public void setIdPresidente(int idPresidente) { 
        this.idPresidente = idPresidente; 
    }
    
    public String getNombrePresidente() { 
        return nombrePresidente; 
    }
    public void setNombrePresidente(String nombrePresidente) { 
        this.nombrePresidente = nombrePresidente; 
    }
    
    @Override
    public String toString() {
        return String.format("Equipo [ID: %d, Nombre: %s, Estadio: %s, Aforo: %d]", 
            idEquipo, nombre, estadio, aforo);
    }
}
