package cn.nukkit.block.custom.properties.exception;

import cn.nukkit.block.custom.properties.BlockProperty;

public class InvalidBlockPropertyException extends IllegalArgumentException {
    private static final long serialVersionUID = -6934630506175381230L;

    private final BlockProperty<?> property;
    
    public InvalidBlockPropertyException(BlockProperty<?> property) {
        super(buildMessage(property));
        this.property = property;
    }

    public InvalidBlockPropertyException(BlockProperty<?> property, String message) {
        super(buildMessage(property) + ". "+message);
        this.property = property;
    }

    public InvalidBlockPropertyException(BlockProperty<?> property, String message, Throwable cause) {
        super(buildMessage(property) + ". "+message, cause);
        this.property = property;
    }

    public InvalidBlockPropertyException(BlockProperty<?> property, Throwable cause) {
        super(buildMessage(property), cause);
        this.property = property;
    }

    private static String buildMessage(BlockProperty<?> property) {
        return "Property: " + property.getName();
    }

    public BlockProperty<?> getProperty() {
        return property;
    }
}
