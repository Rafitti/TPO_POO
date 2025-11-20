package Clases.Exceptions;

public class DatabaseException extends Exception {
    
    private String operation;
    private String table;
    
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DatabaseException(String message, String operation, String table) {
        super(message);
        this.operation = operation;
        this.table = table;
    }
    
    public DatabaseException(String message, String operation, String table, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.table = table;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getTable() {
        return table;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DatabaseException: ");
        sb.append(getMessage());
        if (operation != null) {
            sb.append(" [Operación: ").append(operation).append("]");
        }
        if (table != null) {
            sb.append(" [Tabla: ").append(table).append("]");
        }
        return sb.toString();
    }
}
