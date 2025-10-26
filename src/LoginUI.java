import java.awt.*;
import javax.swing.*;

public class LoginUI extends JFrame {
    private JPanel mainPanel;
    private JTextField textField1;
    private JButton aceptarButton;
    private JPasswordField passwordField1;
    private JLabel Rear;

    public LoginUI() {
        setTitle("Login");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal
        mainPanel = new JPanel();
        mainPanel.setLayout(null); // usamos posición absoluta para simplicidad
        getContentPane().add(mainPanel);

        // Imagen (Rear)
        Rear = new JLabel();
        Rear.setBounds(150, 30, 200, 150); // posición y tamaño
        mainPanel.add(Rear);

        // Campo de texto para usuario
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setBounds(100, 220, 100, 30);
        mainPanel.add(lblUser);

        textField1 = new JTextField();
        textField1.setBounds(200, 220, 180, 30);
        mainPanel.add(textField1);

        // Campo de contraseña
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setBounds(100, 270, 100, 30);
        mainPanel.add(lblPass);

        passwordField1 = new JPasswordField();
        passwordField1.setBounds(200, 270, 180, 30);
        mainPanel.add(passwordField1);

        // Botón Aceptar
        aceptarButton = new JButton("Aceptar");
        aceptarButton.setBounds(200, 330, 100, 35);
        mainPanel.add(aceptarButton);

        // Imagen escalada después de renderizar
        SwingUtilities.invokeLater(() -> {
            ImageIcon icon = new ImageIcon(getClass().getResource("imagePT.png"));
            int maxWidth = 200;
            int maxHeight = 150;
            int width = Math.min(Rear.getWidth(), maxWidth);
            int height = Math.min(Rear.getHeight(), maxHeight);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            Rear.setIcon(new ImageIcon(img));
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginUI::new);
    }
}
