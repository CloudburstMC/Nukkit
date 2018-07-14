package com.nukkitx.api.event.server;

import com.nukkitx.api.Player;
import com.nukkitx.api.Server;
import com.nukkitx.api.plugin.PluginContainer;
import com.nukkitx.api.util.GameMode;

import java.util.Collection;

/**
 * Query information sent to server lists, not the player's server list.
 */
public class RefreshQueryEvent implements ServerEvent {
    private final Collection<PluginContainer> plugins;
    private final Collection<Player> players;
    private GameMode gameMode;
    private int timeout;
    private String serverName;
    private String version;
    private boolean whitelist;
    private boolean pluginListEnabled;
    private String map;
    private int numPlayers;
    private int maxPlayers;

    public RefreshQueryEvent(Server server) {
        this(server, 30);
    }

    public RefreshQueryEvent(Server server, int timeout) {
        this.timeout = timeout;
        serverName = server.getConfiguration().getGeneral().getMotd();
        pluginListEnabled = server.getConfiguration().getNetwork().isQueryingPlugins();
        plugins = server.getPluginManager().getAllPlugins();
        players = server.getOnlinePlayers();
        gameMode = server.getConfiguration().getMechanics().getDefaultGamemode();
        version = server.getMinecraftVersion().toString();
        map = server.getDefaultLevel().getData().getName();
        numPlayers = players.size();
        maxPlayers = server.getConfiguration().getGeneral().getMaximumPlayers();
        whitelist = server.getConfiguration().getGeneral().isWhitelisted();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public boolean isPluginListEnabled() {
        return pluginListEnabled;
    }

    public void setPluginListEnabled(boolean pluginListEnabled) {
        this.pluginListEnabled = pluginListEnabled;
    }

    public Collection<PluginContainer> getPlugins() {
        return plugins;
    }

    public Collection<Player> getPlayers() {
        return players;
    }

    public int getPlayerCount() {
        return numPlayers;
    }

    public void setPlayerCount(int count) {
        this.numPlayers = count;
    }

    public int getMaxPlayerCount() {
        return maxPlayers;
    }

    public void setMaxPlayerCount(int count) {
        this.maxPlayers = count;
    }

    public String getWorld() {
        return map;
    }

    public void setWorld(String world) {
        this.map = world;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public boolean isWhitelisted() {
        return whitelist;
    }

    public void setWhitelisted(boolean whitelisted) {
        this.whitelist = whitelisted;
    }
}
