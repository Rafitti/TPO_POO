public class Empleado extends Usuario {
    private String password;
    private Rol rol;

    public Empleado(String nombre, String password, Rol rol) {
        super(nombre);
        this.password = password;
        this.rol = rol;
    }

    public String getPassword() {
        return password;
    }

    public Rol getRol() {
        return rol;
    }

    @Override
    public void mostrarInfo() {
        System.out.println("Empleado: " + nombre + " | Rol: " + rol);
    }
}
