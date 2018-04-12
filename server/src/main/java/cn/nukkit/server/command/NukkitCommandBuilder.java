/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.command;

import cn.nukkit.api.command.Command;
import cn.nukkit.api.command.CommandBuilder;
import cn.nukkit.api.command.CommandData;
import cn.nukkit.api.command.CommandExecutor;
import com.google.common.base.Preconditions;

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
