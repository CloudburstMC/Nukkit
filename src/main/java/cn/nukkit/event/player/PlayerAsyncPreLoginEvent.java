package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;

import java.util.UUID;

/**
 * This event is called asynchronously
 *
 * @author CreeperFace
 */
public class PlayerAsyncPreLoginEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final String name;
    private final UUID uuid;
    private final String address;
    private final int port;

    private LoginResult loginResult = LoginResult.SUCCESS;
    private String kickMessage = "Plugin Reason";

    public PlayerAsyncPreLoginEvent(Player player, String name, UUID uuid, String address, int port) {
        this.player = player;
        this.name = name;
        this.uuid = uuid;
        this.address = address;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public LoginResult getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(LoginResult loginResult) {
        this.loginResult = loginResult;
    }

    public String getKickMessage() {
        return kickMessage;
    }

    public void setKickMessage(String kickMessage) {
        this.kickMessage = kickMessage;
    }

    public void allow() {
        this.loginResult = LoginResult.SUCCESS;
    }

    public void disAllow(String message) {
        this.loginResult = LoginResult.KICK;
        this.kickMessage = message;
    }

    public enum LoginResult {
        SUCCESS,
        KICK
    }
}
