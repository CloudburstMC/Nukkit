package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.message.GenericMessage;
import cn.nukkit.api.message.Message;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerQuitEvent extends PlayerEvent {

    protected final String reason;
    protected Message quitMessage;
    protected boolean autoSave = true;

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
}
