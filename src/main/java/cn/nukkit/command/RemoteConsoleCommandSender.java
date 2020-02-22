package cn.nukkit.command;

import cn.nukkit.locale.TextContainer;

/**
 * Represents an RCON command sender.
 *
 * @author Tee7even
 */
public class RemoteConsoleCommandSender extends ConsoleCommandSender {
    private final StringBuilder messages = new StringBuilder();

    @Override
    public void sendMessage(String message) {
        message = this.getServer().getLanguage().translate(message);
        this.messages.append(message.trim()).append("\n");
    }

    @Override
    public void sendMessage(TextContainer message) {
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
