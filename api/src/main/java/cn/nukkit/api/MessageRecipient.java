package cn.nukkit.api;

import cn.nukkit.api.message.Message;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * This interface specifies a recipient that can receive messages from a plugin.
 */
@ParametersAreNonnullByDefault
public interface MessageRecipient {
    /**
     * Sends a message to the recipient. For players, the text will be sent to the client, and for the console, the output
     * will be sent to the console.
     * @param text the text to send
     */
    void sendMessage(String text);

    void sendMessage(Message text);
}