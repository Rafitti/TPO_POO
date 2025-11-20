package Clases.Interfaces;

import Clases.Entidades.*;
import Clases.Exceptions.*;
import java.util.List;

public interface IEmpleadoService {
    List<Empleado> obtenerTodos() throws DatabaseException;

    Empleado obtenerPorId(int id) throws DatabaseException;

    Empleado autenticar(String mail, String password) throws DatabaseException, ValidationException;

    Empleado crear(String mail, String nombre, String apellido, String password, Rol rol) throws DatabaseException, ValidationException;
    
    void actualizar(int id, String mail, String nombre, String apellido, String password, Rol rol) throws DatabaseException, ValidationException;
    
    void eliminar(int id) throws DatabaseException, ValidationException;
}
