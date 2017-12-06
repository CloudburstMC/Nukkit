package cn.nukkit.api.event.weather;

import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.level.Level;

/**
 * author: funcraft
 * Nukkit Project
 */
public class ThunderChangeEvent extends WeatherEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final boolean to;

    public ThunderChangeEvent(Level level, boolean to) {
        super(level);
        this.to = to;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Gets the state of thunder that the world is being set to
     *
     * @return true if the thunder is being set to start, false otherwise
     */
    public boolean toThunderState() {
        return to;
    }

}
