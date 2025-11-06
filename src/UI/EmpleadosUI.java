package UI;

import Clases.*;

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

public class EmpleadosUI {
  private final SistemaEmpleados se;
  private JFrame frame;
  private JPanel fondo;
  private JLabel titulo;
  private JButton volverButton;
  private JButton agregarButton;
  private JTable tabla;

  public EmpleadosUI(SistemaEmpleados se, MenuUI menu){
    this.se = se;
    this.frame = new JFrame("Gestión de Empleados");
    this.frame.setSize(800, 500);
    this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.frame.setLayout(null);
    this.frame.setResizable(false);

    this.fondo = new JPanel();
    this.fondo.setBounds(0,0,800,500);
    this.fondo.setBackground(java.awt.Color.LIGHT_GRAY);
    this.fondo.setLayout(null);
    this.frame.add(fondo);

    this.titulo = new JLabel("Gestión de Empleados");
    this.titulo.setBounds(350, 10, 200, 30);
    fondo.add(titulo);

    this.volverButton = new JButton("Volver al Menú");
    this.volverButton.setBounds(10,10,150,30);
    this.volverButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent e) {
        frame.dispose();
        menu.init();
      }
    });
    fondo.add(volverButton);

    this.agregarButton = new JButton("Agregar");
    this.agregarButton.setBounds(680,10,100,30);
    this.agregarButton.addActionListener(new ActionListener() {
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
    List<Empleado> empleados = se.getAll();
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("Id");
    model.addColumn("Mail");
    model.addColumn("Nombre");
    model.addColumn("Apellido");
    model.addColumn("Password");
    model.addColumn("Rol");
    model.addColumn("Acciones");

    for(Empleado e : empleados){
      model.addRow(new Object[]{e.getId(), e.getMail(), e.getNombre(), e.getApellido(), e.getPassword(), e.getRol(), "Acciones"});
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
  accionesColumn.setCellEditor(new ActionCellEditor(new JCheckBox(), se, tabla));
    accionesColumn.setPreferredWidth(160);
    accionesColumn.setMaxWidth(260);

    frame.setVisible(true);
  }

  private void onAgregar(){
    JTextField mailF = new JTextField();
    JTextField nombreF = new JTextField();
    JTextField apellidoF = new JTextField();
    JTextField passwordF = new JTextField();
    JComboBox<Rol> rolBox = new JComboBox<>(Rol.values());

    JPanel p = new JPanel(new GridLayout(0,2));
    p.add(new JLabel("Mail:")); p.add(mailF);
    p.add(new JLabel("Nombre:")); p.add(nombreF);
    p.add(new JLabel("Apellido:")); p.add(apellidoF);
    p.add(new JLabel("Password:")); p.add(passwordF);
    p.add(new JLabel("Rol:")); p.add(rolBox);

    int opt = JOptionPane.showConfirmDialog(frame, p, "Agregar Empleado", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if(opt == JOptionPane.OK_OPTION){
      try{
        String mail = mailF.getText();
        String nombre = nombreF.getText();
        String apellido = apellidoF.getText();
        String pwd = passwordF.getText();
        Rol rol = (Rol) rolBox.getSelectedItem();
        Empleado created = se.create(mail, nombre, apellido, pwd, rol);
        if(created != null){
          DefaultTableModel m = (DefaultTableModel) tabla.getModel();
          m.addRow(new Object[]{created.getId(), created.getMail(), created.getNombre(), created.getApellido(), created.getPassword(), created.getRol(), "Acciones"});
        } else {
          JOptionPane.showMessageDialog(frame, "Error al crear empleado", "Error", JOptionPane.ERROR_MESSAGE);
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

  // Editor that provides working buttons and handles edit/delete actions safely
  private static class ActionCellEditor extends AbstractCellEditor implements TableCellEditor {
    private JPanel panel;
    private JButton editBtn;
    private JButton delBtn;
    private JTable table;
    private SistemaEmpleados se;
    private int currentId;
    private int editingRowViewIndex; // índice de la fila en vista mientras se edita

    public ActionCellEditor(JCheckBox chk, SistemaEmpleados se, JTable table){
      this.se = se; this.table = table;
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
      String mail = m.getValueAt(modelRow,1).toString();
      String nombre = m.getValueAt(modelRow,2).toString();
      String apellido = m.getValueAt(modelRow,3).toString();
      String pwd = m.getValueAt(modelRow,4).toString();
      Rol rolCur = (Rol) m.getValueAt(modelRow,5);

      JTextField mailF = new JTextField(mail);
      JTextField nombreF = new JTextField(nombre);
      JTextField apellidoF = new JTextField(apellido);
      JTextField pwdF = new JTextField(pwd);
      JComboBox<Rol> rolBox = new JComboBox<>(Rol.values());
      rolBox.setSelectedItem(rolCur);

      JPanel p = new JPanel(new GridLayout(0,2));
      p.add(new JLabel("Mail:")); p.add(mailF);
      p.add(new JLabel("Nombre:")); p.add(nombreF);
      p.add(new JLabel("Apellido:")); p.add(apellidoF);
      p.add(new JLabel("Password:")); p.add(pwdF);
      p.add(new JLabel("Rol:")); p.add(rolBox);

      int opt = JOptionPane.showConfirmDialog(table, p, "Editar Empleado", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
      if(opt == JOptionPane.OK_OPTION){
        try{
          Map<String,Object> vals = new HashMap<>();
          vals.put("Mail", mailF.getText());
          vals.put("Nombre", nombreF.getText());
          vals.put("Apellido", apellidoF.getText());
          vals.put("Password", pwdF.getText());
          vals.put("Rol", ((Rol)rolBox.getSelectedItem()).ordinal());
          boolean ok = se.update(currentId, vals);
          if(ok){
            m.setValueAt(mailF.getText(), modelRow, 1);
            m.setValueAt(nombreF.getText(), modelRow, 2);
            m.setValueAt(apellidoF.getText(), modelRow, 3);
            m.setValueAt(pwdF.getText(), modelRow, 4);
            m.setValueAt(rolBox.getSelectedItem(), modelRow, 5);
          } else {
            JOptionPane.showMessageDialog(table, "Error al actualizar empleado", "Error", JOptionPane.ERROR_MESSAGE);
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
        fireEditingCanceled();
        return;
      }
      final int modelRow = table.convertRowIndexToModel(viewRow);
      int confirm = JOptionPane.showConfirmDialog(table, "¿Eliminar este empleado?", "Confirmar", JOptionPane.YES_NO_OPTION);
      if (confirm != JOptionPane.YES_OPTION) {
        fireEditingCanceled();
        return;
      }

      try {
        boolean ok = se.delete(currentId);
        if (!ok) {
          JOptionPane.showMessageDialog(table, "Error al eliminar empleado", "Error", JOptionPane.ERROR_MESSAGE);
          fireEditingCanceled();
          return;
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(table, "Error al eliminar empleado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
