package cn.nukkit.registry;

import cn.nukkit.utils.function.IntObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * Shared code that's used by all registries
 * @param <T> The type of object used by this registry
 */
public abstract class AbstractRegistry<T> {
    protected final Int2ObjectMap<IntObjectFunction<T>> mappings = new Int2ObjectOpenHashMap<>();

    protected AbstractRegistry()    {
        init();
    }

    /**
     * Load the base entries into the registry
     */
    protected abstract void init();

    protected abstract void parseArgs(T obj, Object... args);

    /**
     * Add an item to the registry
     * @param name The registry name of the item to add
     * @param func This should supply an instance of <T> using given arguments
     * @return Whether or not the entry could be added
     */
    public final boolean addEntry(RegistryName name, IntObjectFunction<T> func)    {
        if (mappings.containsKey(name.hashCode()))  {
            return false;
        } else {
            mappings.put(name.hashCode(), func);
            return true;
        }
    }

    /**
     * Get an instance of <T> from the given parameters
     * @param name The registry name of the object to get
     * @param i An integer code used by the function
     * @param args Optional, additional arguments passed to the object
     * @return An instance of <T>
     */
    public T getInstance(RegistryName name, int i, Object... args) {
        IntObjectFunction<T> func = mappings.get(name.hashCode());
        if (func == null)   {
            throw new IllegalArgumentException("No mapping for ID: " + name.toString());
        } else {
            T instance = func.accept(i);
            parseArgs(instance, args);
            return instance;
        }
    }
}
