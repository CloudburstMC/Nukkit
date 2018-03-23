package cn.nukkit.registry;

import cn.nukkit.registry.function.BaseFunction;
import cn.nukkit.registry.function.IntObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Hashtable;
import java.util.Map;

/**
 * Shared code that's used by all registries
 *
 * @param <T> The type of object used by this registry
 * @author DaPorkchop_
 */
public abstract class AbstractRegistry<T, F extends BaseFunction<T>> {
    public final RegistryType type;

    protected final Int2ObjectMap<F> mappings = new Int2ObjectOpenHashMap<>();
    protected final Map<Class<? extends T>, RegistryName> names = new Hashtable<>();

    protected AbstractRegistry(RegistryType type) {
        this.type = type;
        type.setRegistry(this);
        init();
    }

    /**
     * Load the base entries into the registry
     */
    protected abstract void init();

    /**
     * Add an item to the registry
     *
     * @param name  The registry name of the item to add
     * @param func  This should supply an instance of <T> using given arguments
     * @param force Whether or not to force-add this entity to the registry (overwrite existing entries)
     * @return Whether or not the entry could be added
     */
    public boolean addEntry(RegistryName name, F func, Class<? extends T> clazz, boolean force) {
        if (!force && mappings.containsKey(name.hashCode())) {
            return false;
        } else {
            mappings.put(name.hashCode(), func);
            names.put(clazz, name);
            return true;
        }
    }

    /**
     * Gets a registry name instance from the given class
     *
     * @param clazz The class to check for
     * @return An instance of {@link RegistryName}, or null if no name is registered with the given class
     */
    public RegistryName getName(Class<? extends T> clazz) {
        return names.get(clazz);
    }

    /**
     * Checks if the registry contains an item with the given name
     *
     * @param name The name to check for
     * @return Whether or not the name is registered in this registry
     */
    public final boolean containsName(RegistryName name) {
        return mappings.containsKey(name.hashCode());
    }

    /**
     * Get an instance of <T> from the given parameters
     *
     * @param name The registry name of the object to get
     * @param i    An integer code used by the function
     * @param args Optional, additional arguments passed to the object
     * @return An instance of <T>
     */
    public T getInstance(RegistryName name, int i, Object... args) {
        F func = mappings.get(name.hashCode());
        if (func == null) {
            throw new IllegalArgumentException("No mapping for ID: " + name.toString());
        } else {
            return accept(func, i, args);
        }
    }

    /**
     * Convenience method to register an entry in the init() method
     * @param name The name of the entry
     * @param func The initialization method for the entry
     * @param clazz The class of the entry
     */
    protected void register(String name, F func, Class<? extends T> clazz)    {
        addEntry(new RegistryName("nukkit", name), func, clazz, true);
    }

    protected abstract T accept(F func, int i, Object... args);
}
