import java.time.LocalDate;

public class Reserva {
  
  private int id;
    private int idCliente;
    private int idHabitacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public Reserva(int id, int idCliente, int idHabitacion, LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = id;
        this.idCliente = idCliente;
        this.idHabitacion = idHabitacion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public int getId() {
        return id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public int getIdHabitacion() {
        return idHabitacion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

}
