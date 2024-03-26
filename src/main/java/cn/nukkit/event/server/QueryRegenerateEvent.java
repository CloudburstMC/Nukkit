package cn.nukkit.event.server;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.HandlerList;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.utils.Binary;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class QueryRegenerateEvent extends ServerEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private int timeout;
    private String serverName;
    private boolean listPlugins;
    private Plugin[] plugins;
    private Player[] players;
    private final String gameType;
    private final String version;
    private String map;
    private int numPlayers;
    private int maxPlayers;
    private final String whitelist;
    private final int port;
    private final String ip;

    public QueryRegenerateEvent(Server server) {
        this(server, 5);
    }

    public QueryRegenerateEvent(Server server, int timeout) {
        this.timeout = timeout;
        this.serverName = server.getMotd();
        this.listPlugins = server.queryPlugins;
        this.plugins = server.getPluginManager().getPlugins().values().toArray(new Plugin[0]);
        this.players = server.getOnlinePlayers().values().toArray(new Player[0]);
        this.gameType = server.getGamemode() == 1 ? "CMP" : "SMP";
        this.version = server.getVersion();
        this.map = server.getDefaultLevel() == null ? "unknown" : server.getDefaultLevel().getName();
        this.numPlayers = this.players.length;
        this.maxPlayers = server.getMaxPlayers();
        this.whitelist = server.hasWhitelist() ? "on" : "off";
        this.port = server.getPort();
        this.ip = server.getIp();
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

    public boolean canListPlugins() {
        return this.listPlugins;
    }

    public void setListPlugins(boolean listPlugins) {
        this.listPlugins = listPlugins;
    }

    public Plugin[] getPlugins() {
        return plugins;
    }

    public void setPlugins(Plugin[] plugins) {
        this.plugins = plugins;
    }

    public Player[] getPlayerList() {
        return players;
    }

    public void setPlayerList(Player[] players) {
        this.players = players;
    }

    public int getPlayerCount() {
        return this.numPlayers;
    }

    public void setPlayerCount(int count) {
        this.numPlayers = count;
    }

    public int getMaxPlayerCount() {
        return this.maxPlayers;
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

    public byte[] getLongQuery() {
        ByteBuffer query = ByteBuffer.allocate(65536);
        StringBuilder plist = new StringBuilder("Nukkit");
        if (this.listPlugins && this.plugins.length > 0) {
            plist.append(':');
            for (Plugin p : this.plugins) {
                PluginDescription d = p.getDescription();
                plist.append(' ').append(d.getName().replace(";", "").replace(":", "").replace(" ", "_")).append(' ').append(d.getVersion().replace(";", "").replace(":", "").replace(" ", "_")).append(';');
            }
            plist = new StringBuilder(plist.substring(0, plist.length() - 2));
        }

        query.put("splitnum".getBytes(StandardCharsets.UTF_8));
        query.put((byte) 0x00);
        query.put((byte) 128);
        query.put((byte) 0x00);

        Map<String, String> KVdata = new LinkedHashMap<>();
        KVdata.put("hostname", this.serverName);
        KVdata.put("gametype", this.gameType);
        KVdata.put("game_id", "MINECRAFTPE");
        KVdata.put("version", this.version);
        KVdata.put("server_engine", "Nukkit");
        KVdata.put("plugins", plist.toString());
        KVdata.put("map", this.map);
        KVdata.put("numplayers", String.valueOf(this.numPlayers));
        KVdata.put("maxplayers", String.valueOf(this.maxPlayers));
        KVdata.put("whitelist", this.whitelist);
        KVdata.put("hostip", this.ip);
        KVdata.put("hostport", String.valueOf(this.port));

        for (Map.Entry<String, String> entry : KVdata.entrySet()) {
            query.put(entry.getKey().getBytes(StandardCharsets.UTF_8));
            query.put((byte) 0x00);
            query.put(entry.getValue().getBytes(StandardCharsets.UTF_8));
            query.put((byte) 0x00);
        }

        query.put(new byte[]{0x00, 0x01}).put("player_".getBytes(StandardCharsets.UTF_8)).put(new byte[]{0x00, 0x00});

        for (Player player : this.players) {
            query.put(player.getName().getBytes(StandardCharsets.UTF_8));
            query.put((byte) 0x00);
        }

        query.put((byte) 0x00);
        return Arrays.copyOf(query.array(), query.position());
    }

    public byte[] getShortQuery() {
        ByteBuffer query = ByteBuffer.allocate(65536);
        query.put(this.serverName.getBytes(StandardCharsets.UTF_8));
        query.put((byte) 0x00);
        query.put(this.gameType.getBytes(StandardCharsets.UTF_8));
        query.put((byte) 0x00);
        query.put(this.map.getBytes(StandardCharsets.UTF_8));
        query.put((byte) 0x00);
        query.put(String.valueOf(this.numPlayers).getBytes(StandardCharsets.UTF_8));
        query.put((byte) 0x00);
        query.put(String.valueOf(this.maxPlayers).getBytes(StandardCharsets.UTF_8));
        query.put((byte) 0x00);
        query.put(Binary.writeLShort(this.port));
        query.put(this.ip.getBytes(StandardCharsets.UTF_8));
        query.put((byte) 0x00);
        return Arrays.copyOf(query.array(), query.position());
    }
}
