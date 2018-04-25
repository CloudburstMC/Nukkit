package cn.nukkit.server.command;

import cn.nukkit.api.command.CommandData;
import cn.nukkit.api.command.sender.CommandSender;

public abstract class VanillaCommand extends NukkitCommand {
    public VanillaCommand(String name, CommandData data) {
        super(name, data);
    }

    public abstract boolean onCommand(CommandSender sender, String label, String[] args);
}
