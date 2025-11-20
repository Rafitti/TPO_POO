package Clases.Services;

import Clases.Entidades.Reserva;
import Clases.Interfaces.*;
import Clases.Exceptions.*;
import java.time.LocalDateTime;
import java.util.*;

public class ReservaService implements IReservaService {
    private final IReservaRepository reservaRepo;
    
    public ReservaService(IReservaRepository reservaRepo) {
        this.reservaRepo = reservaRepo;
    }
    
    @Override
    public List<Reserva> obtenerTodas() throws DatabaseException {
        return reservaRepo.getAll();
    }
    
    @Override
    public List<Reserva> obtenerPorCliente(int clienteId) throws DatabaseException {
        return reservaRepo.getByClienteId(clienteId);
    }
    
    @Override
    public Reserva obtenerPorId(int id) throws DatabaseException {
        Map<String, Object> where = new HashMap<>();
        where.put("Id", id);

        List<Reserva> reservas = reservaRepo.getBy(where);

        return reservas.isEmpty() ? null : reservas.get(0);
    }
    
    @Override
    public Reserva crear(int idHabitacion, int idCliente, LocalDateTime fechaInicio, LocalDateTime fechaFin) throws DatabaseException, ReservaException, ValidationException {
        validarFechas(fechaInicio, fechaFin);
        
        if (!fechaInicio.isBefore(fechaFin)) {
            throw new ReservaException("La fecha de inicio debe ser anterior a la fecha fin", 
                idHabitacion, fechaInicio, fechaFin);
        }
        
        if (!reservaRepo.isRoomAvailable(idHabitacion, fechaInicio, fechaFin)) {
            throw new ReservaException("La habitación no está disponible en ese intervalo", 
                idHabitacion, fechaInicio, fechaFin);
        }
        
        return reservaRepo.create(idHabitacion, idCliente, fechaInicio, fechaFin);
    }
    
    @Override
    public void actualizar(int id, int idHabitacion, int idCliente, LocalDateTime fechaInicio, LocalDateTime fechaFin) throws DatabaseException, ReservaException, ValidationException {
        validarFechas(fechaInicio, fechaFin);
        
        if (!fechaInicio.isBefore(fechaFin)) {
            throw new ReservaException("La fecha de inicio debe ser anterior a la fecha fin", 
                idHabitacion, fechaInicio, fechaFin);
        }
        
        if (!reservaRepo.isRoomAvailable(idHabitacion, fechaInicio, fechaFin, id)) {
            throw new ReservaException("La habitación no está disponible en ese intervalo", 
                idHabitacion, fechaInicio, fechaFin);
        }
        
        Map<String, Object> values = new HashMap<>();
        values.put("IdHabitacion", idHabitacion);
        values.put("IdCliente", idCliente);
        values.put("FechaInicio", fechaInicio);
        values.put("FechaFin", fechaFin);
        
        boolean resultado = reservaRepo.update(id, values);
        if (!resultado) {
            throw new DatabaseException("No se pudo actualizar la reserva", "UPDATE", "Reservas");
        }
    }
    
    @Override
    public void eliminar(int id) throws DatabaseException, ValidationException {
        boolean resultado = reservaRepo.delete(id);
        if (!resultado) {
            throw new DatabaseException("No se pudo eliminar la reserva", "DELETE", "Reservas");
        }
    }
    
    @Override
    public boolean verificarDisponibilidad(int idHabitacion, LocalDateTime fechaInicio, LocalDateTime fechaFin) throws DatabaseException {
        return reservaRepo.isRoomAvailable(idHabitacion, fechaInicio, fechaFin);
    }
    
    @Override
    public boolean verificarDisponibilidad(int idHabitacion, LocalDateTime fechaInicio, LocalDateTime fechaFin, int reservaIdExcluir) throws DatabaseException {
        return reservaRepo.isRoomAvailable(idHabitacion, fechaInicio, fechaFin, reservaIdExcluir);
    }
    
    private void validarFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws ValidationException {
        if (fechaInicio == null) {
            throw new ValidationException("La fecha de inicio es obligatoria");
        }
        if (fechaFin == null) {
            throw new ValidationException("La fecha fin es obligatoria");
        }
    }
}
