package Clases;

import java.util.*;

public class SistemaHabitaciones {
  private final SistemaDB db;

  public SistemaHabitaciones(SistemaDB db){ this.db = db; }

  private Habitacion mapRowToHabitacion(Map<String,Object> row){
    if(row == null) return null;
    int id = ((Number)row.get("Id")).intValue();
    int numero = ((Number)row.get("Numero")).intValue();
    TipoHabitacion tipo = TipoHabitacion.values()[((Number)row.get("Tipo")).intValue()];
    Integer idEmpleado = row.get("IdEmpleadoACargo") != null ? ((Number)row.get("IdEmpleadoACargo")).intValue() : null;
    boolean faltaLimpiar = ((Number)row.get("FaltaLimpiar")).intValue() != 0;
    
    return new Habitacion(id, numero, tipo, idEmpleado, faltaLimpiar);
  }

  public List<Habitacion> getAll(){
    List<Map<String,Object>> rows = db.entityGetAll("Habitaciones");

    List<Habitacion> out = new ArrayList<>();
    if(rows == null) return out;
    for(Map<String,Object> r : rows) out.add(mapRowToHabitacion(r));

    return out;
  }

  public List<Habitacion> getBy(Map<String,Object> where){
    List<Map<String,Object>> rows = db.entityGetBy("Habitaciones", where);

    List<Habitacion> out = new ArrayList<>();
    if(rows == null) return out;
    for(Map<String,Object> r : rows) out.add(mapRowToHabitacion(r));

    return out;
  }

  public Habitacion create(int numero, TipoHabitacion tipo, int idEmpleadoACargo){
    Map<String,Object> data = new HashMap<>();
    data.put("Numero", numero);
    data.put("Tipo", tipo.ordinal());
    data.put("IdEmpleadoACargo", idEmpleadoACargo);

    Map<String,Object> result = db.entityCreate("Habitaciones", data);
    
    return mapRowToHabitacion(result);
  }

  public boolean update(int id, Map<String,Object> values){
    return db.entityUpdate("Habitaciones", id, values);
  }

  public boolean delete(int id){
    return db.entityDelete("Habitaciones", id);
  }
}
