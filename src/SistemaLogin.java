import java.util.Scanner;

public class SistemaLogin {

    private Scanner scanner =  new Scanner(System.in);

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
                case 2:
                    loginCliente();
            }
        }
        public void loginEmpleado(){
            System.out.println("Ingrese su nombre: ");
            String nombre = scanner.next();
        }
        public void loginCliente(){
            System.out.println("Ingrese su apellido: ");
            String apellido = scanner.next();
        }

}
