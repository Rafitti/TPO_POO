package Clases.Services;

import Clases.Entidades.*;
import Clases.Interfaces.*;
import Clases.Exceptions.*;
import java.util.*;

public class EmpleadoService implements IEmpleadoService {
    private final IEmpleadoRepository empleadoRepo;
    
    public EmpleadoService(IEmpleadoRepository empleadoRepo) {
        this.empleadoRepo = empleadoRepo;
    }
    
    @Override
    public List<Empleado> obtenerTodos() throws DatabaseException {
        return empleadoRepo.getAll();
    }
    
    @Override
    public Empleado obtenerPorId(int id) throws DatabaseException {
        Map<String, Object> where = new HashMap<>();
        where.put("Id", id);

        List<Empleado> empleados = empleadoRepo.getBy(where);

        return empleados.isEmpty() ? null : empleados.get(0);
    }
    
    @Override
    public Empleado autenticar(String mail, String password) throws DatabaseException, ValidationException {
        if (mail == null || mail.trim().isEmpty()) {
            throw new ValidationException("El email es obligatorio");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("La contraseña es obligatoria");
        }
        
        Empleado empleado = empleadoRepo.getByMailAndPassword(mail, password);
        if (empleado == null) {
            throw new ValidationException("Credenciales inválidas");
        }
        
        return empleado;
    }
    
    @Override
    public Empleado crear(String mail, String nombre, String apellido, String password, Rol rol) throws DatabaseException, ValidationException {
        validarDatosEmpleado(mail, nombre, apellido, password, rol);

        return empleadoRepo.create(mail, nombre, apellido, password, rol);
    }
    
    @Override
    public void actualizar(int id, String mail, String nombre, String apellido, String password, Rol rol) throws DatabaseException, ValidationException {
        validarDatosEmpleado(mail, nombre, apellido, password, rol);
        
        Map<String, Object> values = new HashMap<>();
        values.put("Mail", mail);
        values.put("Nombre", nombre);
        values.put("Apellido", apellido);
        values.put("Password", password);
        values.put("Rol", rol.ordinal());
        
        boolean resultado = empleadoRepo.update(id, values);
        if (!resultado) {
            throw new DatabaseException("No se pudo actualizar el empleado", "UPDATE", "Empleados");
        }
    }
    
    @Override
    public void eliminar(int id) throws DatabaseException, ValidationException {
        boolean resultado = empleadoRepo.delete(id);
        if (!resultado) {
            throw new DatabaseException("No se pudo eliminar el empleado", "DELETE", "Empleados");
        }
    }
    
    private void validarDatosEmpleado(String mail, String nombre, String apellido, String password, Rol rol) throws ValidationException {
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
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("La contraseña es obligatoria");
        }
        if (password.length() < 4) {
            throw new ValidationException("La contraseña debe tener al menos 4 caracteres");
        }
        if (rol == null) {
            throw new ValidationException("El rol es obligatorio");
        }
    }
}
