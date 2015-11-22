package cn.nukkit.event.weather;

import cn.nukkit.event.Event;
import cn.nukkit.level.Level;

/**
 * author: funcraft
 * Nukkit Project
 */
public abstract class WeatherEvent extends Event {

    private Level level;

    public WeatherEvent(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }
}
