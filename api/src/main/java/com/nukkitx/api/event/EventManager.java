package com.nukkitx.api.event;

import com.nukkitx.api.plugin.Plugin;

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
