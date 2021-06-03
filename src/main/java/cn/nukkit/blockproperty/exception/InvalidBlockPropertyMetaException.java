package cn.nukkit.blockproperty.exception;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperty;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
public class InvalidBlockPropertyMetaException extends InvalidBlockPropertyException {
    private static final long serialVersionUID = -8493494844859767053L;
    
    @Nonnull
    private final Number currentMeta;
    
    @Nonnull
    private final Number invalidMeta;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockPropertyMetaException(BlockProperty<?> property, Number currentMeta, Number invalidMeta) {
        super(property, buildMessage(currentMeta, invalidMeta));
        this.currentMeta = currentMeta;
        this.invalidMeta = invalidMeta;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockPropertyMetaException(BlockProperty<?> property, Number currentMeta, Number invalidMeta, String message) {
        super(property, buildMessage(currentMeta, invalidMeta)+". "+message);
        this.currentMeta = currentMeta;
        this.invalidMeta = invalidMeta;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockPropertyMetaException(BlockProperty<?> property, Number currentMeta, Number invalidMeta, String message, Throwable cause) {
        super(property, buildMessage(currentMeta, invalidMeta)+". "+message, cause);
        this.currentMeta = currentMeta;
        this.invalidMeta = invalidMeta;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockPropertyMetaException(BlockProperty<?> property, Number currentMeta, Number invalidMeta, Throwable cause) {
        super(property, buildMessage(currentMeta, invalidMeta), cause);
        this.currentMeta = currentMeta;
        this.invalidMeta = invalidMeta;
    }
    
    private static String buildMessage(Object currentValue, Object invalidValue) {
        return "Current Meta: "+currentValue+", Invalid Meta: "+invalidValue;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Number getCurrentMeta() {
        return this.currentMeta;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Number getInvalidMeta() {
        return this.invalidMeta;
    }
}
