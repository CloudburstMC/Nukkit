package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.command.MessageRecipient;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.message.Message;

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
