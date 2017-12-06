package cn.nukkit.api.event.server;

import cn.nukkit.server.event.HandlerList;

/**
 * Called when an RCON command is executed.
 *
 * @author Tee7even
 */
public class RemoteServerCommandEvent extends ServerCommandEvent {
    private static final HandlerList handlers = new HandlerList();

    public RemoteServerCommandEvent(CommandSender sender, String command) {
        super(sender, command);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
