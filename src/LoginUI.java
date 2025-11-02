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
    private SistemaDB db;
    private SistemaLogin sistemaLogin;

    public LoginUI(SistemaDB db,SistemaLogin sistemaLogin) {
        this.db = db;
        this.sistemaLogin = sistemaLogin;

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
                
                if(mail.isEmpty()){
                    JOptionPane.showMessageDialog(fondo,"Debe ingresar un email.");
                    return;
                }

                String password = new String(passwordUsuario.getPassword());

                if(password.isEmpty()){
                    JOptionPane.showMessageDialog(fondo,"Debe ingresar una contraseña.");
                    return;
                }

                Cliente cliente = db.getClienteByMailAndPassword(mail, password);
                if(cliente != null){
                    JOptionPane.showMessageDialog(fondo, "Login exitoso");
                    sistemaLogin.setSessionUser(cliente);
                    frame.dispose();

                    new MenuUI(db, sistemaLogin).init();
                    return;
                }

                
                Empleado empleado = db.getEmpleadoByMailAndPassword(mail, password);
                if(empleado != null){
                    JOptionPane.showMessageDialog(fondo, "Login exitoso");
                    sistemaLogin.setSessionUser(empleado);
                    frame.dispose();

                    new MenuUI(db, sistemaLogin).init();
                    return;
                }

                JOptionPane.showMessageDialog(fondo, "Login fallido. Email o contraseña incorrectos.");
                return;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(fondo, "Error al procesar el login: " + ex.getMessage());
            }
        }
    }

}
