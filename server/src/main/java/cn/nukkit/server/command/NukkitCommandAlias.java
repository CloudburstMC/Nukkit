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
    public void execute(CommandSender sender, String label, String[] args) {
        command.execute(sender, label, args);
    }
}
