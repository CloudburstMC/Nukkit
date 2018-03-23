package cn.nukkit.registry;

/**
 * All the different types of registries
 *
 * @author DaPorkchop_
 */
public enum RegistryType {
    BLOCK,
    ITEM,
    ENTITY;

    private AbstractRegistry registry;

    public void setRegistry(AbstractRegistry registry)  {
        if (this.registry == null)  {
            this.registry = registry;
        } else {
            throw new IllegalStateException("Tried to set registry when it's already set!");
        }
    }

    public AbstractRegistry getRegistry() {
        return registry;
    }
}
