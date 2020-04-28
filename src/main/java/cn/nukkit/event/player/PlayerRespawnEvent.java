package cn.nukkit.event.player;

import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Location;
import cn.nukkit.player.Player;

public class PlayerRespawnEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Location location;

    private final boolean firstSpawn;

    public PlayerRespawnEvent(Player player, Location location) {
        this(player, location, false);
    }

    public PlayerRespawnEvent(Player player, Location location, boolean firstSpawn) {
        super(player);
        this.location = location;
        this.firstSpawn = firstSpawn;
    }

    public Location getRespawnLocation() {
        return location;
    }

    public void setRespawnLocation(Location location) {
        this.location = location;
    }

    public boolean isFirstSpawn() {
        return firstSpawn;
    }
}
