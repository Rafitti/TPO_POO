public class Habitacion {
  private int id;
  private int numero;
  private TipoHabitacion tipo;
  private int IdEmpleadoACargo;
  private boolean faltaLimpiar;

  public Habitacion(int id,int numero, TipoHabitacion tipo, int IdEmpleadoACargo,boolean faltaLimpiar) {
    this.id = id;
    this.numero = numero;
    this.tipo = tipo;
    this.IdEmpleadoACargo = IdEmpleadoACargo;
    this.faltaLimpiar = faltaLimpiar;
  }

  public int getId() {
    return id;
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

  public boolean isFaltaLimpiar() {
    return faltaLimpiar;
  }
}
