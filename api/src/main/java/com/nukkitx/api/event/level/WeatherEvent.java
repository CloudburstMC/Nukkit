package com.nukkitx.api.event.level;

import com.nukkitx.api.level.Level;

public class WeatherEvent implements LevelEvent {
    private final Level level;

    public WeatherEvent(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }
}
