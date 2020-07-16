package cn.nukkit.blockproperty.exception;

import cn.nukkit.blockproperty.BlockProperty;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Getter
public class InvalidBlockPropertyMetaException extends InvalidBlockPropertyException {
    private final transient Number currentMeta;
    private final transient Number invalidMeta;

    public InvalidBlockPropertyMetaException(@Nonnull BlockProperty<?> property, Number currentMeta, Number invalidMeta) {
        super(property, buildMessage(currentMeta, invalidMeta));
        this.currentMeta = currentMeta;
        this.invalidMeta = invalidMeta;
    }

    public InvalidBlockPropertyMetaException(@Nonnull BlockProperty<?> property, Number currentMeta, Number invalidMeta, String message) {
        super(property, buildMessage(currentMeta, invalidMeta)+". "+message);
        this.currentMeta = currentMeta;
        this.invalidMeta = invalidMeta;
    }

    public InvalidBlockPropertyMetaException(@Nonnull BlockProperty<?> property, Number currentMeta, Number invalidMeta, String message, Throwable cause) {
        super(property, buildMessage(currentMeta, invalidMeta)+". "+message, cause);
        this.currentMeta = currentMeta;
        this.invalidMeta = invalidMeta;
    }

    public InvalidBlockPropertyMetaException(@Nonnull BlockProperty<?> property, Number currentMeta, Number invalidMeta, Throwable cause) {
        super(property, buildMessage(currentMeta, invalidMeta), cause);
        this.currentMeta = currentMeta;
        this.invalidMeta = invalidMeta;
    }
    
    private static String buildMessage(Object currentValue, Object invalidValue) {
        return "Current Meta: "+currentValue+", Invalid Meta: "+invalidValue;
    }
}
