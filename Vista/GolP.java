/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista;

import Controlador.GolC;
import Controlador.JugadorC;
import Controlador.PartidoC;
import Modelo.Gol;
import Modelo.Jugador;
import Modelo.Partido;
import java.awt.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 *
 * @author maryori
 */
public class GolP extends JPanel {
    private Principal principal;
    private GolC golC;
    private PartidoC partidoC;
    private JugadorC jugadorC;
    private JTable tablaGoles;
    private DefaultTableModel tableModel;
    private JTextField txtMinuto, txtDescripcion;
    private JComboBox<String> comboPartido, comboJugador;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnLimpiar, btnVolver;
    private int golSeleccionadoId = -1;
    
    public GolP(Principal principal) {
        this.principal = principal;
        this.golC = new GolC();
        this.partidoC = new PartidoC();
        this.jugadorC = new JugadorC();
        initializePanel();
        setupComponents();
        setupEvents();
        cargarDatos();
        cargarCombos();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);
    }
    
    private void setupComponents() {
        // Panel superior - Formulario
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del Gol"));
        formPanel.setBackground(Color.WHITE);
        
        formPanel.add(new JLabel("Partido:*"));
        comboPartido = new JComboBox<>();
        formPanel.add(comboPartido);
        
        formPanel.add(new JLabel("Jugador:*"));
        comboJugador = new JComboBox<>();
        formPanel.add(comboJugador);
        
        formPanel.add(new JLabel("Minuto:*"));
        txtMinuto = new JTextField();
        formPanel.add(txtMinuto);
        
        formPanel.add(new JLabel("Descripción:"));
        txtDescripcion = new JTextField();
        formPanel.add(txtDescripcion);
        
        // Panel central - Tabla
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Lista de Goles"));
        
        String[] columnNames = {"ID", "Partido", "Jugador", "Minuto", "Descripción"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaGoles = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tablaGoles);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior - Botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnAgregar = new JButton("Agregar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        btnVolver = new JButton("Volver al Dashboard");
        
        buttonPanel.add(btnAgregar);
        buttonPanel.add(btnActualizar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnLimpiar);
        buttonPanel.add(btnVolver);
        
        // Agregar todo al panel principal
        add(formPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Inicialmente deshabilitar botones de actualizar/eliminar
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }
    
    private void setupEvents() {
        btnAgregar.addActionListener(e -> agregarGol());
        btnActualizar.addActionListener(e -> actualizarGol());
        btnEliminar.addActionListener(e -> eliminarGol());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnVolver.addActionListener(e -> principal.showDashboard());
        
        // Selección en tabla
        tablaGoles.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaGoles.getSelectedRow() != -1) {
                seleccionarGol();
            }
        });
    }
    
    private void cargarCombos() {
        cargarPartidos();
        cargarJugadores();
    }
    
    private void cargarPartidos() {
        new Thread(() -> {
            try {
                List<Partido> partidos = partidoC.obtenerPartidosParaCombo();
                SwingUtilities.invokeLater(() -> {
                    comboPartido.removeAllItems();
                    comboPartido.addItem("Seleccione partido...");
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    for (Partido partido : partidos) {
                        String item = partido.getIdPartido() + " - " + 
                                    partido.getNombreEquipoCasa() + " vs " + 
                                    partido.getNombreEquipoFuera() + " (" + 
                                    sdf.format(partido.getFecha()) + ")";
                        comboPartido.addItem(item);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    
    private void cargarJugadores() {
        new Thread(() -> {
            try {
                List<Jugador> jugadores = jugadorC.obtenerJugadoresParaCombo();
                SwingUtilities.invokeLater(() -> {
                    comboJugador.removeAllItems();
                    comboJugador.addItem("Seleccione jugador...");
                    
                    for (Jugador jugador : jugadores) {
                        String item = jugador.getIdJugador() + " - " + 
                                    jugador.getNombre1() + " " + jugador.getApellido1() +
                                    " (" + jugador.getNombreEquipo() + ")";
                        comboJugador.addItem(item);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    
    public void recargarDatos() {
        cargarDatos();
        cargarCombos();
    }
    
    public void cargarDatos() {
        new Thread(() -> {
            try {
                List<Gol> goles = golC.obtenerTodosGoles();
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    for (Gol gol : goles) {
                        Object[] row = {
                            gol.getIdGol(),
                            gol.getNombreEquipo(), // Ahora muestra "EquipoA vs EquipoB"
                            gol.getNombreJugador(), // Ahora muestra "Nombre Apellido"
                            gol.getMinuto() + "'",
                            gol.getDescripcion() != null ? gol.getDescripcion() : ""
                        };
                        tableModel.addRow(row);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    
    private void agregarGol() {
        try {
            if (comboPartido.getSelectedIndex() <= 0 || comboJugador.getSelectedIndex() <= 0) {
                JOptionPane.showMessageDialog(this, "Seleccione partido y jugador", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Gol gol = new Gol();
            gol.setMinuto(Integer.parseInt(txtMinuto.getText().trim()));
            gol.setDescripcion(txtDescripcion.getText().trim());
            
            // Obtener ID del partido seleccionado
            String partidoStr = comboPartido.getSelectedItem().toString();
            int idPartido = Integer.parseInt(partidoStr.split(" - ")[0]);
            gol.setIdPartido(idPartido);
            
            // Obtener ID del jugador seleccionado
            String jugadorStr = comboJugador.getSelectedItem().toString();
            int idJugador = Integer.parseInt(jugadorStr.split(" - ")[0]);
            gol.setIdJugador(idJugador);
            
            if (golC.agregarGol(gol)) {
                JOptionPane.showMessageDialog(this, "Gol agregado exitosamente");
                cargarDatos();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al agregar gol");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El minuto debe ser un número válido");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void actualizarGol() {
        if (golSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un gol para actualizar");
            return;
        }
        
        try {
            if (comboPartido.getSelectedIndex() <= 0 || comboJugador.getSelectedIndex() <= 0) {
                JOptionPane.showMessageDialog(this, "Seleccione partido y jugador");
                return;
            }
            
            Gol gol = new Gol();
            gol.setIdGol(golSeleccionadoId);
            gol.setMinuto(Integer.parseInt(txtMinuto.getText().trim()));
            gol.setDescripcion(txtDescripcion.getText().trim());
            
            // Obtener ID del partido seleccionado
            String partidoStr = comboPartido.getSelectedItem().toString();
            int idPartido = Integer.parseInt(partidoStr.split(" - ")[0]);
            gol.setIdPartido(idPartido);
            
            // Obtener ID del jugador seleccionado
            String jugadorStr = comboJugador.getSelectedItem().toString();
            int idJugador = Integer.parseInt(jugadorStr.split(" - ")[0]);
            gol.setIdJugador(idJugador);
            
            if (golC.actualizarGol(gol)) {
                JOptionPane.showMessageDialog(this, "Gol actualizado exitosamente");
                cargarDatos();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar gol");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El minuto debe ser un número válido");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void eliminarGol() {
        if (golSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un gol para eliminar");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar este gol?", 
            "Confirmar", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (golC.eliminarGol(golSeleccionadoId)) {
                JOptionPane.showMessageDialog(this, "Gol eliminado exitosamente");
                cargarDatos();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar gol");
            }
        }
    }
    
    private void seleccionarGol() {
        int row = tablaGoles.getSelectedRow();
        if (row >= 0) {
            golSeleccionadoId = (int) tableModel.getValueAt(row, 0);
            
            // Obtener el gol completo para llenar los combos
            new Thread(() -> {
                try {
                    // Por simplicidad, solo llenamos minuto y descripción
                    // Los combos se mantienen como están
                    String minuto = tableModel.getValueAt(row, 3).toString().replace("'", "");
                    String descripcion = tableModel.getValueAt(row, 4).toString();
                    
                    SwingUtilities.invokeLater(() -> {
                        txtMinuto.setText(minuto);
                        txtDescripcion.setText(descripcion);
                        
                        // Habilitar botones de actualizar/eliminar
                        btnActualizar.setEnabled(true);
                        btnEliminar.setEnabled(true);
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    }
    
    private void limpiarFormulario() {
        txtMinuto.setText("");
        txtDescripcion.setText("");
        comboPartido.setSelectedIndex(0);
        comboJugador.setSelectedIndex(0);
        golSeleccionadoId = -1;
        tablaGoles.clearSelection();
        
        // Deshabilitar botones de actualizar/eliminar
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }
}