/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista;

/**
 *
 * @author maryori
 */

import Controlador.EquipoC;
import Controlador.PresidenteC;
import Modelo.Equipo;
import Modelo.Presidente;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EquipoP extends JPanel {
    private Principal principal;
    private EquipoC equipoC;
    private PresidenteC presidenteC;
    private JTable tablaEquipos;
    private DefaultTableModel tableModel;
    private JTextField txtNombre, txtEstadio, txtAforo, txtFundacion;
    private JComboBox<String> comboDepartamento, comboPresidente;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnBuscar, btnLimpiar, btnVolver;
    private int equipoSeleccionadoId = -1;
    
    public EquipoP(Principal principal) {
        this.principal = principal;
        this.equipoC = new EquipoC();
        this.presidenteC = new PresidenteC();
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
        splitPane.setResizeWeight(0.3);
        
        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Equipo"));
        panel.setBackground(Color.WHITE);
        
        panel.add(new JLabel("Nombre:*"));
        txtNombre = new JTextField();
        panel.add(txtNombre);
        
        panel.add(new JLabel("Estadio:*"));
        txtEstadio = new JTextField();
        panel.add(txtEstadio);
        
        panel.add(new JLabel("Aforo:*"));
        txtAforo = new JTextField();
        panel.add(txtAforo);
        
        panel.add(new JLabel("Año Fundación:*"));
        txtFundacion = new JTextField();
        panel.add(txtFundacion);
        
        panel.add(new JLabel("Departamento:*"));
        comboDepartamento = new JComboBox<>(new String[]{
            "Guatemala", "Sacatepéquez", "Chimaltenango", "Escuintla", 
            "Santa Rosa", "Sololá", "Totonicapán", "Quetzaltenango",
            "Suchitepéquez", "Retalhuleu", "San Marcos", "Huehuetenango",
            "Quiché", "Baja Verapaz", "Alta Verapaz", "Petén",
            "Izabal", "Zacapa", "Chiquimula", "Jalapa", "Jutiapa"
        });
        panel.add(comboDepartamento);
        
        panel.add(new JLabel("Presidente:*"));
        comboPresidente = new JComboBox<>();
        cargarPresidentes();
        panel.add(comboPresidente);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Equipos"));
        panel.setBackground(Color.WHITE);
        
        String[] columnNames = {"ID", "Nombre", "Estadio", "Aforo", "Fundación", "Departamento", "Presidente"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaEquipos = new JTable(tableModel);
        tablaEquipos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEquipos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarEquipo();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaEquipos);
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
        btnAgregar.addActionListener(e -> agregarEquipo());
        btnActualizar.addActionListener(e -> actualizarEquipo());
        btnEliminar.addActionListener(e -> eliminarEquipo());
        btnBuscar.addActionListener(e -> buscarEquipo());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnVolver.addActionListener(e -> principal.showDashboard());
    }
    
    private void cargarPresidentes() {
        new Thread(() -> {
            try {
                List<Presidente> presidentes = presidenteC.obtenerTodosPresidentes();
                SwingUtilities.invokeLater(() -> {
                    comboPresidente.removeAllItems();
                    for (Presidente presidente : presidentes) {
                        comboPresidente.addItem(presidente.getIdPresidente() + " - " + 
                            presidente.getNombre1() + " " + presidente.getApellido1());
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
                List<Equipo> equipos = equipoC.obtenerTodosEquipos();
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    for (Equipo equipo : equipos) {
                        Object[] row = {
                            equipo.getIdEquipo(),
                            equipo.getNombre(),
                            equipo.getEstadio(),
                            equipo.getAforo(),
                            equipo.getFundacion(),
                            equipo.getDepartamento(),
                            equipo.getNombrePresidente()
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
    
    private void agregarEquipo() {
        try {
            Equipo equipo = obtenerEquipoDesdeFormulario();
            if (equipo != null) {
                if (equipoC.agregarEquipo(equipo)) {
                    JOptionPane.showMessageDialog(this, "Equipo agregado exitosamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                    limpiarFormulario();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarEquipo() {
        // Similar a agregar pero con actualización
        JOptionPane.showMessageDialog(this, "Funcionalidad en desarrollo", 
            "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void eliminarEquipo() {
        if (equipoSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un equipo para eliminar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar este equipo?", 
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Funcionalidad en desarrollo", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void buscarEquipo() {
        String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre del equipo a buscar:");
        if (nombre != null && !nombre.trim().isEmpty()) {
            // Implementar búsqueda
            JOptionPane.showMessageDialog(this, "Funcionalidad en desarrollo", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void seleccionarEquipo() {
        int selectedRow = tablaEquipos.getSelectedRow();
        if (selectedRow >= 0) {
            equipoSeleccionadoId = (int) tableModel.getValueAt(selectedRow, 0);
            // Llenar formulario con datos del equipo seleccionado
            llenarFormulario(selectedRow);
        }
    }
    
    private Equipo obtenerEquipoDesdeFormulario() {
        if (txtNombre.getText().trim().isEmpty() || 
            txtEstadio.getText().trim().isEmpty() ||
            txtAforo.getText().trim().isEmpty() ||
            txtFundacion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", 
                "Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        
        try {
            Equipo equipo = new Equipo();
            equipo.setNombre(txtNombre.getText().trim());
            equipo.setEstadio(txtEstadio.getText().trim());
            equipo.setAforo(Integer.parseInt(txtAforo.getText().trim()));
            equipo.setFundacion(Integer.parseInt(txtFundacion.getText().trim()));
            equipo.setDepartamento(comboDepartamento.getSelectedItem().toString());
            
            // Obtener ID del presidente seleccionado
            String presidenteStr = comboPresidente.getSelectedItem().toString();
            int idPresidente = Integer.parseInt(presidenteStr.split(" - ")[0]);
            equipo.setIdPresidente(idPresidente);
            
            return equipo;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Aforo y Fundación deben ser números válidos", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    private void llenarFormulario(int row) {
        txtNombre.setText(tableModel.getValueAt(row, 1).toString());
        txtEstadio.setText(tableModel.getValueAt(row, 2).toString());
        txtAforo.setText(tableModel.getValueAt(row, 3).toString());
        txtFundacion.setText(tableModel.getValueAt(row, 4).toString());
        comboDepartamento.setSelectedItem(tableModel.getValueAt(row, 5).toString());
        // comboPresidente se mantiene como está por simplicidad
    }
    
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtEstadio.setText("");
        txtAforo.setText("");
        txtFundacion.setText("");
        comboDepartamento.setSelectedIndex(0);
        equipoSeleccionadoId = -1;
        tablaEquipos.clearSelection();
    }
}
