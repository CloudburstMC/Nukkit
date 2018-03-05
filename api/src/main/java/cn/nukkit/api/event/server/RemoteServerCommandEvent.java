package cn.nukkit.api.event.server;

import cn.nukkit.api.command.sender.RemoteConsoleCommandSender;

public class RemoteServerCommandEvent extends ServerCommandEvent {
    public RemoteServerCommandEvent(RemoteConsoleCommandSender sender, String command) {
        super(sender, command);
    }
}
