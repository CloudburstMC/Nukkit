package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerCommandPreprocessEvent extends PlayerMessageEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public PlayerCommandPreprocessEvent(Player player, String message) {
        this.player = player;
        this.message = message;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
