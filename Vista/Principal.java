/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista;

/**
 *
 * @author maryori
 */

import Modelo.Conexion;
import javax.swing.*;
import java.awt.*;

public class Principal extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Conexion dbConnection;
    
    // Paneles
    private Dashboard dashboard;
    private PresidenteP presidenteP;
    private EquipoP equipoP;
    private JugadorP jugadorP;
    private PartidoP partidoP;
    
    public Principal() {
        initializeFrame();
        initializeComponents();
        setupPanels();
        showDashboard();
    }
    
    private void initializeFrame() {
        setTitle("Sistema de Gestión - Liga de Fútbol");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(true);
    }
    
    private void initializeComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        dbConnection = Conexion.getInstance();
        
        // Inicializar paneles
        dashboard = new Dashboard(this);
        presidenteP = new PresidenteP(this);
        equipoP = new EquipoP(this);
        jugadorP = new JugadorP(this);
        partidoP = new PartidoP(this);
    }
    
    private void setupPanels() {
        mainPanel.add(dashboard, "DASHBOARD");
        mainPanel.add(presidenteP, "PRESIDENTE");
        mainPanel.add(equipoP, "EQUIPO");
        mainPanel.add(jugadorP, "JUGADOR");
        mainPanel.add(partidoP, "PARTIDO");
        
        add(mainPanel);
    }
    
    public void showDashboard() {
        dashboard.actualizarEstadoConexion();
        cardLayout.show(mainPanel, "DASHBOARD");
    }
    
    public void showPresidentePanel() {
        presidenteP.cargarDatos();
        cardLayout.show(mainPanel, "PRESIDENTE");
    }
    
    public void showEquipoPanel() {
        equipoP.cargarDatos();
        cardLayout.show(mainPanel, "EQUIPO");
    }
    
    public void showJugadorPanel() {
        jugadorP.cargarDatos();
        cardLayout.show(mainPanel, "JUGADOR");
    }
    
    public void showPartidoPanel() {
        partidoP.cargarDatos();
        cardLayout.show(mainPanel, "PARTIDO");
    }
    
    public Conexion getDbConnection() {
        return dbConnection;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Principal().setVisible(true);
        });
    }
}
