package Clases.Repositorios;

import Clases.SistemaDB;
import Clases.Entidades.*;
import Clases.Exceptions.DatabaseException;
import Clases.Interfaces.IEmpleadoRepository;

import java.util.*;

public class SistemaEmpleados implements IEmpleadoRepository {
  private final SistemaDB db;

  public SistemaEmpleados(SistemaDB db){ this.db = db; }

  private Empleado mapRowToEmpleado(Map<String,Object> row){
    if(row == null) return null;

    int id = ((Number)row.get("Id")).intValue();
    String mail = (String) row.get("Mail");
    String nombre = (String) row.get("Nombre");
    String apellido = (String) row.get("Apellido");
    String password = (String) row.get("Password");
    Rol rol = Rol.values()[((Number)row.get("Rol")).intValue()];

    return new Empleado(id, mail, nombre, apellido, password, rol);
  }

  public List<Empleado> getAll() throws DatabaseException {
    List<Map<String,Object>> rows = db.entityGetAll("Empleados");
    List<Empleado> empleados = new ArrayList<>();

    if(rows == null) return empleados;

    for(Map<String,Object> r : rows) empleados.add(mapRowToEmpleado(r));

    return empleados;
  }

  public List<Empleado> getBy(Map<String,Object> where) throws DatabaseException {
    List<Map<String,Object>> rows = db.entityGetBy("Empleados", where);
    List<Empleado> empleados = new ArrayList<>();

    if(rows == null) return empleados;

    for(Map<String,Object> r : rows) empleados.add(mapRowToEmpleado(r));

    return empleados;
  }

  public Empleado getByMailAndPassword(String mail, String password) throws DatabaseException {
    HashMap<String,Object> where = new HashMap<>();
    where.put("Mail", mail);
    where.put("Password", password);

    List<Map<String,Object>> rows = db.entityGetBy("Empleados", where);
    
    if(rows == null || rows.isEmpty()) return null;

    return mapRowToEmpleado(rows.get(0));
  }

  public Empleado create(String mail, String nombre, String apellido, String password, Rol rol) throws DatabaseException {
    HashMap<String,Object> data = new HashMap<>();
    data.put("Mail", mail);
    data.put("Nombre", nombre);
    data.put("Apellido", apellido);
    data.put("Password", password);
    data.put("Rol", rol.ordinal());

    Map<String,Object> result = db.entityCreate("Empleados", data);

    return mapRowToEmpleado(result);
  }

  public boolean update(int id, Map<String,Object> values) throws DatabaseException {
    return db.entityUpdate("Empleados", id, values);
  }

  public boolean delete(int id) throws DatabaseException {
    return db.entityDelete("Empleados", id);
  }
}
