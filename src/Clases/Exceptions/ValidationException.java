package Clases.Exceptions;

public class ValidationException extends Exception {
    
    private String field;
    private Object invalidValue;
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, String field) {
        super(message);
        this.field = field;
    }
    
    public ValidationException(String message, String field, Object invalidValue) {
        super(message);
        this.field = field;
        this.invalidValue = invalidValue;
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public String getField() {
        return field;
    }
    
    public Object getInvalidValue() {
        return invalidValue;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ValidationException: ");
        sb.append(getMessage());
        if (field != null) {
            sb.append(" [Campo: ").append(field).append("]");
        }
        if (invalidValue != null) {
            sb.append(" [Valor inválido: ").append(invalidValue).append("]");
        }
        return sb.toString();
    }
}
