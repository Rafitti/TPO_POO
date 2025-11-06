public class Empleado extends Usuario {
    private Rol rol;
    private String password;

    public Empleado(int id,String mail, String nombre, String apellido, String password, Rol rol) {
        super(id, mail, nombre, apellido);
        this.rol = rol;
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public void mostrarInfo() {
        System.out.println("Empleado: " + getNombre() + " " + getApellido() + " | Rol: " + rol);
    }
}
