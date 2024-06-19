package cn.nukkit.plugin.service;

import cn.nukkit.plugin.Plugin;

public class RegisteredServiceProvider<T> implements Comparable<RegisteredServiceProvider<T>> {

    private final Plugin plugin;
    private final ServicePriority priority;
    private final Class<T> service;
    private final T provider;

    RegisteredServiceProvider(Class<T> service, T provider, ServicePriority priority, Plugin plugin) {
        this.plugin = plugin;
        this.provider = provider;
        this.service = service;
        this.priority = priority;
    }

    /**
     * Return the provided service.
     *
     * @return the provided service
     */
    public Class<T> getService() {
        return this.service;
    }

    /**
     * Return the plugin provide this service.
     *
     * @return the plugin provide this service, or {@code null}
     * only if this service provided by server
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Return the service provider.
     *
     * @return the service provider
     */
    public T getProvider() {
        return provider;
    }

    public ServicePriority getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegisteredServiceProvider<?> that = (RegisteredServiceProvider<?>) o;

        return provider == that.provider || provider.equals(that.provider);
    }

    @Override
    public int hashCode() {
        return provider.hashCode();
    }

    public int compareTo(RegisteredServiceProvider<T> other) {
        return other.priority.ordinal() - priority.ordinal();
    }
}
