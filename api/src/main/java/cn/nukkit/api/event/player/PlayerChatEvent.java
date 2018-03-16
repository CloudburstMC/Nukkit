package cn.nukkit.api.event.player;

import cn.nukkit.api.MessageRecipient;
import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.message.Message;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerChatEvent extends PlayerMessageEvent implements Cancellable {
    protected String format;
    private Set<MessageRecipient> recipients = Collections.newSetFromMap(new ConcurrentHashMap<>()); //TODO
    private boolean cancelled;

    public PlayerChatEvent(Player player, Message message) {
        super(player, message);
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
