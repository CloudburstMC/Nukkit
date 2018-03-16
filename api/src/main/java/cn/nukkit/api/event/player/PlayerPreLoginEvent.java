package cn.nukkit.api.event.player;

import cn.nukkit.api.Session;
import cn.nukkit.api.event.Event;
import cn.nukkit.api.util.data.DisconnectMessage;

import javax.annotation.Nullable;

public class PlayerPreLoginEvent implements Event {
    private final Session session;
    private Result result;
    private String disconnectMessage = null;

    public PlayerPreLoginEvent(Session session, Result result) {
        this.session = session;
        this.result = result;
    }

    public boolean willDisconnect() {
        return result != null;
    }

    public String getDisconnectMessage() {
        return disconnectMessage;
    }

    public void setDisconnectMessage(String disconnectMessage) {
        this.disconnectMessage = disconnectMessage;
    }

    @Nullable
    public Result getResult() {
        return result;
    }

    public void setResult(@Nullable Result result) {
        this.result = result;
    }

    public Session getSession() {
        return session;
    }

    public enum Result {
        DISCONNECTED(DisconnectMessage.NO_REASON),
        BANNED(DisconnectMessage.KICKED),
        NOT_WHITELISTED(DisconnectMessage.NOT_ALLOWED);

        private final DisconnectMessage message;

        Result(DisconnectMessage message) {
            this.message = message;
        }

        public DisconnectMessage getDisconnectMessage() {
            return message;
        }
    }
}
