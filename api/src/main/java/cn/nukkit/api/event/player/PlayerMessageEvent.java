package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;

public abstract class PlayerMessageEvent implements PlayerEvent {
    private final Player player;
    private String message;

    protected PlayerMessageEvent(final Player player, final String message) {
        this.player = player;
        this.message = message;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
