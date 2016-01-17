package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Location;

public class PlayerRespawnEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Location position;

    public PlayerRespawnEvent(Player player, Location position) {
        this.player = player;
        this.position = position;
    }

    public Location getRespawnPosition() {
        return position;
    }

    public void setRespawnPosition(Location position) {
        this.position = position;
    }
}
