package cn.nukkit.blockproperty.exception;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
import java.io.Serializable;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNullableByDefault
public class InvalidBlockPropertyValueException extends InvalidBlockPropertyException {
    private static final long serialVersionUID = -1087431932428639175L;
    
    @Nullable
    private final Serializable currentValue;
    
    @Nullable
    private final Serializable invalidValue;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockPropertyValueException(@Nonnull BlockProperty<?> property, Serializable currentValue, Serializable invalidValue) {
        super(property, buildMessage(currentValue, invalidValue));
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockPropertyValueException(@Nonnull BlockProperty<?> property, Serializable currentValue, Serializable invalidValue, String message) {
        super(property, buildMessage(currentValue, invalidValue)+". "+message);
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockPropertyValueException(@Nonnull BlockProperty<?> property, Serializable currentValue, Serializable invalidValue, String message, Throwable cause) {
        super(property, buildMessage(currentValue, invalidValue)+". "+message, cause);
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockPropertyValueException(@Nonnull BlockProperty<?> property, Serializable currentValue, Serializable invalidValue, Throwable cause) {
        super(property, buildMessage(currentValue, invalidValue), cause);
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }
    
    private static String buildMessage(Object currentValue, Object invalidValue) {
        return "Current Value: "+currentValue+", Invalid Value: "+invalidValue;
    }


    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public Serializable getCurrentValue() {
        return this.currentValue;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public Serializable getInvalidValue() {
        return this.invalidValue;
    }
}
