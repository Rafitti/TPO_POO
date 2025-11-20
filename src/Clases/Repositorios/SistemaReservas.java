package Clases.Repositorios;

import Clases.SistemaDB;
import Clases.Entidades.Reserva;
import Clases.Exceptions.DatabaseException;
import Clases.Interfaces.IReservaRepository;

import java.util.*;
import java.time.*;
import java.time.format.*;

public class SistemaReservas implements IReservaRepository {
  private final SistemaDB db;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public SistemaReservas(SistemaDB db){ this.db = db; }

  private Reserva mapRowToReserva(Map<String,Object> row){
    if(row == null) return null;

    int id = ((Number)row.get("Id")).intValue();
    int idHabitacion = ((Number)row.get("IdHabitacion")).intValue();
    int idCliente = ((Number)row.get("IdCliente")).intValue();
    LocalDateTime fechaInicio = null;
    LocalDateTime fechaFin = null;
    Object fi = row.get("FechaInicio");
    Object ff = row.get("FechaFin");

    if(fi instanceof String) fechaInicio = LocalDateTime.parse((String)fi, formatter);
    if(ff instanceof String) fechaFin = LocalDateTime.parse((String)ff, formatter);

    return new Reserva(id, idCliente, idHabitacion, fechaInicio, fechaFin);
  }

  public List<Reserva> getAll() throws DatabaseException {
    List<Map<String,Object>> rows = db.entityGetAll("Reservas");

    List<Reserva> reservas = new ArrayList<>();

    if(rows == null) return reservas;

    for(Map<String,Object> r : rows) reservas.add(mapRowToReserva(r));

    return reservas;
  }

  public List<Reserva> getBy(Map<String,Object> where) throws DatabaseException {
    List<Map<String,Object>> rows = db.entityGetBy("Reservas", where);
    List<Reserva> reservas = new ArrayList<>();

    if(rows == null) return reservas;

    for(Map<String,Object> r : rows) reservas.add(mapRowToReserva(r));

    return reservas;
  }

  public List<Reserva> getByClienteId(int clienteId) throws DatabaseException {
    Map<String,Object> where = new HashMap<>();
    where.put("IdCliente", clienteId);

    List<Map<String,Object>> result = db.entityGetBy("Reservas", where);

    List<Reserva> reservas = new ArrayList<>();

    if(result != null) {
        for(Map<String,Object> r : result) reservas.add(mapRowToReserva(r));
    }

    return reservas;
  }

  public Reserva create(int idHabitacion, int idCliente, LocalDateTime fechaInicio, LocalDateTime fechaFin) throws DatabaseException {
    Map<String,Object> data = new HashMap<>();
    data.put("IdHabitacion", idHabitacion);
    data.put("IdCliente", idCliente);
    data.put("FechaInicio", fechaInicio.format(formatter));
    data.put("FechaFin", fechaFin.format(formatter));

    Map<String,Object> reserva = db.entityCreate("Reservas", data);

    return mapRowToReserva(reserva);
  }

  public boolean update(int id, Map<String,Object> values) throws DatabaseException {
    if(values.containsKey("FechaInicio") && values.get("FechaInicio") instanceof java.time.LocalDateTime){
      values.put("FechaInicio", ((java.time.LocalDateTime)values.get("FechaInicio")).format(formatter));
    }
    if(values.containsKey("FechaFin") && values.get("FechaFin") instanceof java.time.LocalDateTime){
      values.put("FechaFin", ((java.time.LocalDateTime)values.get("FechaFin")).format(formatter));
    }
    return db.entityUpdate("Reservas", id, values);
  }

  // Verifica si una habitación está disponible en el intervalo [start, end).
  // Si excludeId no es null se ignora la reserva con ese Id (útil al editar).
  public boolean isRoomAvailable(int habitacionId, LocalDateTime start, LocalDateTime end, Integer excludeId) throws DatabaseException {
    if(start == null || end == null) return false;

    List<Reserva> all = getAll();

    for(Reserva r : all){
      if(excludeId != null && r.getId() == excludeId) continue;
      if(r.getIdHabitacion() != habitacionId) continue;
      LocalDateTime rStart = r.getFechaInicio();
      LocalDateTime rEnd = r.getFechaFin();
      if(start.isBefore(rEnd) && rStart.isBefore(end)) return false;
    }
    return true;
  }

  public boolean isRoomAvailable(int habitacionId, LocalDateTime start, LocalDateTime end) throws DatabaseException {
    return isRoomAvailable(habitacionId, start, end, null);
  }

  // Devuelve true si el cliente tiene al menos una reserva registrada
  public boolean clienteTieneReservas(int clienteId) throws DatabaseException {
    List<Reserva> list = getByClienteId(clienteId);
    
    return list != null && !list.isEmpty();
  }

  public boolean delete(int id) throws DatabaseException {
    return db.entityDelete("Reservas", id);
  }
}
