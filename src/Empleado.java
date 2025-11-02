public class Empleado extends Usuario {
    private Rol rol;

    public Empleado(int id,String mail, String nombre, String apellido, String password, Rol rol) {
        super(id, mail, nombre, apellido,password);
        this.rol = rol;
    }

    public Rol getRol() {
        return rol;
    }

    @Override
    public void mostrarInfo() {
        System.out.println("Empleado: " + getNombre() + " " + getApellido() + " | Rol: " + rol);
    }
}
