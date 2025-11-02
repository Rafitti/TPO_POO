import java.time.*;

public class SistemaReservas {
  
  private SistemaDB sistemaDB;
  
  public SistemaReservas(SistemaDB sistemaDB) {
      this.sistemaDB = sistemaDB;
  }

  public void createReserva(int id, int idCliente, int idHabitacion, LocalDateTime fechaInicio, LocalDateTime fechaFin) {

      sistemaDB.createReserva(id, idCliente, idHabitacion, fechaInicio, fechaFin);
  }
}
