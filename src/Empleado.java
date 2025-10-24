public class Empleado extends Usuario {
    private String password;
    private Rol rol;

    public Empleado(int id, String nombre, String apellido, String password, Rol rol) {
        super(id, nombre, apellido);
        this.password = password;
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
