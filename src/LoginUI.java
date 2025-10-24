import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginUI {
    private JPanel Login;
    private JTextField textField1;
    private JButton aceptarButton;
    private JPasswordField passwordField1;
    private JLabel Rear;

    public LoginUI() {

        aceptarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }


    public static void Login() {
        JFrame frame = new JFrame("LoginUI");
        LoginUI loginUI = new LoginUI();
        frame.setContentPane(loginUI.Login);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,500);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            ImageIcon icon = new ImageIcon(loginUI.getClass().getResource("imagePT.png"));
            int maxWidth = 300;
            int maxHeight = 300;

            int width = Math.min(loginUI.Rear.getWidth(), maxWidth);
            int height = Math.min(loginUI.Rear.getHeight(), maxHeight);

            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            loginUI.Rear.setIcon(new ImageIcon(img));
        });

    }
}
