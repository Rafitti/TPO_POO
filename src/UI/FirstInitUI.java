package UI;

import Clases.Entidades.*;
import Clases.Interfaces.*;
import Clases.Exceptions.*;

import javax.swing.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

public class FirstInitUI implements ActionListener {
    private JFrame frame;
    private JPanel fondo;
    private JLabel tituloAdmin;
    private JLabel mailLabel;
    private JTextField mailAdmin;
    private JLabel nombreLabel;
    private JTextField nombreAdmin;
    private JLabel apellidoLabel;
    private JTextField apellidoAdmin;
    private JLabel passwordLabel;
    private JPasswordField passwordAdmin;
    private JButton ingresarButton;
    private IEmpleadoService empleadoService;
    private INavigationService navigation;
    private Logger logger = Logger.getLogger(getClass().getName());

    public FirstInitUI(IEmpleadoService empleadoService, INavigationService navigation) {
        this.empleadoService = empleadoService;
        this.navigation = navigation;

        frame = new JFrame("Registrar Administrador");
        frame.setSize(400,300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setResizable(false);

        fondo = new JPanel();
        fondo.setBounds(0, 0, 400, 300);
        fondo.setBackground(Color.DARK_GRAY);
        fondo.setLayout(null);
        frame.add(fondo);

        tituloAdmin = new JLabel("Bienvenido! Registre al administrador:");
        tituloAdmin.setBounds(90,20,250,30);
        tituloAdmin.setForeground(Color.WHITE);

        mailLabel = new JLabel("Email:");
        mailLabel.setBounds(20,50,80,20);
        mailLabel.setForeground(Color.WHITE);
        mailAdmin = new JTextField();
        mailAdmin.setBounds(100,50,200,20);
        mailAdmin.setToolTipText("Email");

        nombreLabel = new JLabel("Nombre:");
        nombreLabel.setBounds(20,80,80,20);
        nombreLabel.setForeground(Color.WHITE);
        nombreAdmin = new JTextField();
        nombreAdmin.setBounds(100,80, 200,20);
        nombreAdmin.setToolTipText("Nombre");

        apellidoLabel = new JLabel("Apellido:");
        apellidoLabel.setBounds(20,110,80,20);
        apellidoLabel.setForeground(Color.WHITE);
        apellidoAdmin = new JTextField();
        apellidoAdmin.setBounds(100,110,200,20);
        apellidoAdmin.setToolTipText("Apellido");

        passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setBounds(20,140,80,20);
        passwordLabel.setForeground(Color.WHITE);
        passwordAdmin = new JPasswordField();
        passwordAdmin.setBounds(100,140,200,20);
        passwordAdmin.setToolTipText("Contraseña");

        ingresarButton = new JButton("Ingresar");
        ingresarButton.setBounds(100,180,200,30);

        fondo.add(tituloAdmin);
        fondo.add(mailLabel);
        fondo.add(mailAdmin);
        fondo.add(nombreLabel);
        fondo.add(nombreAdmin);
        fondo.add(apellidoLabel);
        fondo.add(apellidoAdmin);
        fondo.add(passwordLabel);   
        fondo.add(passwordAdmin);
        fondo.add(ingresarButton);

        ingresarButton.addActionListener(this);
    }

    public void init(){
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e){

        if(e.getSource() == ingresarButton){
            try{
                String mail = mailAdmin.getText();

                if(mail.isEmpty()){
                    JOptionPane.showMessageDialog(fondo,"Debe ingresar un email.");
                    return;
                }

                String nombre = nombreAdmin.getText();

                if(nombre.isEmpty()){
                    JOptionPane.showMessageDialog(fondo,"Debe ingresar un nombre");
                    return;
                }

                String apellido = apellidoAdmin.getText();

                if(apellido.isEmpty()){
                    JOptionPane.showMessageDialog(fondo,"Debe ingresar un apellido.");
                    return;
                }

                String password = String.copyValueOf(passwordAdmin.getPassword());

                if(password.isEmpty()){
                    JOptionPane.showMessageDialog(fondo,"Debe ingresar una contraseña.");
                    return;
                }

                Empleado admin = empleadoService.crear(mail, nombre, apellido, password, Rol.ADMINISTRADOR);
                JOptionPane.showMessageDialog(fondo,"Administrador creado exitosamente.");
                logger.info("Administrador creado: " + admin.getNombre() + " " + admin.getApellido());

                frame.dispose();
                navigation.mostrarLogin();

            } catch (ValidationException vex) {
                JOptionPane.showMessageDialog(fondo, vex.getMessage(), "Datos inválidos", JOptionPane.WARNING_MESSAGE);
            } catch (DatabaseException dex) {
                JOptionPane.showMessageDialog(fondo, 
                    "Error de base de datos:\n" + dex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(fondo, 
                    "Error inesperado: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
