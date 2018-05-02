package com.nukkitx.api.command;

import com.nukkitx.api.command.sender.CommandSender;

public interface CommandExecutor {

    boolean onCommand(CommandSender sender, String label, String[] args);
}
