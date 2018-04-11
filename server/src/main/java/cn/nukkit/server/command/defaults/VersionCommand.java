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

package cn.nukkit.server.command.defaults;

import cn.nukkit.api.command.CommandData;
import cn.nukkit.api.command.MessageRecipient;
import cn.nukkit.api.command.sender.CommandSender;
import cn.nukkit.api.command.sender.ConsoleCommandSender;
import cn.nukkit.api.command.simple.SimpleCommand;
import cn.nukkit.api.message.TranslationMessage;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.command.NukkitCommand;

public class VersionCommand extends NukkitCommand implements SimpleCommand {

    protected final NukkitServer server;
    private boolean forbidConsole;
    private int maxArgs;
    private int minArgs;

    public VersionCommand(NukkitServer server) {
        super("version",  CommandData.builder()
                .description("Shows the version of Nukkit that we are running")
                .permissionMessage("You do not have permission to execute this command.")
                .permission("nukkit.commands.version")
                .build());
        this.server = server;
        this.setForbidConsole(false);
        this.setMaxArgs(0);
        this.setMinArgs(0);
    }

    @Override
    public void setForbidConsole(boolean forbidConsole) {
        this.forbidConsole = forbidConsole;
    }

    @Override
    public void setMaxArgs(int maxArgs) {
        this.maxArgs = maxArgs;
    }

    @Override
    public void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

    @Override
    public void sendUsageMessage(MessageRecipient recipient) {
        data.getUsage().ifPresent(usage -> recipient.sendMessage(new TranslationMessage("commands.generic.usage", usage)));
    }

    @Override
    public void sendInGameMessage(MessageRecipient recipient) {
        recipient.sendMessage(new TranslationMessage("commands.generic.ingame"));
    }

    @Override
    public boolean onCommand(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }

        sender.sendMessage("Nukkit Version " + server.getNukkitVersion() + " implementing Minecraft Version " + server.getMinecraftVersion());
        return true;
    }

}
