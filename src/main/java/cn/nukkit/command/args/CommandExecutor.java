package cn.nukkit.command.args;

import cn.nukkit.command.CommandSender;

// TODO: rename this as there is already a CommandExecutor and move it out of the args package
@FunctionalInterface
public interface CommandExecutor {

    boolean execute(CommandSender sender, String aliasUsed, String[] args);
}
