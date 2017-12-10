package cn.nukkit.server.command;

import cn.nukkit.api.command.Command;
import cn.nukkit.api.command.CommandExecutorSource;

import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface CommandMap {

    void registerAll(String fallbackPrefix, List<? extends Command> commands);

    boolean register(String fallbackPrefix, Command command);

    boolean register(String fallbackPrefix, Command command, String label);

    <T> void registerSimpleCommands(T object) throws Exception;

    boolean dispatch(CommandExecutorSource sender, String cmdLine);

    void clearCommands();

    Command getCommand(String name);

}
