package cn.nukkit.api.event;

import cn.nukkit.api.plugin.Plugin;

/**
 * This class manages event listeners and fires events.
 */
public interface EventManager {
    /**
     * Registers an object with event listeners.
     * @param plugin the plugin associated
     * @param listener the object
     */
    void register(Listener listener, Plugin plugin);

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
