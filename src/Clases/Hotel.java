package Clases;

import Clases.Exceptions.DatabaseException;
import Clases.Interfaces.*;
import Clases.Repositorios.*;
import Clases.Services.*;

import java.util.logging.Logger;

public class Hotel {

  private final IClienteRepository clienteRepo;
  private final IEmpleadoRepository empleadoRepo;
  private final IHabitacionRepository habitacionRepo;
  private final IReservaRepository reservaRepo;
  private final INavigationService navigation;
  private Logger logger = Logger.getLogger(getClass().getName());

  public Hotel(){
    IClienteRepository tempClienteRepo = null;
    IEmpleadoRepository tempEmpleadoRepo = null;
    IHabitacionRepository tempHabitacionRepo = null;
    IReservaRepository tempReservaRepo = null;
    INavigationService tempNavigation = null;
    
    try {
          // 1. Crear la base de datos y las tablas
          SistemaDB db = new SistemaDB("jdbc:sqlite:hotel.db", "", "");
          db.init();

          // 2. Crear repositorios
          tempClienteRepo = new SistemaClientes(db);
          tempEmpleadoRepo = new SistemaEmpleados(db);
          tempHabitacionRepo = new SistemaHabitaciones(db);
          tempReservaRepo = new SistemaReservas(db);
          
          // 3. Crear servicios
          IClienteService clienteService = new ClienteService(tempClienteRepo, tempReservaRepo);
          IEmpleadoService empleadoService = new EmpleadoService(tempEmpleadoRepo);
          IHabitacionService habitacionService = new HabitacionService(tempHabitacionRepo);
          IReservaService reservaService = new ReservaService(tempReservaRepo);
          
          // 4. Crear servicio de navegación
          tempNavigation = new NavigationService(clienteService, empleadoService, habitacionService, reservaService);
          
    } catch (DatabaseException e) {
          System.err.println("Error de base de datos al inicializar el sistema:");
          System.err.println("   Operación: " + e.getOperation());
          System.err.println("   Tabla: " + e.getTable());
          System.err.println("   Mensaje: " + e.getMessage());
          if (e.getCause() != null) {
              System.err.println("   Causa: " + e.getCause().getMessage());
          }
          System.exit(1);
    } catch (Exception e) {
          System.err.println("Error inesperado al inicializar el sistema: " + e.getMessage());
          System.exit(1);
    }
    
    this.clienteRepo = tempClienteRepo;
    this.empleadoRepo = tempEmpleadoRepo;
    this.habitacionRepo = tempHabitacionRepo;
    this.reservaRepo = tempReservaRepo;
    this.navigation = tempNavigation;
  }

  public void init(){
    try {
      logger.info("Cantidad empleados: " + empleadoRepo.getAll().size() + ".");

      if (empleadoRepo.getAll().isEmpty()) {
          System.out.println("No hay empleados registrados. Creando nuevo administrador...");
          navigation.mostrarPrimeraConfiguracion();
      } else {
        System.out.println("Empleados encontrados. Continuando con el sistema...");
        navigation.mostrarLogin();
      }
    } catch (DatabaseException e) {
          System.err.println("Error de base de datos:");
          System.err.println("   Operación: " + e.getOperation());
          System.err.println("   Tabla: " + e.getTable());
          System.err.println("   Mensaje: " + e.getMessage());
          if (e.getCause() != null) {
              System.err.println("   Causa: " + e.getCause().getMessage());
          }
    }
  }
}
