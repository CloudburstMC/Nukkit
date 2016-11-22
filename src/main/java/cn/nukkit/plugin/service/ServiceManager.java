package cn.nukkit.plugin.service;


import cn.nukkit.plugin.Plugin;

import java.util.List;

/**
 * Created on 16-11-20.
 */
public interface ServiceManager {

    /**
     * Register an object as a service's provider.
     *
     * @param service  the service
     * @param provider the service provider
     * @param plugin   the plugin
     * @param priority the priority
     * @return {@code true}, or {@code false} only if {@code provider}
     * already registered
     */
    <T> boolean register(Class<T> service, T provider, Plugin plugin, ServicePriority priority);

    /**
     * Cancel service's provider(s) offered this plugin.
     *
     * @param plugin the plugin
     * @return a {@link com.google.common.collect.ImmutableList}
     * contains cancelled {@link RegisteredServiceProvider}
     */
    List<RegisteredServiceProvider<?>> cancel(Plugin plugin);

    /**
     * Cancel a service's provider.
     *
     * @param service  the service
     * @param provider the provider
     * @return the cancelled {@link RegisteredServiceProvider}, or {@code null} if not
     * any provider cancelled
     */
    <T> RegisteredServiceProvider<T> cancel(Class<T> service, T provider);

    /**
     * Return the service's provider.
     *
     * @param service the target service
     * @return a {@lingetProvider()k RegisteredService<T>} registered highest priority, or
     * {@code null} if not exists
     */
    <T> RegisteredServiceProvider<T> getProvider(Class<T> service);

    /**
     * Return the known service(s).
     *
     * @return a {@link com.google.common.collect.ImmutableList} contains the
     * known service(s)
     */
    List<Class<?>> getKnownService();

}
