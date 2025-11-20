package UI;

import Clases.Entidades.*;
import Clases.Interfaces.*;
import Clases.Exceptions.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.time.*;
import java.time.format.*;

public class ReservasUI {
  private final IReservaService reservaService;
  private final IClienteService clienteService;
  private final IHabitacionService habitacionService;

  private JFrame frame;
  private JPanel fondo;
  private JLabel titulo;
  private JButton volverButton;
  private JButton agregarButton;
  private JTable tabla;

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public ReservasUI(IReservaService reservaService, IClienteService clienteService, 
                    IHabitacionService habitacionService, MenuUI menu){
    this.reservaService = reservaService; 
    this.clienteService = clienteService; 
    this.habitacionService = habitacionService; 

    frame = new JFrame("Gestión de Reservas");
    frame.setSize(800, 500);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(null);
    frame.setResizable(false);

    fondo = new JPanel();
    fondo.setBounds(0, 0, 800, 500);
    fondo.setBackground(Color.LIGHT_GRAY);
    fondo.setLayout(null);
    frame.add(fondo);

    titulo = new JLabel("Gestión de Reservas");
    titulo.setBounds(350, 10, 200, 30);
    fondo.add(titulo);

    volverButton = new JButton("Volver al Menú");
    volverButton.setBounds(10,10,150,30);
    volverButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        frame.dispose();
        menu.init();
      }
    });
    fondo.add(volverButton);

    agregarButton = new JButton("Agregar");
    agregarButton.setBounds(680,10,100,30);
    agregarButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onAgregar();
      }
    });
    fondo.add(agregarButton);

    tabla = new JTable();
    JScrollPane sp = new JScrollPane(tabla);
    sp.setBounds(20, 60, 760, 380);
    fondo.add(sp);
  }

  public void init(){
    try {
      List<Reserva> reservas = reservaService.obtenerTodas();
      DefaultTableModel model = new DefaultTableModel();
      model.addColumn("Id");
      model.addColumn("Habitacion");
      model.addColumn("Cliente");
      model.addColumn("FechaInicio");
      model.addColumn("FechaFin");
      model.addColumn("Acciones");

      Map<Integer,String> clienteNombres = new HashMap<>();
      for(Cliente c : clienteService.obtenerTodos()){
        clienteNombres.put(c.getId(), c.getNombre() + " " + c.getApellido());
      }

      Map<Integer,Integer> habitNumero = new HashMap<>();
      for(Habitacion h : habitacionService.obtenerTodas()){
        habitNumero.put(h.getId(), h.getNumero());
      }

      for(Reserva r : reservas){
        String clienteName = clienteNombres.getOrDefault(r.getIdCliente(), "#" + r.getIdCliente());
        String habit = habitNumero.containsKey(r.getIdHabitacion()) ? String.valueOf(habitNumero.get(r.getIdHabitacion())) : "#"+r.getIdHabitacion();
        model.addRow(new Object[]{r.getId(), habit, clienteName, r.getFechaInicio().format(formatter), r.getFechaFin().format(formatter), "Acciones"});
      }

      tabla.setModel(model);
      tabla.setRowHeight(32);

      // Ocultar columna Id
      if (tabla.getColumnModel().getColumnCount() > 0) {
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setWidth(0);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(0);
      }

      int accionesCol = tabla.getColumnModel().getColumnCount() - 1;
      TableColumn accionesColumn = tabla.getColumnModel().getColumn(accionesCol);
    accionesColumn.setCellRenderer(new ActionCellRenderer());
    accionesColumn.setCellEditor(new ActionCellEditor(new JCheckBox(), reservaService, clienteService, habitacionService, tabla));
      accionesColumn.setPreferredWidth(160);
      accionesColumn.setMaxWidth(260);

      frame.setVisible(true);
    } catch (DatabaseException e) {
        JOptionPane.showMessageDialog(frame, 
            "Error al cargar reservas desde la base de datos:\n" +
            "Operación: " + e.getOperation() + "\n" +
            "Tabla: " + e.getTable() + "\n" +
            "Mensaje: " + e.getMessage(),
            "Error de Base de Datos",
            JOptionPane.ERROR_MESSAGE);
    }
  }

  private void onAgregar(){
    try {
      List<Cliente> clientes = clienteService.obtenerTodos();
      List<Habitacion> habitaciones = habitacionService.obtenerTodas();
      if(clientes.isEmpty() || habitaciones.isEmpty()){
        JOptionPane.showMessageDialog(frame, "No hay clientes o habitaciones disponibles para crear reservas.", "Atención", JOptionPane.WARNING_MESSAGE);
        return;
      }

      JComboBox<String> clienteBox = new JComboBox<>();
      Map<String,Integer> clienteMap = new HashMap<>();
      for(Cliente c : clientes){
        String label = c.getNombre() + " " + c.getApellido();
        clienteBox.addItem(label);
        clienteMap.put(label, c.getId());
      }

      JComboBox<String> habitBox = new JComboBox<>();
      Map<String,Integer> habitMap = new HashMap<>();
      for(Habitacion h : habitaciones){
        String label = "#"+h.getNumero();
        habitBox.addItem(label);
        habitMap.put(label, h.getId());
      }

      JTextField inicioF = new JTextField(); inicioF.setText(LocalDateTime.now().format(formatter));
      JTextField finF = new JTextField(); finF.setText(LocalDateTime.now().plusDays(1).format(formatter));

      JPanel p = new JPanel(new GridLayout(0,2));
      p.add(new JLabel("Cliente:")); p.add(clienteBox);
      p.add(new JLabel("Habitación:")); p.add(habitBox);
      p.add(new JLabel("Fecha Inicio (yyyy-MM-dd HH:mm:ss):")); p.add(inicioF);
      p.add(new JLabel("Fecha Fin (yyyy-MM-dd HH:mm:ss):")); p.add(finF);

      boolean exitoso = false;
      while (!exitoso) {
        int opt = JOptionPane.showConfirmDialog(frame, p, "Agregar Reserva", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(opt != JOptionPane.OK_OPTION) {
          break; // Usuario canceló
        }
        
        try{
          String selCliente = (String) clienteBox.getSelectedItem();
          String selHabit = (String) habitBox.getSelectedItem();
          if(selCliente == null || selHabit == null){
            JOptionPane.showMessageDialog(frame, "Debe seleccionar cliente y habitación.", "Error", JOptionPane.ERROR_MESSAGE);
            continue; // Volver a mostrar el diálogo
          }
          int idCliente = clienteMap.get(selCliente);
          int idHabit = habitMap.get(selHabit);
          LocalDateTime fi;
          LocalDateTime ff;
          try{
            fi = LocalDateTime.parse(inicioF.getText(), formatter);
            ff = LocalDateTime.parse(finF.getText(), formatter);
          }catch(DateTimeParseException dtpe){
            JOptionPane.showMessageDialog(frame, "Formato de fecha inválido. Use yyyy-MM-dd HH:mm:ss", "Error", JOptionPane.ERROR_MESSAGE);
            continue; // Volver a mostrar el diálogo
          }

          // Las validaciones de negocio están en el servicio
          Reserva created = reservaService.crear(idHabit, idCliente, fi, ff);
          DefaultTableModel m = (DefaultTableModel) tabla.getModel();
          String clienteName = clientes.stream().filter(c->c.getId()==idCliente).findFirst().map(c->c.getNombre()+" "+c.getApellido()).orElse("#"+idCliente);
          String habit = String.valueOf(habitaciones.stream().filter(h->h.getId()==idHabit).findFirst().map(Habitacion::getNumero).orElse(-1));
          m.addRow(new Object[]{created.getId(), habit, clienteName, created.getFechaInicio().format(formatter), created.getFechaFin().format(formatter), "Acciones"});
          exitoso = true; // Éxito, salir del bucle
        }catch(ValidationException vex){
          JOptionPane.showMessageDialog(frame, "Error de validación: " + vex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }catch(DatabaseException dbEx){
          JOptionPane.showMessageDialog(frame, 
              "Error de base de datos:\n" +
              "Operación: " + dbEx.getOperation() + "\n" +
              "Tabla: " + dbEx.getTable() + "\n" +
              "Mensaje: " + dbEx.getMessage(),
              "Error de Base de Datos",
              JOptionPane.ERROR_MESSAGE);
          exitoso = true; // Error de BD
        }catch(Exception ex){
          JOptionPane.showMessageDialog(frame, "Datos inválidos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          exitoso = true; // Error inesperado, salir
        }
      }
    } catch (DatabaseException e) {
        JOptionPane.showMessageDialog(frame, 
            "Error al cargar datos iniciales:\n" +
            "Operación: " + e.getOperation() + "\n" +
            "Tabla: " + e.getTable() + "\n" +
            "Mensaje: " + e.getMessage(),
            "Error de Base de Datos",
            JOptionPane.ERROR_MESSAGE);
    }
  }

  // Celda de acciones que contiene botones Editar y Eliminar
  private static class ActionCellRenderer extends JPanel implements TableCellRenderer {
    public ActionCellRenderer(){
      setLayout(new FlowLayout(FlowLayout.CENTER,6,4));
      setOpaque(true);
      setPreferredSize(new Dimension(150,28));
      JButton edit = new JButton("Editar");
      JButton del = new JButton("Eliminar");
      edit.setEnabled(false); del.setEnabled(false);
      edit.setMargin(new Insets(2,8,2,8));
      del.setMargin(new Insets(2,8,2,8));
      edit.setFocusable(false);
      del.setFocusable(false);
      add(edit); add(del);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
      return this;
    }
  }

  private static class ActionCellEditor extends AbstractCellEditor implements TableCellEditor {
    private JPanel panel;
    private JButton editBtn;
    private JButton delBtn;
    private JTable table;
    private IReservaService reservaService;
    private IClienteService clienteService;
    private IHabitacionService habitacionService;
    private int currentId;
    private int editingRowViewIndex;

    public ActionCellEditor(JCheckBox chk, IReservaService reservaService, IClienteService clienteService, 
                            IHabitacionService habitacionService, JTable table){
      this.reservaService = reservaService; 
      this.clienteService = clienteService; 
      this.habitacionService = habitacionService; 
      this.table = table;
      panel = new JPanel(new FlowLayout(FlowLayout.CENTER,6,4));
      panel.setOpaque(true);
      panel.setPreferredSize(new Dimension(150,28));
      editBtn = new JButton("Editar");
      delBtn = new JButton("Eliminar");
      editBtn.setMargin(new Insets(2,8,2,8));
      delBtn.setMargin(new Insets(2,8,2,8));
      editBtn.setFocusable(false);
      delBtn.setFocusable(false);
      panel.add(editBtn); panel.add(delBtn);

      editBtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          onEdit();
        }
      });
      delBtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          onDelete();
        }
      });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
      editingRowViewIndex = row;
      int modelRow = table.convertRowIndexToModel(row);
      Object idObj = table.getModel().getValueAt(modelRow, 0);
      currentId = (idObj instanceof Number) ? ((Number)idObj).intValue() : Integer.parseInt(idObj.toString());
      return panel;
    }

    @Override
    public Object getCellEditorValue(){ return null; }

    private void onEdit(){
      try {
        int viewRow = editingRowViewIndex;
        if(viewRow < 0) return;
        int modelRow = table.convertRowIndexToModel(viewRow);
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        String habitStr = m.getValueAt(modelRow,1).toString();
        String clienteStr = m.getValueAt(modelRow,2).toString();
        String fiStr = m.getValueAt(modelRow,3).toString();
        String ffStr = m.getValueAt(modelRow,4).toString();

        List<Cliente> clientes = clienteService.obtenerTodos();
        Map<String,Integer> clienteMap = new HashMap<>();
        JComboBox<String> clienteBox = new JComboBox<>();
        for(Cliente c : clientes){
          String label = c.getNombre() + " " + c.getApellido();
          clienteBox.addItem(label);
          clienteMap.put(label, c.getId());
        }
        clienteBox.setSelectedItem(clienteStr);

        List<Habitacion> habitaciones = habitacionService.obtenerTodas();
        Map<String,Integer> habitMap = new HashMap<>();
        JComboBox<String> habitBox = new JComboBox<>();
        for(Habitacion h : habitaciones){
          String label = "#"+h.getNumero();
          habitBox.addItem(label);
          habitMap.put(label, h.getId());
        }

        habitBox.setSelectedItem("#"+habitStr);

        JTextField inicioF = new JTextField(fiStr);
        JTextField finF = new JTextField(ffStr);

        JPanel p = new JPanel(new GridLayout(0,2));
        p.add(new JLabel("Cliente:")); p.add(clienteBox);
        p.add(new JLabel("Habitación:")); p.add(habitBox);
        p.add(new JLabel("Fecha Inicio (yyyy-MM-dd HH:mm:ss):")); p.add(inicioF);
        p.add(new JLabel("Fecha Fin (yyyy-MM-dd HH:mm:ss):")); p.add(finF);

        boolean exitoso = false;
        while (!exitoso) {
          int opt = JOptionPane.showConfirmDialog(table, p, "Editar Reserva", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
          if(opt != JOptionPane.OK_OPTION){
            exitoso = true;
            break;
          }
          try{
            String selCliente = (String) clienteBox.getSelectedItem();
            String selHabit = (String) habitBox.getSelectedItem();
            if(selCliente == null || selHabit == null){
              JOptionPane.showMessageDialog(table, "Debe seleccionar cliente y habitación.", "Error", JOptionPane.ERROR_MESSAGE);
              continue;
            }
            int idCliente = clienteMap.get(selCliente);
            int idHabit = habitMap.get(selHabit);
            LocalDateTime fi;
            LocalDateTime ff;
            try{
              fi = LocalDateTime.parse(inicioF.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
              ff = LocalDateTime.parse(finF.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }catch(DateTimeParseException dtpe){
              JOptionPane.showMessageDialog(table, "Formato de fecha inválido. Use yyyy-MM-dd HH:mm:ss", "Error", JOptionPane.ERROR_MESSAGE);
              continue;
            }

            reservaService.actualizar(currentId, idHabit, idCliente, fi, ff);
            m.setValueAt(String.valueOf(getHabitacionNumeroById(idHabit, habitaciones)), modelRow, 1);
            m.setValueAt(getClienteNameById(idCliente, clientes), modelRow, 2);
            m.setValueAt(fi.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), modelRow, 3);
            m.setValueAt(ff.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), modelRow, 4);
            exitoso = true;
          }catch(ValidationException vex){
            JOptionPane.showMessageDialog(table, "Error de validación: " + vex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }catch(DatabaseException dbEx){
            JOptionPane.showMessageDialog(table, 
                "Error de base de datos:\n" +
                "Operación: " + dbEx.getOperation() + "\n" +
                "Tabla: " + dbEx.getTable() + "\n" +
                "Mensaje: " + dbEx.getMessage(),
                "Error de Base de Datos",
                JOptionPane.ERROR_MESSAGE);
            exitoso = true;
          }catch(Exception ex){
            JOptionPane.showMessageDialog(table, "Datos inválidos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            exitoso = true;
          }
        }

        fireEditingStopped();
      } catch (DatabaseException e) {
          JOptionPane.showMessageDialog(table, 
              "Error al cargar datos:\n" +
              "Operación: " + e.getOperation() + "\n" +
              "Tabla: " + e.getTable() + "\n" +
              "Mensaje: " + e.getMessage(),
              "Error de Base de Datos",
              JOptionPane.ERROR_MESSAGE);
          fireEditingCanceled();
      }
    }

    private void onDelete(){
      int viewRow = editingRowViewIndex;
      if (viewRow < 0) { fireEditingCanceled(); return; }
      final int modelRow = table.convertRowIndexToModel(viewRow);
      int confirm = JOptionPane.showConfirmDialog(table, "¿Eliminar esta reserva?", "Confirmar", JOptionPane.YES_NO_OPTION);
      if (confirm != JOptionPane.YES_OPTION) { fireEditingCanceled(); return; }

      try{
        reservaService.eliminar(currentId);
      }catch(ValidationException vex){
        JOptionPane.showMessageDialog(table, "Error de validación: " + vex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        fireEditingCanceled();
        return;
      }catch(DatabaseException dbEx){ 
        JOptionPane.showMessageDialog(table, 
            "Error de base de datos al eliminar:\n" +
            "Operación: " + dbEx.getOperation() + "\n" +
            "Tabla: " + dbEx.getTable() + "\n" +
            "Mensaje: " + dbEx.getMessage(),
            "Error de Base de Datos",
            JOptionPane.ERROR_MESSAGE); 
        fireEditingCanceled(); 
        return; 
      }catch(Exception ex){ 
        ex.printStackTrace(); 
        JOptionPane.showMessageDialog(table, "Error al eliminar reserva: "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
        fireEditingCanceled(); 
        return; 
      }

      fireEditingCanceled();
      SwingUtilities.invokeLater(() -> {
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        if (modelRow >= 0 && modelRow < m.getRowCount()) m.removeRow(modelRow);
        currentId = -1; editingRowViewIndex = -1;
      });
    }

    private String getClienteNameById(int id, List<Cliente> clientes){
      return clientes.stream().filter(c->c.getId()==id).findFirst().map(c->c.getNombre()+" "+c.getApellido()).orElse("#"+id);
    }

    private int getHabitacionNumeroById(int id, List<Habitacion> habitaciones){
      return habitaciones.stream().filter(h->h.getId()==id).findFirst().map(Habitacion::getNumero).orElse(-1);
    }
  }
}
