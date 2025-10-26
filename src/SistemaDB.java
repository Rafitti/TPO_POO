import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class SistemaDB {
  private String url;
  private String user;
  private String password;
  private Connection conn;
  private Logger logger = Logger.getLogger(getClass().getName());

  public SistemaDB(String url, String user, String password) {
    this.url = url;
    this.user = user;
    this.password = password;
  }

  public void init() {
    try {
      conn = DriverManager.getConnection(url, user, password);
      Statement stmt = conn.createStatement();

      stmt.setQueryTimeout(30);

      // Activar foreign keys
      stmt.execute("PRAGMA foreign_keys = ON;");

      // Tabla Clientes
      stmt.execute("""
          CREATE TABLE IF NOT EXISTS Clientes (
              Id INTEGER PRIMARY KEY AUTOINCREMENT,
              Nombre TEXT NOT NULL,
              Apellido TEXT NOT NULL,
              Password TEXT NOT NULL,
              DNI INTEGER NOT NULL,
              Telefono INTEGER NULLABLE
          );
      """);

      // Tabla Empleados
      stmt.execute("""
          CREATE TABLE IF NOT EXISTS Empleados (
              Id INTEGER PRIMARY KEY AUTOINCREMENT,
              Nombre TEXT NOT NULL,
              Apellido TEXT NOT NULL,
              Password TEXT NOT NULL,
              Rol INTEGER NOT NULL CHECK (Rol BETWEEN 0 AND 3)
          );
      """);

      // Tabla Habitaciones
      stmt.execute("""
          CREATE TABLE IF NOT EXISTS Habitaciones (
              Id INTEGER PRIMARY KEY AUTOINCREMENT,
              Tipo INTEGER NOT NULL CHECK (Tipo BETWEEN 0 AND 2),
              IdEmpleadoACargo INTEGER NOT NULL,
              FOREIGN KEY(IdEmpleadoACargo) REFERENCES Empleados(Id)
          );
      """);

      // Tabla Reservas
      stmt.execute("""
          CREATE TABLE IF NOT EXISTS Reservas (
              Id INTEGER PRIMARY KEY AUTOINCREMENT,
              IdHabitacion INTEGER NOT NULL,
              IdCliente INTEGER NOT NULL,
              FaltaLimpiar BOOLEAN NOT NULL,
              FechaInicio TEXT NOT NULL,
              FechaFin TEXT NOT NULL,
              FOREIGN KEY(IdHabitacion) REFERENCES Habitaciones(Id),
              FOREIGN KEY(IdCliente) REFERENCES Clientes(Id)
          );
      """);

      logger.info("Base de datos inicializada correctamente.");

    }
    catch(SQLException e)
    {
      e.printStackTrace(System.err);
    }
  }

  public ArrayList<Empleado> getEmpleados(){
    try{
      Statement stmt = conn.createStatement();

      ResultSet rs = stmt.executeQuery("SELECT * FROM Empleados;");

      ArrayList<Empleado> empleados = new ArrayList<Empleado>();

      while(rs.next()){
        int id = rs.getInt("Id");
        String nombre = rs.getString("Nombre");
        String apellido = rs.getString("Apellido");
        String password = rs.getString("Password");
        Rol rol = Rol.values()[rs.getInt("Rol")];
        

        Empleado empleado = new Empleado(id, nombre, apellido,password, rol);
        empleados.add(empleado);
      }
      return empleados;
    }  
    catch(SQLException e){
      e.printStackTrace(System.err);
    return null;
    }
  }

  public Empleado getEmpleadoByNombreAndPassword(String nombre, String password) {
    try{
      Statement stmt = conn.createStatement();

      ResultSet rs = stmt.executeQuery("SELECT * FROM Empleados WHERE Nombre = '" + nombre + "' AND Password = '" + password + "';");

      if(rs.next()){
        int id = rs.getInt("Id");
        String apellido = rs.getString("Apellido");
        Rol rol = Rol.values()[rs.getInt("Rol")];
        

        Empleado empleado = new Empleado(id, nombre, apellido, password, rol);
        return empleado;
      }
      return null;
    }  
    catch(SQLException e){
      e.printStackTrace(System.err);
    return null;
    }
  } 

  public ArrayList<Cliente> getClientes(){
    try{
      Statement stmt = conn.createStatement();

      ResultSet rs = stmt.executeQuery("SELECT * FROM Clientes;");

      ArrayList<Cliente> clientes = new ArrayList<Cliente>();

      while(rs.next()){
        int id = rs.getInt("Id");
        String nombre = rs.getString("Nombre");
        String apellido = rs.getString("Apellido");
        String password = rs.getString("Password");
        int dni = rs.getInt("DNI");
        

        Cliente cliente = new Cliente(id, nombre, apellido, dni, password);
        clientes.add(cliente);
      }
      return clientes;
    }  
    catch(SQLException e){
      e.printStackTrace(System.err);
    return null;
    }
  }

  public Cliente getClienteByNombreAndPassword(String nombre, String password) {
    try{
      Statement stmt = conn.createStatement();

      ResultSet rs = stmt.executeQuery("SELECT * FROM Clientes WHERE Nombre = '" + nombre + "' AND Password = '" + password + "';");

      if(rs.next()){
        int id = rs.getInt("Id");
        String apellido = rs.getString("Apellido");
        int dni = rs.getInt("DNI");
        

        Cliente cliente = new Cliente(id, nombre, apellido, dni, password);
        return cliente;
      }
      return null;
    }  
    catch(SQLException e){
      e.printStackTrace(System.err);
    return null;
    }
  }

  public ArrayList<Habitacion> getHabitaciones(){
    try{
      Statement stmt = conn.createStatement();

      ResultSet rs = stmt.executeQuery("SELECT * FROM Habitaciones;");

      ArrayList<Habitacion> habitaciones = new ArrayList<Habitacion>();

      while(rs.next()){
        int numero = rs.getInt("Id");
        TipoHabitacion tipo = TipoHabitacion.values()[rs.getInt("Tipo")];
        int idEmpleadoACargo = rs.getInt("IdEmpleadoACargo");
        

        Habitacion habitacion = new Habitacion(numero, tipo, idEmpleadoACargo);
        habitaciones.add(habitacion);
      }
      return habitaciones;
    }  
    catch(SQLException e){
      e.printStackTrace(System.err);
    return null;
    }
  }

  public ArrayList<Reserva> getReservas(){
    try{
      Statement stmt = conn.createStatement();

      ResultSet rs = stmt.executeQuery("SELECT * FROM Reservas;");

      ArrayList<Reserva> reservas = new ArrayList<Reserva>();

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

      while(rs.next()){
        int id = rs.getInt("Id");
        int idCliente = rs.getInt("IdCliente");
        int idHabitacion = rs.getInt("IdHabitacion");
        LocalDate fechaInicio = LocalDate.parse(rs.getString("FechaInicio"), formatter);
        LocalDate fechaFin = LocalDate.parse(rs.getString("FechaFin"), formatter);
        

        Reserva reserva = new Reserva(id, idCliente, idHabitacion, fechaInicio, fechaFin);
        reservas.add(reserva);
      }
      return reservas;
    }  
    catch(SQLException e){
      e.printStackTrace(System.err);
    return null;
    }
  }

  public ArrayList<Reserva> getReservasByClienteId(int clienteId){
    try{
      Statement stmt = conn.createStatement();

      ResultSet rs = stmt.executeQuery("SELECT * FROM Reservas WHERE IdCliente = " + clienteId + ";");

      ArrayList<Reserva> reservas = new ArrayList<Reserva>();

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

      while(rs.next()){
        int id = rs.getInt("Id");
        int idHabitacion = rs.getInt("IdHabitacion");
        LocalDate fechaInicio = LocalDate.parse(rs.getString("FechaInicio"), formatter);
        LocalDate fechaFin = LocalDate.parse(rs.getString("FechaFin"), formatter);
        

        Reserva reserva = new Reserva(id, clienteId, idHabitacion, fechaInicio, fechaFin);
        reservas.add(reserva);
      }
      return reservas;
    }  
    catch(SQLException e){
      e.printStackTrace(System.err);
    return null;
    }
  }

    public Connection getConnection() {
        return conn;
    }
}
