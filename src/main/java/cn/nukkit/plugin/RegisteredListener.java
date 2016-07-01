package cn.nukkit.plugin;

import cn.nukkit.event.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class RegisteredListener {

    private Listener listener;

    private EventPriority priority;

    private Plugin plugin;

    private EventExecutor executor;

    private boolean ignoreCancelled;

    private TimingsHandler timings;

    public RegisteredListener(Listener listener, EventExecutor executor, EventPriority priority, Plugin plugin, boolean ignoreCancelled, TimingsHandler timings) {
        this.listener = listener;
        this.priority = priority;
        this.plugin = plugin;
        this.executor = executor;
        this.ignoreCancelled = ignoreCancelled;
        this.timings = timings;
    }

    public Listener getListener() {
        return listener;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public EventPriority getPriority() {
        return priority;
    }

    public void callEvent(Event event) {
        if (event instanceof Cancellable) {
            if (event.isCancelled() && isIgnoringCancelled()) {
                return;
            }
        }
        this.timings.startTiming();
        executor.execute(listener, event);
        this.timings.stopTiming();
    }

    public void destruct() {
        this.timings.remove();
    }

    public boolean isIgnoringCancelled() {
        return ignoreCancelled;
    }
}
