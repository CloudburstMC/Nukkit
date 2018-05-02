package com.nukkitx.api.command;

import com.nukkitx.api.command.sender.CommandSender;

public interface Command extends CommandExecutor {
    CommandExecutor getExecutor();

    String getName();

    CommandData getData();

    boolean testPermission(CommandSender sender);

    boolean testPermissionSilent(CommandSender sender);
}
