package cn.nukkit.event.player;

import cn.nukkit.event.HandlerList;

/**
 * Created on 2015/12/23 by xtypr.
 * Package cn.nukkit.event.player in project Nukkit .
 */
public abstract class PlayerMessageEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }


    protected String message;

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
