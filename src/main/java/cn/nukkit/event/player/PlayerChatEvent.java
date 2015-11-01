package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.permission.Permissible;

import java.util.Set;

public class PlayerChatEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected String message;

    protected String format;

    protected Set<Permissible> recipients;

    public PlayerChatEvent(Player player, String message) {
        this(player, message, "chat.type.text", null);
    }

    public PlayerChatEvent(Player player, String message, String format, Set<Permissible> recipients) {
        this.player = player;
        this.message = message;

        this.format = format;

        if (recipients == null) {
            this.recipients = Server.getInstance().getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_USERS);
        } else {
            this.recipients = recipients;
        }
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Changes the player that is sending the message
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Set<Permissible> getRecipients() {
        return this.recipients;
    }

    public void setRecipients(Set<Permissible> recipients) {
        this.recipients = recipients;
    }
}
