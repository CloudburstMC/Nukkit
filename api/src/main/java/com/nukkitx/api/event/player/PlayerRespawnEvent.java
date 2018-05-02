package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.util.Location;

public class PlayerRespawnEvent implements PlayerEvent {
    private final Player player;
    private Location respawnLocation;

    public PlayerRespawnEvent(Player player, Location respawnLocation) {
        this.player = player;
        this.respawnLocation = respawnLocation;
    }

    public Location getRespawnLocation() {
        return respawnLocation;
    }

    public void setRespawnLocation(Location respawnLocation) {
        this.respawnLocation = respawnLocation;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public enum Cause {
        DEATH,
        JOIN
    }
}
