package cn.nukkit.server.command.simple;

import cn.nukkit.api.MessageRecipient;
import cn.nukkit.api.command.CommandExecutorSource;
import cn.nukkit.api.command.simple.SimpleCommand;
import cn.nukkit.api.command.source.ConsoleCommandExecutorSource;
import cn.nukkit.api.message.TranslatedMessage;
import cn.nukkit.server.command.NukkitCommand;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;

/**
 * @author Tee7even
 */
@Log4j2
public class NukkitSimpleCommand extends NukkitCommand implements SimpleCommand {
    private Object object;
    private Method method;
    private boolean forbidConsole;
    private int maxArgs;
    private int minArgs;

    public NukkitSimpleCommand(Object object, Method method, String name, String description, String usageMessage, String[] aliases) {
        super(name, description, usageMessage, aliases);
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
        if (!this.usageMessage.equals("")) {
            recipient.sendMessage(new TranslatedMessage("commands.generic.usage", this.usageMessage));
        }
    }

    @Override
    public void sendInGameMessage(MessageRecipient recipient) {
        recipient.sendMessage(new TranslatedMessage("commands.generic.ingame"));
    }

    @Override
    public boolean execute(CommandExecutorSource sender, String commandLabel, String[] args) {
        boolean canReceiveMessage = (sender instanceof MessageRecipient);

        if (forbidConsole && sender instanceof ConsoleCommandExecutorSource) {
            sendInGameMessage((ConsoleCommandExecutorSource) sender);
            return false;
        } else if (!testPermission(sender)) {
            return false;
        } else if (maxArgs != 0 && args.length > maxArgs) {
            if (canReceiveMessage) {
                sendUsageMessage((MessageRecipient) sender);
            }
            return false;
        } else if (minArgs != 0 && args.length < minArgs) {
            if (canReceiveMessage) {
                sendUsageMessage((MessageRecipient) sender);
            }
            return false;
        }

        boolean success = false;

        try {
            success = (Boolean) method.invoke(object, sender, commandLabel, args);
        } catch (Exception e) {
            log.throwing(e);
        }

        if (!success && canReceiveMessage) {
            sendUsageMessage((MessageRecipient) sender);
        }

        return success;
    }
}
