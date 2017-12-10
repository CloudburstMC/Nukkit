package cn.nukkit.api.event;

/**
 * Register a single class for an event.
 * THIS IS UNFINISHED
 * @param <C>
 */
// TODO: IMPLEMENT
public interface EventListener<C extends Event> extends Listener {

    @EventHandler
    void onEvent(C event);
}
