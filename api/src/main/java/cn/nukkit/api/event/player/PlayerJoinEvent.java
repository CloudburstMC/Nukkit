package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.message.Message;
import cn.nukkit.server.event.HandlerList;

public class PlayerJoinEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected Message joinMessage;

    public PlayerJoinEvent(Player player, Message joinMessage) {
        this.player = player;
        this.joinMessage = joinMessage;
    }

    public PlayerJoinEvent(Player player, String joinMessage) {
        this.player = player;
        this.joinMessage = new Message(joinMessage);
    }

    public Message getJoinMessage() {
        return joinMessage;
    }

    public void setJoinMessage(Message joinMessage) {
        this.joinMessage = joinMessage;
    }

    public void setJoinMessage(String joinMessage) {
        this.setJoinMessage(new Message(joinMessage));
    }
}
