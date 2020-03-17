package cn.nukkit.event.level;

import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Level;
import com.nukkitx.math.vector.Vector3f;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SpawnChangeEvent extends LevelEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Vector3f previousSpawn;

    public SpawnChangeEvent(Level level, Vector3f previousSpawn) {
        super(level);
        this.previousSpawn = previousSpawn;
    }

    public Vector3f getPreviousSpawn() {
        return previousSpawn;
    }
}
