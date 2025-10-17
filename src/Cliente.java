public class Cliente extends Usuario {
    private String dni;

    public Cliente(String nombre, String dni) {
        super(nombre);
        this.dni = dni;
    }

    public String getDni() {
        return dni;
    }

    @Override
    public void mostrarInfo() {
        System.out.println("Cliente: " + nombre + " | DNI: " + dni);
    }
}public class Cliente extends Usuario {
    private String dni;

    public Cliente(String nombre, String dni) {
        super(nombre);
        this.dni = dni;
    }

    public String getDni() {
        return dni;
    }

    @Override
    public void mostrarInfo() {
        System.out.println("Cliente: " + nombre + " | DNI: " + dni);
    }
}