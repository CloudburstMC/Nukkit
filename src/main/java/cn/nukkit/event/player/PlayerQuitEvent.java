package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.TextContainer;

public class PlayerQuitEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected TextContainer quitMessage;
    protected boolean autoSave = true;

    public PlayerQuitEvent(Player player, TextContainer quitMessage) {
        this(player, quitMessage, true);
    }

    public PlayerQuitEvent(Player player, String quitMessage) {
        this(player, quitMessage, true);
    }

    public PlayerQuitEvent(Player player, String quitMessage, boolean autoSave) {
        this(player, new TextContainer(quitMessage), autoSave);
    }

    public PlayerQuitEvent(Player player, TextContainer quitMessage, boolean autoSave) {
        this.player = player;
        this.quitMessage = quitMessage;
        this.autoSave = autoSave;
    }

    public TextContainer getQuitMessage() {
        return quitMessage;
    }

    public void setQuitMessage(TextContainer quitMessage) {
        this.quitMessage = quitMessage;
    }

    public void setQuitMessage(String joinMessage) {
        this.setQuitMessage(new TextContainer(joinMessage));
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
}
