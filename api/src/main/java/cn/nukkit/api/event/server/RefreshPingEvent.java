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

import cn.nukkit.api.Server;
import cn.nukkit.api.util.GameMode;

/**
 * Ping sent to player's on their server list screen before joining a server.
 */
public class RefreshPingEvent implements ServerEvent {
    private String motd;
    private String subMotd;
    private int maxPlayerCount;
    private int playerCount;
    private GameMode defaultGamemode;
    private int protocolVersion;
    private int timeout = 5;

    public RefreshPingEvent(Server server, int protocolVersion) {
        motd = server.getConfiguration().getGeneral().getMotd();
        subMotd = server.getConfiguration().getGeneral().getSubMotd();
        maxPlayerCount = server.getConfiguration().getGeneral().getMaximumPlayers();
        playerCount = server.getOnlinePlayers().size();
        defaultGamemode = server.getConfiguration().getMechanics().getDefaultGamemode();
        this.protocolVersion = protocolVersion;
    }

    public String getMotd(){
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public String getSubMotd() {
        return subMotd;
    }

    public void setSubMotd(String subMotd) {
        this.subMotd = subMotd;
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public void setMaxPlayerCount(int maxPlayerCount) {
        this.maxPlayerCount = maxPlayerCount;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public GameMode getDefaultGamemode() {
        return defaultGamemode;
    }

    public void setDefaultGamemode(GameMode defaultGamemode) {
        this.defaultGamemode = defaultGamemode;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
