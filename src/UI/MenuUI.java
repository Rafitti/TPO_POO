package UI;

import Clases.Entidades.*;
import Clases.Interfaces.*;

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
    private JButton botonCerrarSesion;

    private IClienteService clienteService;
    private IEmpleadoService empleadoService;
    private IHabitacionService habitacionService;
    private IReservaService reservaService;
    private INavigationService navigation;
    private Empleado empleadoLogueado;

    public MenuUI(
        IClienteService clienteService, 
        IEmpleadoService empleadoService,
        IHabitacionService habitacionService, 
        IReservaService reservaService,
        INavigationService navigation,
        Empleado empleadoLogueado
    ) {
        this.clienteService = clienteService;
        this.empleadoService = empleadoService;
        this.habitacionService = habitacionService;
        this.reservaService = reservaService;
        this.navigation = navigation;
        this.empleadoLogueado = empleadoLogueado;

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

        Rol rol = empleadoLogueado.getRol();

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

        if(rol == Rol.ADMINISTRADOR){
            botonEmpleados = new JButton("Gestión de Empleados");
            botonEmpleados.setBounds(150, 260, 200, 40);
            botonEmpleados.addActionListener(this);
            fondo.add(botonEmpleados);
        }

        botonCerrarSesion = new JButton("Cerrar Sesión");
        botonCerrarSesion.setBounds(150, 320, 200, 40);
        botonCerrarSesion.addActionListener(this);
        fondo.add(botonCerrarSesion);
    }

    public void init() {
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botonReservas) {
            new ReservasUI(reservaService, clienteService, habitacionService, this).init();
            frame.setVisible(false);
        } else if (e.getSource() == botonHabitaciones) {
            new HabitacionesUI(habitacionService, this).init();
            frame.setVisible(false);
        } else if (e.getSource() == botonClientes) {
            new ClientesUI(clienteService, this).init();
            frame.setVisible(false);
        } else if (e.getSource() == botonEmpleados) {
            new EmpleadosUI(empleadoService, this).init();
            frame.setVisible(false);
        } else if (e.getSource() == botonCerrarSesion) {
            frame.dispose();
            navigation.cerrarSesion();
        }
    }
}
