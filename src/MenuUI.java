import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuUI implements ActionListener {
    private JFrame frame;
    private JPanel fondo;
    private JLabel titulo;
    private JButton botonReservas;
    private JButton botonHabitaciones;
    private JButton botonClientes;
    private JButton botonEmpleados;
    private JButton botonBuffet;
    private JButton botonCerrarSesion;

    private SistemaLogin sl;
    private SistemaReservas sr;
    private SistemaHabitaciones sh;
    private SistemaClientes sc;
    private SistemaEmpleados se;
    private SistemaBuffet sb;

    public MenuUI(SistemaLogin sl, SistemaReservas sr, SistemaHabitaciones sh, SistemaClientes sc, SistemaEmpleados se, SistemaBuffet sb) {
        this.sl = sl;
        this.sr = sr;
        this.sh = sh;
        this.sc = sc;
        this.se = se;
        this.sb = sb;

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

        titulo = new JLabel("Menú Principal");
        titulo.setBounds(200, 30, 150, 30);
        fondo.add(titulo);

        Rol rol = sl.getSessionRole();

        if(rol == Rol.RECEPCIONISTA || rol == Rol.ADMINISTRADOR){
            botonReservas = new JButton("Gestión de Reservas");
            botonReservas.setBounds(150, 80, 200, 40);
            botonReservas.addActionListener(this);
            fondo.add(botonReservas);

            botonClientes = new JButton("Gestión de Clientes");
            botonClientes.setBounds(150, 140, 200, 40);
            botonClientes.addActionListener(this);
            fondo.add(botonClientes);
        }

        if(rol == Rol.MUCAMA || rol == Rol.ADMINISTRADOR){
            botonHabitaciones = new JButton("Gestión de Habitaciones");
            botonHabitaciones.setBounds(150, 200, 200, 40);
            botonHabitaciones.addActionListener(this);
            fondo.add(botonHabitaciones);
        }

        if(rol == Rol.COCINERO || rol == Rol.ADMINISTRADOR){  
            botonBuffet = new JButton("Gestión de Buffet");
            botonBuffet.setBounds(150, 260, 200, 40);
            botonBuffet.addActionListener(this);
            fondo.add(botonBuffet);
        }

        if(rol == Rol.ADMINISTRADOR){
            botonEmpleados = new JButton("Gestión de Empleados");
            botonEmpleados.setBounds(150, 320, 200, 40);
            botonEmpleados.addActionListener(this);
            fondo.add(botonEmpleados);
        }

        botonCerrarSesion = new JButton("Cerrar Sesión");
        botonCerrarSesion.setBounds(150, 380, 200, 40);
        botonCerrarSesion.addActionListener(this);
        fondo.add(botonCerrarSesion);
    }

    public void init() {
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botonReservas) {
            new ReservasUI(sr, sc, sh, this).init();
            frame.setVisible(false);
        } else if (e.getSource() == botonHabitaciones) {
            new HabitacionesUI(sh, this).init();
            frame.setVisible(false);
        } else if (e.getSource() == botonClientes) {
            new ClientesUI(sc, this).init();
            frame.setVisible(false);
        } else if (e.getSource() == botonEmpleados) {
            new EmpleadosUI(se, this).init();
            frame.setVisible(false);
        } else if (e.getSource() == botonBuffet) {
            new BuffetUI(sb).init();
            frame.setVisible(false);
        } else if (e.getSource() == botonCerrarSesion) {
            frame.dispose();
            sl.init();
        }
    }
}
