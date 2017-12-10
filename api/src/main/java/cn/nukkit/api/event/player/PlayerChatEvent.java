package cn.nukkit.api.event.player;

import cn.nukkit.api.MessageRecipient;
import cn.nukkit.api.Player;
import cn.nukkit.api.Server;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.permission.Permissible;
import cn.nukkit.server.NukkitServer;

import java.util.HashSet;
import java.util.Set;

public class PlayerChatEvent extends PlayerMessageEvent implements Cancellable {

    protected String format;

    protected Set<MessageRecipient> recipients = new HashSet<>();

    public PlayerChatEvent(Player player, String message) {
        this(player, message, "chat.type.text", null);
    }

    public PlayerChatEvent(Server server, Player player, String message, String format, Set<MessageRecipient> recipients) {
        this.player = player;
        this.message = message;

        this.format = format;

        if (recipients == null) {
            for (Permissible permissible : server.getPluginManager().getPermissionSubscriptions(NukkitServer.BROADCAST_CHANNEL_USERS)) {
                if (permissible instanceof MessageRecipient) {
                    this.recipients.add((MessageRecipient) permissible);
                }
            }
        } else {
            this.recipients = recipients;
        }
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

    public Set<MessageRecipient> getRecipients() {
        return this.recipients;
    }
}
