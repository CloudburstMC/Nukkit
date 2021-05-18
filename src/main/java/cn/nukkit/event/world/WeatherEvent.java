package cn.nukkit.event.world;

import cn.nukkit.event.Event;
import cn.nukkit.world.World;

/**
 * author: funcraft
 * Nukkit Project
 */
public abstract class WeatherEvent extends Event {

    private final World world;

    public WeatherEvent(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
}
