package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.message.Message;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;

public class PlayerKickEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

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

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected Message quitMessage;

    protected final Reason reason;
    protected final String reasonString;

    @Deprecated
    public PlayerKickEvent(Player player, String reason, String quitMessage) {
        this(player, Reason.UNKNOWN, reason, new Message(quitMessage));
    }

    @Deprecated
    public PlayerKickEvent(Player player, String reason, Message quitMessage) {
        this(player, Reason.UNKNOWN, reason, quitMessage);
    }

    public PlayerKickEvent(Player player, Reason reason, Message quitMessage) {
        this(player, reason, reason.toString(), quitMessage);
    }

    public PlayerKickEvent(Player player, Reason reason, String quitMessage) {
        this(player, reason, new Message(quitMessage));
    }

    public PlayerKickEvent(Player player, Reason reason, String reasonString, Message quitMessage) {
        this.player = player;
        this.quitMessage = quitMessage;
        this.reason = reason;
        this.reasonString = reason.name();
    }

    public String getReason() {
        return reasonString;
    }

    public Reason getReasonEnum() {
        return this.reason;
    }

    public Message getQuitMessage() {
        return quitMessage;
    }

    public void setQuitMessage(Message quitMessage) {
        this.quitMessage = quitMessage;
    }

    public void setQuitMessage(String joinMessage) {
        this.setQuitMessage(new Message(joinMessage));
    }
}
