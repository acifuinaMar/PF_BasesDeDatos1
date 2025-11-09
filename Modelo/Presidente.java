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

public class Presidente {
    private int idPresidente;
    private String dpi;
    private String nombre1;
    private String nombre2;
    private String nombre3;
    private String apellido1;
    private String apellido2;
    private Date fechaNac;
    private String municipio;
    private int eleccion;
    private List<String> correos;
    
    public Presidente() {
        this.correos = new ArrayList<>();
    }
    
    public Presidente(int idPresidente, String dpi, String nombre1, String apellido1, 
                     String apellido2, Date fechaNac, String municipio, int añoEleccion) {
        this();
        this.idPresidente = idPresidente;
        this.dpi = dpi;
        this.nombre1 = nombre1;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.fechaNac = fechaNac;
        this.municipio = municipio;
        this.eleccion = añoEleccion;
    }
    
    // Getters y Setters
    public int getIdPresidente() { 
        return idPresidente; 
    }
    public void setIdPresidente(int idPresidente) { 
        this.idPresidente = idPresidente; 
    }
    public String getDpi() { 
        return dpi; 
    }
    public void setDpi(String dpi) { 
        this.dpi = dpi; 
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
    public Date getFechaNac() { 
        return fechaNac; 
    }
    public void setFechaNac(Date fechaNac) { 
        this.fechaNac = fechaNac; 
    }
    public String getMunicipio() { 
        return municipio; 
    }
    public void setMunicipio(String municipio) { 
        this.municipio = municipio; 
    }
    public int getAñoEleccion() { 
        return eleccion; 
    }
    public void setAñoEleccion(int añoEleccion) {
        this.eleccion = añoEleccion; 
    }
    public List<String> getCorreos() { 
        return correos; 
    }
    public void setCorreos(List<String> correos) {
        this.correos = correos; 
    }
    public void agregarCorreo(String correo) {
        if (this.correos == null) {
            this.correos = new ArrayList<>();
        }
        this.correos.add(correo);
    }
    
    @Override
    public String toString() {
        return String.format("Presidente [ID: %d, Nombre: %s %s, DPI: %s, Año Elección: %d]", 
            idPresidente, nombre1, apellido1, dpi, eleccion);
    }
}
