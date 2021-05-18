package cn.nukkit.event.world;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.world.World;

/**
 * author: funcraft
 * Nukkit Project
 */
public class ThunderChangeEvent extends WeatherEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final boolean to;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public ThunderChangeEvent(World world, boolean to) {
        super(world);
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

}
