package cn.nukkit.server.console;

import cn.nukkit.api.Server;
import cn.nukkit.api.command.ConsoleCommandExecutorSource;
import cn.nukkit.api.message.Message;

public class NukkitConsoleCommandExecutorSource implements ConsoleCommandExecutorSource{
    @Override
    public void sendMessage(String text) {

    }

    @Override
    public void sendMessage(Message text) {

    }

    @Override
    public Server getServer() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }
}
