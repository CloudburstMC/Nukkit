package cn.nukkit.api.event.server;

import cn.nukkit.api.GameMode;
import cn.nukkit.api.Player;
import cn.nukkit.api.Server;
import cn.nukkit.api.plugin.PluginContainer;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Query information sent to server lists.
 */
public class QueryRegenerateEvent implements ServerEvent {
    private final Collection<PluginContainer> plugins;
    private final Collection<Player> players;
    private GameMode gameType;
    private int timeout;
    private String serverName;
    private String version;
    private boolean whitelist;
    private boolean pluginListEnabled;
    private String map;
    private int numPlayers;
    private int maxPlayers;

    public QueryRegenerateEvent(Server server) {
        this(server, 30);
    }

    public QueryRegenerateEvent(Server server, int timeout) {
        this.timeout = timeout;
        serverName = server.getServerProperties().getMotd();
        pluginListEnabled = (boolean) server.getConfig("settings.query-plugins", true);
        plugins = server.getPluginManager().getAllPlugins();
        players = new ArrayList<>(server.getOnlinePlayers().values());
        gameType = server.getDefaultGameMode();
        version = server.getVersion();
        map = server.getDefaultLevel() == null ? "unknown" : server.getDefaultLevel().getName();
        numPlayers = players.size();
        maxPlayers = server.getServerProperties().getMaxPlayers();
        whitelist = server.getServerProperties().isWhitelistEnabled();
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

    public GameMode getGameType() {
        return gameType;
    }

    public void setGameType(GameMode gameType) {
        this.gameType = gameType;
    }

    public boolean isWhitelisted() {
        return whitelist;
    }

    public void setWhitelisted(boolean whitelisted) {
        this.whitelist = whitelisted;
    }
}
