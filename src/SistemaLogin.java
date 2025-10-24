import java.util.Scanner;

public class SistemaLogin {

    private SistemaDB sistemaDB;
    private Scanner scanner =  new Scanner(System.in);

    public SistemaLogin(SistemaDB sistemaDB) {
        this.sistemaDB = sistemaDB;
    }

    public void inicio(){
        System.out.println("Seleccione una opcion: ");
        System.out.println("1. Empleado");
        System.out.println("2. Cliente");
        int opcion = scanner.nextInt();
        while (opcion != 1 && opcion != 2) {
            System.out.println("El programa no reconoce el parametro reingrese");
            opcion = scanner.nextInt();
        }
        switch (opcion) {
            case 1:
                loginEmpleado();
                return;
            case 2:
                loginCliente();
                return;
        }
    }

    public void loginEmpleado(){
        System.out.println("Ingrese su Nombre: ");
        String nombre = scanner.next();
        System.out.println("Ingrese su Contraseña: ");
        String password = scanner.next();

        Empleado empleado = sistemaDB.getEmpleadoByNombreAndPassword(nombre, password);

        if(empleado != null){
            System.out.println("Login exitoso. Bienvenido, " + empleado.getNombre() + "!");
        } else {
            System.out.println("Login fallido. Nombre o contraseña incorrectos.");
        }
    }

    public void loginCliente(){
        System.out.println("Ingrese su Nombre: ");
        String nombre = scanner.next();
        System.out.println("Ingrese su Contraseña: ");
        String password = scanner.next();

        Cliente cliente = sistemaDB.getClienteByNombreAndPassword(nombre, password);
        
        if(cliente != null){
            System.out.println("Login exitoso. Bienvenido, " + cliente.getNombre() + "!");
        } else {
            System.out.println("Login fallido. Nombre o contraseña incorrectos.");
        }
        //todo: metodo para conectarme con el gestor de archivos
    }

}
