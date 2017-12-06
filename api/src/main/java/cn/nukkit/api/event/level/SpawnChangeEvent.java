package cn.nukkit.api.event.level;

import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.level.Level;
import cn.nukkit.server.level.Position;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SpawnChangeEvent extends LevelEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Position previousSpawn;

    public SpawnChangeEvent(Level level, Position previousSpawn) {
        super(level);
        this.previousSpawn = previousSpawn;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Position getPreviousSpawn() {
        return previousSpawn;
    }
}
