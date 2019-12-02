package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerLoginEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    protected String kickMessage;

    public PlayerLoginEvent(Player player, String kickMessage) {
        this.player = player;
        this.kickMessage = kickMessage;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public String getKickMessage() {
        return kickMessage;
    }

    public void setKickMessage(String kickMessage) {
        this.kickMessage = kickMessage;
    }
}
