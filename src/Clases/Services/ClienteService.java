package Clases.Services;

import Clases.Entidades.Cliente;
import Clases.Interfaces.*;
import Clases.Exceptions.*;
import java.util.*;

public class ClienteService implements IClienteService {
    private final IClienteRepository clienteRepo;
    private final IReservaRepository reservaRepo;
    
    public ClienteService(IClienteRepository clienteRepo, IReservaRepository reservaRepo) {
        this.clienteRepo = clienteRepo;
        this.reservaRepo = reservaRepo;
    }
    
    @Override
    public List<Cliente> obtenerTodos() throws DatabaseException {
        return clienteRepo.getAll();
    }
    
    @Override
    public Cliente obtenerPorId(int id) throws DatabaseException {
        List<Cliente> clientes = clienteRepo.getById(id);

        return clientes.isEmpty() ? null : clientes.get(0);
    }
    
    @Override
    public Cliente crear(String mail, String nombre, String apellido, int dni, Integer telefono) throws DatabaseException, ValidationException {
        validarDatosCliente(mail, nombre, apellido, dni);

        return clienteRepo.create(mail, nombre, apellido, dni, telefono);
    }
    
    @Override
    public void actualizar(int id, String mail, String nombre, String apellido, int dni, Integer telefono) throws DatabaseException, ValidationException {
        validarDatosCliente(mail, nombre, apellido, dni);
        
        Map<String, Object> values = new HashMap<>();
        values.put("Mail", mail);
        values.put("Nombre", nombre);
        values.put("Apellido", apellido);
        values.put("DNI", dni);
        values.put("Telefono", telefono);
        
        boolean resultado = clienteRepo.update(id, values);
        if (!resultado) {
            throw new DatabaseException("No se pudo actualizar el cliente", "UPDATE", "Clientes");
        }
    }
    
    @Override
    public void eliminar(int id) throws DatabaseException, ValidationException {
        if (reservaRepo.clienteTieneReservas(id)) {
            throw new ValidationException("No se puede eliminar el cliente porque tiene reservas asociadas");
        }
        
        boolean resultado = clienteRepo.delete(id);
        if (!resultado) {
            throw new DatabaseException("No se pudo eliminar el cliente", "DELETE", "Clientes");
        }
    }
    
    @Override
    public boolean puedeEliminar(int id) throws DatabaseException {
        return !reservaRepo.clienteTieneReservas(id);
    }
    
    private void validarDatosCliente(String mail, String nombre, String apellido, int dni) throws ValidationException {
        if (mail == null || mail.trim().isEmpty()) {
            throw new ValidationException("El email es obligatorio");
        }
        if (!mail.contains("@")) {
            throw new ValidationException("El email debe tener formato válido");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("El nombre es obligatorio");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new ValidationException("El apellido es obligatorio");
        }
        if (dni <= 0) {
            throw new ValidationException("El DNI debe ser mayor a 0");
        }
    }
}
