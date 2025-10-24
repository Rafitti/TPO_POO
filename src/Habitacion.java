public class Habitacion {
  
  private int numero;
  private TipoHabitacion tipo;
  private int IdEmpleadoACargo;

  public Habitacion(int numero, TipoHabitacion tipo, int IdEmpleadoACargo) {
    this.numero = numero;
    this.tipo = tipo;
    this.IdEmpleadoACargo = IdEmpleadoACargo;
  }

  public int getNumero() {
    return numero;
  }

  public TipoHabitacion getTipo() {
    return tipo;
  }

  public int getIdEmpleadoACargo() {
    return IdEmpleadoACargo;
  }
}
