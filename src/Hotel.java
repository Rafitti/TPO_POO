public class Hotel {

  private SistemaDB sistemaDB ;
  private SistemaLogin sistemaLogin;
  private SistemaReservas sistemaReservas;
  private SistemaEmpleados sistemaEmpleados;

  public Hotel(){
    try {
          sistemaDB = new SistemaDB("jdbc:sqlite:hotel.db", "", "");

          sistemaDB.init();

          sistemaLogin = new SistemaLogin(sistemaDB);
          sistemaReservas = new SistemaReservas(sistemaDB);
          sistemaEmpleados = new SistemaEmpleados(sistemaDB);
          
    } catch (Exception e) {
          System.out.println("Error al inicializar el sistema: " + e.getMessage());
    }
  }

  public void init(){
    sistemaLogin.inicio();
  }
}
