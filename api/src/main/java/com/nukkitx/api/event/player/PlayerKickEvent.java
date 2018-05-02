package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.message.ChatMessage;
import com.nukkitx.api.message.Message;

public class PlayerKickEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final Reason reason;
    private final String reasonString;
    private Message quitMessage;
    private boolean cancelled;

    public PlayerKickEvent(Player player, Reason reason, String quitMessage) {
        this(player, reason, new ChatMessage(quitMessage));
    }

    public PlayerKickEvent(Player player, Reason reason, Message quitMessage) {
        this(player, reason, reason.toString(), quitMessage);
    }

    public PlayerKickEvent(Player player, Reason reason, String reasonString, Message quitMessage) {
        this.player = player;
        this.quitMessage = quitMessage;
        this.reason = reason;
        this.reasonString = reason.name();
    }

    public Reason getReason() {
        return reason;
    }

    public Message getQuitMessage() {
        return quitMessage;
    }

    public void setQuitMessage(Message quitMessage) {
        this.quitMessage = quitMessage;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public String getReasonString() {
        return reasonString;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public enum Reason {
        NEW_CONNECTION,
        KICKED_BY_ADMIN,
        NOT_WHITELISTED,
        IP_BANNED,
        NAME_BANNED,
        INVALID_PVE,
        LOGIN_TIMEOUT,
        SERVER_FULL,
        FLYING_DISABLED,
        UNKNOWN;

        @Override
        public String toString() {
            return this.name();
        }
    }
}
