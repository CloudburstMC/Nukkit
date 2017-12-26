package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.message.GenericMessage;
import cn.nukkit.api.message.Message;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerKickEvent extends PlayerEvent implements Cancellable {

    protected final Reason reason;
    protected final String reasonString;
    protected Message quitMessage;
    private boolean cancelled;

    public PlayerKickEvent(Player player, Reason reason, String quitMessage) {
        this(player, reason, new GenericMessage(quitMessage));
    }

    public PlayerKickEvent(Player player, Reason reason, Message quitMessage) {
        this(player, reason, reason.toString(), quitMessage);
    }

    public PlayerKickEvent(Player player, Reason reason, String reasonString, Message quitMessage) {
        super(player);
        this.quitMessage = quitMessage;
        this.reason = reason;
        this.reasonString = reason.name();
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
