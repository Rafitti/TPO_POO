package Clases.Interfaces;

import Clases.Entidades.*;
import Clases.Exceptions.*;
import java.util.List;

public interface IHabitacionService {
    List<Habitacion> obtenerTodas() throws DatabaseException;
    
    Habitacion obtenerPorId(int id) throws DatabaseException;
    
    Habitacion crear(int numero, TipoHabitacion tipo, Integer idEmpleadoACargo) throws DatabaseException, ValidationException;
    
    void actualizar(int id, int numero, TipoHabitacion tipo, Integer idEmpleadoACargo, boolean faltaLimpiar) throws DatabaseException, ValidationException;
    
    void actualizarEstadoLimpieza(int id, boolean faltaLimpiar) throws DatabaseException;
    
    void eliminar(int id) throws DatabaseException, ValidationException;
}
