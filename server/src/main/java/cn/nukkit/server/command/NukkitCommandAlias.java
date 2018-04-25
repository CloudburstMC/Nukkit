package cn.nukkit.server.command;

import cn.nukkit.api.command.AliasCommand;
import cn.nukkit.api.command.Command;
import cn.nukkit.api.command.CommandData;
import cn.nukkit.api.command.CommandExecutor;
import cn.nukkit.api.command.sender.CommandSender;

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
