package Clases.Entidades;

import java.time.*;

public class Reserva {
  
  private int id;
    private int idCliente;
    private int idHabitacion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    public Reserva(int id, int idCliente, int idHabitacion, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
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

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

}
