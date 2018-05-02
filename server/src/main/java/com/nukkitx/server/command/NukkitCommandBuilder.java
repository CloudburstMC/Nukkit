package com.nukkitx.server.command;

import com.google.common.base.Preconditions;
import com.nukkitx.api.command.Command;
import com.nukkitx.api.command.CommandBuilder;
import com.nukkitx.api.command.CommandData;
import com.nukkitx.api.command.CommandExecutor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class NukkitCommandBuilder implements CommandBuilder {
    private final Set<String> aliases;
    private CommandExecutor executor;
    private String command;
    private String usageMessage;
    private String description;
    private String permission;
    private String permissionMessage;

    public NukkitCommandBuilder() {
        aliases = new HashSet<>();
    }

    public NukkitCommandBuilder(NukkitCommand command) {
        CommandData data = command.getData();
        this.aliases = new HashSet<>(data.getAliases());
        this.executor = command.getExecutor();
        this.command = command.getName();
        if (data.getUsage().isPresent()) {
            this.usageMessage = data.getUsage().get();
        }
        if (data.getDescription().isPresent()) {
            this.description = data.getDescription().get();
        }
        if (data.getPermission().isPresent()) {
            this.permission = data.getPermission().get();
        }
        if (data.getPermissionMessage().isPresent()) {
            this.permissionMessage = data.getPermissionMessage().get();
        }
    }

    @Override
    public CommandBuilder setExecutor(CommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public CommandBuilder setCommand(String command) {
        this.command = command;
        return this;
    }

    @Override
    public CommandBuilder setAliases(Collection<String> aliases) {
        this.aliases.addAll(aliases);
        return this;
    }

    @Override
    public CommandBuilder setAlias(String alias) {
        aliases.add(alias);
        return this;
    }

    @Override
    public CommandBuilder clearAliases() {
        aliases.clear();
        return this;
    }

    @Override
    public CommandBuilder removeAlias(String alias) {
        aliases.remove(alias);
        return this;
    }

    @Override
    public CommandBuilder removeAliases(Collection<String> aliases) {
        this.aliases.removeAll(aliases);
        return this;
    }

    @Override
    public CommandBuilder setUsageMessage(String usageMessage) {
        this.usageMessage = usageMessage;
        return this;
    }

    @Override
    public CommandBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public CommandBuilder setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    @Override
    public Command build() {
        Preconditions.checkArgument(executor != null, "Executor cannot be null");
        Preconditions.checkArgument(command != null && !command.trim().isEmpty() && !command.contains("\0"), "Null, empty or invalid command name");

        CommandData data = new CommandData(permission, aliases, description, usageMessage, permissionMessage);
        return new NukkitCommand(command, executor, data);
    }
}
