package cn.nukkit.server.network;

import cn.nukkit.api.Player;
import cn.nukkit.server.network.minecraft.session.MinecraftSession;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class SessionManager {
    private static final int SESSIONS_PER_THREAD = 50;

    private final ConcurrentMap<InetSocketAddress, MinecraftSession> sessions = new ConcurrentHashMap<>();
    private final ThreadPoolExecutor sessionTicker = new ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("Session Ticker - #%d").setDaemon(true).build());

    public boolean add(InetSocketAddress address, MinecraftSession session) {
        boolean added = sessions.putIfAbsent(address, session) == null;
        if (added) {
            adjustPoolSize();
        }
        return added;
    }

    public boolean remove(MinecraftSession address) {
        boolean removed = sessions.values().remove(address);
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

    public long playerSessionCount() {
        long count = 0;
        for (MinecraftSession session : sessions.values()) {
            if (session.getPlayerSession() != null) {
                count++;
            }
        }
        return count;
    }

    public List<Player> allPlayers() {
        List<Player> players = new ArrayList<>();
        for (MinecraftSession session : sessions.values()) {
            if (session.getPlayerSession() != null) {
                players.add(session.getPlayerSession());
            }
        }
        return players;
    }

    private void adjustPoolSize() {
        int threads = Math.max(1, sessions.size() / SESSIONS_PER_THREAD);
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
