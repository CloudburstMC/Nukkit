package cn.nukkit.blockproperty.exception;

import cn.nukkit.blockproperty.BlockProperty;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;

@ParametersAreNullableByDefault
@Getter
public class InvalidBlockPropertyException extends IllegalArgumentException {
    private final transient BlockProperty<?> property;

    public InvalidBlockPropertyException(@Nonnull BlockProperty<?> property) {
        super(buildMessage(property));
        this.property = property;
    }

    public InvalidBlockPropertyException(@Nonnull BlockProperty<?> property, String message) {
        super(buildMessage(property)+". "+message);
        this.property = property;
    }

    public InvalidBlockPropertyException(@Nonnull BlockProperty<?> property, String message, Throwable cause) {
        super(buildMessage(property)+". "+message, cause);
        this.property = property;
    }

    public InvalidBlockPropertyException(@Nonnull BlockProperty<?> property, Throwable cause) {
        super(buildMessage(property), cause);
        this.property = property;
    }

    private static String buildMessage(@Nonnull BlockProperty<?> property) {
        return "Property: "+property.getName();
    }
}
