package cn.nukkit.server.network.rcon;

import cn.nukkit.api.command.sender.RemoteConsoleCommandSender;
import cn.nukkit.api.message.Message;
import cn.nukkit.server.NukkitServer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RconConsoleCommandSender implements RemoteConsoleCommandSender {
    private final NukkitServer server;
    private final Queue<String> messageQueue = new ConcurrentLinkedQueue<>();


    public RconConsoleCommandSender(NukkitServer server) {
        this.server = server;
    }

    @Override
    public void sendMessage(@Nonnull String text) {
        messageQueue.add(text);
    }

    @Override
    public void sendMessage(@Nonnull Message text) {
        sendMessage(server.getLocaleManager().getMessage(text));
    }

    @Override
    public String getName() {
        return "RCON";
    }

    @Override
    public NukkitServer getServer() {
        return server;
    }

    public List<String> getMessages() {
        List<String> messages = new ArrayList<>();
        String message;
        while ((message = messageQueue.poll()) != null) {
            messages.add(message);
        }
        return messages;
    }
}
