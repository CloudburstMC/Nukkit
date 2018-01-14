package cn.nukkit.server.command;

import cn.nukkit.api.command.CommandExecutor;
import cn.nukkit.api.command.CommandManager;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NukkitCommandManager implements CommandManager {
    private final Map<String, CommandExecutor> commandMap = new ConcurrentHashMap<>();

    @Override
    public void register(@Nonnull String command, @Nonnull CommandExecutor executor) {
        Preconditions.checkNotNull(command, "command");
        Preconditions.checkNotNull(executor, "executor");
        commandMap.put(command, executor);
    }

    public <T> void registerCommands(T object) throws Exception {

    }
}
