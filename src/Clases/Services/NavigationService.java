package Clases.Services;

import Clases.Entidades.Empleado;
import Clases.Interfaces.*;
import UI.*;

public class NavigationService implements INavigationService {
    private final IClienteService clienteService;
    private final IEmpleadoService empleadoService;
    private final IHabitacionService habitacionService;
    private final IReservaService reservaService;
    
    public NavigationService(
        IClienteService clienteService,
        IEmpleadoService empleadoService,
        IHabitacionService habitacionService,
        IReservaService reservaService
    ) {
        this.clienteService = clienteService;
        this.empleadoService = empleadoService;
        this.habitacionService = habitacionService;
        this.reservaService = reservaService;
    }
    
    @Override
    public void mostrarLogin() {
        new LoginUI(empleadoService, this).init();
    }
    
    @Override
    public void mostrarPrimeraConfiguracion() {
        new FirstInitUI(empleadoService, this).init();
    }
    
    @Override
    public void mostrarMenuPrincipal(Empleado empleadoLogueado) {
        new MenuUI(clienteService, empleadoService, habitacionService, reservaService, this, empleadoLogueado).init();
    }
    
    @Override
    public void cerrarSesion() {
        mostrarLogin();
    }
}
