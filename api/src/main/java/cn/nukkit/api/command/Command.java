package cn.nukkit.api.command;

import cn.nukkit.api.command.sender.CommandSender;

import java.util.Collection;

public interface Command {
    CommandExecutor getExecutor();

    String getName();

    Collection<String> getAliases();

    String getPermissions();

    String getDescription();

    String getUsageMessage();

    String getPermissionMessage();

    boolean testPermission(CommandSender sender);

    boolean testPermissionSilent(CommandSender sender);

    void execute(CommandSender sender, Command command, String[] args);
}
