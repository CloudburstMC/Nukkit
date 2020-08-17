package cn.nukkit.event.player;

/**
 * @author xtypr
 * @since 2015/12/23
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
