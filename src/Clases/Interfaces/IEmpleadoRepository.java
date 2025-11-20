package Clases.Interfaces;

import Clases.Entidades.*;
import Clases.Exceptions.DatabaseException;
import java.util.List;
import java.util.Map;

public interface IEmpleadoRepository {
    List<Empleado> getAll() throws DatabaseException;

    List<Empleado> getBy(Map<String,Object> where) throws DatabaseException;

    Empleado getByMailAndPassword(String mail, String password) throws DatabaseException;

    Empleado create(String mail, String nombre, String apellido, String password, Rol rol) throws DatabaseException;

    boolean update(int id, Map<String,Object> values) throws DatabaseException;

    boolean delete(int id) throws DatabaseException;
}
