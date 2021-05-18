package cn.nukkit.event.world;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.world.World;

/**
 * author: funcraft
 * Nukkit Project
 */
public class WeatherChangeEvent extends WeatherEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final boolean to;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public WeatherChangeEvent(World world, boolean to) {
        super(world);
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

}
