import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Statement;

public class FirstInitUI {
    private JPanel AdminUI;
    private JPasswordField passwordAdmin;
    private JTextField SurnameAdmin;
    private JTextField NameAdmim;
    private JButton button1;
    private SistemaDB db;


    public FirstInitUI() {

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OnClick();
            }
        });
    }

    private void OnClick(){
        String nombre = NameAdmim.getText();
        String apellido = SurnameAdmin.getText();
        String password = new String(passwordAdmin.getPassword());

        if (nombre.isEmpty() || apellido.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(AdminUI, "Todos los campos son obligatorios");
            return;
        }
        try (Statement stm = db.getConnection().createStatement()){
            stm.executeUpdate(
                    "INSERT INTO EMPLEADOS VALUES (?, ?, ?, ?, ?)");
        }
        catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(AdminUI, "Error al crear admin" + e.getMessage());
        }
    }

    public FirstInitUI(SistemaDB sistemaDB) {
        this.db = sistemaDB;
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
