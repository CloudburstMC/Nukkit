package cn.nukkit.event.server;

import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.format.wool.WoolFormat;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class WoolLevelSaveRequestEvent extends ServerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    protected WoolFormat provied;

    public WoolLevelSaveRequestEvent(WoolFormat provied) {
        this.provied = provied;
    }

    public WoolFormat getProvied() {
        return provied;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
