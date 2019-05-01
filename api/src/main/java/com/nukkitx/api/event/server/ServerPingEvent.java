package com.nukkitx.api.event.server;

import com.nukkitx.api.Server;
import com.nukkitx.api.util.GameMode;

import java.net.InetSocketAddress;

/**
 * Ping sent to player's on their server list screen before joining a server.
 */
public class ServerPingEvent implements ServerEvent {
    private final InetSocketAddress address;
    private String motd;
    private String subMotd;
    private int maxPlayerCount;
    private int playerCount;
    private GameMode defaultGamemode;

    public ServerPingEvent(InetSocketAddress address, Server server) {
        this.address = address;
        this.motd = server.getConfiguration().getGeneral().getMotd();
        this.subMotd = server.getConfiguration().getGeneral().getSubMotd();
        this.maxPlayerCount = server.getConfiguration().getGeneral().getMaximumPlayers();
        this.playerCount = server.getOnlinePlayers().size();
        this.defaultGamemode = server.getConfiguration().getMechanics().getDefaultGamemode();
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public String getMotd(){
        return this.motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public String getSubMotd() {
        return this.subMotd;
    }

    public void setSubMotd(String subMotd) {
        this.subMotd = subMotd;
    }

    public int getMaxPlayerCount() {
        return this.maxPlayerCount;
    }

    public void setMaxPlayerCount(int maxPlayerCount) {
        this.maxPlayerCount = maxPlayerCount;
    }

    public int getPlayerCount() {
        return this.playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public GameMode getDefaultGamemode() {
        return this.defaultGamemode;
    }

    public void setDefaultGamemode(GameMode defaultGamemode) {
        this.defaultGamemode = defaultGamemode;
    }
}
