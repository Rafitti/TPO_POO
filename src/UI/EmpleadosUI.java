package UI;

import Clases.Entidades.*;
import Clases.Interfaces.IEmpleadoService;
import Clases.Exceptions.*;

import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.*;

public class EmpleadosUI {
  private final IEmpleadoService empleadoService;
  private JFrame frame;
  private JPanel fondo;
  private JLabel titulo;
  private JButton volverButton;
  private JButton agregarButton;
  private JTable tabla;

  public EmpleadosUI(IEmpleadoService empleadoService, MenuUI menu){
    this.empleadoService = empleadoService;
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
    try {
      List<Empleado> empleados = empleadoService.obtenerTodos();
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
    accionesColumn.setCellEditor(new ActionCellEditor(new JCheckBox(), empleadoService, tabla));
      accionesColumn.setPreferredWidth(160);
      accionesColumn.setMaxWidth(260);

      frame.setVisible(true);
    } catch (DatabaseException e) {
        JOptionPane.showMessageDialog(frame, 
            "Error al cargar empleados desde la base de datos:\n" +
            "Operación: " + e.getOperation() + "\n" +
            "Tabla: " + e.getTable() + "\n" +
            "Mensaje: " + e.getMessage(),
            "Error de Base de Datos",
            JOptionPane.ERROR_MESSAGE);
    }
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

    boolean exitoso = false;
    while (!exitoso) {
      int opt = JOptionPane.showConfirmDialog(frame, p, "Agregar Empleado", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
      if(opt != JOptionPane.OK_OPTION) {
        break; // Usuario canceló
      }
      
      try{
        String mail = mailF.getText();
        String nombre = nombreF.getText();
        String apellido = apellidoF.getText();
        String pwd = passwordF.getText();
        Rol rol = (Rol) rolBox.getSelectedItem();
        Empleado created = empleadoService.crear(mail, nombre, apellido, pwd, rol);
        DefaultTableModel m = (DefaultTableModel) tabla.getModel();
        m.addRow(new Object[]{created.getId(), created.getMail(), created.getNombre(), created.getApellido(), created.getPassword(), created.getRol(), "Acciones"});
        exitoso = true; // Éxito, salir del bucle
      } catch(ValidationException ex){
        JOptionPane.showMessageDialog(frame, "Error de validación: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        // El diálogo se volverá a mostrar
      } catch(DatabaseException ex){
        JOptionPane.showMessageDialog(frame, "Error de base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        exitoso = true; // Error de BD
      } catch(Exception ex){
        JOptionPane.showMessageDialog(frame, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        exitoso = true; // Error inesperado, salir
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
    private IEmpleadoService empleadoService;
    private int currentId;
    private int editingRowViewIndex; // índice de la fila en vista mientras se edita

    public ActionCellEditor(JCheckBox chk, IEmpleadoService empleadoService, JTable table){
      this.empleadoService = empleadoService; this.table = table;
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

      boolean exitoso = false;
      while (!exitoso) {
        int opt = JOptionPane.showConfirmDialog(table, p, "Editar Empleado", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(opt != JOptionPane.OK_OPTION){
          exitoso = true;
          break;
        }
        try{
          String mailNuevo = mailF.getText();
          String nombreNuevo = nombreF.getText();
          String apellidoNuevo = apellidoF.getText();
          String pwdNuevo = pwdF.getText();
          Rol rolNuevo = (Rol) rolBox.getSelectedItem();
          
          empleadoService.actualizar(currentId, mailNuevo, nombreNuevo, apellidoNuevo, pwdNuevo, rolNuevo);
          m.setValueAt(mailNuevo, modelRow, 1);
          m.setValueAt(nombreNuevo, modelRow, 2);
          m.setValueAt(apellidoNuevo, modelRow, 3);
          m.setValueAt(pwdNuevo, modelRow, 4);
          m.setValueAt(rolNuevo, modelRow, 5);
          exitoso = true;
        }catch(ValidationException ex){
          JOptionPane.showMessageDialog(table, "Error de validación: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }catch(DatabaseException ex){
          JOptionPane.showMessageDialog(table, "Error de base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          exitoso = true;
        }catch(Exception ex){
          JOptionPane.showMessageDialog(table, "Datos inválidos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          exitoso = true;
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
        empleadoService.eliminar(currentId);
      } catch (ValidationException ex) {
        JOptionPane.showMessageDialog(table, "Error de validación: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        fireEditingCanceled();
        return;
      } catch (DatabaseException ex) {
        JOptionPane.showMessageDialog(table, "Error de base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        fireEditingCanceled();
        return;
      } catch (Exception ex) {
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
