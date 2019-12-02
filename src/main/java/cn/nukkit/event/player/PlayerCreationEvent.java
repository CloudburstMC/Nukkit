package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.network.SourceInterface;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PlayerCreationEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final SourceInterface interfaz;
    private final Long clientId;
    private final String address;
    private final int port;
    private Class<? extends Player> baseClass;
    private Class<? extends Player> playerClass;

    public PlayerCreationEvent(SourceInterface interfaz, Class<? extends Player> baseClass, Class<? extends Player> playerClass, Long clientId, String address, int port) {
        this.interfaz = interfaz;
        this.clientId = clientId;
        this.address = address;
        this.port = port;

        this.baseClass = baseClass;
        this.playerClass = playerClass;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public SourceInterface getInterface() {
        return interfaz;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public Long getClientId() {
        return clientId;
    }

    public Class<? extends Player> getBaseClass() {
        return baseClass;
    }

    public void setBaseClass(Class<? extends Player> baseClass) {
        this.baseClass = baseClass;
    }

    public Class<? extends Player> getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(Class<? extends Player> playerClass) {
        this.playerClass = playerClass;
    }
}
