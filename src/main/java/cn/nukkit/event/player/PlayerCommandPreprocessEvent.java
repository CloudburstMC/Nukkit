package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerCommandPreprocessEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected String message;


    public PlayerCommandPreprocessEvent(Player player, String message) {
        this.player = player;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
