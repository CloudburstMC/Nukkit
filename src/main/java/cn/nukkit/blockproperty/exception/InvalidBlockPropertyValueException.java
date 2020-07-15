package cn.nukkit.blockproperty.exception;

import cn.nukkit.blockproperty.BlockProperty;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;

@ParametersAreNullableByDefault
@Getter
public class InvalidBlockPropertyValueException extends InvalidBlockPropertyException {
    private final transient Object currentValue;
    private final transient Object invalidValue;

    public InvalidBlockPropertyValueException(@Nonnull BlockProperty<?> property, Object currentValue, Object invalidValue) {
        super(property, buildMessage(currentValue, invalidValue));
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }

    public InvalidBlockPropertyValueException(@Nonnull BlockProperty<?> property, Object currentValue, Object invalidValue, String message) {
        super(property, buildMessage(currentValue, invalidValue)+". "+message);
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }

    public InvalidBlockPropertyValueException(@Nonnull BlockProperty<?> property, Object currentValue, Object invalidValue, String message, Throwable cause) {
        super(property, buildMessage(currentValue, invalidValue)+". "+message, cause);
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }

    public InvalidBlockPropertyValueException(@Nonnull BlockProperty<?> property, Object currentValue, Object invalidValue, Throwable cause) {
        super(property, buildMessage(currentValue, invalidValue), cause);
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }
    
    private static String buildMessage(Object currentValue, Object invalidValue) {
        return "Current Value: "+currentValue+", Invalid Value: "+invalidValue;
    }
}
