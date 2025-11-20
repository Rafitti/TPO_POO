package UI;

import Clases.Entidades.*;
import Clases.Interfaces.IClienteService;
import Clases.Exceptions.*;

import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.*;

public class ClientesUI {
  private final IClienteService clienteService;
  private JFrame frame;
  private JPanel fondo;
  private JLabel titulo;
  private JButton volverButton;
  private JButton agregarButton;
  private JTable tabla;

  public ClientesUI(IClienteService clienteService, MenuUI menu){
    this.clienteService = clienteService;
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
    try {
      List<Cliente> clientes = clienteService.obtenerTodos();
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
      accionesColumn.setCellEditor(new ActionCellEditor(new JCheckBox(), clienteService, tabla));
      accionesColumn.setPreferredWidth(160);
      accionesColumn.setMaxWidth(260);

      frame.setVisible(true);
    } catch (DatabaseException e) {
        JOptionPane.showMessageDialog(frame, 
            "Error al cargar clientes desde la base de datos:\n" +
            "Operación: " + e.getOperation() + "\n" +
            "Tabla: " + e.getTable() + "\n" +
            "Mensaje: " + e.getMessage(),
            "Error de Base de Datos",
            JOptionPane.ERROR_MESSAGE);
    }
  }

  private void onAgregar(){
    JTextField mailCliente = new JTextField();
    JTextField nombreCliente = new JTextField();
    JTextField apellidoCliente = new JTextField();
    JTextField dniCliente = new JTextField();
    JTextField telefonoCliente = new JTextField();

    JPanel p = new JPanel(new GridLayout(0,2));
    p.add(new JLabel("Mail:")); p.add(mailCliente);
    p.add(new JLabel("Nombre:")); p.add(nombreCliente);
    p.add(new JLabel("Apellido:")); p.add(apellidoCliente);
    p.add(new JLabel("DNI:")); p.add(dniCliente);
    p.add(new JLabel("Telefono:")); p.add(telefonoCliente);

    boolean exitoso = false;
    while (!exitoso) {
      int opt = JOptionPane.showConfirmDialog(frame, p, "Agregar Cliente", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
      if(opt != JOptionPane.OK_OPTION) {
        break; // Usuario canceló
      }
      
      try{
        String mail = mailCliente.getText();
        String nombre = nombreCliente.getText();
        String apellido = apellidoCliente.getText();
        
        int dni = Integer.parseInt(dniCliente.getText().trim());
        
        Integer tel = null;
        String telStr = telefonoCliente.getText().trim();
        if (!telStr.isBlank()) {
          tel = Integer.parseInt(telStr);
        }

        Cliente created = clienteService.crear(mail, nombre, apellido, dni, tel);
        DefaultTableModel m = (DefaultTableModel) tabla.getModel();
        m.addRow(new Object[]{created.getId(), created.getMail(), created.getNombre(), created.getApellido(), created.getDni(), created.getTelefono(), "Acciones"});
        exitoso = true; // Salir del bucle si todo salió bien
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(frame, 
            "Error de formato:\n" +
            "DNI y Teléfono deben ser números válidos",
            "Formato Inválido",
            JOptionPane.ERROR_MESSAGE);
      } catch (ValidationException ex) {
        JOptionPane.showMessageDialog(frame, 
            "Error de validación:\n" +
            "Campo: " + ex.getField() + "\n" +
            "Mensaje: " + ex.getMessage(),
            "Datos Inválidos",
            JOptionPane.ERROR_MESSAGE);
      } catch (DatabaseException ex) {
        JOptionPane.showMessageDialog(frame, 
            "Error de base de datos:\n" +
            "Operación: " + ex.getOperation() + "\n" +
            "Tabla: " + ex.getTable() + "\n" +
            "Mensaje: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        exitoso = true; // Error de BD
      } catch (Exception ex) {
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
    private IClienteService clienteService;
    private int currentId;
    private int editingRowViewIndex;

  public ActionCellEditor(JCheckBox chk, IClienteService clienteService, JTable table){
      this.clienteService = clienteService;
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
      int viewRow = editingRowViewIndex;
      if(viewRow < 0) return;
      int modelRow = table.convertRowIndexToModel(viewRow);
      DefaultTableModel m = (DefaultTableModel) table.getModel();
      String mail = m.getValueAt(modelRow,1).toString();
      String nombre = m.getValueAt(modelRow,2).toString();
      String apellido = m.getValueAt(modelRow,3).toString();
      String dniActual = m.getValueAt(modelRow,4).toString();
      String telActual = m.getValueAt(modelRow,5) == null ? "" : m.getValueAt(modelRow,5).toString();

      JTextField mailCliente = new JTextField(mail);
      JTextField nombreCliente = new JTextField(nombre);
      JTextField apellidoCliente = new JTextField(apellido);
      JTextField dniCliente = new JTextField(dniActual);
      JTextField telCliente = new JTextField(telActual);

      JPanel p = new JPanel(new GridLayout(0,2));
      p.add(new JLabel("Mail:")); p.add(mailCliente);
      p.add(new JLabel("Nombre:")); p.add(nombreCliente);
      p.add(new JLabel("Apellido:")); p.add(apellidoCliente);
      p.add(new JLabel("DNI:")); p.add(dniCliente);
      p.add(new JLabel("Telefono:")); p.add(telCliente);

      boolean exitoso = false;
      while (!exitoso) {
        int opt = JOptionPane.showConfirmDialog(table, p, "Editar Cliente", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(opt != JOptionPane.OK_OPTION) {
          break; // Usuario canceló
        }
        
        try{
          int dni = Integer.parseInt(dniCliente.getText().trim());
          
          Integer tel = null;
          String telStr = telCliente.getText().trim();
          if (!telStr.isBlank()) {
            tel = Integer.parseInt(telStr);
          }
          
          String mailNuevo = mailCliente.getText();
          String nombreNuevo = nombreCliente.getText();
          String apellidoNuevo = apellidoCliente.getText();
          
          clienteService.actualizar(currentId, mailNuevo, nombreNuevo, apellidoNuevo, dni, tel);
          m.setValueAt(mailNuevo, modelRow, 1);
          m.setValueAt(nombreNuevo, modelRow, 2);
          m.setValueAt(apellidoNuevo, modelRow, 3);
          m.setValueAt(dni, modelRow, 4);
          m.setValueAt(tel, modelRow, 5);
          exitoso = true; // Éxito, salir del bucle
        } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(table, 
              "Error de formato:\n" +
              "DNI y Teléfono deben ser números válidos",
              "Formato Inválido",
              JOptionPane.ERROR_MESSAGE);
        } catch (ValidationException ex) {
          JOptionPane.showMessageDialog(table, 
              "Error de validación:\n" +
              "Campo: " + ex.getField() + "\n" +
              "Mensaje: " + ex.getMessage(),
              "Datos Inválidos",
              JOptionPane.ERROR_MESSAGE);
        } catch (DatabaseException ex) {
          JOptionPane.showMessageDialog(table, 
              "Error de base de datos:\n" +
              "Operación: " + ex.getOperation() + "\n" +
              "Tabla: " + ex.getTable() + "\n" +
              "Mensaje: " + ex.getMessage(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
          exitoso = true; // Error de BD
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(table, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          exitoso = true; // Error inesperado, salir
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
      // Validación: no permitir eliminar si el cliente tiene reservas
      try {
        if (!clienteService.puedeEliminar(currentId)) {
          JOptionPane.showMessageDialog(table, "No se puede eliminar: el cliente tiene reservas asociadas.", "Error", JOptionPane.ERROR_MESSAGE);
          fireEditingCanceled();
          return;
        }

        clienteService.eliminar(currentId);
      } catch (ValidationException ex) {
        JOptionPane.showMessageDialog(table, 
            "Error de validación:\n" +
            "Campo: " + ex.getField() + "\n" +
            "Mensaje: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        fireEditingCanceled();
        return;
      } catch (DatabaseException ex) {
        JOptionPane.showMessageDialog(table, 
            "Error de base de datos:\n" +
            "Operación: " + ex.getOperation() + "\n" +
            "Tabla: " + ex.getTable() + "\n" +
            "Mensaje: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        fireEditingCanceled();
        return;
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(table, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
