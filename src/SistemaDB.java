import java.sql.*;
import java.util.ArrayList;
import java.time.*;
import java.time.format.*;
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
              Mail TEXT NOT NULL,
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
              Mail TEXT NOT NULL,
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
              Numero INTEGER NOT NULL,
              Tipo INTEGER NOT NULL CHECK (Tipo BETWEEN 0 AND 2),
              IdEmpleadoACargo INTEGER NOT NULL,
              FaltaLimpiar BOOLEAN NOT NULL DEFAULT 1,
              FOREIGN KEY(IdEmpleadoACargo) REFERENCES Empleados(Id)
          );
      """);

      // Tabla Reservas
      stmt.execute("""
          CREATE TABLE IF NOT EXISTS Reservas (
              Id INTEGER PRIMARY KEY AUTOINCREMENT,
              IdHabitacion INTEGER NOT NULL,
              IdCliente INTEGER NOT NULL,
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
        String mail = rs.getString("Mail");
        String nombre = rs.getString("Nombre");
        String apellido = rs.getString("Apellido");
        String password = rs.getString("Password");
        Rol rol = Rol.values()[rs.getInt("Rol")];
        

        Empleado empleado = new Empleado(id, mail, nombre, apellido,password, rol);
        empleados.add(empleado);
      }
      return empleados;
    }  
    catch(SQLException e){
      e.printStackTrace(System.err);
    return null;
    }
  }

  public Empleado getEmpleadoByMailAndPassword(String mail, String password) {
    try{
      Statement stmt = conn.createStatement();

      ResultSet rs = stmt.executeQuery("SELECT * FROM Empleados WHERE Mail = '" + mail + "' AND Password = '" + password + "';");

      if(rs.next()){
        int id = rs.getInt("Id");
        String nombre = rs.getString("Nombre");
        String apellido = rs.getString("Apellido");
        Rol rol = Rol.values()[rs.getInt("Rol")];

        Empleado empleado = new Empleado(id, mail, nombre, apellido, password, rol);
        return empleado;
      }
      return null;
    }  
    catch(SQLException e){
      e.printStackTrace(System.err);
    return null;
    }
  } 

  public Empleado createEmpleado(String mail, String nombre, String apellido, String password, Rol rol){
      try {
          String sql = "INSERT INTO Empleados (Mail, Nombre, Apellido, Password, Rol) VALUES (?,?,?,?,?)";

          PreparedStatement pstmt = conn.prepareStatement(sql);

          pstmt.setString(1, mail);
          pstmt.setString(2, nombre);
          pstmt.setString(3, apellido);
          pstmt.setString(4, password);
          pstmt.setInt(5, Rol.ADMINISTRADOR.ordinal());

          pstmt.executeUpdate();

          ResultSet generatedKeys = pstmt.getGeneratedKeys();
          int id = 0;
          if (generatedKeys.next()) {
              id = generatedKeys.getInt(1);
          }
          return new Empleado(id, mail, nombre, apellido, password, rol);
      }
      catch(SQLException e){
          logger.info(e.getMessage());
          return null;
      }
  }

  public boolean deleteEmpleado(int id){
      try {
          String sql = "DELETE FROM Empleados WHERE Id = ?";

          PreparedStatement pstmt = conn.prepareStatement(sql);

          pstmt.setInt(1, id);

          int affectedRows = pstmt.executeUpdate();

          return affectedRows > 0;
      }
      catch(SQLException e){
          logger.info(e.getMessage());
          return false;
      }
  }

  public ArrayList<Cliente> getClientes(){
    try{
      Statement stmt = conn.createStatement();

      ResultSet rs = stmt.executeQuery("SELECT * FROM Clientes;");

      ArrayList<Cliente> clientes = new ArrayList<Cliente>();

      while(rs.next()){
        int id = rs.getInt("Id");
        String mail = rs.getString("Mail");
        String nombre = rs.getString("Nombre");
        String apellido = rs.getString("Apellido");
        String password = rs.getString("Password");
        int dni = rs.getInt("DNI");
        Integer telefono = rs.getInt("Telefono");

        Cliente cliente = new Cliente(id, mail, nombre, apellido, dni, password, telefono);
        clientes.add(cliente);
      }
      return clientes;
    }  
    catch(SQLException e){
      e.printStackTrace(System.err);
    return null;
    }
  }

  public Cliente getClienteByMailAndPassword(String mail, String password) {
    try{
      Statement stmt = conn.createStatement();

      ResultSet rs = stmt.executeQuery("SELECT * FROM Clientes WHERE Mail = '" + mail + "' AND Password = '" + password + "';");

      if(rs.next()){
        int id = rs.getInt("Id");
        String nombre = rs.getString("Nombre");
        String apellido = rs.getString("Apellido");
        int dni = rs.getInt("DNI");
        Integer telefono = rs.getInt("Telefono");

        Cliente cliente = new Cliente(id, mail, nombre, apellido, dni, password, telefono);
        return cliente;
      }
      return null;
    }  
    catch(SQLException e){
      e.printStackTrace(System.err);
    return null;
    }
  }

  public Cliente createCliente(String mail, String nombre, String apellido, String password, int dni, Integer telefono){
      try {
          String sql = "INSERT INTO Clientes (Mail, Nombre, Apellido, Password, DNI) VALUES (?,?,?,?,?)";

          PreparedStatement pstmt = conn.prepareStatement(sql);

          pstmt.setString(1, mail);
          pstmt.setString(2, nombre);
          pstmt.setString(3, apellido);
          pstmt.setString(4, password);
          pstmt.setInt(5, dni);
          pstmt.setObject(6, telefono);

          pstmt.executeUpdate();

          ResultSet generatedKeys = pstmt.getGeneratedKeys();
          int id = 0;
          if (generatedKeys.next()) {
              id = generatedKeys.getInt(1);
          }
          return new Cliente(id, mail, nombre, apellido, dni, password, telefono);
      }
      catch(SQLException e){
          logger.info(e.getMessage());
          return null;
      }
  }

  public boolean deleteCliente(int id){
      try {
          String sql = "DELETE FROM Clientes WHERE Id = ?";

          PreparedStatement pstmt = conn.prepareStatement(sql);

          pstmt.setInt(1, id);

          int affectedRows = pstmt.executeUpdate();

          return affectedRows > 0;
      }
      catch(SQLException e){
          logger.info(e.getMessage());
          return false;
      }
  }

  public ArrayList<Habitacion> getHabitaciones(){
    try{
      Statement stmt = conn.createStatement();

      ResultSet rs = stmt.executeQuery("SELECT * FROM Habitaciones;");

      ArrayList<Habitacion> habitaciones = new ArrayList<Habitacion>();

      while(rs.next()){
        int id = rs.getInt("Id");
        int numero = rs.getInt("Numero");
        TipoHabitacion tipo = TipoHabitacion.values()[rs.getInt("Tipo")];
        int idEmpleadoACargo = rs.getInt("IdEmpleadoACargo");
        boolean faltaLimpiar = rs.getBoolean("FaltaLimpiar");

        Habitacion habitacion = new Habitacion(id,numero, tipo, idEmpleadoACargo, faltaLimpiar);
        habitaciones.add(habitacion);
      }
      return habitaciones;
    }  
    catch(SQLException e){
      e.printStackTrace(System.err);
    return null;
    }
  }

  public Habitacion createHabitacion(int numero, TipoHabitacion tipo, int idEmpleadoACargo){
      try {
          String sql = "INSERT INTO Habitaciones (Tipo, IdEmpleadoACargo) VALUES (?,?,?)";

          PreparedStatement pstmt = conn.prepareStatement(sql);

          pstmt.setInt(1, numero);
          pstmt.setInt(2, tipo.ordinal());
          pstmt.setInt(3, idEmpleadoACargo);

          pstmt.executeUpdate();

          ResultSet generatedKeys = pstmt.getGeneratedKeys();

          int id = 0;
          if (generatedKeys.next()) {
              id = generatedKeys.getInt(1);
          }

          return new Habitacion(id, numero, tipo, idEmpleadoACargo, false);
      }
      catch(SQLException e){
          logger.info(e.getMessage());
          return null;
      }
  }

  public boolean deleteHabitacion(int id){  
      try {
          String sql = "DELETE FROM Habitaciones WHERE Id = ?";

          PreparedStatement pstmt = conn.prepareStatement(sql);

          pstmt.setInt(1, id);

          int affectedRows = pstmt.executeUpdate();

          return affectedRows > 0;
      }
      catch(SQLException e){
          logger.info(e.getMessage());
          return false;
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
        LocalDateTime fechaInicio = LocalDateTime.parse(rs.getString("FechaInicio"), formatter);
        LocalDateTime fechaFin = LocalDateTime.parse(rs.getString("FechaFin"), formatter);

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
        LocalDateTime fechaInicio = LocalDateTime.parse(rs.getString("FechaInicio"), formatter);
        LocalDateTime fechaFin = LocalDateTime.parse(rs.getString("FechaFin"), formatter);
        

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

  public Reserva createReserva(int idHabitacion, int idCliente, LocalDateTime fechaInicio, LocalDateTime fechaFin){
      try {
        String sql = "INSERT INTO Reservas (IdHabitacion, IdCliente, FechaInicio, FechaFin) VALUES (?,?,?,?)";

        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setInt(1, idHabitacion);
        pstmt.setInt(2, idCliente);
        pstmt.setObject(3, fechaInicio);
        pstmt.setObject(4, fechaFin);

        pstmt.executeUpdate();
        ResultSet generatedKeys = pstmt.getGeneratedKeys();

        int id = 0;
        if (generatedKeys.next()) {
          id = generatedKeys.getInt(1);
        }

        return new Reserva(id, idCliente, idHabitacion, fechaInicio, fechaFin);
      }
      catch(SQLException e){
          logger.info(e.getMessage());
          return null;
      }
  }

  public boolean deleteReserva(int id){
      try {
          String sql = "DELETE FROM Reservas WHERE Id = ?";

          PreparedStatement pstmt = conn.prepareStatement(sql);

          pstmt.setInt(1, id);

          int affectedRows = pstmt.executeUpdate();

          return affectedRows > 0;
      }
      catch(SQLException e){
          logger.info(e.getMessage());
          return false;
      }
  } 
}
