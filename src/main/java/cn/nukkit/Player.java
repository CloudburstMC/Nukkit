package cn.nukkit;

import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Human;
import cn.nukkit.event.TextContainer;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.level.ChunkLoader;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.CompoundTag;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.utils.TextFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Player extends Human implements CommandSender, InventoryHolder, ChunkLoader, IPlayer {

    public static final int SURVIVAL = 0;
    public static final int CREATIVE = 1;
    public static final int ADVENTURE = 2;
    public static final int SPECTATOR = 3;
    public static final int VIEW = SPECTATOR;

    public static final int SURVIVAL_SLOTS = 36;
    public static final int CREATIVE_SLOTS = 112;

    protected SourceInterface interfaz;

    public boolean spawned = false;
    public boolean loggedIn = false;
    public byte gamemode;
    public long lastBreak;

    protected int windowCnt = 2;

    protected Map<Inventory, Integer> windows;

    protected Map<Integer, Inventory> windowIndex = new HashMap<>();

    protected int messageCounter = 2;

    protected int sendIndex = 0;

    public Vector3 speed = null;

    public boolean blocked = false;
    //todo achievement and crafting

    public long creationTime = 0;

    protected long randomClientId;
    protected UUID uuid;

    protected double lastMovement = 0;

    protected Vector3 forceMovement = null;

    protected Vector3 teleportPosition = null;
    protected boolean connected = true;
    protected String ip;
    protected boolean removeFormat = true;
    protected int port;
    protected String username;
    protected String iusername;
    protected String displayName;
    protected int startAction = -1;

    protected Vector3 sleeping = null;
    protected Long clientID = null;

    private Integer loadId = null;

    protected float stepHeight = 0.6f;

    public Map<String, Boolean> usedChunks = new HashMap<>();

    protected int chunkLoadCount = 0;
    protected Map<String, Integer> loadQueue = new HashMap<>();
    protected int nextChunkOrderRun = 5;

    protected Map<String, Player> hiddenPlayers = new HashMap<>();

    protected Vector3 newPosition;

    protected int viewDistance;
    protected int chunksPerTick;
    protected int spawnThreshold;

    private Position spawnPosition = null;

    protected int inAirTicks = 0;
    protected int startAirTicks = 5;

    protected boolean autoJump = true;

    protected boolean allowFlight = false;

    private Map<String, Boolean> needACK = new HashMap<>();

    private Map<Integer, List<DataPacket>> batchedPackets = new HashMap<>();

    private PermissibleBase perm = null;

    public TranslationContainer getLeaveMessage() {
        return new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.left", this.getDisplayName());
    }

    @Deprecated
    public long getClientId() {
        return randomClientId;
    }

    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public boolean isBanned() {
        return this.server.getNameBans().isBanned(this.getName().toLowerCase());
    }

    @Override
    public void setBanned(boolean value) {
        if (value) {
            this.server.getNameBans().addBan(this.getName(), null, null, null);
            this.kick("You have been banned");
        } else {
            this.server.getNameBans().remove(this.getName());
        }
    }

    @Override
    public boolean isWhitelisted() {
        return this.server.isWhitelisted(this.getName().toLowerCase());
    }

    @Override
    public void setWhitelisted(boolean value) {
        if (value) {
            this.server.addWhitelist(this.getName().toLowerCase());
        } else {
            this.server.removeWhitelist(this.getName().toLowerCase());
        }
    }

    @Override
    public Player getPlayer() {
        return this;
    }

    @Override
    public Long getFirstPlayed() {
        return this.namedTag != null ? this.namedTag.getLong("firstPlayed") : null;
    }

    @Override
    public Long getLastPlayed() {
        return this.namedTag != null ? this.namedTag.getLong("lastPlayed") : null;
    }

    @Override
    public Object hasPlayedBefore() {
        return this.namedTag != null;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean isOnline() {
        //todo !!!
        return false;
    }

    public String getAddress() {
        return this.ip;
    }

    public boolean isSurvival() {
        return (this.gamemode & 0x01) == 0;
    }

    public boolean isCreative() {
        return (this.gamemode & 0x01) > 0;
    }

    public boolean isSpectator() {
        return this.gamemode == 3;
    }

    public boolean isAdventure() {
        return (this.gamemode & 0x02) > 0;
    }

    public void handleDataPacket(DataPacket packet) {
        //todo
    }

    @Override
    public void close() {
        this.close("");
    }

    public void close(String message) {
        this.close(message, "generic");
    }

    public void close(String message, String reason) {
        this.close(message, reason, true);
    }

    public void close(String message, String reason, boolean notify) {

    }

    public void close(TextContainer message) {
        this.close(message, "generic");
    }

    public void close(TextContainer message, String reason) {
        this.close(message, reason, true);
    }

    public void close(TextContainer message, String reason, boolean notify) {

    }

    public String getName() {
        return this.username;
    }
}
