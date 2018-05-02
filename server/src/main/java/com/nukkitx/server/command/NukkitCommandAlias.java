package com.nukkitx.server.command;

import com.nukkitx.api.command.AliasCommand;
import com.nukkitx.api.command.Command;
import com.nukkitx.api.command.CommandData;
import com.nukkitx.api.command.CommandExecutor;
import com.nukkitx.api.command.sender.CommandSender;

public class NukkitCommandAlias implements AliasCommand {
    private final NukkitCommand command;
    private final String name;

    public NukkitCommandAlias(NukkitCommand command, String name) {
        this.command = command;
        this.name = name;
    }

    @Override
    public Command getParent() {
        return command;
    }

    @Override
    public CommandExecutor getExecutor() {
        return command.getExecutor();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CommandData getData() {
        return command.data;
    }

    @Override
    public boolean testPermission(CommandSender sender) {
        return command.testPermission(sender);
    }

    @Override
    public boolean testPermissionSilent(CommandSender sender) {
        return testPermissionSilent(sender);
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        return command.onCommand(sender, label, args);
    }
}
