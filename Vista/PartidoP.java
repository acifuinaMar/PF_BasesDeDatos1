/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista;

/**
 *
 * @author maryori
 */

import Controlador.PartidoC;
import Controlador.EquipoC;
import Modelo.Partido;
import Modelo.Equipo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PartidoP extends JPanel {
    private Principal principal;
    private PartidoC partidoC;
    private EquipoC equipoC;
    private JTable tablaPartidos;
    private DefaultTableModel tableModel;
    private JComboBox<String> comboEquipoCasa, comboEquipoFuera;
    private JFormattedTextField txtFecha;
    private JTextField txtGolesCasa, txtGolesFuera, txtBuscar;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnBuscar, btnLimpiar, btnVolver;
    private int partidoSeleccionadoId = -1;
    private JCheckBox chkBuscarPorFecha;
    
    public PartidoP(Principal principal) {
        this.principal = principal;
        this.partidoC = new PartidoC();
        this.equipoC = new EquipoC();
        initializePanel();
        setupComponents();
        setupEvents();
        cargarDatos();
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
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Partido"));
        panel.setBackground(Color.WHITE);
        
        // Campos del formulario
        panel.add(new JLabel("Equipo Local:*"));
        comboEquipoCasa = new JComboBox<>();
        cargarEquipos();
        panel.add(comboEquipoCasa);
        
        panel.add(new JLabel("Equipo Visitante:*"));
        comboEquipoFuera = new JComboBox<>();
        cargarEquipos();
        panel.add(comboEquipoFuera);
        
        panel.add(new JLabel("Fecha (dd/MM/yyyy):*"));
        txtFecha = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
        txtFecha.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        panel.add(txtFecha);
        
        panel.add(new JLabel("Goles Equipo Local:*"));
        txtGolesCasa = new JTextField("0");
        panel.add(txtGolesCasa);
        
        panel.add(new JLabel("Goles Equipo Visitante:*"));
        txtGolesFuera = new JTextField("0");
        panel.add(txtGolesFuera);
        
        // Panel para mostrar resultado
        JPanel resultadoPanel = new JPanel(new FlowLayout());
        resultadoPanel.setBackground(Color.LIGHT_GRAY);
        JLabel lblResultado = new JLabel("Resultado: -");
        resultadoPanel.add(lblResultado);
        
        panel.add(new JLabel("Resultado:"));
        panel.add(resultadoPanel);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Partidos"));
        panel.setBackground(Color.WHITE);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        
        searchPanel.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(15);
        searchPanel.add(txtBuscar);
        
        chkBuscarPorFecha = new JCheckBox("Por fecha (YYYY-MM-DD)");
        searchPanel.add(chkBuscarPorFecha);
        
        btnBuscar = new JButton("Buscar");
        searchPanel.add(btnBuscar);
        
        String[] columnNames = {"ID", "Fecha", "Equipo Local", "Goles", "Equipo Visitante", "Goles", "Resultado"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaPartidos = new JTable(tableModel);
        tablaPartidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPartidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarPartido();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaPartidos);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.LIGHT_GRAY);
        
        btnAgregar = new JButton("Agregar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnBuscar = new JButton("Buscar por Fecha");
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
        btnAgregar.addActionListener(e -> agregarPartido());
        btnActualizar.addActionListener(e -> actualizarPartido());
        btnEliminar.addActionListener(e -> eliminarPartido());
        btnBuscar.addActionListener(e -> buscarPartido());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnVolver.addActionListener(e -> principal.showDashboard());
        
        // Actualizar resultado en tiempo real
        ActionListener actualizarResultado = e -> actualizarResultado();
        txtGolesCasa.addActionListener(actualizarResultado);
        txtGolesFuera.addActionListener(actualizarResultado);
    }
    
    private void validarEquiposDiferentes() {
        if (comboEquipoCasa.getSelectedIndex() > 0 && comboEquipoFuera.getSelectedIndex() > 0) {
            String equipoCasa = comboEquipoCasa.getSelectedItem().toString();
            String equipoFuera = comboEquipoFuera.getSelectedItem().toString();
            
            if (equipoCasa.equals(equipoFuera)) {
                JOptionPane.showMessageDialog(this, 
                    "No puede seleccionar el mismo equipo como local y visitante", 
                    "Validación", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void cargarEquipos() {
        new Thread(() -> {
            try {
                List<Equipo> equipos = equipoC.obtenerEquiposDisponibles();
                SwingUtilities.invokeLater(() -> {
                    comboEquipoCasa.removeAllItems();
                    comboEquipoFuera.removeAllItems();
                    
                    for (Equipo equipo : equipos) {
                        String item = equipo.getIdEquipo() + " - " + equipo.getNombre();
                        comboEquipoCasa.addItem(item);
                        comboEquipoFuera.addItem(item);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    
    public void recargarDatos(){
        cargarDatos();
        cargarEquipos();
    }
    
    public void cargarDatos() {
        new Thread(() -> {
            try {
                List<Partido> partidos = partidoC.obtenerTodosPartidos();
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    for (Partido partido : partidos) {
                        Object[] row = {
                            partido.getIdPartido(),
                            new SimpleDateFormat("dd/MM/yyyy").format(partido.getFecha()),
                            partido.getNombreEquipoCasa(),
                            partido.getGolesCasa(),
                            partido.getNombreEquipoFuera(),
                            partido.getGolesFuera(),
                            partido.getResultado() + " (" + partido.getEquipoGanador() + ")"
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
    
    private void actualizarTabla(List<Partido> partidos) {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        for (Partido partido : partidos) {
            String resultado = partido.getGolesCasa() + " - " + partido.getGolesFuera();
            
            Object[] row = {
                partido.getIdPartido(),
                sdf.format(partido.getFecha()),
                partido.getNombreEquipoCasa(),
                partido.getGolesCasa(),
                partido.getGolesFuera(),
                partido.getNombreEquipoFuera(),
                resultado
            };
            tableModel.addRow(row);
        }
    }
    
    private void agregarPartido() {
        try {
            Partido partido = obtenerPartidoDesdeFormulario();
            if (partido != null) {
                if (partidoC.agregarPartido(partido)) {
                    JOptionPane.showMessageDialog(this, "Partido agregado exitosamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                    limpiarFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar partido", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarPartido() {
        if (partidoSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un partido para actualizar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Partido partido = obtenerPartidoDesdeFormulario();
            if (partido != null) {
                partido.setIdPartido(partidoSeleccionadoId);
                
                if (partidoC.actualizarPartido(partido)) {
                    JOptionPane.showMessageDialog(this, "Partido actualizado exitosamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                    limpiarFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar partido", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarPartido() {
        if (partidoSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un partido para eliminar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar este partido?\nEsta acción eliminará también los goles asociados.", 
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (partidoC.eliminarPartido(partidoSeleccionadoId)) {
                    JOptionPane.showMessageDialog(this, "Partido eliminado exitosamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                    limpiarFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar partido", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void buscarPartido() {
        String busqueda = txtBuscar.getText().trim();
        if (busqueda.isEmpty()) {
            cargarDatos(); // Si está vacío, cargar todos
            return;
        }
        new Thread(() -> {
            try {
                List<Partido> partidos;
                
                if (chkBuscarPorFecha.isSelected()) {
                    // Buscar por fecha
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date fecha = sdf.parse(busqueda);
                    partidos = partidoC.buscarPartidosPorFecha((java.sql.Date) fecha);
                } else {
                    // Buscar por nombre de equipo
                    partidos = partidoC.buscarPartidosPorEquipo(busqueda);
                }
                
                SwingUtilities.invokeLater(() -> {
                    if (partidos.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "No se encontraron partidos", 
                            "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
                    }
                    actualizarTabla(partidos);
                });
            } catch (java.text.ParseException ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use YYYY-MM-DD", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error en búsqueda: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
        
    }
    
    private void seleccionarPartido() {
        int selectedRow = tablaPartidos.getSelectedRow();
        if (selectedRow >= 0) {
            partidoSeleccionadoId = (int) tableModel.getValueAt(selectedRow, 0);
            // Cargar datos completos del partido seleccionado
            new Thread(() -> {
                List<Partido> partidos = partidoC.obtenerTodosPartidos();
                for (Partido partido : partidos) {
                    if (partido.getIdPartido() == partidoSeleccionadoId) {
                        SwingUtilities.invokeLater(() -> {
                            llenarFormulario(partido);
                        });
                        break;
                    }
                }
            }).start();
        }
    }
    
    private Partido obtenerPartidoDesdeFormulario() throws Exception {
        // Validaciones básicas
        if (comboEquipoCasa.getSelectedItem() == null || 
            comboEquipoFuera.getSelectedItem() == null ||
            txtFecha.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", 
                "Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        
        // Validar que no sea el mismo equipo
        if (comboEquipoCasa.getSelectedItem().equals(comboEquipoFuera.getSelectedItem())) {
            JOptionPane.showMessageDialog(this, "Un equipo no puede jugar contra sí mismo", 
                "Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        
        // Validar goles
        int golesCasa, golesFuera;
        try {
            golesCasa = Integer.parseInt(txtGolesCasa.getText().trim());
            golesFuera = Integer.parseInt(txtGolesFuera.getText().trim());
            
            if (golesCasa < 0 || golesFuera < 0) {
                JOptionPane.showMessageDialog(this, "Los goles no pueden ser negativos", 
                    "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Los goles deben ser números válidos", 
                "Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        
        Partido partido = new Partido();
        partido.setFecha(new SimpleDateFormat("dd/MM/yyyy").parse(txtFecha.getText()));
        partido.setGolesCasa(golesCasa);
        partido.setGolesFuera(golesFuera);
        
        // Obtener IDs de equipos
        String equipoCasaStr = comboEquipoCasa.getSelectedItem().toString();
        String equipoFueraStr = comboEquipoFuera.getSelectedItem().toString();
        
        int idEquipoCasa = Integer.parseInt(equipoCasaStr.split(" - ")[0]);
        int idEquipoFuera = Integer.parseInt(equipoFueraStr.split(" - ")[0]);
        
        partido.setIdEquipoCasa(idEquipoCasa);
        partido.setIdEquipoFuera(idEquipoFuera);
        
        return partido;
    }
    
    private void llenarFormulario(Partido partido) {
        // Seleccionar equipo local
        for (int i = 0; i < comboEquipoCasa.getItemCount(); i++) {
            if (comboEquipoCasa.getItemAt(i).startsWith(partido.getIdEquipoCasa() + " - ")) {
                comboEquipoCasa.setSelectedIndex(i);
                break;
            }
        }
        
        // Seleccionar equipo visitante
        for (int i = 0; i < comboEquipoFuera.getItemCount(); i++) {
            if (comboEquipoFuera.getItemAt(i).startsWith(partido.getIdEquipoFuera() + " - ")) {
                comboEquipoFuera.setSelectedIndex(i);
                break;
            }
        }
        
        txtFecha.setText(new SimpleDateFormat("dd/MM/yyyy").format(partido.getFecha()));
        txtGolesCasa.setText(String.valueOf(partido.getGolesCasa()));
        txtGolesFuera.setText(String.valueOf(partido.getGolesFuera()));
        
        actualizarResultado();
    }
    
    private void actualizarResultado() {
        try {
            int golesCasa = Integer.parseInt(txtGolesCasa.getText().trim());
            int golesFuera = Integer.parseInt(txtGolesFuera.getText().trim());
            
            String resultado = golesCasa + " - " + golesFuera;
            // Podríamos mostrar esto en un label si lo agregamos al formulario
            System.out.println("Resultado actualizado: " + resultado);
        } catch (NumberFormatException ex) {
            // Ignorar si los valores no son numéricos aún
        }
    }
    
    private void limpiarFormulario() {
        comboEquipoCasa.setSelectedIndex(0);
        comboEquipoFuera.setSelectedIndex(1); // Diferente al local por defecto
        txtFecha.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        txtGolesCasa.setText("0");
        txtGolesFuera.setText("0");
        partidoSeleccionadoId = -1;
        tablaPartidos.clearSelection();
    }
}
