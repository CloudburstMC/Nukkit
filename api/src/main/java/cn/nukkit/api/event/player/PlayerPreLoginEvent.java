package cn.nukkit.api.event.player;

import cn.nukkit.api.Session;
import cn.nukkit.api.event.Event;

public class PlayerPreLoginEvent implements Event {
    private final Session session;
    private Result result;
    private String disconnectMessage;

    public PlayerPreLoginEvent(Session session, Result result) {
        this.session = session;
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

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Session getSession() {
        return session;
    }

    public enum Result {
        BANNED,
        NOT_WHITELISTED
    }
}
