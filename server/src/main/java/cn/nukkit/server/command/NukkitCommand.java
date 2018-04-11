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
import cn.nukkit.api.command.CommandData;
import cn.nukkit.api.command.CommandExecutor;
import cn.nukkit.api.command.sender.CommandSender;
import cn.nukkit.api.message.TranslationMessage;
import cn.nukkit.api.util.TextFormat;
import com.google.common.base.Preconditions;

import java.util.Optional;

public class NukkitCommand implements Command {
    private final String name;
    private final CommandExecutor executor;
    protected final CommandData data;

    public NukkitCommand(String name, CommandExecutor executor, CommandData data) {
        this.name = Preconditions.checkNotNull(name, "name");
        this.executor = Preconditions.checkNotNull(executor, "executor");
        this.data = Preconditions.checkNotNull(data, "data");
    }

    protected NukkitCommand(String name, CommandData data) {
        Preconditions.checkArgument(this instanceof CommandExecutor, "Command must implement CommandExecutor");
        this.executor = (CommandExecutor) this;
        this.name = Preconditions.checkNotNull(name, "name");
        this.data = Preconditions.checkNotNull(data, "data");
    }

    @Override
    public CommandExecutor getExecutor() {
        return executor;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CommandData getData() {
        return data;
    }

    public boolean testPermission(CommandSender target) {
        if (this.testPermissionSilent(target)) {
            return true;
        }

        Optional<String> permissionMessage = data.getPermissionMessage();
        if (!permissionMessage.isPresent()) {
            target.sendMessage(new TranslationMessage(TextFormat.RED + "%commands.generic.unknown", name));
        } else {
            data.getPermission().ifPresent(permission -> target.sendMessage(permissionMessage.get().replace("<permission>", permission)));
        }

        return false;
    }

    public boolean testPermissionSilent(CommandSender target) {
        if (!data.getPermission().isPresent()) return true;

        String[] permissions = data.getPermission().get().split(";");
        for (String permission : permissions) {
            if (target.hasPermission(permission)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        executor.onCommand(sender, label, args);
    }

    @Override
    public String toString() {
        return this.name;
    }

}
