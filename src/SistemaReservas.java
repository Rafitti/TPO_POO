import java.util.*;
import java.time.*;
import java.time.format.*;

public class SistemaReservas {
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

  public List<Reserva> getAll(){
  List<Map<String,Object>> rows = db.entityGetAll("Reservas");

    List<Reserva> reservas = new ArrayList<>();
    if(rows == null) return reservas;
    for(Map<String,Object> r : rows) reservas.add(mapRowToReserva(r));

    return reservas;
  }

  public List<Reserva> getBy(Map<String,Object> where){
    List<Map<String,Object>> rows = db.entityGetBy("Reservas", where);
    List<Reserva> reservas = new ArrayList<>();
    if(rows == null) return reservas;
    for(Map<String,Object> r : rows) reservas.add(mapRowToReserva(r));

    return reservas;
  }

  public List<Reserva> getByClienteId(int clienteId){
    Map<String,Object> where = new HashMap<>();
    where.put("IdCliente", clienteId);

    List<Map<String,Object>> result = db.entityGetBy("Reservas", where);

    List<Reserva> reservas = new ArrayList<>();
    if(result != null) {
        for(Map<String,Object> r : result) reservas.add(mapRowToReserva(r));
    }

    return reservas;
  }

  public Reserva create(int idHabitacion, int idCliente, LocalDateTime fechaInicio, LocalDateTime fechaFin){
    Map<String,Object> data = new HashMap<>();
    data.put("IdHabitacion", idHabitacion);
    data.put("IdCliente", idCliente);
    data.put("FechaInicio", fechaInicio.format(formatter));
    data.put("FechaFin", fechaFin.format(formatter));

    Map<String,Object> reserva = db.entityCreate("Reservas", data);

    return mapRowToReserva(reserva);
  }

  public boolean delete(int id){
    return db.entityDelete("Reservas", id);
  }
}
