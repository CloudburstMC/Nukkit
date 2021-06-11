package cn.nukkit.blockproperty.exception;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;

import java.util.NoSuchElementException;

@PowerNukkitOnly
@Since("1.5.0.0-PN")
public class BlockPropertyNotFoundException extends NoSuchElementException {
    private final String propertyName;

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public BlockPropertyNotFoundException(String propertyName) {
        super("The property \""+propertyName+"\" was not found.");
        this.propertyName = propertyName;
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public BlockPropertyNotFoundException(String propertyName, String details) {
        super(propertyName+": " + details);
        this.propertyName = propertyName;
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public BlockPropertyNotFoundException(String propertyName, BlockProperties properties) {
        super("The property \""+propertyName+"\" was not found. Valid properties: " + properties.getNames());
        this.propertyName = propertyName;
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public String getPropertyName() {
        return propertyName;
    }
}
