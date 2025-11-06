public abstract class Usuario {
    protected int id;
    protected String mail;
    protected String nombre;
    protected String apellido;
    protected String password;


    public Usuario(int id,String mail, String nombre, String apellido) {
        this.id = id;
        this.mail = mail;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public int getId() {
        return id;
    }

    public String getMail() {
        return mail;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public abstract void mostrarInfo();
}
