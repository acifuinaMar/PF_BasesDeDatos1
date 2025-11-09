/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista;

/**
 *
 * @author maryori
 */

import Controlador.PresidenteC;
import Modelo.Presidente;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class PresidenteP extends JPanel {
    private Principal principal;
    private PresidenteC controlador;
    private JTable tablaPresidentes;
    private DefaultTableModel tableModel;
    private JTextField txtDpi, txtNombre1, txtNombre2, txtNombre3, txtApellido1, txtApellido2;
    private JTextField txtMunicipio, txtAnioEleccion;
    private JFormattedTextField txtFechaNac;
    private JTextArea txtCorreos;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnBuscar, btnLimpiar, btnVolver;
    private int presidenteSeleccionadoId = -1;
    
    public PresidenteP(Principal principal) {
        this.principal = principal;
        this.controlador = new PresidenteC();
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
        
        // Dividir el espacio
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tablePanel);
        splitPane.setResizeWeight(0.4);
        
        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Presidente"));
        panel.setBackground(Color.WHITE);
        
        // Campos del formulario
        panel.add(new JLabel("DPI:"));
        txtDpi = new JTextField();
        panel.add(txtDpi);
        
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
        
        panel.add(new JLabel("Año Elección:*"));
        txtAnioEleccion = new JTextField();
        panel.add(txtAnioEleccion);
        
        panel.add(new JLabel("Correos (uno por línea):"));
        txtCorreos = new JTextArea(3, 20);
        JScrollPane scrollCorreos = new JScrollPane(txtCorreos);
        panel.add(scrollCorreos);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Presidentes"));
        panel.setBackground(Color.WHITE);
        
        // Modelo de tabla
        String[] columnNames = {"ID", "DPI", "Nombre", "Apellidos", "Fecha Nac", "Municipio", "Año Elección", "Correos"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaPresidentes = new JTable(tableModel);
        tablaPresidentes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPresidentes.getColumnModel().getColumn(7).setPreferredWidth(150);
        tablaPresidentes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarPresidente();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaPresidentes);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.LIGHT_GRAY);
        
        btnAgregar = new JButton("Agregar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnBuscar = new JButton("Buscar por DPI");
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
        btnAgregar.addActionListener(e -> agregarPresidente());
        btnActualizar.addActionListener(e -> actualizarPresidente());
        btnEliminar.addActionListener(e -> eliminarPresidente());
        btnBuscar.addActionListener(e -> buscarPresidente());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnVolver.addActionListener(e -> principal.showDashboard());
    }
    
    public void cargarDatos() {
        new Thread(() -> {
            try {
                List<Presidente> presidentes = controlador.obtenerTodosPresidentes();
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    for (Presidente presidente : presidentes) {
                        String correosStr = String.join(", ", presidente.getCorreos());
                        Object[] row = {
                            presidente.getIdPresidente(),
                            presidente.getDpi(),
                            presidente.getNombre1() + " " + 
                                (presidente.getNombre2() != null ? presidente.getNombre2() + " " : "") +
                                (presidente.getNombre3() != null ? presidente.getNombre3() : ""),
                            presidente.getApellido1() + " " + presidente.getApellido2(),
                            new SimpleDateFormat("dd/MM/yyyy").format(presidente.getFechaNac()),
                            presidente.getMunicipio(),
                            presidente.getAñoEleccion(),
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
    
    private void agregarPresidente() {
        try {
            Presidente presidente = obtenerPresidenteDesdeFormulario();
            if (presidente != null) {
                if (controlador.agregarPresidente(presidente)) {
                    JOptionPane.showMessageDialog(this, "Presidente agregado exitosamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                    limpiarFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar presidente", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarPresidente() {
        if (presidenteSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un presidente para actualizar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Presidente presidente = obtenerPresidenteDesdeFormulario();
            if (presidente != null) {
                presidente.setIdPresidente(presidenteSeleccionadoId);
                if (controlador.actualizarPresidente(presidente)) {
                    JOptionPane.showMessageDialog(this, "Presidente actualizado exitosamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                    limpiarFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar presidente", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarPresidente() {
        if (presidenteSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un presidente para eliminar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar este presidente?", 
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controlador.eliminarPresidente(presidenteSeleccionadoId)) {
                JOptionPane.showMessageDialog(this, "Presidente eliminado exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar presidente", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void buscarPresidente() {
        String dpi = JOptionPane.showInputDialog(this, "Ingrese el DPI a buscar:");
        if (dpi != null && !dpi.trim().isEmpty()) {
            new Thread(() -> {
                try {
                    // Buscar en la lista local
                    List<Presidente> presidentes = controlador.obtenerTodosPresidentes();
                    for (Presidente presidente : presidentes) {
                        if (presidente.getDpi().equals(dpi.trim())) {
                            SwingUtilities.invokeLater(() -> {
                                llenarFormulario(presidente);
                                JOptionPane.showMessageDialog(this, "Presidente encontrado", 
                                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            });
                            return;
                        }
                    }
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "No se encontró presidente con ese DPI", 
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
    
    private void seleccionarPresidente() {
        int selectedRow = tablaPresidentes.getSelectedRow();
        if (selectedRow >= 0) {
            presidenteSeleccionadoId = (int) tableModel.getValueAt(selectedRow, 0);
            // Cargar datos completos del presidente seleccionado
            new Thread(() -> {
                Presidente presidente = controlador.obtenerPresidentePorId(presidenteSeleccionadoId);
                if (presidente != null) {
                    SwingUtilities.invokeLater(() -> {
                        llenarFormulario(presidente);
                    });
                }
            }).start();
        }
    }
    
    private Presidente obtenerPresidenteDesdeFormulario() throws Exception {
        // Validaciones básicas
        if (txtNombre1.getText().trim().isEmpty() || 
            txtApellido1.getText().trim().isEmpty() || 
            txtApellido2.getText().trim().isEmpty() ||
            txtDpi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Los campos marcados con * son obligatorios", 
                "Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        
        Presidente presidente = new Presidente();
        presidente.setDpi(txtDpi.getText().trim());
        presidente.setNombre1(txtNombre1.getText().trim());
        presidente.setNombre2(txtNombre2.getText().trim().isEmpty() ? null : txtNombre2.getText().trim());
        presidente.setNombre3(txtNombre3.getText().trim().isEmpty() ? null : txtNombre3.getText().trim());
        presidente.setApellido1(txtApellido1.getText().trim());
        presidente.setApellido2(txtApellido2.getText().trim());
        presidente.setFechaNac(new SimpleDateFormat("dd/MM/yyyy").parse(txtFechaNac.getText()));
        presidente.setMunicipio(txtMunicipio.getText().trim());
        presidente.setAñoEleccion(Integer.parseInt(txtAnioEleccion.getText().trim()));
        
        // Procesar correos
        String[] correosArray = txtCorreos.getText().split("\n");
        for (String correo : correosArray) {
            if (!correo.trim().isEmpty()) {
                presidente.agregarCorreo(correo.trim());
            }
        }
        
        return presidente;
    }
    
    private void llenarFormulario(Presidente presidente) {
        txtDpi.setText(presidente.getDpi());
        txtNombre1.setText(presidente.getNombre1());
        txtNombre2.setText(presidente.getNombre2() != null ? presidente.getNombre2() : "");
        txtNombre3.setText(presidente.getNombre3() != null ? presidente.getNombre3() : "");
        txtApellido1.setText(presidente.getApellido1());
        txtApellido2.setText(presidente.getApellido2());
        txtFechaNac.setText(new SimpleDateFormat("dd/MM/yyyy").format(presidente.getFechaNac()));
        txtMunicipio.setText(presidente.getMunicipio());
        txtAnioEleccion.setText(String.valueOf(presidente.getAñoEleccion()));
        
        // Llenar correos
        StringBuilder correosBuilder = new StringBuilder();
        for (String correo : presidente.getCorreos()) {
            correosBuilder.append(correo).append("\n");
        }
        txtCorreos.setText(correosBuilder.toString());
    }
    
    private void limpiarFormulario() {
        txtDpi.setText("");
        txtNombre1.setText("");
        txtNombre2.setText("");
        txtNombre3.setText("");
        txtApellido1.setText("");
        txtApellido2.setText("");
        txtFechaNac.setText("");
        txtMunicipio.setText("");
        txtAnioEleccion.setText("");
        txtCorreos.setText("");
        presidenteSeleccionadoId = -1;
        tablaPresidentes.clearSelection();
    }
}
