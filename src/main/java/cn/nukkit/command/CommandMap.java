package cn.nukkit.command;

import cn.nukkit.plugin.Plugin;

import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface CommandMap {

    default void registerAll(Plugin plugin, List<? extends Command> commands) {
        for (Command command : commands) {
            this.register(plugin, command);
        }
    }

    default boolean register(Plugin plugin, Command command) {
        return register(plugin, command, null);
    }

    boolean register(Plugin plugin, Command command, String label);

    void registerSimpleCommands(Object object);

    boolean dispatch(CommandSender sender, String cmdLine);

    void clearCommands();

    Command getCommand(String name);

}
