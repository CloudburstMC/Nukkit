package com.nukkitx.api.event.level.weather;

import com.nukkitx.api.entity.weather.Lightning;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.level.Level;

public class LightningStrikeEvent implements WeatherEvent, Cancellable {
    private final Level level;
    private final Lightning bolt;
    private boolean cancelled;

    public LightningStrikeEvent(Level level, final Lightning bolt) {
        this.level = level;
        this.bolt = bolt;
    }

    public Lightning getLightning() {
        return bolt;
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
