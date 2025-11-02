import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.LocalTime;
import java.util.logging.Logger;

public class Hotel {

  private SistemaDB sistemaDB ;
  private SistemaLogin sistemaLogin;
  private SistemaReservas sistemaReservas;
  private SistemaEmpleados sistemaEmpleados;
  private Logger logger = Logger.getLogger(getClass().getName());

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
    logger.info(String.valueOf("empleados null?: " + (sistemaDB.getEmpleados() == null)));
    logger.info("empleados: " + sistemaDB.getEmpleados().isEmpty());

    if (sistemaDB.getEmpleados() == null || sistemaDB.getEmpleados().isEmpty()) {
        System.out.println("⚠️ No hay empleados registrados. Creando nuevo administrador...");
        sistemaLogin.firstInit();
    } else {
      System.out.println("✅ Empleados encontrados. Continuando con el sistema...");
      sistemaLogin.init();
    }
  }
}
