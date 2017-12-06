package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.level.Position;

public class PlayerRespawnEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Position position;

    public PlayerRespawnEvent(Player player, Position position) {
        this.player = player;
        this.position = position;
    }

    public Position getRespawnPosition() {
        return position;
    }

    public void setRespawnPosition(Position position) {
        this.position = position;
    }
}
