package cn.nukkit.event.server;

import cn.nukkit.command.CommandSender;
import cn.nukkit.event.HandlerList;

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
