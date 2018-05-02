package com.nukkitx.api.event.player;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;
import com.nukkitx.api.Player;
import com.nukkitx.api.level.Level;
import com.nukkitx.api.util.GameMode;
import com.nukkitx.api.util.Rotation;

/**
 * When the player has started to initialize but not spawned yet.
 */
public class PlayerLoginEvent implements PlayerEvent {
    private final Player player;
    private Vector3f spawnPosition;
    private Level spawnLevel;
    private Rotation rotation;
    private GameMode gameMode;

    public PlayerLoginEvent(Player player, Vector3f spawnPosition, Level spawnLevel, Rotation rotation, GameMode gameMode) {
        this.player = player;
        this.spawnPosition = Preconditions.checkNotNull(spawnPosition, "spawnLocation");
        this.spawnLevel = Preconditions.checkNotNull(spawnLevel, "spawnLevel");
        this.rotation = Preconditions.checkNotNull(rotation, "rotation");
        this.gameMode = Preconditions.checkNotNull(gameMode, "gameMode");
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
     * @return position
     */
    public Vector3f getSpawnPosition() {
        return spawnPosition;
    }

    /**
     * Set the position at which the player will spawn.
     * @param spawnPosition location
     */
    public void setSpawnLocation(Vector3f spawnPosition) {
        this.spawnPosition = spawnPosition;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}
