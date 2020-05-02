package cn.nukkit.command.simple;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandFactory;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

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

    public SimpleCommand(Object object, Method method, String name, String description, String usageMessage, String[] aliases) {
        super(name, description, usageMessage, aliases);
        this.object = object;
        this.method = method;
    }

    public static CommandFactory factory(Object object, Method method, String desc, String usage, String[] aliases) {
        return name -> {
            SimpleCommand sc = new SimpleCommand(object, method, name, desc, usage, aliases);
            Arguments args = method.getAnnotation(Arguments.class);
            if (args != null) {
                sc.setMaxArgs(args.max());
                sc.setMinArgs(args.min());
            }

            CommandPermission perm = method.getAnnotation(CommandPermission.class);
            if (perm != null) {
                sc.setPermission(perm.value());
            }

            if (method.isAnnotationPresent(ForbidConsole.class)) {
                sc.setForbidConsole(true);
            }

            CommandParameters params = method.getAnnotation(CommandParameters.class);
            if (params != null) {
                Collection<CommandParameter[]> map = Arrays.stream(params.parameters())
                        .collect(Collectors.toMap(Parameters::name, parameters -> Arrays.stream(parameters.parameters())
                                .map(parameter -> new CommandParameter(parameter.name(), parameter.type(), parameter.optional()))
                                .distinct()
                                .toArray(CommandParameter[]::new))).values();
                sc.commandParameters.addAll(map);
            }
            return sc;
        };
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

    public void sendUsageMessage(CommandSender sender) {
        if (!this.usageMessage.equals("")) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
        }
    }

    public void sendInGameMessage(CommandSender sender) {
        sender.sendMessage(new TranslationContainer("commands.locate.fail.noplayer"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (this.forbidConsole && sender instanceof ConsoleCommandSender) {
            this.sendInGameMessage(sender);
            return false;
        } else if (!this.testPermission(sender)) {
            return false;
        } else if (this.maxArgs != 0 && args.length > this.maxArgs) {
            this.sendUsageMessage(sender);
            return false;
        } else if (this.minArgs != 0 && args.length < this.minArgs) {
            this.sendUsageMessage(sender);
            return false;
        }

        boolean success = false;

        try {
            success = (Boolean) this.method.invoke(this.object, sender, commandLabel, args);
        } catch (Exception exception) {
            log.throwing(Level.ERROR, exception);
        }

        if (!success) {
            this.sendUsageMessage(sender);
        }

        return success;
    }
}
