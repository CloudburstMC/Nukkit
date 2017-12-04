package cn.nukkit.server.event.server;

import cn.nukkit.server.command.CommandSender;
import cn.nukkit.server.event.HandlerList;

/**
 * Called when an RCON command is executed.
 *
 * @author Tee7even
 */
public class RemoteServerCommandEvent extends ServerCommandEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RemoteServerCommandEvent(CommandSender sender, String command) {
        super(sender, command);
    }
}
