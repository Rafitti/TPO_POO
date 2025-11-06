import javax.swing.*;
import java.awt.*;

public class ReservasUI {
  private JFrame frame;
  private JPanel fondo;
  private JLabel titulo;
  
  public ReservasUI(SistemaReservas sr) {
    frame = new JFrame("Gestión de Reservas");
    frame.setSize(600, 400);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setLayout(null);
    frame.setResizable(false);
    fondo = new JPanel();
    fondo.setBounds(0, 0, 600, 400);
    fondo.setBackground(Color.WHITE);
    fondo.setLayout(null);
    frame.add(fondo);

    titulo = new JLabel("Gestión de Reservas");
    titulo.setBounds(250, 20, 200, 30);
    fondo.add(titulo);

  }

  public void init(){
    frame.setVisible(true);
  }

}
