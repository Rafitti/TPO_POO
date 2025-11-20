package UI;

import Clases.Entidades.*;
import Clases.Interfaces.IHabitacionService;
import Clases.Exceptions.DatabaseException;

import java.util.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.*;


public class HabitacionesUI {
  private IHabitacionService habitacionService;
  private JFrame frame;
  private JPanel fondo;
  private JLabel titulo;
  private JButton volverButton;
  private JTable tablaHabitaciones;


  public HabitacionesUI(IHabitacionService habitacionService, MenuUI menu) {
    this.habitacionService = habitacionService;
    this.frame = new JFrame("Gestión de Habitaciones");
    this.frame.setSize(500, 500);
    this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.frame.setLayout(null);
    this.frame.setResizable(false);

    this.fondo = new JPanel();
    this.fondo.setBounds(0, 0, 500, 500);
    this.fondo.setBackground(java.awt.Color.LIGHT_GRAY);
    this.fondo.setLayout(null);
    this.frame.add(fondo);

    this.titulo = new JLabel("Gestión de Habitaciones");
    this.titulo.setBounds(200, 30, 150, 30);
    this.fondo.add(titulo);

    this.volverButton = new JButton("Volver al Menú");
    this.volverButton.setBounds(10, 10, 150, 30);
    this.volverButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        frame.dispose();
        menu.init();
      }
    });
    this.fondo.add(volverButton);

    tablaHabitaciones = new JTable();
    
    JScrollPane scrollPane = new JScrollPane(tablaHabitaciones);
    scrollPane.setBounds(50, 80, 400, 300);
    fondo.add(scrollPane);

  }

  public void init() {
    try {
      List<Habitacion> habitaciones = habitacionService.obtenerTodas();

      DefaultTableModel model = new DefaultTableModel();
      model.addColumn("Id");
      model.addColumn("Número");
      model.addColumn("Tipo");
      model.addColumn("Falta Limpiar");
      model.addColumn("Acciones");

      for (Habitacion hab : habitaciones) {
        JButton btn = new JButton("Editar");
        model.addRow(new Object[]{hab.getId(), hab.getNumero(), hab.getTipo(), hab.isFaltaLimpiar(), btn});
      }

      tablaHabitaciones.setModel(model);  

      // Ocultar columna Id 
      if (tablaHabitaciones.getColumnModel().getColumnCount() > 0) {
        tablaHabitaciones.getColumnModel().getColumn(0).setMinWidth(0);
        tablaHabitaciones.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaHabitaciones.getColumnModel().getColumn(0).setWidth(0);
        tablaHabitaciones.getColumnModel().getColumn(0).setPreferredWidth(0);
      }

      int accionesCol = tablaHabitaciones.getColumnModel().getColumnCount() - 1;
    tablaHabitaciones.getColumnModel().getColumn(accionesCol).setCellRenderer(new ActionCellRenderer());
    tablaHabitaciones.getColumnModel().getColumn(accionesCol).setCellEditor(new ActionCellEditor(new JCheckBox(), habitacionService, tablaHabitaciones));
      
      frame.setVisible(true);
    } catch (DatabaseException e) {
        JOptionPane.showMessageDialog(frame, 
            "Error al cargar habitaciones desde la base de datos:\n" +
            "Operación: " + e.getOperation() + "\n" +
            "Tabla: " + e.getTable() + "\n" +
            "Mensaje: " + e.getMessage(),
            "Error de Base de Datos",
            JOptionPane.ERROR_MESSAGE);
    }
  }

  // Renderer para mostrar el botón en la tabla
  private static class ActionCellRenderer extends JButton implements TableCellRenderer {
    public ActionCellRenderer() { setOpaque(true); }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (value instanceof JButton) {
        return (JButton) value;
      }
      setText(value == null ? "" : value.toString());
      return this;
    }
  }

  // Editor que maneja clicks en el botón y abre diálogo para editar FaltaLimpiar
  private static class ActionCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    private JButton button;
    private JTable table;
    private IHabitacionService habitacionService;
    private boolean currentValue;
    private int currentId;

  public ActionCellEditor(JCheckBox checkBox, IHabitacionService habitacionService, JTable table) {
      this.button = new JButton("Editar");
      this.button.addActionListener(this);
      this.habitacionService = habitacionService;
      this.table = table;
    }

    @Override
    public Object getCellEditorValue() {
      return button;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      Object idObj = table.getModel().getValueAt(row, 0);
      Object faltaObj = table.getModel().getValueAt(row, 3);
      if (idObj instanceof Number) currentId = ((Number) idObj).intValue();
      else currentId = Integer.parseInt(idObj.toString());
      currentValue = (faltaObj instanceof Boolean) ? (Boolean) faltaObj : Boolean.parseBoolean(faltaObj.toString());
      return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      JCheckBox chk = new JCheckBox("Falta Limpiar", currentValue);
      
      boolean exitoso = false;
      while (!exitoso) {
        int option = JOptionPane.showConfirmDialog(table, chk, "Editar FaltaLimpiar", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
          break; // Usuario canceló
        }
        
        boolean newVal = chk.isSelected();
        try {
          habitacionService.actualizarEstadoLimpieza(currentId, newVal);
          // la columna 3 es Falta Limpiar
          int editingRow = table.getSelectedRow();
          if (editingRow >= 0) table.getModel().setValueAt(newVal, editingRow, 3);
          exitoso = true; // Éxito, salir del bucle
        } catch (DatabaseException ex) {
          JOptionPane.showMessageDialog(table, 
              "Error de base de datos al actualizar:\n" +
              "Operación: " + ex.getOperation() + "\n" +
              "Tabla: " + ex.getTable() + "\n" +
              "Mensaje: " + ex.getMessage(),
              "Error de Base de Datos",
              JOptionPane.ERROR_MESSAGE);
          exitoso = true; // Error de BD
        }
      }
      fireEditingStopped();
    }
  }
}
