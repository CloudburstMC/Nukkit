package cn.nukkit.event.player;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.player.Player;

import java.net.InetSocketAddress;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PlayerCreationEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final SourceInterface interfaz;

    private final Long clientId;

    private final InetSocketAddress socketAddress;

    private Class<? extends Player> baseClass;

    private Class<? extends Player> playerClass;

    public PlayerCreationEvent(SourceInterface interfaz, Class<? extends Player> baseClass, Class<? extends Player> playerClass, Long clientId, InetSocketAddress socketAddress) {
        this.interfaz = interfaz;
        this.clientId = clientId;
        this.socketAddress = socketAddress;

        this.baseClass = baseClass;
        this.playerClass = playerClass;
    }

    public SourceInterface getInterface() {
        return interfaz;
    }

    public String getAddress() {
        return this.socketAddress.getAddress().toString();
    }

    public int getPort() {
        return this.socketAddress.getPort();
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
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
