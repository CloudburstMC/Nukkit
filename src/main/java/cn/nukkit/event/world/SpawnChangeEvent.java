package cn.nukkit.event.world;

import cn.nukkit.event.HandlerList;
import cn.nukkit.world.World;
import cn.nukkit.world.Position;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SpawnChangeEvent extends WorldEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Position previousSpawn;

    public SpawnChangeEvent(World world, Position previousSpawn) {
        super(world);
        this.previousSpawn = previousSpawn;
    }

    public Position getPreviousSpawn() {
        return previousSpawn;
    }
}
