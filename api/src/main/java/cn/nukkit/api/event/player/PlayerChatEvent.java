package cn.nukkit.api.event.player;

import cn.nukkit.api.MessageRecipient;
import cn.nukkit.api.Player;
import cn.nukkit.api.Server;
import cn.nukkit.api.event.Cancellable;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerChatEvent extends PlayerMessageEvent implements Cancellable {

    protected String format;
    private Set<MessageRecipient> recipients = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private boolean cancelled;

    public PlayerChatEvent(Player player, String message) {
        this(player.getServer(), player, message, "chat.type.text", null);
    }

    public PlayerChatEvent(Server server, Player player, String message, String format, Set<MessageRecipient> recipients) {
        super(player, message);
        this.format = format;
        this.recipients.addAll(recipients);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
