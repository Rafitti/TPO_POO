package Clases.Interfaces;

import Clases.Entidades.*;
import Clases.Exceptions.DatabaseException;
import java.util.List;
import java.util.Map;

public interface IHabitacionRepository {
    List<Habitacion> getAll() throws DatabaseException;
    
    List<Habitacion> getBy(Map<String,Object> where) throws DatabaseException;
    
    Habitacion create(int numero, TipoHabitacion tipo, int idEmpleadoACargo) throws DatabaseException;
    
    boolean update(int id, Map<String,Object> values) throws DatabaseException;
    
    boolean delete(int id) throws DatabaseException;
}
