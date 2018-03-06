package cn.nukkit.api.message;

/**
 * Message that contains the sender's name.
 */
public interface SenderMessage extends Message {
    /**
     * Get sender's name.
     *
     * @return name of sender
     */
    String getSender();
}
