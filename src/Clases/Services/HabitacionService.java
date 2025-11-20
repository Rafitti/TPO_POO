package Clases.Services;

import Clases.Entidades.*;
import Clases.Interfaces.*;
import Clases.Exceptions.*;
import java.util.*;

public class HabitacionService implements IHabitacionService {
    private final IHabitacionRepository habitacionRepo;
    
    public HabitacionService(IHabitacionRepository habitacionRepo) {
        this.habitacionRepo = habitacionRepo;
    }
    
    @Override
    public List<Habitacion> obtenerTodas() throws DatabaseException {
        return habitacionRepo.getAll();
    }
    
    @Override
    public Habitacion obtenerPorId(int id) throws DatabaseException {
        Map<String, Object> where = new HashMap<>();
        where.put("Id", id);

        List<Habitacion> habitaciones = habitacionRepo.getBy(where);

        return habitaciones.isEmpty() ? null : habitaciones.get(0);
    }
    
    @Override
    public Habitacion crear(int numero, TipoHabitacion tipo, Integer idEmpleadoACargo) throws DatabaseException, ValidationException {
        validarDatosHabitacion(numero, tipo);

        return habitacionRepo.create(numero, tipo, idEmpleadoACargo == null ? 0 : idEmpleadoACargo);
    }
    
    @Override
    public void actualizar(int id, int numero, TipoHabitacion tipo, Integer idEmpleadoACargo, boolean faltaLimpiar) throws DatabaseException, ValidationException {
        validarDatosHabitacion(numero, tipo);
        
        Map<String, Object> values = new HashMap<>();
        values.put("Numero", numero);
        values.put("Tipo", tipo.ordinal());
        values.put("IdEmpleadoACargo", idEmpleadoACargo);
        values.put("FaltaLimpiar", faltaLimpiar ? 1 : 0);
        
        boolean resultado = habitacionRepo.update(id, values);
        if (!resultado) {
            throw new DatabaseException("No se pudo actualizar la habitación", "UPDATE", "Habitaciones");
        }
    }
    
    @Override
    public void actualizarEstadoLimpieza(int id, boolean faltaLimpiar) throws DatabaseException {
        Map<String, Object> values = new HashMap<>();
        values.put("FaltaLimpiar", faltaLimpiar ? 1 : 0);
        
        boolean resultado = habitacionRepo.update(id, values);
        if (!resultado) {
            throw new DatabaseException("No se pudo actualizar el estado de limpieza", "UPDATE", "Habitaciones");
        }
    }
    
    @Override
    public void eliminar(int id) throws DatabaseException, ValidationException {
        boolean resultado = habitacionRepo.delete(id);
        if (!resultado) {
            throw new DatabaseException("No se pudo eliminar la habitación", "DELETE", "Habitaciones");
        }
    }
    
    private void validarDatosHabitacion(int numero, TipoHabitacion tipo) throws ValidationException {
        if (numero <= 0) {
            throw new ValidationException("El número de habitación debe ser mayor a 0");
        }
        if (tipo == null) {
            throw new ValidationException("El tipo de habitación es obligatorio");
        }
    }
}
