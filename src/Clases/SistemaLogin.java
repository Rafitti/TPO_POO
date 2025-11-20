package Clases;

import UI.FirstInitUI;
import UI.LoginUI;

public class SistemaLogin {
    private SistemaReservas sr;
    private SistemaHabitaciones sh;
    private SistemaClientes sc;
    private SistemaEmpleados se;
    private Empleado empleadoLogueado;

    public SistemaLogin(SistemaEmpleados se, SistemaReservas sr, SistemaHabitaciones sh, SistemaClientes sc) {
        this.se = se;
        this.sr = sr;
        this.sh = sh;
        this.sc = sc;
    }

    public void firstInit(){
        new FirstInitUI(se, this).init();
    }

    public void init(){
        new LoginUI(this, sr, sh, sc, se).init();
    }

    public void setSessionUser(Empleado empleado){
        this.empleadoLogueado = empleado;
    }

    public Rol getSessionRole(){
        if(empleadoLogueado != null){
            return empleadoLogueado.getRol();
        }
        return null;
    }
}
