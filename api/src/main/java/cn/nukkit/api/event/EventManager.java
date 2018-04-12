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

package cn.nukkit.api.event;

import cn.nukkit.api.plugin.Plugin;

/**
 * This class manages event listeners and fires events.
 */
public interface EventManager {
    /**
     * Registers a object that extends {@link Listener} with event listeners.
     * @param plugin the plugin associated
     * @param listener the listener object
     */
    void registerListener(Listener listener, Plugin plugin);

    /**
     * Fires an event.
     * @param event the event to fire
     */
    void fire(Event event);

    /**
     * Unregisters an object's event listeners.
     * @param listener the object to deregister
     */
    void unregisterListener(Listener listener);

    /**
     * Unregisters a plugin's event listeners.
     * @param plugin the plugin to deregister
     */
    void unregisterAllListeners(Plugin plugin);
}
