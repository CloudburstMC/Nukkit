package cn.nukkit.server.console;

import cn.nukkit.api.command.sender.ConsoleCommandSender;
import cn.nukkit.api.message.Message;
import cn.nukkit.server.NukkitServer;
import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;

@Log4j2
public class NukkitConsoleCommandSender implements ConsoleCommandSender {
    private final NukkitServer server;

    public NukkitConsoleCommandSender(NukkitServer server) {
        this.server = server;
    }

    @Override
    public void sendMessage(@Nonnull String text) {
        Preconditions.checkNotNull(text, "text");
        log.info(text);
    }

    @Override
    public void sendMessage(@Nonnull Message text) {
        Preconditions.checkNotNull(text, "text");
        log.info(text.getMessage());
    }

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public NukkitServer getServer() {
        return server;
    }
}
