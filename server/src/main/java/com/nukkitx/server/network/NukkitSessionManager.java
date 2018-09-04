package com.nukkitx.server.network;

import com.flowpowered.math.GenericMath;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.nukkitx.api.Player;
import com.nukkitx.network.SessionManager;
import com.nukkitx.server.network.bedrock.session.BedrockSession;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

@Log4j2
public class NukkitSessionManager implements SessionManager<BedrockSession> {
    private static final int SESSIONS_PER_THREAD = 50;

    private final ConcurrentMap<InetSocketAddress, BedrockSession> sessions = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, Player> playerSessions = new ConcurrentHashMap<>();
    private final ThreadPoolExecutor sessionTicker = new ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("Session Ticker - #%d").setDaemon(true).build());

    @Override
    public boolean add(InetSocketAddress address, BedrockSession session) {
        boolean added = sessions.putIfAbsent(address, session) == null;
        if (added) {
            adjustPoolSize();
        }
        return added;
    }

    @Override
    public boolean remove(BedrockSession session) {
        boolean removed = sessions.values().remove(session);
        if (session.getPlayerSession() != null) {
            playerSessions.values().remove(session.getPlayerSession());
        }
        if (removed) {
            adjustPoolSize();
        }
        return removed;
    }

    @Override
    public BedrockSession get(InetSocketAddress address) {
        return sessions.get(address);
    }

    @Override
    public Collection<BedrockSession> all() {
        return ImmutableList.copyOf(sessions.values());
    }

    @Override
    public int getCount() {
        return sessions.size();
    }

    public int playerSessionCount() {
        return playerSessions.size();
    }

    public boolean add(BedrockSession session) {
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
        for (BedrockSession session : sessions.values()) {
            sessionTicker.execute(session::onTick);
        }
    }
}
