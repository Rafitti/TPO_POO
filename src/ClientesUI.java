import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.*;

public class ClientesUI {
  private final SistemaClientes sc;
  private JFrame frame;
  private JPanel fondo;
  private JLabel titulo;
  private JButton volverButton;
  private JButton agregarButton;
  private JTable tabla;

  public ClientesUI(SistemaClientes sc, MenuUI menu){
    this.sc = sc;
    this.frame = new JFrame("Gestión de Clientes");
    this.frame.setSize(700, 500);
    this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.frame.setLayout(null);
    this.frame.setResizable(false);

    this.fondo = new JPanel();
    this.fondo.setBounds(0,0,700,500);
    this.fondo.setBackground(java.awt.Color.LIGHT_GRAY);
    this.fondo.setLayout(null);
    this.frame.add(fondo);

    this.titulo = new JLabel("Gestión de Clientes");
    this.titulo.setBounds(300, 10, 200, 30);
    fondo.add(titulo);

    this.volverButton = new JButton("Volver al Menú");
    this.volverButton.setBounds(10,10,150,30);
    this.volverButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) { 
        frame.dispose();
        menu.init(); }
    });
    fondo.add(volverButton);

    this.agregarButton = new JButton("Agregar");
    this.agregarButton.setBounds(580,10,100,30);
    this.agregarButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) { onAgregar(); }
    });
    fondo.add(agregarButton);

    tabla = new JTable();
    JScrollPane sp = new JScrollPane(tabla);
    sp.setBounds(20, 60, 660, 380);
    fondo.add(sp);
  }

  public void init(){
    List<Cliente> clientes = sc.getAll();
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("Id");
    model.addColumn("Mail");
    model.addColumn("Nombre");
    model.addColumn("Apellido");
    model.addColumn("DNI");
    model.addColumn("Telefono");
    model.addColumn("Acciones");

    for(Cliente c : clientes){
      model.addRow(new Object[]{c.getId(), c.getMail(), c.getNombre(), c.getApellido(), c.getDni(), c.getTelefono(), "Acciones"});
    }

    tabla.setModel(model);
    // asegurar suficiente alto de fila para botones y ajustar ancho columna acciones
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
    accionesColumn.setCellEditor(new ClientActionCellEditor(new JCheckBox(), sc, tabla));
    accionesColumn.setPreferredWidth(160);
    accionesColumn.setMaxWidth(260);

    frame.setVisible(true);
  }

  private void onAgregar(){
    JTextField mailF = new JTextField();
    JTextField nombreF = new JTextField();
    JTextField apellidoF = new JTextField();
    JTextField dniF = new JTextField();
    JTextField telefonoF = new JTextField();

    JPanel p = new JPanel(new GridLayout(0,2));
    p.add(new JLabel("Mail:")); p.add(mailF);
    p.add(new JLabel("Nombre:")); p.add(nombreF);
    p.add(new JLabel("Apellido:")); p.add(apellidoF);
    p.add(new JLabel("DNI:")); p.add(dniF);
    p.add(new JLabel("Telefono:")); p.add(telefonoF);

    int opt = JOptionPane.showConfirmDialog(frame, p, "Agregar Cliente", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if(opt == JOptionPane.OK_OPTION){
      try{
        String mail = mailF.getText();
        String nombre = nombreF.getText();
        String apellido = apellidoF.getText();
        int dni = Integer.parseInt(dniF.getText());
        Integer tel = telefonoF.getText().isBlank() ? null : Integer.parseInt(telefonoF.getText());
        Cliente created = sc.create(mail, nombre, apellido, dni, tel);
        if(created != null){
          DefaultTableModel m = (DefaultTableModel) tabla.getModel();
          m.addRow(new Object[]{created.getId(), created.getMail(), created.getNombre(), created.getApellido(), created.getDni(), created.getTelefono(), "Acciones"});
        } else {
          JOptionPane.showMessageDialog(frame, "Error al crear cliente", "Error", JOptionPane.ERROR_MESSAGE);
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

  private static class ClientActionCellEditor extends AbstractCellEditor implements TableCellEditor {
    private JPanel panel;
    private JButton editBtn;
    private JButton delBtn;
    private JTable table;
    private SistemaClientes sc;
    private int currentId;
    private int editingRowViewIndex; // índice de la fila en vista mientras se edita

    public ClientActionCellEditor(JCheckBox chk, SistemaClientes sc, JTable table){
      this.sc = sc; this.table = table;
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

      editBtn.addActionListener(e -> onEdit());
      delBtn.addActionListener(e -> onDelete());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
      // row es el índice de vista; convertir a modelo por seguridad
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
      String mail = m.getValueAt(modelRow,1).toString();
      String nombre = m.getValueAt(modelRow,2).toString();
      String apellido = m.getValueAt(modelRow,3).toString();
      String dniStr = m.getValueAt(modelRow,4).toString();
      String telStr = m.getValueAt(modelRow,5) == null ? "" : m.getValueAt(modelRow,5).toString();

      JTextField mailF = new JTextField(mail);
      JTextField nombreF = new JTextField(nombre);
      JTextField apellidoF = new JTextField(apellido);
      JTextField dniF = new JTextField(dniStr);
      JTextField telF = new JTextField(telStr);

      JPanel p = new JPanel(new GridLayout(0,2));
      p.add(new JLabel("Mail:")); p.add(mailF);
      p.add(new JLabel("Nombre:")); p.add(nombreF);
      p.add(new JLabel("Apellido:")); p.add(apellidoF);
      p.add(new JLabel("DNI:")); p.add(dniF);
      p.add(new JLabel("Telefono:")); p.add(telF);

      int opt = JOptionPane.showConfirmDialog(table, p, "Editar Cliente", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
      if(opt == JOptionPane.OK_OPTION){
        try{
          Map<String,Object> vals = new HashMap<>();
          vals.put("Mail", mailF.getText());
          vals.put("Nombre", nombreF.getText());
          vals.put("Apellido", apellidoF.getText());
          vals.put("DNI", Integer.parseInt(dniF.getText()));
          vals.put("Telefono", telF.getText().isBlank() ? null : Integer.parseInt(telF.getText()));
          boolean ok = sc.update(currentId, vals);
          if(ok){
            m.setValueAt(mailF.getText(), modelRow, 1);
            m.setValueAt(nombreF.getText(), modelRow, 2);
            m.setValueAt(apellidoF.getText(), modelRow, 3);
            m.setValueAt(Integer.parseInt(dniF.getText()), modelRow, 4);
            m.setValueAt(telF.getText().isBlank() ? null : Integer.parseInt(telF.getText()), modelRow, 5);
          } else {
            JOptionPane.showMessageDialog(table, "Error al actualizar cliente", "Error", JOptionPane.ERROR_MESSAGE);
          }
        }catch(Exception ex){
          JOptionPane.showMessageDialog(table, "Datos inválidos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
      fireEditingStopped();
    }

    private void onDelete(){
      int viewRow = editingRowViewIndex;
      if (viewRow < 0) {
        fireEditingCanceled(); // cancelar edición si no hay fila válida
        return;
      }
      final int modelRow = table.convertRowIndexToModel(viewRow);
      int confirm = JOptionPane.showConfirmDialog(table, "¿Eliminar este cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);
      if (confirm != JOptionPane.YES_OPTION) {
        fireEditingCanceled();
        return;
      }

      try {
        boolean ok = sc.delete(currentId);
        if (!ok) {
          JOptionPane.showMessageDialog(table, "Error al eliminar cliente", "Error", JOptionPane.ERROR_MESSAGE);
          fireEditingCanceled();
          return;
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(table, "Error al eliminar cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        fireEditingCanceled();
        return;
      }

      fireEditingCanceled();
      SwingUtilities.invokeLater(() -> {
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        if (modelRow >= 0 && modelRow < m.getRowCount()) {
          m.removeRow(modelRow);
        }
        currentId = -1;
        editingRowViewIndex = -1;
      });
    }
  }
}
