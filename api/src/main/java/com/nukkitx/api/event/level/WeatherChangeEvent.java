package com.nukkitx.api.event.level;

import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.level.Level;

public class WeatherChangeEvent extends WeatherEvent implements Cancellable {
    private final State state;
    private final boolean starting;
    private boolean cancelled;

    public WeatherChangeEvent(Level level, State state, boolean starting) {
        super(level);
        this.state = state;
        this.starting = starting;
    }

    /**
     * Gets the state of weather that the world is being set to
     *
     * @return state
     */
    public State getState() {
        return state;
    }

    public boolean isStarting() {
        return starting;
    }

    public boolean isStopping() {
        return !starting;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public enum State {
        RAIN,
        THUNDER
    }
}
