package cn.nukkit.api.event.weather;

import cn.nukkit.server.level.Level;

/**
 * author: funcraft
 * Nukkit Project
 */
public abstract class WeatherEvent extends Event {

    private final Level level;

    public WeatherEvent(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }
}
