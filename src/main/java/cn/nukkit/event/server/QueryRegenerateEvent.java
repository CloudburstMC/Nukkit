package cn.nukkit.event.server;

import cn.nukkit.Server;
import cn.nukkit.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class QueryRegenerateEvent extends ServerEvent {
    //alot todo

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private static final String GAME_ID = "MINECRAFTPE";

    private int timeout;
    private String serverName;
    private int numPlayers;
    private int maxPlayers;

    public QueryRegenerateEvent(Server server) {
        this(server, 5);
    }

    public QueryRegenerateEvent(Server server, int timeout) {
        this.timeout = timeout;
        //todo alot
    }

    public int getPlayerCount() {
        return this.numPlayers;
    }

    public int getMaxPlayerCount() {
        return this.maxPlayers;
    }
}
