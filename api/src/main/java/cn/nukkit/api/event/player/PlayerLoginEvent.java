package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.level.Level;
import cn.nukkit.api.util.Rotation;
import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;

/**
 * When the player has started to initialize but not spawned yet.
 */
public class PlayerLoginEvent extends PlayerEvent {
    private Vector3f spawnLocation;
    private Level spawnLevel;
    private Rotation rotation;

    public PlayerLoginEvent(Player player, Vector3f spawnLocation, Level spawnLevel, Rotation rotation) {
        super(player);
        this.spawnLocation = Preconditions.checkNotNull(spawnLocation, "spawnLocation");
        this.spawnLevel = Preconditions.checkNotNull(spawnLevel, "spawnLevel");
        this.rotation = Preconditions.checkNotNull(rotation, "rotation");
    }

    /**
     * Level in which the player will spawn
     * @return level
     */
    public Level getSpawnLevel() {
        return spawnLevel;
    }

    /**
     * Set which level the player should spawn to.
     * @param spawnLevel level
     */
    public void setSpawnLevel(Level spawnLevel) {
        this.spawnLevel = spawnLevel;
    }

    /**
     * Get the rotation at which the player will spawn.
     * @return rotation
     */
    public Rotation getRotation() {
        return rotation;
    }

    /**
     * Set the rotation the player will spawn.
     * @param rotation rotation
     */
    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    /**
     * The spawn position that the player will spawn at.
     * @return location
     */
    public Vector3f getSpawnLocation() {
        return spawnLocation;
    }

    /**
     * Set the position at which the player will spawn.
     * @param spawnLocation location
     */
    public void setSpawnLocation(Vector3f spawnLocation) {
        this.spawnLocation = spawnLocation;
    }
}
