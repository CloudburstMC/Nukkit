package com.nukkitx.server.command;

import com.nukkitx.api.command.CommandData;
import com.nukkitx.api.command.sender.CommandSender;

public abstract class VanillaCommand extends NukkitCommand {
    public VanillaCommand(String name, CommandData data) {
        super(name, data);
    }

    public abstract boolean onCommand(CommandSender sender, String label, String[] args);
}
