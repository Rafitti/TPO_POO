package Clases;

import Clases.Exceptions.DatabaseException;

import java.sql.*;
import java.util.*;
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

  public void init() throws DatabaseException {
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
              IdEmpleadoACargo INTEGER NULLABLE,
              FaltaLimpiar BOOLEAN NOT NULL DEFAULT 1,
              FOREIGN KEY(IdEmpleadoACargo) REFERENCES Empleados(Id)
          );
      """);

      ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM Habitaciones;");
      int count = 0;
      if (rs.next()) {
          count = rs.getInt("count");
      }

      if(count == 0){
          // Insertar habitaciones iniciales
          String[] insertStatements = {
              "INSERT INTO Habitaciones (Numero, Tipo, IdEmpleadoACargo, FaltaLimpiar) VALUES (101, 0, NULL, 1);",
              "INSERT INTO Habitaciones (Numero, Tipo, IdEmpleadoACargo, FaltaLimpiar) VALUES (102, 1, NULL, 1);",
              "INSERT INTO Habitaciones (Numero, Tipo, IdEmpleadoACargo, FaltaLimpiar) VALUES (103, 2, NULL, 1);",
              "INSERT INTO Habitaciones (Numero, Tipo, IdEmpleadoACargo, FaltaLimpiar) VALUES (104, 0, NULL, 1);",
              "INSERT INTO Habitaciones (Numero, Tipo, IdEmpleadoACargo, FaltaLimpiar) VALUES (105, 1, NULL, 1);"
          };

          for (String sql : insertStatements) {
              stmt.executeUpdate(sql);
          }
      }

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
      throw new DatabaseException("Error al inicializar la base de datos", "INIT", "ALL", e);
    }
  }

  public List<Map<String,Object>> entityGetAll(String table) throws DatabaseException {
    try{
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM " + table + ";");
      List<Map<String,Object>> rows = new ArrayList<>();

      ResultSetMetaData md = rs.getMetaData();
      int cols = md.getColumnCount();

      while(rs.next()){
        Map<String,Object> row = new HashMap<>();
        for(int i=1;i<=cols;i++){
          String colName = md.getColumnName(i);
          Object val = rs.getObject(i);
          row.put(colName, val);
        }
        rows.add(row);
      }
      return rows;
    }catch(SQLException e){
      throw new DatabaseException("Error al obtener todos los registros", "SELECT", table, e);
    }
  }

  public List<Map<String,Object>> entityGetBy(String table, Map<String,Object> where) throws DatabaseException {
    if(where == null || where.isEmpty()) return entityGetAll(table);

    StringBuilder sql = new StringBuilder("SELECT * FROM ").append(table).append(" WHERE ");
    List<Object> params = new ArrayList<>();

    int i = 0;
    for(String col : where.keySet()){
      if(i++ > 0) sql.append(" AND ");
      sql.append(col).append(" = ?");
      params.add(where.get(col));
    }
    sql.append(";");

    try{
      PreparedStatement pstmt = conn.prepareStatement(sql.toString());
      for(int j=0;j<params.size();j++){
        pstmt.setObject(j+1, params.get(j));
      }
      ResultSet rs = pstmt.executeQuery();

      List<Map<String,Object>> rows = new ArrayList<>();
      ResultSetMetaData md = rs.getMetaData();

      int cols = md.getColumnCount();

      while(rs.next()){
        Map<String,Object> row = new HashMap<>();
        for(int k=1;k<=cols;k++){
          String colName = md.getColumnName(k);
          Object val = rs.getObject(k);
          row.put(colName, val);
        }
        rows.add(row);
      }
      return rows;
    }catch(SQLException e){
      throw new DatabaseException("Error al obtener registros con filtro", "SELECT", table, e);
    }
  }

  public Map<String,Object> entityCreate(String table, Map<String,Object> values) throws DatabaseException {
    if(values == null || values.isEmpty()) return null;

    StringBuilder cols = new StringBuilder();
    StringBuilder holders = new StringBuilder();
    List<Object> params = new ArrayList<>();

    int i = 0;
    for(String col : values.keySet()){
      if(i++ > 0){ cols.append(", "); holders.append(", "); }
      cols.append(col);
      holders.append("?");
      params.add(values.get(col));
    }

    String sql = "INSERT INTO " + table + " (" + cols.toString() + ") VALUES (" + holders.toString() + ");";

    try{
      PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      for(int j=0;j<params.size();j++) pstmt.setObject(j+1, params.get(j));
      pstmt.executeUpdate();

      ResultSet gen = pstmt.getGeneratedKeys();
      Integer id = null;
      if(gen.next()) id = gen.getInt(1);

      if(id != null){
        Map<String,Object> where = new HashMap<>();
        where.put("Id", id);
        List<Map<String,Object>> rows = entityGetBy(table, where);
        if(rows != null && !rows.isEmpty()) return rows.get(0);
      }

      return null;
    }catch(SQLException e){
      throw new DatabaseException("Error al crear registro", "INSERT", table, e);
    }
  }

  public boolean entityUpdate(String table, int id, Map<String,Object> values) throws DatabaseException {
    if(values == null || values.isEmpty()) return false;

    StringBuilder set = new StringBuilder();
    List<Object> params = new ArrayList<>();

    int i = 0;
    for(String col : values.keySet()){
      if(i++ > 0) set.append(", ");
      set.append(col).append(" = ?");
      params.add(values.get(col));
    }

    String sql = "UPDATE " + table + " SET " + set.toString() + " WHERE Id = ?;";

    try{
      PreparedStatement pstmt = conn.prepareStatement(sql);
      int idx = 1;
      for(Object p : params) pstmt.setObject(idx++, p);
      pstmt.setInt(idx, id);
      int affected = pstmt.executeUpdate();
      return affected > 0;
    }catch(SQLException e){
      throw new DatabaseException("Error al actualizar registro", "UPDATE", table, e);
    }
  }

  public boolean entityDelete(String table, int id) throws DatabaseException {
    String sql = "DELETE FROM " + table + " WHERE Id = ?;";

    try{
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, id);

      int affected = pstmt.executeUpdate();

      return affected > 0;
    }catch(SQLException e){
      throw new DatabaseException("Error al eliminar registro", "DELETE", table, e);
    }
  }
}
