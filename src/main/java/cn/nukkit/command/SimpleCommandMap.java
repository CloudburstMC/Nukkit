package cn.nukkit.command;

import cn.nukkit.Server;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.command.simple.*;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class SimpleCommandMap {
    protected final Map<String, Command> knownCommands = new HashMap<>();
    protected final Map<String, Command> registeredCommands = new HashMap<>();

    private final Server server;

    public SimpleCommandMap(Server server) {
        this.server = server;

    }

    public void registerSimpleCommands(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            cn.nukkit.command.simple.Command def = method.getAnnotation(cn.nukkit.command.simple.Command.class);
            if (def != null) {
                SimpleCommand sc = new SimpleCommand(object, method, def.name(), def.description(), def.usageMessage(), def.aliases());

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

                CommandParameters commandParameters = method.getAnnotation(CommandParameters.class);
                if (commandParameters != null) {
                    Collection<CommandParameter[]> map = Arrays.stream(commandParameters.parameters())
                            .collect(Collectors.toMap(Parameters::name, parameters -> Arrays.stream(parameters.parameters())
                                    .map(parameter -> new CommandParameter(parameter.name(), parameter.type(), parameter.optional()))
                                    .distinct()
                                    .toArray(CommandParameter[]::new))).values();

                    sc.commandParameters.addAll(map);
                }

               // this.register(def.name(), sc);
            }
        }
    }

    private boolean registerAlias(Command command, boolean isAlias, String fallbackPrefix, String label) {
        this.knownCommands.put(fallbackPrefix + ":" + label, command);

        //if you're registering a command alias that is already registered, then return false
        boolean alreadyRegistered = this.knownCommands.containsKey(label);
        Command existingCommand = this.knownCommands.get(label);
        boolean existingCommandIsNotVanilla = alreadyRegistered && !(existingCommand instanceof VanillaCommand);
        //basically, if we're an alias and it's already registered, or we're a vanilla command, then we can't override it
        if ((command instanceof VanillaCommand || isAlias) && alreadyRegistered && existingCommandIsNotVanilla) {
            return false;
        }

        //if you're registering a name (alias or label) which is identical to another command who's primary name is the same
        //so basically we can't override the main name of a command, but we can override aliases if we're not an alias

        //added the last statement which will allow us to override a VanillaCommand unconditionally
        if (alreadyRegistered && existingCommand.getLabel() != null && existingCommand.getLabel().equals(label) && existingCommandIsNotVanilla) {
            return false;
        }

        //you can now assume that the command is either uniquely named, or overriding another command's alias (and is not itself, an alias)

        if (!isAlias) {
            command.setLabel(label);
        }

        // Then we need to check if there isn't any command conflicts with vanilla commands
        ArrayList<String> toRemove = new ArrayList<>();

        for (Entry<String, Command> entry : knownCommands.entrySet()) {
            Command cmd = entry.getValue();
            if (cmd.getLabel().equalsIgnoreCase(command.getLabel()) && !cmd.equals(command)) { // If the new command conflicts... (But if it isn't the same command)
                if (cmd instanceof VanillaCommand) { // And if the old command is a vanilla command...
                    // Remove it!
                    toRemove.add(entry.getKey());
                }
            }
        }

        // Now we loop the toRemove list to remove the command conflicts from the knownCommands map
        for (String cmd : toRemove) {
            knownCommands.remove(cmd);
        }

        this.knownCommands.put(label, command);

        return true;
    }

}