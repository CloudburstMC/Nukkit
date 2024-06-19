package cn.nukkit.customblock.properties.exception;

import cn.nukkit.customblock.properties.BlockProperty;

public class InvalidBlockPropertyMetaException extends InvalidBlockPropertyException {
    private static final long serialVersionUID = -8493494844859767053L;

    private final Number currentMeta;
    private final Number invalidMeta;
    
    public InvalidBlockPropertyMetaException(BlockProperty<?> property, Number currentMeta, Number invalidMeta) {
        super(property, buildMessage(currentMeta, invalidMeta));
        this.currentMeta = currentMeta;
        this.invalidMeta = invalidMeta;
    }
    
    public InvalidBlockPropertyMetaException(BlockProperty<?> property, Number currentMeta, Number invalidMeta, String message) {
        super(property, buildMessage(currentMeta, invalidMeta)+". "+message);
        this.currentMeta = currentMeta;
        this.invalidMeta = invalidMeta;
    }

    public InvalidBlockPropertyMetaException(BlockProperty<?> property, Number currentMeta, Number invalidMeta, String message, Throwable cause) {
        super(property, buildMessage(currentMeta, invalidMeta)+". "+message, cause);
        this.currentMeta = currentMeta;
        this.invalidMeta = invalidMeta;
    }
    
    public InvalidBlockPropertyMetaException(BlockProperty<?> property, Number currentMeta, Number invalidMeta, Throwable cause) {
        super(property, buildMessage(currentMeta, invalidMeta), cause);
        this.currentMeta = currentMeta;
        this.invalidMeta = invalidMeta;
    }

    private static String buildMessage(Object currentValue, Object invalidValue) {
        return "Current Meta: "+currentValue+", Invalid Meta: "+invalidValue;
    }

    public Number getCurrentMeta() {
        return this.currentMeta;
    }
    
    public Number getInvalidMeta() {
        return this.invalidMeta;
    }
}
