package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;

/**
 * Created on 2015/12/23 by xtypr.
 * Package cn.nukkit.server.event.player in project Nukkit .
 */
public abstract class PlayerMessageEvent extends PlayerEvent {

    protected String message;

    protected PlayerMessageEvent(final Player player) {
        super(player);
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
