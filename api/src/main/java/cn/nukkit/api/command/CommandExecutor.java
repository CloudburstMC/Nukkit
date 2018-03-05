package cn.nukkit.api.command;

import cn.nukkit.api.command.sender.CommandSender;

public interface CommandExecutor {
    boolean onCommand(CommandSender sender, Command command, String label, String[] args);
}
