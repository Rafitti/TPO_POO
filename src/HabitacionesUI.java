import java.util.*;
import java.awt.Component;
import java.awt.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.*;


public class HabitacionesUI {
  private SistemaHabitaciones sh;
  private MenuUI menu;
  private JFrame frame;
  private JPanel fondo;
  private JLabel titulo;
  private JButton volverButton;
  private JTable tablaHabitaciones;


  public HabitacionesUI(SistemaHabitaciones sh,MenuUI menu) {
    this.sh = sh;
    this.menu = menu;
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
    List<Habitacion> habitaciones = sh.getAll();

    // Aquí se llenaría la tabla con los datos de las habitaciones
    DefaultTableModel model = new DefaultTableModel();
    // Añadimos columna Id oculta para poder identificar la fila en la DB
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
    tablaHabitaciones.getColumnModel().getColumn(accionesCol).setCellRenderer(new ButtonRenderer());
    tablaHabitaciones.getColumnModel().getColumn(accionesCol).setCellEditor(new ButtonEditor(new JCheckBox(), sh, tablaHabitaciones));
    
    frame.setVisible(true);
  }

  // Renderer para mostrar el botón en la tabla
  private static class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() { setOpaque(true); }
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
  private static class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    private JButton button;
    private JTable table;
    private SistemaHabitaciones sh;
    private boolean currentValue;
    private int currentId;

    public ButtonEditor(JCheckBox checkBox, SistemaHabitaciones sh, JTable table) {
      this.button = new JButton("Editar");
      this.button.addActionListener(this);
      this.sh = sh;
      this.table = table;
    }

    @Override
    public Object getCellEditorValue() {
      return button;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      // Obtener id y estado actual de la fila
      Object idObj = table.getModel().getValueAt(row, 0);
      Object faltaObj = table.getModel().getValueAt(row, 3);
      if (idObj instanceof Number) currentId = ((Number) idObj).intValue();
      else currentId = Integer.parseInt(idObj.toString());
      currentValue = (faltaObj instanceof Boolean) ? (Boolean) faltaObj : Boolean.parseBoolean(faltaObj.toString());
      return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      // Abrir diálogo para cambiar estado
      JCheckBox chk = new JCheckBox("Falta Limpiar", currentValue);
      int option = JOptionPane.showConfirmDialog(table, chk, "Editar FaltaLimpiar", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
      if (option == JOptionPane.OK_OPTION) {
        boolean newVal = chk.isSelected();
        // Actualizar en DB
        Map<String,Object> values = new HashMap<>();
        values.put("FaltaLimpiar", newVal ? 1 : 0);
        boolean ok = sh.update(currentId, values);
        if (ok) {
          // Actualizar modelo visual
          // la columna 3 es Falta Limpiar
          int editingRow = table.getSelectedRow();
          if (editingRow >= 0) table.getModel().setValueAt(newVal, editingRow, 3);
        } else {
          JOptionPane.showMessageDialog(table, "Error al actualizar en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
      fireEditingStopped();
    }
  }
}
