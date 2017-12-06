package cn.nukkit.server.command;

import cn.nukkit.api.message.Message;

/**
 * Represents an RCON command sender.
 *
 * @author Tee7even
 */
public class RemoteConsoleCommandSender extends ConsoleCommandSender {
    private final StringBuilder messages = new StringBuilder();

    @Override
    public void sendMessage(String message) {
        message = this.getServer().getLanguage().translateString(message);
        this.messages.append(message.trim()).append("\n");
    }

    @Override
    public void sendMessage(Message message) {
        this.sendMessage(this.getServer().getLanguage().translate(message));
    }

    public String getMessages() {
        return messages.toString();
    }

    @Override
    public String getName() {
        return "Rcon";
    }
}
