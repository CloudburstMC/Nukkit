package cn.nukkit.api.event.level;

import cn.nukkit.api.level.Level;
import com.flowpowered.math.vector.Vector3f;

public class SpawnChangeEvent implements LevelEvent {
    private final Level level;
    private final Vector3f previousSpawn;

    public SpawnChangeEvent(Level level, Vector3f previousSpawn) {
        this.level = level;
        this.previousSpawn = previousSpawn;
    }

    public Vector3f getPreviousSpawn() {
        return previousSpawn;
    }

    @Override
    public Level getLevel() {
        return level;
    }
}
