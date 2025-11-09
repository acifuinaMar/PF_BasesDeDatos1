/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista;

/**
 *
 * @author maryori
 */

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class Dashboard extends JPanel {
    private Principal principal;
    private JLabel estadoConexionLabel;
    //private JButton conectarBtn;
    //private JButton desconectarBtn;
    
    public Dashboard (Principal principal) {
        this.principal = principal;
        initializePanel();
        setupComponents();
        setupEvents();
        //actualizarEstadoConexion();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
    }
    
    private void setupComponents() {
        // Panel superior con estado de conexión
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBackground(Color.LIGHT_GRAY);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        estadoConexionLabel = new JLabel("Estado: Verificando...");
        estadoConexionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        
        topPanel.add(estadoConexionLabel);
        topPanel.add(Box.createHorizontalStrut(20));
        
        
        // Panel central con botones de gestión
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        centerPanel.setBackground(Color.WHITE);
        
        JButton presidenteBtn = createMenuButton("Gestión de Presidentes", "👨‍💼", Color.BLUE);
        JButton equipoBtn = createMenuButton("Gestión de Equipos", "⚽", Color.GREEN);
        JButton jugadorBtn = createMenuButton("Gestión de Jugadores", "👤", Color.ORANGE);
        JButton partidoBtn = createMenuButton("Gestión de Partidos", "📅", Color.RED);
        JButton golBtn = createMenuButton("Gestión de Goles" , "🏆", Color.CYAN);
        centerPanel.add(presidenteBtn);
        centerPanel.add(equipoBtn);
        centerPanel.add(jugadorBtn);
        centerPanel.add(partidoBtn);
        centerPanel.add(golBtn);
        
        // Configurar eventos de botones
        presidenteBtn.addActionListener(e -> principal.showPresidentePanel());
        equipoBtn.addActionListener(e -> principal.showEquipoPanel());
        jugadorBtn.addActionListener(e -> principal.showJugadorPanel());
        partidoBtn.addActionListener(e -> principal.showPartidoPanel());
        golBtn.addActionListener(e -> principal.showGolPanel());
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void setupEvents() {
        /*conectarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                conectarBaseDatos();
            }
        });
        
        desconectarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                desconectarBaseDatos();
            }
        });*/
    }
    
    private JButton createMenuButton(String text, String emoji, Color color) {
        JButton button = new JButton("<html><center><font size='5'>" + emoji + "</font><br>" + text + "</center></html>");
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    public void actualizarEstadoConexion() {
        SwingUtilities.invokeLater(() -> {
            try {
                if (principal.getDbConnection().getConnection() != null && 
                    !principal.getDbConnection().getConnection().isClosed()) {
                    estadoConexionLabel.setText("Estado: CONECTADO");
                    estadoConexionLabel.setForeground(Color.GREEN.darker());
                    //conectarBtn.setEnabled(false);
                    //desconectarBtn.setEnabled(true);
                } else {
                    estadoConexionLabel.setText("Estado: DESCONECTADO");
                    estadoConexionLabel.setForeground(Color.RED);
                    //conectarBtn.setEnabled(true);
                    //desconectarBtn.setEnabled(false);
                }
            } catch (SQLException ex) {
                estadoConexionLabel.setText("Estado: ERROR");
                estadoConexionLabel.setForeground(Color.RED);
                //conectarBtn.setEnabled(true);
                //desconectarBtn.setEnabled(false);
            }
        });
    }
    /*
    private void conectarBaseDatos() {
        try {
            // Forzar nueva conexión
            Conexion.getInstance().getConnection();
            actualizarEstadoConexion();
            JOptionPane.showMessageDialog(this, "Conexión establecida correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al conectar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void desconectarBaseDatos() {
        try {
            principal.getDbConnection().closeConnection();
            actualizarEstadoConexion();
            JOptionPane.showMessageDialog(this, "Conexión cerrada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al desconectar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }*/
}
