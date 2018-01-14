package cn.nukkit.api.event.player;

import cn.nukkit.api.Session;
import cn.nukkit.api.event.Event;
import lombok.Getter;

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

    @Getter
    public enum Result {
        BANNED("disconnectionScreen.notAllowed"),
        NOT_WHITELISTED("disconnectionScreen.notAllowed");

        private final String i18n;

        Result(String i18n) {
            this.i18n = i18n;
        }
    }
}
