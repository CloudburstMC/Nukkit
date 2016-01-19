package cn.nukkit.event.level;

import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SpawnChangeEvent extends LevelEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Location previousSpawn;

    public SpawnChangeEvent(Level level, Location previousSpawn) {
        super(level);
        this.previousSpawn = previousSpawn;
    }

    public Location getPreviousSpawn() {
        return previousSpawn;
    }
}
