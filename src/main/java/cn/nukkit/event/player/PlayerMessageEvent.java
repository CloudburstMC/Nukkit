package cn.nukkit.event.player;

import cn.nukkit.player.Player;

/**
 * Created on 2015/12/23 by xtypr.
 * Package cn.nukkit.event.player in project Nukkit .
 */
public abstract class PlayerMessageEvent extends PlayerEvent {

    protected Player sender;
    protected String message;

    public PlayerMessageEvent(Player player, String message) {
        super(player);
        this.sender = player;
        this.message = message;
    }

    /**
     * Changes the player that is sending the message
     *
     * @param sender messenger
     */
    public void setSender(Player sender) {
        this.sender = sender;
    }

    public Player getSender() {
        return sender;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
