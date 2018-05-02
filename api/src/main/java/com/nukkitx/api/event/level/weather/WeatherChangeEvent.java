package com.nukkitx.api.event.level.weather;

import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.level.Level;

public class WeatherChangeEvent implements WeatherEvent, Cancellable {
    private final Level level;
    private final boolean to;
    private boolean cancelled;

    public WeatherChangeEvent(Level level, boolean to) {
        this.level = level;
        this.to = to;
    }

    /**
     * Gets the state of weather that the world is being set to
     *
     * @return true if the weather is being set to raining, false otherwise
     */
    public boolean toWeatherState() {
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
