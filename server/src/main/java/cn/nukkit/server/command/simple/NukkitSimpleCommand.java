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

package cn.nukkit.server.command.simple;

import cn.nukkit.api.command.CommandData;
import cn.nukkit.api.command.MessageRecipient;
import cn.nukkit.api.command.sender.CommandSender;
import cn.nukkit.api.command.sender.ConsoleCommandSender;
import cn.nukkit.api.command.simple.SimpleCommand;
import cn.nukkit.api.message.TranslationMessage;
import cn.nukkit.server.command.NukkitCommand;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;

@Log4j2
public class NukkitSimpleCommand extends NukkitCommand implements SimpleCommand {
    private Object object;
    private Method method;
    private boolean forbidConsole;
    private int maxArgs;
    private int minArgs;

    public NukkitSimpleCommand(Object object, Method method, String name, CommandData data) {
        super(name, data);
        this.object = object;
        this.method = method;
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

        if (forbidConsole && sender instanceof ConsoleCommandSender) {
            sendInGameMessage(sender);
            return false;
        } else if (!testPermission(sender)) {
            return false;
        } else if (maxArgs != 0 && args.length > maxArgs) {
            sendUsageMessage(sender);
            return false;
        } else if (minArgs != 0 && args.length < minArgs) {
            sendUsageMessage(sender);
            return false;
        }

        boolean success = false;

        try {
            success = (boolean) method.invoke(object, sender, commandLabel, args);
        } catch (Exception e) {
            log.throwing(e);
        }

        if (!success) {
            sendUsageMessage(sender);
        }

        return success;
    }
}
