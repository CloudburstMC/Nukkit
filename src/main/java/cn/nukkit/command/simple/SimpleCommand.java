package cn.nukkit.command.simple;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Tee7even
 */
@Log4j2
public class SimpleCommand extends Command {
    private final Object object;
    private final Method method;
    private boolean forbidConsole;
    private int maxArgs;
    private int minArgs;

    public SimpleCommand(Object object, Method method, String name, String description, String usageMessage, String[] aliases, String permissions, List<CommandParameter[]> parameters) {
        super(CommandData.builder(name)
                .setDescription(description)
                .setUsageMessage(usageMessage)
                .setAliases(aliases)
                .setPermissions(permissions)
                .setParameters(parameters)
                .build());
        this.object = object;
        this.method = method;
    }

    public void setForbidConsole(boolean forbidConsole) {
        this.forbidConsole = forbidConsole;
    }

    public void setMaxArgs(int maxArgs) {
        this.maxArgs = maxArgs;
    }

    public void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

    public void sendInGameMessage(CommandSender sender) {
        sender.sendMessage(new TranslationContainer("commands.locate.fail.noplayer"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (this.forbidConsole && sender instanceof ConsoleCommandSender) {
            this.sendInGameMessage(sender);
            return true;
        } else if (!this.testPermission(sender)) {
            return true;
        } else if (this.maxArgs != 0 && args.length > this.maxArgs) {
            return false;
        } else if (this.minArgs != 0 && args.length < this.minArgs) {
            return false;
        }

        boolean success = false;

        try {
            success = (Boolean) this.method.invoke(this.object, sender, commandLabel, args);
        } catch (Exception exception) {
            log.throwing(Level.ERROR, exception);
        }
        return success;
    }
}
