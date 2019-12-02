package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * Called when the player logs in, before things have been set up
 */
public class PlayerPreLoginEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    protected String kickMessage;

    public PlayerPreLoginEvent(Player player, String kickMessage) {
        this.player = player;
        this.kickMessage = kickMessage;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public String getKickMessage() {
        return this.kickMessage;
    }

    public void setKickMessage(String kickMessage) {
        this.kickMessage = kickMessage;
    }
}
