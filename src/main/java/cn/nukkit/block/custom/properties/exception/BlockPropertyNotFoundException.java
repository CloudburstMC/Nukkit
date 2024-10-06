package cn.nukkit.block.custom.properties.exception;

import cn.nukkit.block.custom.properties.BlockProperties;

import java.util.NoSuchElementException;

public class BlockPropertyNotFoundException extends NoSuchElementException {
    private final String propertyName;

    public BlockPropertyNotFoundException(String propertyName) {
        super("The property \""+propertyName+"\" was not found.");
        this.propertyName = propertyName;
    }

    public BlockPropertyNotFoundException(String propertyName, String details) {
        super(propertyName+": " + details);
        this.propertyName = propertyName;
    }

    public BlockPropertyNotFoundException(String propertyName, BlockProperties properties) {
        super("The property " + propertyName + " was not found. Valid properties: " + properties.getNames());
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}