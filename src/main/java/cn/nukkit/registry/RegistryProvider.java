package cn.nukkit.registry;

import cn.nukkit.plugin.Plugin;

import javax.annotation.Nonnull;

public class RegistryProvider<T> implements Comparable<RegistryProvider<T>> {
    private final T value;
    private final Plugin plugin;
    private final int priority;

    RegistryProvider(T value, Plugin plugin, int priority) {
        this.value = value;
        this.plugin = plugin;
        this.priority = priority;
    }

    @Nonnull
    public T getValue() {
        return value;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(RegistryProvider<T> that) {
        return Integer.compare(this.priority, that.priority);
    }
}
