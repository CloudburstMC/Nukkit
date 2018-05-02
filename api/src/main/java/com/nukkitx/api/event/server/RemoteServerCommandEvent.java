package com.nukkitx.api.event.server;

import com.nukkitx.api.command.sender.RemoteConsoleCommandSender;

public class RemoteServerCommandEvent extends ServerCommandEvent {
    public RemoteServerCommandEvent(RemoteConsoleCommandSender sender, String command) {
        super(sender, command);
    }
}
