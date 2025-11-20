package Clases.Interfaces;

import Clases.Entidades.Empleado;

public interface INavigationService {
    void mostrarLogin();
    
    void mostrarPrimeraConfiguracion();
    
    void mostrarMenuPrincipal(Empleado empleadoLogueado);
    
    void cerrarSesion();
}
