package UI;

import Clases.Entidades.*;
import Clases.Interfaces.*;
import Clases.Exceptions.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginUI implements ActionListener {
    private JPanel fondo;
    private JFrame frame;
    private JLabel titulo;
    private JLabel mailLabel;
    private JTextField mailUsuario;
    private JLabel passwordLabel;
    private JPasswordField passwordUsuario;
    private JButton aceptarButton;
    private IEmpleadoService empleadoService;
    private INavigationService navigation;

    public LoginUI(IEmpleadoService empleadoService, INavigationService navigation) {
        this.empleadoService = empleadoService;
        this.navigation = navigation;

        frame = new JFrame("Login");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setResizable(false);
        fondo = new JPanel();
        fondo.setBounds(0, 0, 500, 500);
        fondo.setBackground(Color.LIGHT_GRAY);
        fondo.setLayout(null);
        frame.add(fondo);

        titulo = new JLabel("Iniciar Sesión");
        titulo.setBounds(200, 30, 150, 30);
        mailLabel = new JLabel("Email:");
        mailLabel.setBounds(100, 100, 80, 20);

        mailUsuario = new JTextField();
        mailUsuario.setBounds(200, 100, 200, 20);
        mailUsuario.setToolTipText("Email");
        passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setBounds(100, 150, 80, 20);
        passwordUsuario = new JPasswordField();
        passwordUsuario.setBounds(200, 150, 200, 20);
        passwordUsuario.setToolTipText("Contraseña");
        aceptarButton = new JButton("Aceptar");
        aceptarButton.setBounds(200, 200, 100, 30);

        fondo.add(titulo);
        fondo.add(mailLabel);
        fondo.add(mailUsuario);
        fondo.add(passwordLabel);
        fondo.add(passwordUsuario);
        fondo.add(aceptarButton);

        aceptarButton.addActionListener(this);
    }

    public void init(){
        frame.setVisible(true);
    }

     @Override
    public void actionPerformed(ActionEvent e){

        if(e.getSource() == aceptarButton){
            try{
                String mail = mailUsuario.getText();
                String password = new String(passwordUsuario.getPassword());
                
                Empleado empleado = empleadoService.autenticar(mail, password);
                
                JOptionPane.showMessageDialog(fondo, "Login exitoso");
                frame.dispose();
                navigation.mostrarMenuPrincipal(empleado);
                
            } catch (ValidationException vex) {
                JOptionPane.showMessageDialog(fondo, vex.getMessage(), "Datos inválidos", JOptionPane.WARNING_MESSAGE);
            } catch (DatabaseException dex) {
                JOptionPane.showMessageDialog(fondo, 
                    "Error de base de datos:\n" + dex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(fondo, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
