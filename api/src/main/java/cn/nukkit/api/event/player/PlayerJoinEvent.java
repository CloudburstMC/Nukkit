package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.message.GenericMessage;
import cn.nukkit.api.message.Message;

public class PlayerJoinEvent extends PlayerEvent {

    protected Message joinMessage;

    public PlayerJoinEvent(Player player, Message joinMessage) {
        super(player);
        this.joinMessage = joinMessage;
    }

    public PlayerJoinEvent(Player player, String joinMessage) {
        super(player);
        this.joinMessage = new GenericMessage(joinMessage);
    }

    public Message getJoinMessage() {
        return joinMessage;
    }

    public void setJoinMessage(String joinMessage) {
        this.setJoinMessage(new GenericMessage(joinMessage));
    }

    public void setJoinMessage(Message joinMessage) {
        this.joinMessage = joinMessage;
    }
}
