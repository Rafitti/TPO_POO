package Clases.Exceptions;

import java.time.LocalDateTime;

public class ReservaException extends Exception {
    
    private Integer habitacionId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    
    public ReservaException(String message) {
        super(message);
    }
    
    public ReservaException(String message, Integer habitacionId) {
        super(message);
        this.habitacionId = habitacionId;
    }
    
    public ReservaException(String message, Integer habitacionId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        super(message);
        this.habitacionId = habitacionId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
    
    public ReservaException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public Integer getHabitacionId() {
        return habitacionId;
    }
    
    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }
    
    public LocalDateTime getFechaFin() {
        return fechaFin;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ReservaException: ");
        sb.append(getMessage());
        if (habitacionId != null) {
            sb.append(" [Habitación ID: ").append(habitacionId).append("]");
        }
        if (fechaInicio != null && fechaFin != null) {
            sb.append(" [Período: ").append(fechaInicio).append(" - ").append(fechaFin).append("]");
        }
        return sb.toString();
    }
}
