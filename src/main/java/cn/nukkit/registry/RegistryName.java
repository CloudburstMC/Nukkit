package cn.nukkit.registry;

/**
 * Similar to Minecraft Java's ResourceLocation class
 *
 * @author DaPorkchop_
 */
public final class RegistryName {
    /**
     * The name of the provider that this registry item is provided by
     * All vanilla items/blocks use `nukkit` here
     */
    public final String namespace;

    /**
     * The id (name) of this registry item
     */
    public final String id;

    /**
     * The hash of this registry name, cached here for fast access
     */
    private final int hash;

    public RegistryName(String namespace, String id)    {
        this.namespace = namespace;
        this.id = id;
        this.hash = namespace.hashCode() * 31 + id.hashCode();
    }

    @Override
    public String toString() {
        return namespace + ":" + id;
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
