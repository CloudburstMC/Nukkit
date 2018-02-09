package cn.nukkit.event.server;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.HandlerList;
import cn.nukkit.nbt.stream.FastByteArrayOutputStream;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.utils.Binary;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    private boolean listPlugins;
    private Plugin[] plugins;
    private Player[] players;

    private final String gameType;
    private final String version;
    private final String server_engine;
    private String map;
    private int numPlayers;
    private int maxPlayers;
    private final String whitelist;
    private final int port;
    private final String ip;

    private Map<String, String> extraData = new HashMap<>();

    public QueryRegenerateEvent(Server server) {
        this(server, 5);
    }

    public QueryRegenerateEvent(Server server, int timeout) {
        this.timeout = timeout;
        this.serverName = server.getMotd();
        this.listPlugins = (boolean) server.getConfig("settings.query-plugins", true);
        this.plugins = server.getPluginManager().getPlugins().values().toArray(new Plugin[server.getPluginManager().getPlugins().values().size()]);
        List<Player> players = new ArrayList<>();
        for (Player player : server.getOnlinePlayers().values()) {
            if (player.isOnline()) {
                players.add(player);
            }
        }
        this.players = players.toArray(new Player[players.size()]);

        this.gameType = (server.getGamemode() & 0x01) == 0 ? "SMP" : "CMP";
        this.version = server.getVersion();
        this.server_engine = server.getName() + " " + server.getNukkitVersion();
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

    public Map<String, String> getExtraData() {
        return extraData;
    }

    public void setExtraData(Map<String, String> extraData) {
        this.extraData = extraData;
    }

    public byte[] getLongQuery(byte[] buffer) {
        if (buffer == null) buffer = new byte[Character.MAX_VALUE];
        FastByteArrayOutputStream query = new FastByteArrayOutputStream(buffer);
        try {
            String plist = this.server_engine;
            if (this.plugins.length > 0 && this.listPlugins) {
                plist += ":";
                for (Plugin p : this.plugins) {
                    PluginDescription d = p.getDescription();
                    plist += " " + d.getName().replace(";", "").replace(":", "").replace(" ", "_") + " " + d.getVersion().replace(";", "").replace(":", "").replace(" ", "_") + ";";
                }
                plist = plist.substring(0, plist.length() - 2);
            }

            query.write("splitnum".getBytes());
            query.write((byte) 0x00);
            query.write((byte) 128);
            query.write((byte) 0x00);

            LinkedHashMap<String, String> KVdata = new LinkedHashMap<>();
            KVdata.put("hostname", this.serverName);
            KVdata.put("gametype", this.gameType);
            KVdata.put("game_id", GAME_ID);
            KVdata.put("version", this.version);
            KVdata.put("server_engine", this.server_engine);
            KVdata.put("plugins", plist);
            KVdata.put("map", this.map);
            KVdata.put("numplayers", String.valueOf(this.numPlayers));
            KVdata.put("maxplayers", String.valueOf(this.maxPlayers));
            KVdata.put("whitelist", this.whitelist);
            KVdata.put("hostip", this.ip);
            KVdata.put("hostport", String.valueOf(this.port));

            for (Map.Entry<String, String> entry : KVdata.entrySet()) {
                query.write(entry.getKey().getBytes(StandardCharsets.UTF_8));
                query.write((byte) 0x00);
                query.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
                query.write((byte) 0x00);
            }

            query.write(new byte[]{0x00, 0x01});
            query.write("player_".getBytes());
            query.write(new byte[]{0x00, 0x00});

            for (Player player : this.players) {
                query.write(player.getName().getBytes(StandardCharsets.UTF_8));
                query.write((byte) 0x00);
            }

            query.write((byte) 0x00);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return query.toByteArray();
    }

    public byte[] getShortQuery(byte[] buffer) {
        if (buffer == null) buffer = new byte[Character.MAX_VALUE];
        FastByteArrayOutputStream query = new FastByteArrayOutputStream(buffer);
        try {
            query.write(this.serverName.getBytes(StandardCharsets.UTF_8));
            query.write((byte) 0x00);
            query.write(this.gameType.getBytes(StandardCharsets.UTF_8));
            query.write((byte) 0x00);
            query.write(this.map.getBytes(StandardCharsets.UTF_8));
            query.write((byte) 0x00);
            query.write(String.valueOf(this.numPlayers).getBytes(StandardCharsets.UTF_8));
            query.write((byte) 0x00);
            query.write(String.valueOf(this.maxPlayers).getBytes(StandardCharsets.UTF_8));
            query.write((byte) 0x00);
            query.write(Binary.writeLShort(this.port));
            query.write(this.ip.getBytes(StandardCharsets.UTF_8));
            query.write((byte) 0x00);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return query.toByteArray();
    }

}
