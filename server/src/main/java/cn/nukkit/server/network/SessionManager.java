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

package cn.nukkit.server.network;

import cn.nukkit.api.Player;
import cn.nukkit.server.network.minecraft.session.MinecraftSession;
import com.flowpowered.math.GenericMath;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

@Log4j2
public class SessionManager {
    private static final int SESSIONS_PER_THREAD = 50;

    private final ConcurrentMap<InetSocketAddress, MinecraftSession> sessions = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, Player> playerSessions = new ConcurrentHashMap<>();
    private final ThreadPoolExecutor sessionTicker = new ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("Session Ticker - #%d").setDaemon(true).build());

    public boolean add(InetSocketAddress address, MinecraftSession session) {
        boolean added = sessions.putIfAbsent(address, session) == null;
        if (added) {
            adjustPoolSize();
        }
        return added;
    }

    public boolean remove(MinecraftSession session) {
        boolean removed = sessions.values().remove(session);
        if (session.getPlayerSession() != null) {
            playerSessions.values().remove(session.getPlayerSession());
        }
        if (removed) {
            adjustPoolSize();
        }
        return removed;
    }

    public MinecraftSession get(InetSocketAddress address) {
        return sessions.get(address);
    }

    public Collection<MinecraftSession> all() {
        return ImmutableList.copyOf(sessions.values());
    }

    public int playerSessionCount() {
        return playerSessions.size();
    }

    public boolean add(MinecraftSession session) {
        return playerSessions.putIfAbsent(session.getAuthData().getIdentity(), session.getPlayerSession()) == null;
    }

    @Nullable
    public Player getPlayer(UUID uuid) {
        return playerSessions.get(uuid);
    }

    @Nullable
    public Player getPlayer(String name) {
        Player found = null;
        name = name.toLowerCase();
        int delta = Integer.MAX_VALUE;
        for (Player player : allPlayers()) {
            if (player.getName().toLowerCase().startsWith(name)) {
                int curDelta = player.getName().length() - name.length();
                if (curDelta < delta) {
                    found = player;
                    delta = curDelta;
                }
                if (curDelta == 0) {
                    break;
                }
            }
        }

        return found;
    }

    public List<Player> allPlayers() {
        return ImmutableList.copyOf(playerSessions.values());
    }

    private void adjustPoolSize() {
        int threads = GenericMath.clamp(sessions.size() / SESSIONS_PER_THREAD, 1, Runtime.getRuntime().availableProcessors());
        if (sessionTicker.getMaximumPoolSize() != threads) {
            sessionTicker.setMaximumPoolSize(threads);
        }
    }

    public void onTick() {
        for (MinecraftSession session : sessions.values()) {
            sessionTicker.execute(session::onTick);
        }
    }
}
