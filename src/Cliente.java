public class Cliente extends Usuario {
    private int dni;
    private String password;

    public Cliente(int id, String nombre, String apellido, int dni, String password) {
        super(id, nombre, apellido);
        this.dni = dni;
        this.password = password;
    }

    public int getDni() {
        return dni;
    }

    @Override
    public void mostrarInfo() {
        System.out.println("Cliente: " + getNombre() + " " + getApellido() + " | DNI: " + dni);
    }
}
