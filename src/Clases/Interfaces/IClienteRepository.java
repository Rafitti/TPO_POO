package Clases.Interfaces;

import Clases.Entidades.Cliente;
import Clases.Exceptions.DatabaseException;
import java.util.List;
import java.util.Map;

public interface IClienteRepository {
    List<Cliente> getAll() throws DatabaseException;

    List<Cliente> getById(int id) throws DatabaseException;

    Cliente create(String mail, String nombre, String apellido, int dni, Integer telefono) throws DatabaseException;

    boolean update(int id, Map<String,Object> values) throws DatabaseException;

    boolean delete(int id) throws DatabaseException;
}
