package com.nukkitx.server.command;

import com.nukkitx.api.command.CommandData;
import com.nukkitx.api.command.MessageRecipient;
import com.nukkitx.api.command.sender.CommandSender;
import com.nukkitx.api.command.sender.ConsoleCommandSender;
import com.nukkitx.api.command.simple.SimpleCommand;
import com.nukkitx.api.message.TranslationMessage;
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
