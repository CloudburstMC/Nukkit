package cn.nukkit.event.world;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.world.World;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class WorldUnloadEvent extends WorldEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public WorldUnloadEvent(World world) {
        super(world);
    }

}
