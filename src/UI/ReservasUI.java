package UI;

import Clases.*;

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
  private final SistemaReservas sr;
  private final SistemaClientes sc;
  private final SistemaHabitaciones sh;

  private JFrame frame;
  private JPanel fondo;
  private JLabel titulo;
  private JButton volverButton;
  private JButton agregarButton;
  private JTable tabla;

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public ReservasUI(SistemaReservas sr, SistemaClientes sc, SistemaHabitaciones sh, MenuUI menu){
    this.sr = sr; 
    this.sc = sc; 
    this.sh = sh; 

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
    List<Reserva> reservas = sr.getAll();
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("Id");
    model.addColumn("Habitacion");
    model.addColumn("Cliente");
    model.addColumn("FechaInicio");
    model.addColumn("FechaFin");
    model.addColumn("Acciones");

    Map<Integer,String> clienteNombres = new HashMap<>();
    for(Cliente c : sc.getAll()){
      clienteNombres.put(c.getId(), c.getNombre() + " " + c.getApellido());
    }

    Map<Integer,Integer> habitNumero = new HashMap<>();
    for(Habitacion h : sh.getAll()){
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
  accionesColumn.setCellEditor(new ActionCellEditor(new JCheckBox(), sr, sc, sh, tabla));
    accionesColumn.setPreferredWidth(160);
    accionesColumn.setMaxWidth(260);

    frame.setVisible(true);
  }

  private void onAgregar(){
    List<Cliente> clientes = sc.getAll();
    List<Habitacion> habitaciones = sh.getAll();
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

    int opt = JOptionPane.showConfirmDialog(frame, p, "Agregar Reserva", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if(opt == JOptionPane.OK_OPTION){
      try{
        String selCliente = (String) clienteBox.getSelectedItem();
        String selHabit = (String) habitBox.getSelectedItem();
        if(selCliente == null || selHabit == null){
          JOptionPane.showMessageDialog(frame, "Debe seleccionar cliente y habitación.", "Error", JOptionPane.ERROR_MESSAGE);
          return;
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
          return;
        }

        if(!fi.isBefore(ff)){
          JOptionPane.showMessageDialog(frame, "La fecha de inicio debe ser anterior a la fecha fin.", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }

        // validar disponibilidad
        if(!sr.isRoomAvailable(idHabit, fi, ff)){
          JOptionPane.showMessageDialog(frame, "La habitación no está disponible en ese intervalo.", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }

        Reserva created = sr.create(idHabit, idCliente, fi, ff);
        if(created != null){
          DefaultTableModel m = (DefaultTableModel) tabla.getModel();
          String clienteName = clientes.stream().filter(c->c.getId()==idCliente).findFirst().map(c->c.getNombre()+" "+c.getApellido()).orElse("#"+idCliente);
          String habit = String.valueOf(habitaciones.stream().filter(h->h.getId()==idHabit).findFirst().map(Habitacion::getNumero).orElse(-1));
          m.addRow(new Object[]{created.getId(), habit, clienteName, created.getFechaInicio().format(formatter), created.getFechaFin().format(formatter), "Acciones"});
        } else {
          JOptionPane.showMessageDialog(frame, "Error al crear reserva", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }catch(Exception ex){
        JOptionPane.showMessageDialog(frame, "Datos inválidos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
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
    private SistemaReservas sr;
    private SistemaClientes sc;
    private SistemaHabitaciones sh;
    private int currentId;
    private int editingRowViewIndex;

    public ActionCellEditor(JCheckBox chk, SistemaReservas sr, SistemaClientes sc, SistemaHabitaciones sh, JTable table){
      this.sr = sr; this.sc = sc; this.sh = sh; this.table = table;
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
      int viewRow = editingRowViewIndex;
      if(viewRow < 0) return;
      int modelRow = table.convertRowIndexToModel(viewRow);
      DefaultTableModel m = (DefaultTableModel) table.getModel();
      String habitStr = m.getValueAt(modelRow,1).toString();
      String clienteStr = m.getValueAt(modelRow,2).toString();
      String fiStr = m.getValueAt(modelRow,3).toString();
      String ffStr = m.getValueAt(modelRow,4).toString();

      List<Cliente> clientes = sc.getAll();
      Map<String,Integer> clienteMap = new HashMap<>();
      JComboBox<String> clienteBox = new JComboBox<>();
      for(Cliente c : clientes){
        String label = c.getNombre() + " " + c.getApellido();
        clienteBox.addItem(label);
        clienteMap.put(label, c.getId());
      }
      clienteBox.setSelectedItem(clienteStr);

      List<Habitacion> habitaciones = sh.getAll();
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

      int opt = JOptionPane.showConfirmDialog(table, p, "Editar Reserva", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
      if(opt == JOptionPane.OK_OPTION){
        try{
          String selCliente = (String) clienteBox.getSelectedItem();
          String selHabit = (String) habitBox.getSelectedItem();
          if(selCliente == null || selHabit == null){
            JOptionPane.showMessageDialog(table, "Debe seleccionar cliente y habitación.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
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
            return;
          }

          if(!fi.isBefore(ff)){
            JOptionPane.showMessageDialog(table, "La fecha de inicio debe ser anterior a la fecha fin.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }

          // validar disponibilidad excluyendo la reserva actual
          if(!sr.isRoomAvailable(idHabit, fi, ff, currentId)){
            JOptionPane.showMessageDialog(table, "La habitación no está disponible en ese intervalo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }

          Map<String,Object> vals = new HashMap<>();
          vals.put("IdCliente", idCliente);
          vals.put("IdHabitacion", idHabit);
          vals.put("FechaInicio", fi.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
          vals.put("FechaFin", ff.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

          boolean ok = sr.update(currentId, vals);
          if(ok){
            m.setValueAt(String.valueOf(getHabitNumeroById(idHabit, habitaciones)), modelRow, 1);
            m.setValueAt(getClienteNameById(idCliente, clientes), modelRow, 2);
            m.setValueAt(fi.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), modelRow, 3);
            m.setValueAt(ff.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), modelRow, 4);
          } else {
            JOptionPane.showMessageDialog(table, "Error al actualizar reserva", "Error", JOptionPane.ERROR_MESSAGE);
          }
        }catch(Exception ex){
          JOptionPane.showMessageDialog(table, "Datos inválidos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }

      fireEditingStopped();
    }

    private void onDelete(){
      int viewRow = editingRowViewIndex;
      if (viewRow < 0) { fireEditingCanceled(); return; }
      final int modelRow = table.convertRowIndexToModel(viewRow);
      int confirm = JOptionPane.showConfirmDialog(table, "¿Eliminar esta reserva?", "Confirmar", JOptionPane.YES_NO_OPTION);
      if (confirm != JOptionPane.YES_OPTION) { fireEditingCanceled(); return; }

      try{
        boolean ok = sr.delete(currentId);
        if(!ok){ JOptionPane.showMessageDialog(table, "Error al eliminar reserva", "Error", JOptionPane.ERROR_MESSAGE); fireEditingCanceled(); return; }
      }catch(Exception ex){ ex.printStackTrace(); JOptionPane.showMessageDialog(table, "Error al eliminar reserva: "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); fireEditingCanceled(); return; }

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

    private int getHabitNumeroById(int id, List<Habitacion> habitaciones){
      return habitaciones.stream().filter(h->h.getId()==id).findFirst().map(Habitacion::getNumero).orElse(-1);
    }
  }
}
