public class Cliente extends Usuario {
    private int dni;
    private Integer telefono;

    public Cliente(int id,String mail, String nombre, String apellido, int dni,String password, Integer telefono) {
        super(id, mail, nombre, apellido,password);
        this.dni = dni;
        this.telefono = telefono;
    }

    public int getDni() {
        return dni;
    }

    public int getTelefono() {
        return telefono;
    }

    @Override
    public void mostrarInfo() {
        System.out.println("Cliente: " + getNombre() + " " + getApellido() + " | DNI: " + dni);
    }
}
