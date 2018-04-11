/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.level.Level;
import cn.nukkit.api.util.GameMode;
import cn.nukkit.api.util.Rotation;
import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;

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
