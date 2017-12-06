package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.message.GenericMessage;
import cn.nukkit.api.message.Message;

public class PlayerQuitEvent extends PlayerEvent {
    protected Message quitMessage;
    protected boolean autoSave = true;
    protected String reason;

    public PlayerQuitEvent(Player player, Message quitMessage, String reason) {
        this(player, quitMessage, true, reason);
    }

    public PlayerQuitEvent(Player player, Message quitMessage) {
        this(player, quitMessage, true);
    }

    public PlayerQuitEvent(Player player, String quitMessage, String reason) {
        this(player, quitMessage, true, reason);
    }

    public PlayerQuitEvent(Player player, String quitMessage) {
        this(player, quitMessage, true);
    }

    public PlayerQuitEvent(Player player, String quitMessage, boolean autoSave, String reason) {
        this(player, new GenericMessage(quitMessage), autoSave, reason);
    }

    public PlayerQuitEvent(Player player, String quitMessage, boolean autoSave) {
        this(player, new GenericMessage(quitMessage), autoSave);
    }

    public PlayerQuitEvent(Player player, Message quitMessage, boolean autoSave) {
        this(player, quitMessage, autoSave, "No reason");
    }

    public PlayerQuitEvent(Player player, Message quitMessage, boolean autoSave, String reason) {
        super(player);
        this.quitMessage = quitMessage;
        this.autoSave = autoSave;
        this.reason = reason;
    }

    public Message getQuitMessage() {
        return quitMessage;
    }

    public void setQuitMessage(Message quitMessage) {
        this.quitMessage = quitMessage;
    }

    public void setQuitMessage(String quitMessage) {
        this.setQuitMessage(new Message(quitMessage));
    }

    public boolean getAutoSave() {
        return this.autoSave;
    }

    public void setAutoSave() {
        this.setAutoSave(true);
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public String getReason() {
        return reason;
    }
}
