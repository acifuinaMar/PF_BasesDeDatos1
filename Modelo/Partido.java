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

public class Partido {
    private int idPartido;
    private Date fecha;
    private int golesCasa;
    private int golesFuera;
    private int idEquipoCasa;
    private int idEquipoFuera;
    private String nombreEquipoCasa;
    private String nombreEquipoFuera;
    
    public Partido() {}
    
    public Partido(int idPartido, Date fecha, int golesCasa, int golesFuera, int idEquipoCasa, int idEquipoFuera) {
        this.idPartido = idPartido;
        this.fecha = fecha;
        this.golesCasa = golesCasa;
        this.golesFuera = golesFuera;
        this.idEquipoCasa = idEquipoCasa;
        this.idEquipoFuera = idEquipoFuera;
    }
    
    // Getters y Setters
    public int getIdPartido() { 
        return idPartido; 
    }
    public void setIdPartido(int idPartido) { 
        this.idPartido = idPartido; 
    }
    
    public Date getFecha() { 
        return fecha; 
    }
    public void setFecha(Date fecha) { 
        this.fecha = fecha; 
    }
    
    public int getGolesCasa() { 
        return golesCasa; 
    }
    public void setGolesCasa(int golesCasa) { 
        this.golesCasa = golesCasa; 
    }
    
    public int getGolesFuera() { 
        return golesFuera; 
    }
    public void setGolesFuera(int golesFuera) { 
        this.golesFuera = golesFuera; 
    }
    
    public int getIdEquipoCasa() { 
        return idEquipoCasa; 
    }
    public void setIdEquipoCasa(int idEquipoCasa) { 
        this.idEquipoCasa = idEquipoCasa; 
    }
    
    public int getIdEquipoFuera() { 
        return idEquipoFuera; 
    }
    public void setIdEquipoFuera(int idEquipoFuera) { 
        this.idEquipoFuera = idEquipoFuera; 
    }
    
    public String getNombreEquipoCasa() { 
        return nombreEquipoCasa; 
    }
    public void setNombreEquipoCasa(String nombreEquipoCasa) {
        this.nombreEquipoCasa = nombreEquipoCasa; 
    }
    
    public String getNombreEquipoFuera() { 
        return nombreEquipoFuera; 
    }
    public void setNombreEquipoFuera(String nombreEquipoFuera) { 
        this.nombreEquipoFuera = nombreEquipoFuera; 
    }
    
    public String getResultado() {
        return golesCasa + " - " + golesFuera;
    }
    
    public String getEquipoGanador() {
        if (golesCasa > golesFuera) {
            return nombreEquipoCasa;
        } else if (golesFuera > golesCasa) {
            return nombreEquipoFuera;
        } else {
            return "Empate";
        }
    }
    
    @Override
    public String toString() {
        return String.format("Partido [ID: %d, %s vs %s, Resultado: %s]", 
            idPartido, nombreEquipoCasa, nombreEquipoFuera, getResultado());
    }
}