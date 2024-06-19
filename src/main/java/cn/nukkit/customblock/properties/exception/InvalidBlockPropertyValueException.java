package cn.nukkit.customblock.properties.exception;

import cn.nukkit.customblock.properties.BlockProperty;

import java.io.Serializable;

public class InvalidBlockPropertyValueException extends InvalidBlockPropertyException {
    private static final long serialVersionUID = -1087431932428639175L;
    
    private final Serializable currentValue;
    private final Serializable invalidValue;
    
    public InvalidBlockPropertyValueException(BlockProperty<?> property, Serializable currentValue, Serializable invalidValue) {
        super(property, buildMessage(currentValue, invalidValue));
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }
    
    public InvalidBlockPropertyValueException(BlockProperty<?> property, Serializable currentValue, Serializable invalidValue, String message) {
        super(property, buildMessage(currentValue, invalidValue)+". "+message);
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }
    
    public InvalidBlockPropertyValueException(BlockProperty<?> property, Serializable currentValue, Serializable invalidValue, String message, Throwable cause) {
        super(property, buildMessage(currentValue, invalidValue)+". "+message, cause);
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }
    
    public InvalidBlockPropertyValueException(BlockProperty<?> property, Serializable currentValue, Serializable invalidValue, Throwable cause) {
        super(property, buildMessage(currentValue, invalidValue), cause);
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }

    private static String buildMessage(Object currentValue, Object invalidValue) {
        return "Current Value: "+currentValue+", Invalid Value: "+invalidValue;
    }
    
    
    public Serializable getCurrentValue() {
        return this.currentValue;
    }
    
    public Serializable getInvalidValue() {
        return this.invalidValue;
    }
}
