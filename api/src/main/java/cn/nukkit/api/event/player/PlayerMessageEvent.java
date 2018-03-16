package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.message.Message;

public abstract class PlayerMessageEvent implements PlayerEvent {
    private final Player player;
    private Message message;

    protected PlayerMessageEvent(final Player player, final Message message) {
        this.player = player;
        this.message = message;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
