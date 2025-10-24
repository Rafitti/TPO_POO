import javax.swing.*;

public class FirstInitUI {
    private JPanel AdminUI;
    private JPasswordField passwordAdmin;
    private JTextField SurnameAdmin;
    private JTextField NameAdmim;
    private JButton button1;
    private SistemaDB db;


    public FirstInitUI() {

    }

    public FirstInitUI(SistemaDB sistemaDB) {
        this.db = db;
        JFrame frame = new JFrame("Registrar Administrador");
        frame.setContentPane(new FirstInitUI().AdminUI);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(400,300);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

}
