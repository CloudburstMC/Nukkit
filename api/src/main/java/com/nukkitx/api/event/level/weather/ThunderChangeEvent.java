package com.nukkitx.api.event.level.weather;

import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.level.Level;

public class ThunderChangeEvent implements WeatherEvent, Cancellable {
    private final Level level;
    private final boolean to;
    private boolean cancelled;

    public ThunderChangeEvent(Level level, boolean to) {
        this.level = level;
        this.to = to;
    }

    /**
     * Gets the state of thunder that the world is being set to
     *
     * @return true if the thunder is being set to start, false otherwise
     */
    public boolean toThunderState() {
        return to;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Level getLevel() {
        return level;
    }
}
