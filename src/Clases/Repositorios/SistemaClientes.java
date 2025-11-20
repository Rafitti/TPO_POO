package Clases.Repositorios;

import Clases.SistemaDB;
import Clases.Entidades.Cliente;
import Clases.Exceptions.DatabaseException;
import Clases.Interfaces.IClienteRepository;

import java.util.*;

public class SistemaClientes implements IClienteRepository {
  private final SistemaDB db;

  public SistemaClientes(SistemaDB db){ this.db = db; }

  private Cliente mapRowToCliente(Map<String,Object> row){
    if(row == null) return null;
    int id = ((Number)row.get("Id")).intValue();
    String mail = (String) row.get("Mail");
    String nombre = (String) row.get("Nombre");
    String apellido = (String) row.get("Apellido");
    int dni = row.get("DNI") == null ? 0 : ((Number)row.get("DNI")).intValue();
    Integer telefono = row.get("Telefono") == null ? 0 : ((Number)row.get("Telefono")).intValue();

    return new Cliente(id, mail, nombre, apellido, dni, telefono);
  }

  public List<Cliente> getAll() throws DatabaseException {
    List<Map<String,Object>> rows = db.entityGetAll("Clientes");

    List<Cliente> clientes = new ArrayList<>();
    if(rows == null) return clientes;

    for(Map<String,Object> r : rows) clientes.add(mapRowToCliente(r));

    return clientes;
  }

  public List<Cliente> getById(int id) throws DatabaseException {
    Map<String,Object> where = new HashMap<>();
    where.put("Id", id);

    List<Map<String,Object>> rows = db.entityGetBy("Clientes", where);

    List<Cliente> clientes = new ArrayList<>();
    if(rows == null) return clientes;
    
    for(Map<String,Object> r : rows) clientes.add(mapRowToCliente(r));

    return clientes;
  }

  public Cliente create(String mail, String nombre, String apellido, int dni, Integer telefono) throws DatabaseException {
    Map<String,Object> data = new HashMap<>();
    data.put("Mail", mail);
    data.put("Nombre", nombre);
    data.put("Apellido", apellido);
    data.put("DNI", dni);
    data.put("Telefono", telefono);

    Map<String,Object> result = db.entityCreate("Clientes", data);

    return mapRowToCliente(result);
  }

  public boolean update(int id, Map<String,Object> values) throws DatabaseException {
    return db.entityUpdate("Clientes", id, values);
  }

  public boolean delete(int id) throws DatabaseException {
    return db.entityDelete("Clientes", id);
  }
}
