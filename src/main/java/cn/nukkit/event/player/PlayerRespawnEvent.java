package cn.nukkit.event.player;

import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;
import cn.nukkit.player.Player;

public class PlayerRespawnEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Position position;

    private boolean firstSpawn;

    public PlayerRespawnEvent(Player player, Position position) {
        this(player, position, false);
    }

    public PlayerRespawnEvent(Player player, Position position, boolean firstSpawn) {
        this.player = player;
        this.position = position;
        this.firstSpawn = firstSpawn;
    }

    public Position getRespawnPosition() {
        return position;
    }

    public void setRespawnPosition(Position position) {
        this.position = position;
    }

    public boolean isFirstSpawn() {
        return firstSpawn;
    }
}
