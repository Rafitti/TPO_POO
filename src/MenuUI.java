import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuUI implements ActionListener {
  private SistemaDB sistemaDB;
  private SistemaLogin sistemaLogin;
  private JFrame frame;
  private JPanel fondo;
  private JButton botonReservas;
  private JButton botonHabitaciones;
  private JButton botonClientes;
  private JButton botonEmpleados;
  private JButton botonBuffet;

  public MenuUI(SistemaDB sistemaDB, SistemaLogin sistemaLogin) {
      this.sistemaDB = sistemaDB;
      this.sistemaLogin = sistemaLogin;

      frame = new JFrame("Menú Principal");
      frame.setSize(500, 500);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setLayout(null);
      frame.setResizable(false);
      fondo = new JPanel();
      fondo.setBounds(0, 0, 500, 500);
      fondo.setBackground(Color.GRAY);
      fondo.setLayout(null);
      frame.add(fondo);

      String labelReservas = "Gestión de Reservas";
      if(sistemaLogin.getSessionUserRole() == Rol.CLIENTE){
          labelReservas = "Ver Mis Reservas";
      } 

      if(sistemaLogin.getSessionUserRole() == Rol.CLIENTE){
          botonHabitaciones = new JButton("Ver Habitaciones");
          botonHabitaciones.setBounds(150, 110, 200, 40);
      }
      botonReservas = new JButton(labelReservas);
      botonReservas.setBounds(150, 50, 200, 40);
      botonHabitaciones = new JButton("Gestión de Habitaciones");
      botonHabitaciones.setBounds(150, 110, 200, 40);
      botonClientes = new JButton("Gestión de Clientes");
      botonClientes.setBounds(150, 170, 200, 40);
      botonEmpleados = new JButton("Gestión de Empleados");
      botonEmpleados.setBounds(150, 230, 200, 40);
      botonBuffet = new JButton("Gestión de Buffet");
      botonBuffet.setBounds(150, 290, 200, 40);
  }

  public void init() {
      frame.setVisible(true);
  }
}
