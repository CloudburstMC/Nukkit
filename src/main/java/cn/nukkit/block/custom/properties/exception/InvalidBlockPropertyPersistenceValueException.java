package cn.nukkit.block.custom.properties.exception;

import cn.nukkit.block.custom.properties.BlockProperty;

public class InvalidBlockPropertyPersistenceValueException extends InvalidBlockPropertyException {
    private static final long serialVersionUID = 1L;
    
    private final String currentValue;    
    private final String invalidValue;
    
    public InvalidBlockPropertyPersistenceValueException(BlockProperty<?> property, String currentValue, String invalidValue) {
        super(property, buildMessage(currentValue, invalidValue));
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }
    
    
    public InvalidBlockPropertyPersistenceValueException(BlockProperty<?> property, String currentValue, String invalidValue, String message) {
        super(property, buildMessage(currentValue, invalidValue)+". "+message);
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }
    
    public InvalidBlockPropertyPersistenceValueException(BlockProperty<?> property, String currentValue, String invalidValue, String message, Throwable cause) {
        super(property, buildMessage(currentValue, invalidValue)+". "+message, cause);
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }
    
    public InvalidBlockPropertyPersistenceValueException(BlockProperty<?> property, String currentValue, String invalidValue, Throwable cause) {
        super(property, buildMessage(currentValue, invalidValue), cause);
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }

    private static String buildMessage(Object currentValue, Object invalidValue) {
        return "Current Value: "+currentValue+", Invalid Value: "+invalidValue;
    }    
    
    public String getCurrentValue() {
        return this.currentValue;
    }
    
    public String getInvalidValue() {
        return this.invalidValue;
    }
}
