/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista;

/**
 *
 * @author maryori
 */

import Controlador.JugadorC;
import Controlador.EquipoC;
import Modelo.Jugador;
import Modelo.Equipo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class JugadorP extends JPanel {
    private Principal principal;
    private JugadorC jugadorC;
    private EquipoC equipoC;
    private JTable tablaJugadores;
    private DefaultTableModel tableModel;
    private JTextField txtNombre1, txtNombre2, txtNombre3, txtApellido1, txtApellido2;
    private JTextField txtMunicipio;
    private JFormattedTextField txtFechaNac;
    private JComboBox<String> comboPosicion, comboEquipo;
    private JTextArea txtCorreos;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnBuscar, btnLimpiar, btnVolver;
    private int jugadorSeleccionadoId = -1;
    
    public JugadorP(Principal principal) {
        this.principal = principal;
        this.jugadorC = new JugadorC();
        this.equipoC = new EquipoC();
        initializePanel();
        setupComponents();
        setupEvents();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
    }
    
    private void setupComponents() {
        // Panel superior - Formulario
        JPanel formPanel = createFormPanel();
        
        // Panel central - Tabla
        JPanel tablePanel = createTablePanel();
        
        // Panel inferior - Botones
        JPanel buttonPanel = createButtonPanel();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tablePanel);
        splitPane.setResizeWeight(0.4);
        
        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Jugador"));
        panel.setBackground(Color.WHITE);
        
        // Campos del formulario
        panel.add(new JLabel("Primer Nombre:*"));
        txtNombre1 = new JTextField();
        panel.add(txtNombre1);
        
        panel.add(new JLabel("Segundo Nombre:"));
        txtNombre2 = new JTextField();
        panel.add(txtNombre2);
        
        panel.add(new JLabel("Tercer Nombre:"));
        txtNombre3 = new JTextField();
        panel.add(txtNombre3);
        
        panel.add(new JLabel("Primer Apellido:*"));
        txtApellido1 = new JTextField();
        panel.add(txtApellido1);
        
        panel.add(new JLabel("Segundo Apellido:*"));
        txtApellido2 = new JTextField();
        panel.add(txtApellido2);
        
        panel.add(new JLabel("Fecha Nacimiento (dd/MM/yyyy):*"));
        txtFechaNac = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
        panel.add(txtFechaNac);
        
        panel.add(new JLabel("Municipio:*"));
        txtMunicipio = new JTextField();
        panel.add(txtMunicipio);
        
        panel.add(new JLabel("Posición:*"));
        comboPosicion = new JComboBox<>(new String[]{
            "Portero", "Defensa", "Centrocampista", "Delantero"
        });
        panel.add(comboPosicion);
        
        panel.add(new JLabel("Equipo:*"));
        comboEquipo = new JComboBox<>();
        cargarEquipos();
        panel.add(comboEquipo);
        
        panel.add(new JLabel("Correos (uno por línea):"));
        txtCorreos = new JTextArea(3, 20);
        JScrollPane scrollCorreos = new JScrollPane(txtCorreos);
        panel.add(scrollCorreos);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Jugadores"));
        panel.setBackground(Color.WHITE);
        
        String[] columnNames = {"ID", "Nombre", "Apellidos", "Fecha Nac", "Municipio", "Posición", "Equipo", "Correos"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaJugadores = new JTable(tableModel);
        tablaJugadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaJugadores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarJugador();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaJugadores);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.LIGHT_GRAY);
        
        btnAgregar = new JButton("Agregar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnBuscar = new JButton("Buscar por Nombre");
        btnLimpiar = new JButton("Limpiar");
        btnVolver = new JButton("Volver al Dashboard");
        
        panel.add(btnAgregar);
        panel.add(btnActualizar);
        panel.add(btnEliminar);
        panel.add(btnBuscar);
        panel.add(btnLimpiar);
        panel.add(btnVolver);
        
        return panel;
    }
    
    private void setupEvents() {
        btnAgregar.addActionListener(e -> agregarJugador());
        btnActualizar.addActionListener(e -> actualizarJugador());
        btnEliminar.addActionListener(e -> eliminarJugador());
        btnBuscar.addActionListener(e -> buscarJugador());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnVolver.addActionListener(e -> principal.showDashboard());
    }
    
    private void cargarEquipos() {
        new Thread(() -> {
            try {
                List<Equipo> equipos = equipoC.obtenerEquiposDisponibles();
                SwingUtilities.invokeLater(() -> {
                    comboEquipo.removeAllItems();
                    for (Equipo equipo : equipos) {
                        comboEquipo.addItem(equipo.getIdEquipo() + " - " + equipo.getNombre());
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    
    public void cargarDatos() {
        new Thread(() -> {
            try {
                List<Jugador> jugadores = jugadorC.obtenerTodosJugadores();
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    for (Jugador jugador : jugadores) {
                        String correosStr = String.join(", ", jugador.getCorreos());
                        Object[] row = {
                            jugador.getIdJugador(),
                            jugador.getNombre1() + " " + 
                                (jugador.getNombre2() != null ? jugador.getNombre2() + " " : "") +
                                (jugador.getNombre3() != null ? jugador.getNombre3() : ""),
                            jugador.getApellido1() + " " + jugador.getApellido2(),
                            new SimpleDateFormat("dd/MM/yyyy").format(jugador.getFechaNac()),
                            jugador.getMunicipio(),
                            jugador.getPosicion(),
                            jugador.getNombreEquipo(),
                            correosStr
                        };
                        tableModel.addRow(row);
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error al cargar datos: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }
    
    private void agregarJugador() {
        try {
            Jugador jugador = obtenerJugadorDesdeFormulario();
            if (jugador != null) {
                if (jugadorC.agregarJugador(jugador)) {
                    JOptionPane.showMessageDialog(this, "Jugador agregado exitosamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                    limpiarFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar jugador", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarJugador() {
        if (jugadorSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un jugador para actualizar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Jugador jugador = obtenerJugadorDesdeFormulario();
            if (jugador != null) {
                // Para actualizar necesitaríamos un método en el controller
                JOptionPane.showMessageDialog(this, "Funcionalidad de actualización en desarrollo", 
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarJugador() {
        if (jugadorSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un jugador para eliminar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar este jugador?", 
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Para eliminar necesitaríamos un método en el controller
            JOptionPane.showMessageDialog(this, "Funcionalidad de eliminación en desarrollo", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void buscarJugador() {
        String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre a buscar:");
        if (nombre != null && !nombre.trim().isEmpty()) {
            new Thread(() -> {
                try {
                    List<Jugador> jugadores = jugadorC.obtenerTodosJugadores();
                    for (Jugador jugador : jugadores) {
                        String nombreCompleto = jugador.getNombre1() + " " + jugador.getApellido1();
                        if (nombreCompleto.toLowerCase().contains(nombre.toLowerCase().trim())) {
                            SwingUtilities.invokeLater(() -> {
                                llenarFormulario(jugador);
                                JOptionPane.showMessageDialog(this, "Jugador encontrado", 
                                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            });
                            return;
                        }
                    }
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "No se encontró jugador con ese nombre", 
                            "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Error en búsqueda: " + ex.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
        }
    }
    
    private void seleccionarJugador() {
        int selectedRow = tablaJugadores.getSelectedRow();
        if (selectedRow >= 0) {
            jugadorSeleccionadoId = (int) tableModel.getValueAt(selectedRow, 0);
            // Cargar datos completos del jugador seleccionado
            new Thread(() -> {
                // Necesitaríamos un método en el controller para obtener jugador por ID
                List<Jugador> jugadores = jugadorC.obtenerTodosJugadores();
                for (Jugador jugador : jugadores) {
                    if (jugador.getIdJugador() == jugadorSeleccionadoId) {
                        SwingUtilities.invokeLater(() -> {
                            llenarFormulario(jugador);
                        });
                        break;
                    }
                }
            }).start();
        }
    }
    
    private Jugador obtenerJugadorDesdeFormulario() throws Exception {
        // Validaciones básicas
        if (txtNombre1.getText().trim().isEmpty() || 
            txtApellido1.getText().trim().isEmpty() || 
            txtApellido2.getText().trim().isEmpty() ||
            txtMunicipio.getText().trim().isEmpty() ||
            txtFechaNac.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Los campos marcados con * son obligatorios", 
                "Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        
        // Validar edad
        java.util.Date fechaNac = new SimpleDateFormat("dd/MM/yyyy").parse(txtFechaNac.getText());
        if (!jugadorC.validarEdadJugador(fechaNac)) {
            JOptionPane.showMessageDialog(this, "El jugador debe tener al menos 16 años", 
                "Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        
        Jugador jugador = new Jugador();
        jugador.setNombre1(txtNombre1.getText().trim());
        jugador.setNombre2(txtNombre2.getText().trim().isEmpty() ? null : txtNombre2.getText().trim());
        jugador.setNombre3(txtNombre3.getText().trim().isEmpty() ? null : txtNombre3.getText().trim());
        jugador.setApellido1(txtApellido1.getText().trim());
        jugador.setApellido2(txtApellido2.getText().trim());
        jugador.setFechaNac(fechaNac);
        jugador.setMunicipio(txtMunicipio.getText().trim());
        jugador.setPosicion(comboPosicion.getSelectedItem().toString());
        
        // Obtener ID del equipo seleccionado
        String equipoStr = comboEquipo.getSelectedItem().toString();
        int idEquipo = Integer.parseInt(equipoStr.split(" - ")[0]);
        jugador.setIdEquipo(idEquipo);
        
        // Procesar correos
        String[] correosArray = txtCorreos.getText().split("\n");
        for (String correo : correosArray) {
            if (!correo.trim().isEmpty()) {
                jugador.agregarCorreo(correo.trim());
            }
        }
        
        return jugador;
    }
    
    private void llenarFormulario(Jugador jugador) {
        txtNombre1.setText(jugador.getNombre1());
        txtNombre2.setText(jugador.getNombre2() != null ? jugador.getNombre2() : "");
        txtNombre3.setText(jugador.getNombre3() != null ? jugador.getNombre3() : "");
        txtApellido1.setText(jugador.getApellido1());
        txtApellido2.setText(jugador.getApellido2());
        txtFechaNac.setText(new SimpleDateFormat("dd/MM/yyyy").format(jugador.getFechaNac()));
        txtMunicipio.setText(jugador.getMunicipio());
        comboPosicion.setSelectedItem(jugador.getPosicion());
        
        // Seleccionar equipo en combo
        for (int i = 0; i < comboEquipo.getItemCount(); i++) {
            if (comboEquipo.getItemAt(i).startsWith(jugador.getIdEquipo() + " - ")) {
                comboEquipo.setSelectedIndex(i);
                break;
            }
        }
        
        // Llenar correos
        StringBuilder correosBuilder = new StringBuilder();
        for (String correo : jugador.getCorreos()) {
            correosBuilder.append(correo).append("\n");
        }
        txtCorreos.setText(correosBuilder.toString());
    }
    
    private void limpiarFormulario() {
        txtNombre1.setText("");
        txtNombre2.setText("");
        txtNombre3.setText("");
        txtApellido1.setText("");
        txtApellido2.setText("");
        txtFechaNac.setText("");
        txtMunicipio.setText("");
        comboPosicion.setSelectedIndex(0);
        comboEquipo.setSelectedIndex(0);
        txtCorreos.setText("");
        jugadorSeleccionadoId = -1;
        tablaJugadores.clearSelection();
    }
}
