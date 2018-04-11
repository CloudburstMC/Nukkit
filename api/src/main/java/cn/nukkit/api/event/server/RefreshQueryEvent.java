/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.api.event.server;

import cn.nukkit.api.Player;
import cn.nukkit.api.Server;
import cn.nukkit.api.plugin.Plugin;
import cn.nukkit.api.util.GameMode;

import java.util.Collection;

/**
 * Query information sent to server lists, not the player's server list.
 */
public class RefreshQueryEvent implements ServerEvent {
    private final Collection<Plugin> plugins;
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

    public Collection<Plugin> getPlugins() {
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
