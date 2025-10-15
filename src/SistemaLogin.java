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
                    return;
                case 2:
                    loginCliente();
                    return;
            }
        }
        public void loginEmpleado(){
            System.out.println("Ingrese su legajo: ");
            String legajo = scanner.next();
            System.out.println("Ingrese su password: ");
            String password = scanner.next();
            //todo: conectarme con el gestor de archivos para poder verificar si el login fue ejecutado correctamente

        }

        public void loginCliente(){
            System.out.println("Ingrese su Identificacion: ");
            String id = scanner.next();
            System.out.println("Ingrese la cantidad de personas que se hospedaran: ");
            Number cantidad = scanner.nextInt();
            //todo: metodo para recuperar habitaciones y mostrarle al cliente cuales se encuentran vacias
        }

}
