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
import cn.nukkit.server.NukkitServer;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.*;

/**
 * Created on 16-11-20.
 */
public class NKServiceManager implements ServiceManager {

    private final Map<Class<?>, List<RegisteredServiceProvider<?>>> handle = new HashMap<>();

    @Override
    public <T> boolean register(Class<T> service, T provider, Plugin plugin, ServicePriority priority) {
        Preconditions.checkNotNull(provider);
        Preconditions.checkNotNull(priority);
        Preconditions.checkNotNull(service);

        // build-in service provider needn't plugin param
        if (plugin == null && provider.getClass().getClassLoader() != NukkitServer.class.getClassLoader()) {
            throw new NullPointerException("plugin");
        }

        return provide(service, provider, plugin, priority);
    }

    protected <T> boolean provide(Class<T> service, T instance, Plugin plugin, ServicePriority priority) {
        synchronized (handle) {
            List<RegisteredServiceProvider<?>> list = handle.computeIfAbsent(service, k -> new ArrayList<>());

            RegisteredServiceProvider<T> registered = new RegisteredServiceProvider<>(service, instance, priority, plugin);

            int position = Collections.binarySearch(list, registered);

            if (position > -1) return false;

            list.add(-(position + 1), registered);
        }

        return true;
    }

    @Override
    public List<RegisteredServiceProvider<?>> cancel(Plugin plugin) {
        ImmutableList.Builder<RegisteredServiceProvider<?>> builder = ImmutableList.builder();

        Iterator<RegisteredServiceProvider<?>> it;
        RegisteredServiceProvider<?> registered;

        synchronized (handle) {
            for (List<RegisteredServiceProvider<?>> list : handle.values()) {
                it = list.iterator();

                while (it.hasNext()) {
                    registered = it.next();
                    if (registered.getPlugin() == plugin) {
                        it.remove();
                        builder.add(registered);
                    }
                }

            }
        }

        return builder.build();
    }

    @Override
    public <T> RegisteredServiceProvider<T> cancel(Class<T> service, T provider) {
        RegisteredServiceProvider<T> result = null;

        synchronized (handle) {
            Iterator<RegisteredServiceProvider<?>> it = handle.get(service).iterator();
            RegisteredServiceProvider next;

            while (it.hasNext() && result == null) {
                next = it.next();
                if (next.getProvider() == provider) {
                    it.remove();
                    result = next;
                }
            }

        }

        return result;
    }

    @Override
    public <T> RegisteredServiceProvider<T> getProvider(Class<T> service) {
        synchronized (handle) {
            List<RegisteredServiceProvider<?>> list = handle.get(service);
            if (list == null || list.isEmpty()) return null;
            return (RegisteredServiceProvider<T>) list.get(0);
        }
    }

    @Override
    public List<Class<?>> getKnownService() {
        return ImmutableList.copyOf(handle.keySet());
    }

}
