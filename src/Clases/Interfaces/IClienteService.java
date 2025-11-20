package Clases.Interfaces;

import Clases.Entidades.Cliente;
import Clases.Exceptions.*;
import java.util.List;

public interface IClienteService {
    List<Cliente> obtenerTodos() throws DatabaseException;

    Cliente obtenerPorId(int id) throws DatabaseException;

    Cliente crear(String mail, String nombre, String apellido, int dni, Integer telefono) throws DatabaseException, ValidationException;
    
    void actualizar(int id, String mail, String nombre, String apellido, int dni, Integer telefono) throws DatabaseException, ValidationException;
    
    void eliminar(int id) throws DatabaseException, ValidationException;
    
    boolean puedeEliminar(int id) throws DatabaseException;
}
