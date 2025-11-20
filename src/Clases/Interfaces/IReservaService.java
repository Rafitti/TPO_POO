package Clases.Interfaces;

import Clases.Entidades.Reserva;
import Clases.Exceptions.*;
import java.time.LocalDateTime;
import java.util.List;

public interface IReservaService {
    
    List<Reserva> obtenerTodas() throws DatabaseException;
    
    List<Reserva> obtenerPorCliente(int clienteId) throws DatabaseException;
    
    Reserva obtenerPorId(int id) throws DatabaseException;
    
    Reserva crear(int idHabitacion, int idCliente, LocalDateTime fechaInicio, LocalDateTime fechaFin) throws DatabaseException, ReservaException, ValidationException;
    
    void actualizar(int id, int idHabitacion, int idCliente, LocalDateTime fechaInicio, LocalDateTime fechaFin) throws DatabaseException, ReservaException, ValidationException;
    
    void eliminar(int id) throws DatabaseException, ValidationException;
    
    boolean verificarDisponibilidad(int idHabitacion, LocalDateTime fechaInicio, LocalDateTime fechaFin) throws DatabaseException;
    
    boolean verificarDisponibilidad(int idHabitacion, LocalDateTime fechaInicio, LocalDateTime fechaFin, int reservaIdExcluir) throws DatabaseException;
}
