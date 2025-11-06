import java.util.logging.Logger;

public class Hotel {

  private SistemaDB sistemaDB ;
  private SistemaLogin sistemaLogin;
  private SistemaReservas sistemaReservas;
  private SistemaHabitaciones sistemaHabitaciones;
  private SistemaClientes sistemaClientes;
  private SistemaEmpleados sistemaEmpleados;
  private SistemaBuffet sistemaBuffet;
  private Logger logger = Logger.getLogger(getClass().getName());

  public Hotel(){
    try {
          sistemaDB = new SistemaDB("jdbc:sqlite:hotel.db", "", "");

          sistemaDB.init();

          sistemaReservas = new SistemaReservas(sistemaDB);
          sistemaEmpleados = new SistemaEmpleados(sistemaDB);
          sistemaHabitaciones = new SistemaHabitaciones(sistemaDB);
          sistemaClientes = new SistemaClientes(sistemaDB);
          sistemaBuffet = new SistemaBuffet(sistemaDB);
          sistemaLogin = new SistemaLogin(sistemaEmpleados, sistemaReservas, sistemaHabitaciones, sistemaClientes, sistemaBuffet);
          
    } catch (Exception e) {
          System.out.println("Error al inicializar el sistema: " + e.getMessage());
    }
  }

  public void init(){

    logger.info("Cantidad empleados: " + sistemaEmpleados.getAll().size());

    if (sistemaEmpleados.getAll().isEmpty()) {
        System.out.println("⚠️ No hay empleados registrados. Creando nuevo administrador...");
        sistemaLogin.firstInit();
    } else {
      System.out.println("✅ Empleados encontrados. Continuando con el sistema...");
      sistemaLogin.init();
    }
  }
}
