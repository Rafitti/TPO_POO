package Clases.Interfaces;

import Clases.Entidades.Reserva;
import Clases.Exceptions.DatabaseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IReservaRepository {
    List<Reserva> getAll() throws DatabaseException;
    
    List<Reserva> getBy(Map<String,Object> where) throws DatabaseException;
    
    List<Reserva> getByClienteId(int clienteId) throws DatabaseException;
    
    Reserva create(int idHabitacion, int idCliente, LocalDateTime fechaInicio, LocalDateTime fechaFin) throws DatabaseException;
    
    boolean update(int id, Map<String,Object> values) throws DatabaseException;
    
    boolean delete(int id) throws DatabaseException;
    
    boolean isRoomAvailable(int habitacionId, LocalDateTime start, LocalDateTime end, Integer excludeId) throws DatabaseException;
    
    boolean isRoomAvailable(int habitacionId, LocalDateTime start, LocalDateTime end) throws DatabaseException;
    
    boolean clienteTieneReservas(int clienteId) throws DatabaseException;
}
