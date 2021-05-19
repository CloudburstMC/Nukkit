package cn.nukkit.blockproperty.exception;

import cn.nukkit.blockproperty.BlockProperty;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
import java.io.Serializable;

@ParametersAreNullableByDefault
@Getter
public class InvalidBlockPropertyValueException extends InvalidBlockPropertyException {
    private static final long serialVersionUID = -1087431932428639175L;
    
    @Nullable
    private final Serializable currentValue;
    
    @Nullable
    private final Serializable invalidValue;

    public InvalidBlockPropertyValueException(@Nonnull BlockProperty<?> property, Serializable currentValue, Serializable invalidValue) {
        super(property, buildMessage(currentValue, invalidValue));
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }

    public InvalidBlockPropertyValueException(@Nonnull BlockProperty<?> property, Serializable currentValue, Serializable invalidValue, String message) {
        super(property, buildMessage(currentValue, invalidValue)+". "+message);
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }

    public InvalidBlockPropertyValueException(@Nonnull BlockProperty<?> property, Serializable currentValue, Serializable invalidValue, String message, Throwable cause) {
        super(property, buildMessage(currentValue, invalidValue)+". "+message, cause);
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }

    public InvalidBlockPropertyValueException(@Nonnull BlockProperty<?> property, Serializable currentValue, Serializable invalidValue, Throwable cause) {
        super(property, buildMessage(currentValue, invalidValue), cause);
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }
    
    private static String buildMessage(Object currentValue, Object invalidValue) {
        return "Current Value: "+currentValue+", Invalid Value: "+invalidValue;
    }
}
