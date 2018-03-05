package cn.nukkit.api.event.level;

import cn.nukkit.api.level.Level;

public abstract class WeatherEvent implements LevelEvent {
    private final Level level;

    public WeatherEvent(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }
}
