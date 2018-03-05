package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.message.ChatMessage;
import cn.nukkit.api.message.Message;

public class PlayerJoinEvent implements PlayerEvent {
    private final Player player;
    private Message joinMessage;

    public PlayerJoinEvent(Player player, Message joinMessage) {
        this.player = player;
        this.joinMessage = joinMessage;
    }

    public PlayerJoinEvent(Player player, String joinMessage) {
        this.player = player;
        this.joinMessage = new ChatMessage(joinMessage);
    }

    public Message getJoinMessage() {
        return joinMessage;
    }

    public void setJoinMessage(String joinMessage) {
        this.setJoinMessage(new ChatMessage(joinMessage));
    }

    public void setJoinMessage(Message joinMessage) {
        this.joinMessage = joinMessage;
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}
