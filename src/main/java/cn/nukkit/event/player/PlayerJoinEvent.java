package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.level.Position;

public class PlayerJoinEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected TextContainer joinMessage;
    protected Position position;

    public PlayerJoinEvent(Player player, TextContainer joinMessage, Position position) {
        this.player = player;
        this.joinMessage = joinMessage;
        this.position = position;
    }

    public PlayerJoinEvent(Player player, String joinMessage, Position position) {
        this.player = player;
        this.joinMessage = new TextContainer(joinMessage);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public TextContainer getJoinMessage() {
        return joinMessage;
    }

    public void setJoinMessage(TextContainer joinMessage) {
        this.joinMessage = joinMessage;
    }

    public void setJoinMessage(String joinMessage) {
        this.setJoinMessage(new TextContainer(joinMessage));
    }
}
