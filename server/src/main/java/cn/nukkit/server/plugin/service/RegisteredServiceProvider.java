/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.plugin.service;


import cn.nukkit.api.plugin.Plugin;

/**
 * Created on 16-11-20.
 */
public class RegisteredServiceProvider<T> implements Comparable<RegisteredServiceProvider<T>> {

    private Plugin plugin;
    private ServicePriority priority;
    private Class<T> service;
    private T provider;

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
