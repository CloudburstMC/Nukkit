package cn.nukkit.event.world;

import cn.nukkit.event.Event;
import cn.nukkit.world.World;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class WorldEvent extends Event {

    private final World world;

    public WorldEvent(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
}
