package cn.nukkit.server.event.player;

/**
 * Created on 2015/12/23 by xtypr.
 * Package cn.nukkit.server.event.player in project Nukkit .
 */
public abstract class PlayerMessageEvent extends PlayerEvent {

    protected String message;

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
