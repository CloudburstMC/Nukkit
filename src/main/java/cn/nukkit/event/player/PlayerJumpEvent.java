package cn.nukkit.event.player;

import cn.nukkit.event.HandlerList;
import cn.nukkit.player.Player;

public class PlayerJumpEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public PlayerJumpEvent(Player player) {
        super(player);
    }
}
