package cn.nukkit;

import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.data.args.CommandArg;
import cn.nukkit.command.data.args.CommandArgBlockVector;
import cn.nukkit.entity.*;
import cn.nukkit.entity.data.*;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.mob.EntityCreeper;
import cn.nukkit.entity.projectile.*;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityDamageEvent.DamageModifier;
import cn.nukkit.event.inventory.*;
import cn.nukkit.event.player.*;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.inventory.*;
import cn.nukkit.item.*;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.food.Food;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.ChunkLoader;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.particle.CriticalParticle;
import cn.nukkit.level.particle.PunchBlockParticle;
import cn.nukkit.level.sound.ExperienceOrbSound;
import cn.nukkit.level.sound.ItemFrameItemRemovedSound;
import cn.nukkit.level.sound.LaunchSound;
import cn.nukkit.math.*;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.*;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import cn.nukkit.resourcepacks.ResourcePack;
import cn.nukkit.utils.*;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

/**
 * author: MagicDroidX & Box
 * Nukkit Project
 */
public class Player extends EntityHuman implements CommandSender, InventoryHolder, ChunkLoader, IPlayer {

    public static final int SURVIVAL = 0;
    public static final int CREATIVE = 1;
    public static final int ADVENTURE = 2;
    public static final int SPECTATOR = 3;
    public static final int VIEW = SPECTATOR;

    public static final int SURVIVAL_SLOTS = 36;
    public static final int CREATIVE_SLOTS = 112;

    public static final int CRAFTING_SMALL = 0;
    public static final int CRAFTING_BIG = 1;
    public static final int CRAFTING_ANVIL = 2;
    public static final int CRAFTING_ENCHANT = 3;

    public static final float DEFAULT_SPEED = 0.1f;
    public static final float MAXIMUM_SPEED = 0.5f;

    protected final SourceInterface interfaz;

    public boolean playedBefore;
    public boolean spawned = false;
    public boolean loggedIn = false;
    public int gamemode;
    public long lastBreak;

    protected int windowCnt = 2;

    protected Map<Inventory, Integer> windows;

    protected Map<Integer, Inventory> windowIndex = new HashMap<>();

    protected int messageCounter = 2;

    private String clientSecret;

    public Vector3 speed = null;

    public final HashSet<String> achievements = new HashSet<>();

    protected SimpleTransactionGroup currentTransaction = null;

    public int craftingType = CRAFTING_SMALL;

    public long creationTime = 0;

    protected long randomClientId;

    protected Vector3 forceMovement = null;

    protected Vector3 teleportPosition = null;

    protected boolean connected = true;
    protected final String ip;
    protected boolean removeFormat = true;

    protected final int port;
    protected String username;
    protected String iusername;
    protected String displayName;

    protected int startAction = -1;

    protected Vector3 sleeping = null;
    protected Long clientID = null;

    private Integer loaderId = null;

    protected float stepHeight = 0.6f;

    public Map<Long, Boolean> usedChunks = new HashMap<>();

    protected int chunkLoadCount = 0;
    protected Map<Long, Integer> loadQueue = new HashMap<>();
    protected int nextChunkOrderRun = 5;

    protected Map<UUID, Player> hiddenPlayers = new HashMap<>();

    protected Vector3 newPosition = null;

    protected int chunkRadius;
    protected int viewDistance;
    protected final int chunksPerTick;
    protected final int spawnThreshold;

    protected Position spawnPosition = null;

    protected int inAirTicks = 0;
    protected int startAirTicks = 5;

    protected AdventureSettings adventureSettings;

    protected boolean checkMovement = true;

    private final Map<Integer, Boolean> needACK = new HashMap<>();

    private Map<Integer, List<DataPacket>> batchedPackets = new TreeMap<>();

    private PermissibleBase perm = null;

    private int exp = 0;
    private int expLevel = 0;

    protected PlayerFood foodData = null;

    private Entity killer = null;

    private final AtomicReference<Locale> locale = new AtomicReference<>(null);

    private int hash;

    private String buttonText = "Button";

    protected boolean enableClientCommand = true;

    private BlockEnderChest viewingEnderChest = null;

    protected int lastEnderPearl = -1;

    private ClientChainData loginChainData;

    public int pickedXPOrb = 0;

    public BlockEnderChest getViewingEnderChest() {
        return viewingEnderChest;
    }

    public void setViewingEnderChest(BlockEnderChest chest) {
        if (chest == null && this.viewingEnderChest != null) {
            this.viewingEnderChest.getViewers().remove(this);
        } else if (chest != null) {
            chest.getViewers().add(this);
        }
        this.viewingEnderChest = chest;
    }

    public TranslationContainer getLeaveMessage() {
        return new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.left", this.getDisplayName());
    }

    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * This might disappear in the future.
     * Please use getUniqueId() instead (IP + clientId + name combo, in the future it'll change to real UUID for online auth)
     */
    @Deprecated
    public Long getClientId() {
        return randomClientId;
    }

    @Override
    public boolean isBanned() {
        return this.server.getNameBans().isBanned(this.getName());
    }

    @Override
    public void setBanned(boolean value) {
        if (value) {
            this.server.getNameBans().addBan(this.getName(), null, null, null);
            this.kick(PlayerKickEvent.Reason.NAME_BANNED, "Banned by admin");
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
    public boolean hasPlayedBefore() {
        return this.playedBefore;
    }

    public AdventureSettings getAdventureSettings() {
        return adventureSettings;
    }

    public void setAdventureSettings(AdventureSettings adventureSettings) {
        this.adventureSettings = adventureSettings.clone(this);
        this.adventureSettings.update();
    }

    public void resetInAirTicks() {
        this.inAirTicks = 0;
    }

    @Deprecated
    public void setAllowFlight(boolean value) {
        this.getAdventureSettings().setCanFly(value);
        this.getAdventureSettings().update();
    }

    @Deprecated
    public boolean getAllowFlight() {
        return this.getAdventureSettings().canFly();
    }

    @Deprecated
    public void setAutoJump(boolean value) {
        this.getAdventureSettings().setAutoJump(value);
        this.getAdventureSettings().update();
    }

    @Deprecated
    public boolean hasAutoJump() {
        return this.getAdventureSettings().isAutoJumpEnabled();
    }

    @Override
    public void spawnTo(Player player) {
        if (this.spawned && player.spawned && this.isAlive() && player.isAlive() && player.getLevel() == this.level && player.canSee(this) && !this.isSpectator()) {
            super.spawnTo(player);
        }
    }

    @Override
    public Server getServer() {
        return this.server;
    }

    public boolean getRemoveFormat() {
        return removeFormat;
    }

    public void setRemoveFormat() {
        this.setRemoveFormat(true);
    }

    public void setRemoveFormat(boolean remove) {
        this.removeFormat = remove;
    }

    public boolean canSee(Player player) {
        return !this.hiddenPlayers.containsKey(player.getUniqueId());
    }

    public void hidePlayer(Player player) {
        if (this == player) {
            return;
        }
        this.hiddenPlayers.put(player.getUniqueId(), player);
        player.despawnFrom(this);
    }

    public void showPlayer(Player player) {
        if (this == player) {
            return;
        }
        this.hiddenPlayers.remove(player.getUniqueId());
        if (player.isOnline()) {
            player.spawnTo(this);
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public void resetFallDistance() {
        super.resetFallDistance();
        if (this.inAirTicks != 0) {
            this.startAirTicks = 5;
        }
        this.inAirTicks = 0;
        this.highestPosition = this.y;
    }

    @Override
    public boolean isOnline() {
        return this.connected && this.loggedIn;
    }

    @Override
    public boolean isOp() {
        return this.server.isOp(this.getName());
    }

    @Override
    public void setOp(boolean value) {
        if (value == this.isOp()) {
            return;
        }

        if (value) {
            this.server.addOp(this.getName());
        } else {
            this.server.removeOp(this.getName());
        }

        this.recalculatePermissions();
        this.getAdventureSettings().update();
        this.sendCommandData();
    }

    @Override
    public boolean isPermissionSet(String name) {
        return this.perm.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return this.perm.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(String name) {
        return this.perm != null && this.perm.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return this.perm.hasPermission(permission);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.addAttachment(plugin, null);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name) {
        return this.addAttachment(plugin, name, null);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, Boolean value) {
        return this.perm.addAttachment(plugin, name, value);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        this.perm.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        this.server.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_USERS, this);
        this.server.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this);

        if (this.perm == null) {
            return;
        }

        this.perm.recalculatePermissions();

        if (this.hasPermission(Server.BROADCAST_CHANNEL_USERS)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_USERS, this);
        }

        if (this.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this);
        }

        if (this.isEnableClientCommand()) this.sendCommandData();
    }

    public boolean isEnableClientCommand() {
        return this.enableClientCommand;
    }

    public void setEnableClientCommand(boolean enable) {
        this.enableClientCommand = enable;
        SetCommandsEnabledPacket pk = new SetCommandsEnabledPacket();
        pk.enabled = enable;
        this.dataPacket(pk);
        if (enable) this.sendCommandData();
    }

    public void sendCommandData() {
        AvailableCommandsPacket pk = new AvailableCommandsPacket();
        Map<String, CommandDataVersions> data = new HashMap<>();
        int count = 0;
        for (Command command : this.server.getCommandMap().getCommands().values()) {
            if (!command.testPermissionSilent(this)) {
                continue;
            }
            ++count;
            CommandDataVersions data0 = command.generateCustomCommandData(this);
            data.put(command.getName(), data0);
        }
        if (count > 0) {
            //TODO: structure checking
            pk.commands = new Gson().toJson(data);
            int identifier = this.dataPacket(pk, true); // We *need* ACK so we can be sure that the client received the packet or not
            Thread t = new Thread() {
                public void run() {
                    // We are going to wait 3 seconds, if after 3 seconds we didn't receive a reply from the client, resend the packet.
                    try {
                        Thread.sleep(3000);
                        boolean status = needACK.get(identifier);
                        if (!status && isOnline()) {
                            sendCommandData();
                            return;
                        }
                    } catch (InterruptedException e) {}
                }
            };
            t.start();
        }
    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return this.perm.getEffectivePermissions();
    }

    public Player(SourceInterface interfaz, Long clientID, String ip, int port) {
        super(null, new CompoundTag());
        this.interfaz = interfaz;
        this.windows = new HashMap<>();
        this.perm = new PermissibleBase(this);
        this.server = Server.getInstance();
        this.lastBreak = Long.MAX_VALUE;
        this.ip = ip;
        this.port = port;
        this.clientID = clientID;
        this.loaderId = Level.generateChunkLoaderId(this);
        this.chunksPerTick = (int) this.server.getConfig("chunk-sending.per-tick", 4);
        this.spawnThreshold = (int) this.server.getConfig("chunk-sending.spawn-threshold", 56);
        this.spawnPosition = null;
        this.gamemode = this.server.getGamemode();
        this.setLevel(this.server.getDefaultLevel());
        this.viewDistance = this.server.getViewDistance();
        this.chunkRadius = viewDistance;
        //this.newPosition = new Vector3(0, 0, 0);
        this.boundingBox = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

        this.uuid = null;
        this.rawUUID = null;

        this.creationTime = System.currentTimeMillis();
    }

    public boolean isPlayer() {
        return true;
    }

    public void removeAchievement(String achievementId) {
        achievements.remove(achievementId);
    }

    public boolean hasAchievement(String achievementId) {
        return achievements.contains(achievementId);
    }

    public boolean isConnected() {
        return connected;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        if (this.spawned) {
            this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getDisplayName(), this.getSkin());
        }
    }

    @Override
    public void setSkin(Skin skin) {
        super.setSkin(skin);
        if (this.spawned) {
            this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getDisplayName(), skin);
        }
    }

    public String getAddress() {
        return this.ip;
    }

    public int getPort() {
        return port;
    }

    public Position getNextPosition() {
        return this.newPosition != null ? new Position(this.newPosition.x, this.newPosition.y, this.newPosition.z, this.level) : this.getPosition();
    }

    public boolean isSleeping() {
        return this.sleeping != null;
    }

    public int getInAirTicks() {
        return this.inAirTicks;
    }

    public String getButtonText() {
        return this.buttonText;
    }

    public void setButtonText(String text) {
        this.buttonText = text;
        this.setDataProperty(new StringEntityData(Entity.DATA_INTERACTIVE_TAG, this.buttonText));
    }

    @Override
    protected boolean switchLevel(Level targetLevel) {
        Level oldLevel = this.level;
        if (super.switchLevel(targetLevel)) {
            for (long index : new ArrayList<>(this.usedChunks.keySet())) {
                int chunkX = Level.getHashX(index);
                int chunkZ = Level.getHashZ(index);
                this.unloadChunk(chunkX, chunkZ, oldLevel);
            }

            this.usedChunks = new HashMap<>();
            SetTimePacket pk = new SetTimePacket();
            pk.time = this.level.getTime();
            this.dataPacket(pk);

            // TODO: Remove this hack
            int distance = this.viewDistance * 2 * 16 * 2;
            this.sendPosition(this.add(distance, 0, distance), this.yaw, this.pitch, MovePlayerPacket.MODE_RESET);
            return true;
        }
        return false;
    }

    public void unloadChunk(int x, int z) {
        this.unloadChunk(x, z, null);
    }

    public void unloadChunk(int x, int z, Level level) {
        level = level == null ? this.level : level;
        long index = Level.chunkHash(x, z);
        if (this.usedChunks.containsKey(index)) {
            for (Entity entity : level.getChunkEntities(x, z).values()) {
                if (entity != this) {
                    entity.despawnFrom(this);
                }
            }

            this.usedChunks.remove(index);
        }
        level.unregisterChunkLoader(this, x, z);
        this.loadQueue.remove(index);
    }

    public Position getSpawn() {
        if (this.spawnPosition != null && this.spawnPosition.getLevel() != null) {
            return this.spawnPosition;
        } else {
            return this.server.getDefaultLevel().getSafeSpawn();
        }
    }

    public void sendChunk(int x, int z, DataPacket packet) {
        if (!this.connected) {
            return;
        }

        this.usedChunks.put(Level.chunkHash(x, z), true);
        this.chunkLoadCount++;

        this.dataPacket(packet);

        if (this.spawned) {
            for (Entity entity : this.level.getChunkEntities(x, z).values()) {
                if (this != entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this);
                }
            }
        }
    }

    public void sendChunk(int x, int z, byte[] payload) {
        if (!this.connected) {
            return;
        }

        this.usedChunks.put(Level.chunkHash(x, z), true);
        this.chunkLoadCount++;

        FullChunkDataPacket pk = new FullChunkDataPacket();
        pk.chunkX = x;
        pk.chunkZ = z;
        pk.data = payload;

        this.batchDataPacket(pk);

        if (this.spawned) {
            for (Entity entity : this.level.getChunkEntities(x, z).values()) {
                if (this != entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this);
                }
            }
        }
    }

    protected void sendNextChunk() {
        if (!this.connected) {
            return;
        }

        Timings.playerChunkSendTimer.startTiming();

        int count = 0;

        List<Map.Entry<Long, Integer>> entryList = new ArrayList<>(this.loadQueue.entrySet());
        entryList.sort(Comparator.comparingInt(Map.Entry::getValue));

        for (Map.Entry<Long, Integer> entry : entryList) {
            long index = entry.getKey();
            if (count >= this.chunksPerTick) {
                break;
            }
            int chunkX = Level.getHashX(index);
            int chunkZ = Level.getHashZ(index);

            ++count;

            this.usedChunks.put(index, false);
            this.level.registerChunkLoader(this, chunkX, chunkZ, false);

            if (!this.level.populateChunk(chunkX, chunkZ)) {
                if (this.spawned && this.teleportPosition == null) {
                    continue;
                } else {
                    break;
                }
            }

            this.loadQueue.remove(index);

            PlayerChunkRequestEvent ev = new PlayerChunkRequestEvent(this, chunkX, chunkZ);
            this.server.getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                this.level.requestChunk(chunkX, chunkZ, this);
            }
        }
        if (this.chunkLoadCount >= this.spawnThreshold && !this.spawned && this.teleportPosition == null) {
            this.doFirstSpawn();
        }
        Timings.playerChunkSendTimer.stopTiming();
    }

    protected void doFirstSpawn() {
        this.spawned = true;

        this.server.sendRecipeList(this);
        this.getAdventureSettings().update();

        this.sendPotionEffects(this);
        this.sendData(this);
        this.inventory.sendContents(this);
        this.inventory.sendArmorContents(this);

        SetTimePacket setTimePacket = new SetTimePacket();
        setTimePacket.time = this.level.getTime();
        this.dataPacket(setTimePacket);

        Position pos = this.level.getSafeSpawn(this);

        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(this, pos);

        this.server.getPluginManager().callEvent(respawnEvent);

        pos = respawnEvent.getRespawnPosition();

        RespawnPacket respawnPacket = new RespawnPacket();
        respawnPacket.x = (float) pos.x;
        respawnPacket.y = (float) pos.y;
        respawnPacket.z = (float) pos.z;
        this.dataPacket(respawnPacket);

        PlayStatusPacket playStatusPacket = new PlayStatusPacket();
        playStatusPacket.status = PlayStatusPacket.PLAYER_SPAWN;
        this.dataPacket(playStatusPacket);

        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(this,
                new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.joined", new String[]{
                        this.getDisplayName()
                })
        );

        this.server.getPluginManager().callEvent(playerJoinEvent);

        if (playerJoinEvent.getJoinMessage().toString().trim().length() > 0) {
            this.server.broadcastMessage(playerJoinEvent.getJoinMessage());
        }

        this.noDamageTicks = 60;

        for (long index : this.usedChunks.keySet()) {
            int chunkX = Level.getHashX(index);
            int chunkZ = Level.getHashZ(index);
            for (Entity entity : this.level.getChunkEntities(chunkX, chunkZ).values()) {
                if (this != entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this);
                }
            }
        }

        this.sendExperience(this.getExperience());
        this.sendExperienceLevel(this.getExperienceLevel());

        this.teleport(pos, null); // Prevent PlayerTeleportEvent during player spawn

        if (!this.isSpectator()) {
            this.spawnToAll();
        }

        //todo Updater

        if (this.getHealth() <= 0) {
            respawnPacket = new RespawnPacket();
            pos = this.getSpawn();
            respawnPacket.x = (float) pos.x;
            respawnPacket.y = (float) pos.y;
            respawnPacket.z = (float) pos.z;
            this.dataPacket(respawnPacket);
        }

        //Weather
        this.getLevel().sendWeather(this);

        //FoodLevel
        this.getFoodData().sendFoodLevel();
    }

    protected boolean orderChunks() {
        if (!this.connected) {
            return false;
        }

        Timings.playerChunkOrderTimer.startTiming();

        this.nextChunkOrderRun = 200;

        Map<Long, Integer> newOrder = new HashMap<>();
        Map<Long, Boolean> lastChunk = new HashMap<>(this.usedChunks);

        int centerX = (int) this.x >> 4;
        int centerZ = (int) this.z >> 4;
        int count = 0;

        for (int x = -this.chunkRadius; x <= this.chunkRadius; x++) {
            for (int z = -this.chunkRadius; z <= this.chunkRadius; z++) {
                int chunkX = x + centerX;
                int chunkZ = z + centerZ;
                int distance = (int) Math.sqrt((double) x * x + (double) z * z);
                if (distance <= this.chunkRadius) {
                    long index;
                    if (!(this.usedChunks.containsKey(index = Level.chunkHash(chunkX, chunkZ))) || !this.usedChunks.get(index)) {
                        newOrder.put(index, distance);
                        count++;
                    }
                    lastChunk.remove(index);
                }
            }
        }

        for (long index : new ArrayList<>(lastChunk.keySet())) {
            this.unloadChunk(Level.getHashX(index), Level.getHashZ(index));
        }

        this.loadQueue = newOrder;
        Timings.playerChunkOrderTimer.stopTiming();
        return true;
    }

    public boolean batchDataPacket(DataPacket packet) {
        if (!this.connected) {
            return false;
        }

        try (Timing timing = Timings.getSendDataPacketTiming(packet)) {
            DataPacketSendEvent event = new DataPacketSendEvent(this, packet);
            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                timing.stopTiming();
                return false;
            }

            if (!this.batchedPackets.containsKey(packet.getChannel())) {
                this.batchedPackets.put(packet.getChannel(), new ArrayList<>());
            }

            this.batchedPackets.get(packet.getChannel()).add(packet.clone());
        }
        return true;
    }

    /**
     * 0 is true
     * -1 is false
     * other is identifer
     */
    public boolean dataPacket(DataPacket packet) {
        return this.dataPacket(packet, false) != -1;
    }

    public int dataPacket(DataPacket packet, boolean needACK) {
        if (!this.connected) {
            return -1;
        }

        try (Timing timing = Timings.getSendDataPacketTiming(packet)) {
            DataPacketSendEvent ev = new DataPacketSendEvent(this, packet);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                timing.stopTiming();
                return -1;
            }

            Integer identifier = this.interfaz.putPacket(this, packet, needACK, false);

            if (needACK && identifier != null) {
                this.needACK.put(identifier, false);
                timing.stopTiming();
                return identifier;
            }
        }
        return 0;
    }

    /**
     * 0 is true
     * -1 is false
     * other is identifer
     */
    public boolean directDataPacket(DataPacket packet) {
        return this.directDataPacket(packet, false) != -1;
    }

    public int directDataPacket(DataPacket packet, boolean needACK) {
        if (!this.connected) {
            return -1;
        }

        try (Timing timing = Timings.getSendDataPacketTiming(packet)) {
            DataPacketSendEvent ev = new DataPacketSendEvent(this, packet);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                timing.stopTiming();
                return -1;
            }

            Integer identifier = this.interfaz.putPacket(this, packet, needACK, true);

            if (needACK && identifier != null) {
                this.needACK.put(identifier, false);
                timing.stopTiming();
                return identifier;
            }
        }
        return 0;
    }

    public int getPing() {
        return this.interfaz.getNetworkLatency(this);
    }

    public boolean sleepOn(Vector3 pos) {
        if (!this.isOnline()) {
            return false;
        }

        for (Entity p : this.level.getNearbyEntities(this.boundingBox.grow(2, 1, 2), this)) {
            if (p instanceof Player) {
                if (((Player) p).sleeping != null && pos.distance(((Player) p).sleeping) <= 0.1) {
                    return false;
                }
            }
        }

        PlayerBedEnterEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerBedEnterEvent(this, this.level.getBlock(pos)));
        if (ev.isCancelled()) {
            return false;
        }

        this.sleeping = pos.clone();
        this.teleport(new Location(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, this.yaw, this.pitch, this.level), null);

        this.setDataProperty(new IntPositionEntityData(DATA_PLAYER_BED_POSITION, (int) pos.x, (int) pos.y, (int) pos.z));
        this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, true);

        this.setSpawn(pos);

        this.level.sleepTicks = 60;

        return true;
    }

    public void setSpawn(Vector3 pos) {
        Level level;
        if (!(pos instanceof Position)) {
            level = this.level;
        } else {
            level = ((Position) pos).getLevel();
        }
        this.spawnPosition = new Position(pos.x, pos.y, pos.z, level);
        SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
        pk.spawnType = SetSpawnPositionPacket.TYPE_PLAYER_SPAWN;
        pk.x = (int) this.spawnPosition.x;
        pk.y = (int) this.spawnPosition.y;
        pk.z = (int) this.spawnPosition.z;
        this.dataPacket(pk);
    }

    public void stopSleep() {
        if (this.sleeping != null) {
            this.server.getPluginManager().callEvent(new PlayerBedLeaveEvent(this, this.level.getBlock(this.sleeping)));

            this.sleeping = null;
            this.setDataProperty(new IntPositionEntityData(DATA_PLAYER_BED_POSITION, 0, 0, 0));
            this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, false);


            this.level.sleepTicks = 0;

            AnimatePacket pk = new AnimatePacket();
            pk.eid = this.id;
            pk.action = 3; //Wake up
            this.dataPacket(pk);
        }
    }

    public boolean awardAchievement(String achievementId) {
        if (!Server.getInstance().getPropertyBoolean("achievements", true)) {
            return false;
        }

        Achievement achievement = Achievement.achievements.get(achievementId);

        if (achievement == null || hasAchievement(achievementId)) {
            return false;
        }

        for (String id : achievement.requires) {
            if (!this.hasAchievement(id)) {
                return false;
            }
        }
        PlayerAchievementAwardedEvent event = new PlayerAchievementAwardedEvent(this, achievementId);
        this.server.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        this.achievements.add(achievementId);
        achievement.broadcast(this);
        return true;
    }

    public int getGamemode() {
        return gamemode;
    }

    /**
     *
     * Returns a client-friendly gamemode of the specified real gamemode
     * This function takes care of handling gamemodes known to MCPE (as of 1.1.0.3, that includes Survival, Creative and Adventure)
     *
     * TODO: remove this when Spectator Mode gets added properly to MCPE
     *
     */
    private static int getClientFriendlyGamemode(int gamemode){
        gamemode &= 0x03;
        if (gamemode == Player.SPECTATOR) {
            return Player.CREATIVE;
        }
        return gamemode;
    }

    public boolean setGamemode(int gamemode) {
        return this.setGamemode(gamemode, false, null);
    }

    public boolean setGamemode(int gamemode, boolean clientSide) {
        return this.setGamemode(gamemode, clientSide, null);
    }

    public boolean setGamemode(int gamemode, boolean clientSide, AdventureSettings newSettings) {
        if (gamemode < 0 || gamemode > 3 || this.gamemode == gamemode) {
            return false;
        }

        if (newSettings == null) {
            newSettings = this.getAdventureSettings().clone(this);
            newSettings.setCanDestroyBlock(gamemode != 3);
            newSettings.setCanFly((gamemode & 0x01) > 0);
            newSettings.setNoclip(gamemode == 0x03);
            newSettings.setFlying(gamemode == 0x03);
        }

        PlayerGameModeChangeEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerGameModeChangeEvent(this, gamemode, newSettings));

        if (ev.isCancelled()) {
            return false;
        }

        this.gamemode = gamemode;

        if (this.isSpectator()) {
            this.keepMovement = true;
            this.despawnFromAll();
        } else {
            this.keepMovement = false;
            this.spawnToAll();
        }

        this.namedTag.putInt("playerGameType", this.gamemode);

        if (!clientSide) {
            SetPlayerGameTypePacket pk = new SetPlayerGameTypePacket();
            pk.gamemode = getClientFriendlyGamemode(gamemode);
            this.dataPacket(pk);
        }

        this.setAdventureSettings(ev.getNewAdventureSettings());

        if (this.isSpectator()) {
            this.getAdventureSettings().setFlying(true);
            this.teleport(this.temporalVector.setComponents(this.x, this.y + 0.1, this.z));

            ContainerSetContentPacket containerSetContentPacket = new ContainerSetContentPacket();
            containerSetContentPacket.windowid = ContainerSetContentPacket.SPECIAL_CREATIVE;
            containerSetContentPacket.eid = this.id;
            this.dataPacket(containerSetContentPacket);
        } else {
            if (this.isSurvival()) {
                this.getAdventureSettings().setFlying(false);
            }
            ContainerSetContentPacket containerSetContentPacket = new ContainerSetContentPacket();
            containerSetContentPacket.windowid = ContainerSetContentPacket.SPECIAL_CREATIVE;
            containerSetContentPacket.eid = this.id;
            containerSetContentPacket.slots = Item.getCreativeItems().stream().toArray(Item[]::new);
            this.dataPacket(containerSetContentPacket);
        }

        this.resetFallDistance();

        this.inventory.sendContents(this);
        this.inventory.sendContents(this.getViewers().values());
        this.inventory.sendHeldItem(this.hasSpawned.values());

        return true;
    }

    @Deprecated
    public void sendSettings() {
        this.getAdventureSettings().update();
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

    @Override
    public Item[] getDrops() {
        if (!this.isCreative()) {
            return super.getDrops();
        }

        return new Item[0];
    }

    @Override
    public boolean setDataProperty(EntityData data) {
        return setDataProperty(data, true);
    }

    @Override
    public boolean setDataProperty(EntityData data, boolean send) {
        if (super.setDataProperty(data, send)) {
            if (send) this.sendData(this, new EntityMetadata().put(this.getDataProperty(data.getId())));
            return true;
        }
        return false;
    }

    @Override
    protected void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        if (!this.onGround || movX != 0 || movY != 0 || movZ != 0) {
            boolean onGround = false;

            AxisAlignedBB bb = this.boundingBox.clone();
            bb.maxY = bb.minY + 0.5;
            bb.minY -= 1;

            AxisAlignedBB realBB = this.boundingBox.clone();
            realBB.maxY = realBB.minY + 0.1;
            realBB.minY -= 0.2;

            int minX = NukkitMath.floorDouble(bb.minX);
            int minY = NukkitMath.floorDouble(bb.minY);
            int minZ = NukkitMath.floorDouble(bb.minZ);
            int maxX = NukkitMath.ceilDouble(bb.maxX);
            int maxY = NukkitMath.ceilDouble(bb.maxY);
            int maxZ = NukkitMath.ceilDouble(bb.maxZ);

            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = this.level.getBlock(this.temporalVector.setComponents(x, y, z));

                        if (!block.canPassThrough() && block.collidesWithBB(realBB)) {
                            onGround = true;
                            break;
                        }
                    }
                }
            }

            this.onGround = onGround;
        }

        this.isCollided = this.onGround;
    }

    @Override
    protected void checkBlockCollision() {
        boolean portal = false;

        for (Block block : this.getCollisionBlocks()) {
            if (block.getId() == Block.NETHER_PORTAL) {
                portal = true;
                continue;
            }

            block.onEntityCollide(this);
        }

        if (portal) {
            inPortalTicks++;
        } else {
            this.inPortalTicks = 0;
        }
    }

    protected void checkNearEntities() {
        for (Entity entity : this.level.getNearbyEntities(this.boundingBox.grow(1, 0.5, 1), this)) {
            entity.scheduleUpdate();

            if (!entity.isAlive() || !this.isAlive()) {
                continue;
            }

            this.pickupEntity(entity, true);
        }
    }

    protected void processMovement(int tickDiff) {
        if (!this.isAlive() || !this.spawned || this.newPosition == null || this.teleportPosition != null || this.isSleeping()) {
            return;
        }
        Vector3 newPos = this.newPosition;
        double distanceSquared = newPos.distanceSquared(this);
        boolean revert = false;
        if ((distanceSquared / ((double) (tickDiff * tickDiff))) > 100 && (newPos.y - this.y) > -5) {
            revert = true;
        } else {
            if (this.chunk == null || !this.chunk.isGenerated()) {
                BaseFullChunk chunk = this.level.getChunk((int) newPos.x >> 4, (int) newPos.z >> 4, false);
                if (chunk == null || !chunk.isGenerated()) {
                    revert = true;
                    this.nextChunkOrderRun = 0;
                } else {
                    if (this.chunk != null) {
                        this.chunk.removeEntity(this);
                    }
                    this.chunk = chunk;
                }
            }
        }

        double tdx = newPos.x - this.x;
        double tdz = newPos.z - this.z;
        double distance = Math.sqrt(tdx * tdx + tdz * tdz);

        if (!revert && distanceSquared != 0) {
            double dx = newPos.x - this.x;
            double dy = newPos.y - this.y;
            double dz = newPos.z - this.z;

            this.fastMove(dx, dy, dz);

            double diffX = this.x - newPos.x;
            double diffY = this.y - newPos.y;
            double diffZ = this.z - newPos.z;

            double yS = 0.5 + this.ySize;
            if (diffY >= -yS || diffY <= yS) {
                diffY = 0;
            }

            if (diffX != 0 || diffY != 0 || diffZ != 0) {
                if (this.checkMovement && !server.getAllowFlight() && this.isSurvival()) {
                    // Some say: I cant move my head when riding because the server 
                    // blocked my movement
                    if (!this.isSleeping() && this.riding == null) {
                        double diffHorizontalSqr = (diffX * diffX + diffZ * diffZ) / ((double) (tickDiff * tickDiff));
                        if (diffHorizontalSqr > 0.125) {
                            PlayerInvalidMoveEvent ev;
                            this.getServer().getPluginManager().callEvent(ev = new PlayerInvalidMoveEvent(this, true));
                            if (!ev.isCancelled()) {
                                revert = ev.isRevert();

                                if (revert) {
                                    this.server.getLogger().warning(this.getServer().getLanguage().translateString("nukkit.player.invalidMove", this.getName()));
                                }
                            }
                        }
                    }
                }


                this.x = newPos.x;
                this.y = newPos.y;
                this.z = newPos.z;
                double radius = this.getWidth() / 2;
                this.boundingBox.setBounds(this.x - radius, this.y, this.z - radius, this.x + radius, this.y + this.getHeight(), this.z + radius);
            }
        }

        Location from = new Location(
                this.lastX,
                this.lastY,
                this.lastZ,
                this.lastYaw,
                this.lastPitch,
                this.level);
        Location to = this.getLocation();

        double delta = Math.pow(this.lastX - to.x, 2) + Math.pow(this.lastY - to.y, 2) + Math.pow(this.z - to.z, 2);
        double deltaAngle = Math.abs(this.lastYaw - to.yaw) + Math.abs(this.lastPitch - to.pitch);

        if (!revert && (delta > 0.0001d || deltaAngle > 1d)) {
            boolean isFirst = this.firstMove;

            this.firstMove = false;
            this.lastX = to.x;
            this.lastY = to.y;
            this.lastZ = to.z;

            this.lastYaw = to.yaw;
            this.lastPitch = to.pitch;

            if (!isFirst) {
                List<Block> blocksAround = new ArrayList<>(this.blocksAround);
                List<Block> collidingBlocks = new ArrayList<>(this.collisionBlocks);

                PlayerMoveEvent ev = new PlayerMoveEvent(this, from, to);

                this.blocksAround = null;
                this.collisionBlocks = null;

                this.server.getPluginManager().callEvent(ev);

                if (!(revert = ev.isCancelled())) { //Yes, this is intended
                    if (!to.equals(ev.getTo())) { //If plugins modify the destination
                        this.teleport(ev.getTo(), null);
                    } else {
                        this.addMovement(this.x, this.y + this.getEyeHeight(), this.z, this.yaw, this.pitch, this.yaw);
                    }
                } else {
                    this.blocksAround = blocksAround;
                    this.collisionBlocks = collidingBlocks;
                }
            }

            if (!this.isSpectator()) {
                this.checkNearEntities();
            }
            if (this.speed == null) speed = new Vector3(from.x - to.x, from.y - to.y, from.z - to.z);
            else this.speed.setComponents(from.x - to.x, from.y - to.y, from.z - to.z);
        } else {
            if (this.speed == null) speed = new Vector3(0, 0, 0);
            else this.speed.setComponents(0, 0, 0);
        }

        if (!revert && (this.isFoodEnabled() || this.getServer().getDifficulty() == 0)) {
            if ((this.isSurvival() || this.isAdventure())/* && !this.getRiddingOn() instanceof Entity*/) {

                //UpdateFoodExpLevel
                if (distance >= 0.05) {
                    double jump = 0;
                    double swimming = this.isInsideOfWater() ? 0.015 * distance : 0;
                    if (swimming != 0) distance = 0;
                    if (this.isSprinting()) {  //Running
                        if (this.inAirTicks == 3 && swimming == 0) {
                            jump = 0.7;
                        }
                        this.getFoodData().updateFoodExpLevel(0.1 * distance + jump + swimming);
                    } else {
                        if (this.inAirTicks == 3 && swimming == 0) {
                            jump = 0.2;
                        }
                        this.getFoodData().updateFoodExpLevel(0.01 * distance + jump + swimming);
                    }
                }
            }
        }

        if (revert) {

            this.lastX = from.x;
            this.lastY = from.y;
            this.lastZ = from.z;

            this.lastYaw = from.yaw;
            this.lastPitch = from.pitch;

            this.sendPosition(from, from.yaw, from.pitch, MovePlayerPacket.MODE_RESET);
            //this.sendSettings();
            this.forceMovement = new Vector3(from.x, from.y, from.z);
        } else {
            this.forceMovement = null;
            if (distanceSquared != 0 && this.nextChunkOrderRun > 20) {
                this.nextChunkOrderRun = 20;
            }
        }

        this.newPosition = null;
    }

    @Override
    public boolean setMotion(Vector3 motion) {
        if (super.setMotion(motion)) {
            if (this.chunk != null) {
                this.getLevel().addEntityMotion(this.chunk.getX(), this.chunk.getZ(), this.getId(), this.motionX, this.motionY, this.motionZ);  //Send to others
                SetEntityMotionPacket pk = new SetEntityMotionPacket();
                pk.eid = this.id;
                pk.motionX = (float) motion.x;
                pk.motionY = (float) motion.y;
                pk.motionZ = (float) motion.z;
                this.dataPacket(pk);  //Send to self
            }

            if (this.motionY > 0) {
                //todo: check this
                this.startAirTicks = (int) ((-(Math.log(this.getGravity() / (this.getGravity() + this.getDrag() * this.motionY))) / this.getDrag()) * 2 + 5);
            }

            return true;
        }

        return false;
    }

    public void sendAttributes() {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entityId = this.getId();
        pk.entries = new Attribute[]{
                Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(this.getMaxHealth()).setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0),
                Attribute.getAttribute(Attribute.MAX_HUNGER).setValue(this.getFoodData().getLevel()),
                Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue(this.getMovementSpeed()),
                Attribute.getAttribute(Attribute.EXPERIENCE_LEVEL).setValue(this.getExperienceLevel()),
                Attribute.getAttribute(Attribute.EXPERIENCE).setValue(((float) this.getExperience()) / calculateRequireExperience(this.getExperienceLevel()))
        };
        this.dataPacket(pk);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (!this.loggedIn) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0) {
            return true;
        }

        this.messageCounter = 2;

        this.lastUpdate = currentTick;

        if (!this.isAlive() && this.spawned) {
            ++this.deadTicks;
            if (this.deadTicks >= 10) {
                this.despawnFromAll();
            }
            return true;
        }

        if (this.spawned) {
            this.processMovement(tickDiff);

            this.entityBaseTick(tickDiff);

            if (this.getServer().getDifficulty() == 0 && this.level.getGameRules().getBoolean("naturalRegeneration")) {
                if (this.getHealth() < this.getMaxHealth() && this.ticksLived % 20 == 0) {
                    this.heal(1);
                }

                PlayerFood foodData = this.getFoodData();

                if (foodData.getLevel() < 20 && this.ticksLived % 10 == 0) {
                    foodData.addFoodLevel(1, 0);
                }
            }

            if (this.isOnFire() && this.lastUpdate % 10 == 0) {
                if (this.isCreative() && !this.isInsideOfFire()) {
                    this.extinguish();
                } else if (this.getLevel().isRaining()) {
                    if (this.getLevel().canBlockSeeSky(this)) {
                        this.extinguish();
                    }
                }
            }

            if (!this.isSpectator() && this.speed != null) {
                if (this.onGround) {
                    if (this.inAirTicks != 0) {
                        this.startAirTicks = 5;
                    }
                    this.inAirTicks = 0;
                    this.highestPosition = this.y;
                } else {
                    if (!this.isGliding() && !server.getAllowFlight() && !this.getAdventureSettings().canFly() && this.inAirTicks > 10 && !this.isSleeping() && !this.isImmobile()) {
                        double expectedVelocity = (-this.getGravity()) / ((double) this.getDrag()) - ((-this.getGravity()) / ((double) this.getDrag())) * Math.exp(-((double) this.getDrag()) * ((double) (this.inAirTicks - this.startAirTicks)));
                        double diff = (this.speed.y - expectedVelocity) * (this.speed.y - expectedVelocity);

                        if (!this.hasEffect(Effect.JUMP) && diff > 0.6 && expectedVelocity < this.speed.y) {
                            if (this.inAirTicks < 100) {
                                //this.sendSettings();
                                this.setMotion(new Vector3(0, expectedVelocity, 0));
                            } else if (this.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server")) {
                                return false;
                            }
                        }
                    }

                    if (this.y > highestPosition) {
                        this.highestPosition = this.y;
                    }

                    if (this.isGliding()) this.resetFallDistance();

                    ++this.inAirTicks;

                }

                if (this.isSurvival() || this.isAdventure()) {
                    if (this.getFoodData() != null) this.getFoodData().update(tickDiff);
                }
            }
        }

        this.checkTeleportPosition();
        this.checkInteractNearby();

        // TODO: remove this workaround (broken client MCPE 1.0.0)
        if (!messageQueue.isEmpty()) {
            TextPacket pk = new TextPacket();
            pk.type = TextPacket.TYPE_RAW;
            pk.message = String.join("\n", messageQueue);
            this.dataPacket(pk);
            messageQueue.clear();
        }
        return true;
    }
    
    public void checkInteractNearby(){
        int interactDistance = isCreative() ? 5 : 3;
        if(canInteract(this, interactDistance)){
            if(getEntityPlayerLookingAt(interactDistance) != null){
                EntityInteractable onInteract = getEntityPlayerLookingAt(interactDistance);
                setButtonText(onInteract.getInteractButtonText());
            } else {
                setButtonText("");
            }
        } else {
            setButtonText("");
        }
    }

     /**
     * Returns the Entity the player is looking at currently
     *
     * @param maxDistance the maximum distance to check for entities
     * @return Entity|null    either NULL if no entity is found or an instance of the entity
     */
    public EntityInteractable getEntityPlayerLookingAt(int maxDistance) {
        timing.startTiming();
        
        EntityInteractable entity = null;

        // just a fix because player MAY not be fully initialized
        if (temporalVector != null) {
            Entity[] nearbyEntities = level.getNearbyEntities(boundingBox.grow(maxDistance, maxDistance, maxDistance), this);

            // get all blocks in looking direction until the max interact distance is reached (it's possible that startblock isn't found!)
            try {
                BlockIterator itr = new BlockIterator(level, getPosition(), getDirectionVector(), getEyeHeight(), maxDistance);
                if (itr.hasNext()) {
                    Block block;
                    while (itr.hasNext()) {
                        block = itr.next();
                        entity = getEntityAtPosition(nearbyEntities, block.getFloorX(), block.getFloorY(), block.getFloorZ());
                        if (entity != null) {
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                // nothing to log here!
            }
        }

        timing.stopTiming();

        return entity;
    }
    
    private EntityInteractable getEntityAtPosition(Entity[] nearbyEntities, int x, int y, int z) {
        for (Entity nearestEntity : nearbyEntities) {
            if (nearestEntity.getFloorX() == x && nearestEntity.getFloorY() == y && nearestEntity.getFloorZ() == z
                    && nearestEntity instanceof EntityInteractable
                    && ((EntityInteractable) nearestEntity).canDoInteraction()) {
                return (EntityInteractable) nearestEntity;
            }
        }
        return null;
    }
    
    private ArrayList<String> messageQueue = new ArrayList<>();

    public void checkNetwork() {
        if (!this.isOnline()) {
            return;
        }

        if (this.nextChunkOrderRun-- <= 0 || this.chunk == null) {
            this.orderChunks();
        }

        if (!this.loadQueue.isEmpty() || !this.spawned) {
            this.sendNextChunk();
        }

        if (!this.batchedPackets.isEmpty()) {
            for (int channel : this.batchedPackets.keySet()) {
                this.server.batchPackets(new Player[]{this}, batchedPackets.get(channel).stream().toArray(DataPacket[]::new), false);
            }
            this.batchedPackets = new TreeMap<>();
        }

    }

    public boolean canInteract(Vector3 pos, double maxDistance) {
        return this.canInteract(pos, maxDistance, 0.5);
    }

    public boolean canInteract(Vector3 pos, double maxDistance, double maxDiff) {
        if (this.distanceSquared(pos) > maxDistance * maxDistance) {
            return false;
        }

        Vector2 dV = this.getDirectionPlane();
        double dot = dV.dot(new Vector2(this.x, this.z));
        double dot1 = dV.dot(new Vector2(pos.x, pos.z));
        return (dot1 - dot) >= -maxDiff;
    }

    protected void processLogin() {
        if (!this.server.isWhitelisted((this.getName()).toLowerCase())) {
            this.kick(PlayerKickEvent.Reason.NOT_WHITELISTED, "Server is white-listed");

            return;
        } else if (this.isBanned()) {
            this.kick(PlayerKickEvent.Reason.NAME_BANNED, "You are banned");
            return;
        } else if (this.server.getIPBans().isBanned(this.getAddress())) {
            this.kick(PlayerKickEvent.Reason.IP_BANNED, "You are banned");
            return;
        }

        if (this.hasPermission(Server.BROADCAST_CHANNEL_USERS)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_USERS, this);
        }
        if (this.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this);
        }

        for (Player p : new ArrayList<>(this.server.getOnlinePlayers().values())) {
            if (p != this && p.getName() != null && p.getName().equalsIgnoreCase(this.getName())) {
                if (!p.kick(PlayerKickEvent.Reason.NEW_CONNECTION, "logged in from another location")) {
                    this.close(this.getLeaveMessage(), "Already connected");
                    return;
                }
            } else if (p.loggedIn && this.getUniqueId().equals(p.getUniqueId())) {
                if (!p.kick(PlayerKickEvent.Reason.NEW_CONNECTION, "logged in from another location")) {
                    this.close(this.getLeaveMessage(), "Already connected");
                    return;
                }
            }
        }

        CompoundTag nbt = this.server.getOfflinePlayerData(this.username);
        if (nbt == null) {
            this.close(this.getLeaveMessage(), "Invalid data");

            return;
        }

        this.playedBefore = (nbt.getLong("lastPlayed") - nbt.getLong("firstPlayed")) > 1;

        boolean alive = true;

        nbt.putString("NameTag", this.username);

        if (0 >= nbt.getShort("Health")) {
            alive = false;
        }

        int exp = nbt.getInt("EXP");
        int expLevel = nbt.getInt("expLevel");
        this.setExperience(exp, expLevel);

        this.gamemode = nbt.getInt("playerGameType") & 0x03;
        if (this.server.getForceGamemode()) {
            this.gamemode = this.server.getGamemode();
            nbt.putInt("playerGameType", this.gamemode);
        }

        this.adventureSettings = new AdventureSettings.Builder(this)
                .canDestroyBlock(!isAdventure())
                .autoJump(true)
                .canFly(isCreative())
                .noclip(isSpectator())
                .build();

        Level level;
        if ((level = this.server.getLevelByName(nbt.getString("Level"))) == null || !alive) {
            this.setLevel(this.server.getDefaultLevel());
            nbt.putString("Level", this.level.getName());
            nbt.getList("Pos", DoubleTag.class)
                    .add(new DoubleTag("0", this.level.getSpawnLocation().x))
                    .add(new DoubleTag("1", this.level.getSpawnLocation().y))
                    .add(new DoubleTag("2", this.level.getSpawnLocation().z));
        } else {
            this.setLevel(level);
        }

        for (Tag achievement : nbt.getCompound("Achievements").getAllTags()) {
            if (!(achievement instanceof ByteTag)) {
                continue;
            }

            if (((ByteTag) achievement).getData() > 0) {
                this.achievements.add(achievement.getName());
            }
        }

        nbt.putLong("lastPlayed", System.currentTimeMillis() / 1000);

        if (this.server.getAutoSave()) {
            this.server.saveOfflinePlayerData(this.username, nbt, true);
        }

        ListTag<DoubleTag> posList = nbt.getList("Pos", DoubleTag.class);

        super.init(this.level.getChunk((int) posList.get(0).data >> 4, (int) posList.get(2).data >> 4, true), nbt);

        if (!this.namedTag.contains("foodLevel")) {
            this.namedTag.putInt("foodLevel", 20);
        }
        int foodLevel = this.namedTag.getInt("foodLevel");
        if (!this.namedTag.contains("FoodSaturationLevel")) {
            this.namedTag.putFloat("FoodSaturationLevel", 20);
        }
        float foodSaturationLevel = this.namedTag.getFloat("foodSaturationLevel");
        this.foodData = new PlayerFood(this, foodLevel, foodSaturationLevel);

        PlayerLoginEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerLoginEvent(this, "Plugin reason"));
        if (ev.isCancelled()) {
            this.close(this.getLeaveMessage(), ev.getKickMessage());

            return;
        }

        this.server.addOnlinePlayer(this);
        this.loggedIn = true;

        if (this.isCreative()) {
            this.inventory.setHeldItemSlot(0);
        } else {
            this.inventory.setHeldItemSlot(this.inventory.getHotbarSlotIndex(0));
        }

        if (this.isSpectator()) this.keepMovement = true;

        if (this.spawnPosition == null && this.namedTag.contains("SpawnLevel") && (level = this.server.getLevelByName(this.namedTag.getString("SpawnLevel"))) != null) {
            this.spawnPosition = new Position(this.namedTag.getInt("SpawnX"), this.namedTag.getInt("SpawnY"), this.namedTag.getInt("SpawnZ"), level);
        }

        Position spawnPosition = this.getSpawn();
        StartGamePacket startGamePacket = new StartGamePacket();
        startGamePacket.entityUniqueId = this.id;
        startGamePacket.entityRuntimeId = this.id;
        startGamePacket.playerGamemode = getClientFriendlyGamemode(this.gamemode);
        startGamePacket.x = (float) this.x;
        startGamePacket.y = (float) this.y;
        startGamePacket.z = (float) this.z;
        startGamePacket.yaw = (float) this.yaw;
        startGamePacket.pitch = (float) this.pitch;
        startGamePacket.seed = -1;
        startGamePacket.dimension = (byte) (this.level.getDimension() & 0xff);
        startGamePacket.gamemode = getClientFriendlyGamemode(this.gamemode);
        startGamePacket.difficulty = this.server.getDifficulty();
        startGamePacket.spawnX = (int) spawnPosition.x;
        startGamePacket.spawnY = (int) spawnPosition.y;
        startGamePacket.spawnZ = (int) spawnPosition.z;
        startGamePacket.hasAchievementsDisabled = true;
        startGamePacket.dayCycleStopTime = -1;
        startGamePacket.eduMode = false;
        startGamePacket.rainLevel = 0;
        startGamePacket.lightningLevel = 0;
        startGamePacket.commandsEnabled = this.isEnableClientCommand();
        startGamePacket.levelId = "";
        startGamePacket.worldName = this.getServer().getNetwork().getName();
        startGamePacket.generator = 1; //0 old, 1 infinite, 2 flat
        this.dataPacket(startGamePacket);

        SetTimePacket setTimePacket = new SetTimePacket();
        setTimePacket.time = this.level.getTime();
        this.dataPacket(setTimePacket);

        this.setMovementSpeed(DEFAULT_SPEED);
        this.sendAttributes();
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setCanClimb(true);

        this.server.getLogger().info(this.getServer().getLanguage().translateString("nukkit.player.logIn",
                TextFormat.AQUA + this.username + TextFormat.WHITE,
                this.ip,
                String.valueOf(this.port),
                String.valueOf(this.id),
                this.level.getName(),
                String.valueOf(NukkitMath.round(this.x, 4)),
                String.valueOf(NukkitMath.round(this.y, 4)),
                String.valueOf(NukkitMath.round(this.z, 4))));

        if (this.isOp()) {
            this.setRemoveFormat(false);
        }

        if (this.gamemode == Player.SPECTATOR) {
            ContainerSetContentPacket containerSetContentPacket = new ContainerSetContentPacket();
            containerSetContentPacket.windowid = ContainerSetContentPacket.SPECIAL_CREATIVE;
            containerSetContentPacket.eid = this.id;
            this.dataPacket(containerSetContentPacket);
        } else {
            ContainerSetContentPacket containerSetContentPacket = new ContainerSetContentPacket();
            containerSetContentPacket.windowid = ContainerSetContentPacket.SPECIAL_CREATIVE;
            containerSetContentPacket.eid = this.id;
            containerSetContentPacket.slots = Item.getCreativeItems().stream().toArray(Item[]::new);
            this.dataPacket(containerSetContentPacket);
        }

        this.setEnableClientCommand(true);

        this.server.sendFullPlayerListData(this);

        this.forceMovement = this.teleportPosition = this.getPosition();

        this.server.onPlayerLogin(this);
    }

    public void handleDataPacket(DataPacket packet) {
        if (!connected) {
            return;
        }

        try (Timing timing = Timings.getReceiveDataPacketTiming(packet)) {
            DataPacketReceiveEvent ev = new DataPacketReceiveEvent(this, packet);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                timing.stopTiming();
                return;
            }

            if (packet.pid() == ProtocolInfo.BATCH_PACKET) {
                timing.stopTiming();
                this.server.getNetwork().processBatch((BatchPacket) packet, this);
                return;
            }

            packetswitch:
            switch (packet.pid()) {
                case ProtocolInfo.LOGIN_PACKET:
                    if (this.loggedIn) {
                        break;
                    }

                    LoginPacket loginPacket = (LoginPacket) packet;

                    String message;
                    if (loginPacket.getProtocol() != ProtocolInfo.CURRENT_PROTOCOL) {
                        if (loginPacket.getProtocol() < ProtocolInfo.CURRENT_PROTOCOL) {
                            message = "disconnectionScreen.outdatedClient";

                            PlayStatusPacket pk = new PlayStatusPacket();
                            pk.status = PlayStatusPacket.LOGIN_FAILED_CLIENT;
                            this.directDataPacket(pk);
                        } else {
                            message = "disconnectionScreen.outdatedServer";

                            PlayStatusPacket pk = new PlayStatusPacket();
                            pk.status = PlayStatusPacket.LOGIN_FAILED_SERVER;
                            this.directDataPacket(pk);
                        }
                        this.close("", message, false);
                        break;
                    }

                    this.username = TextFormat.clean(loginPacket.username);
                    this.displayName = this.username;
                    this.iusername = this.username.toLowerCase();
                    this.setDataProperty(new StringEntityData(DATA_NAMETAG, this.username), false);

                    this.loginChainData = ClientChainData.read(loginPacket);

                    if (this.server.getOnlinePlayers().size() >= this.server.getMaxPlayers() && this.kick(PlayerKickEvent.Reason.SERVER_FULL, "disconnectionScreen.serverFull", false)) {
                        break;
                    }

                    this.randomClientId = loginPacket.clientId;

                    this.uuid = loginPacket.clientUUID;
                    this.rawUUID = Binary.writeUUID(this.uuid);

                    boolean valid = true;
                    int len = loginPacket.username.length();
                    if (len > 16 || len < 3) {
                        valid = false;
                    }

                    for (int i = 0; i < len && valid; i++) {
                        char c = loginPacket.username.charAt(i);
                        if ((c >= 'a' && c <= 'z') ||
                                (c >= 'A' && c <= 'Z') ||
                                (c >= '0' && c <= '9') ||
                                c == '_' || c == ' '
                                ) {
                            continue;
                        }

                        valid = false;
                        break;
                    }

                    if (!valid || Objects.equals(this.iusername, "rcon") || Objects.equals(this.iusername, "console")) {
                        this.close("", "disconnectionScreen.invalidName");

                        break;
                    }

                    if (!loginPacket.skin.isValid()) {
                        this.close("", "disconnectionScreen.invalidSkin");
                        break;
                    } else {
                        this.setSkin(loginPacket.getSkin());
                    }

                    PlayerPreLoginEvent playerPreLoginEvent;
                    this.server.getPluginManager().callEvent(playerPreLoginEvent = new PlayerPreLoginEvent(this, "Plugin reason"));
                    if (playerPreLoginEvent.isCancelled()) {
                        this.close("", playerPreLoginEvent.getKickMessage());

                        break;
                    }

                    PlayStatusPacket statusPacket = new PlayStatusPacket();
                    statusPacket.status = PlayStatusPacket.LOGIN_SUCCESS;
                    this.dataPacket(statusPacket);

                    ResourcePacksInfoPacket infoPacket = new ResourcePacksInfoPacket();
                    infoPacket.resourcePackEntries = this.server.getResourcePackManager().getResourceStack();
                    infoPacket.mustAccept = this.server.getForceResources();
                    this.dataPacket(infoPacket);

                    break;
                case ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET:
                    ResourcePackClientResponsePacket responsePacket = (ResourcePackClientResponsePacket) packet;
                    switch (responsePacket.responseStatus) {
                        case ResourcePackClientResponsePacket.STATUS_REFUSED:
                            this.close("", "disconnectionScreen.noReason");
                            break;
                        case ResourcePackClientResponsePacket.STATUS_SEND_PACKS:
                            for (String id : responsePacket.packIds) {
                                ResourcePack resourcePack = this.server.getResourcePackManager().getPackById(id);
                                if (resourcePack == null) {
                                    this.close("", "disconnectionScreen.resourcePack");
                                    break;
                                }

                                ResourcePackDataInfoPacket dataInfoPacket = new ResourcePackDataInfoPacket();
                                dataInfoPacket.packId = resourcePack.getPackId();
                                dataInfoPacket.maxChunkSize = 1048576; //megabyte
                                dataInfoPacket.chunkCount = resourcePack.getPackSize() / dataInfoPacket.maxChunkSize;
                                dataInfoPacket.compressedPackSize = resourcePack.getPackSize();
                                dataInfoPacket.sha256 = resourcePack.getSha256();
                                this.dataPacket(dataInfoPacket);
                            }
                            break;
                        case ResourcePackClientResponsePacket.STATUS_HAVE_ALL_PACKS:
                            ResourcePackStackPacket stackPacket = new ResourcePackStackPacket();
                            stackPacket.mustAccept = this.server.getForceResources();
                            stackPacket.resourcePackStack = this.server.getResourcePackManager().getResourceStack();
                            this.dataPacket(stackPacket);
                            break;
                        case ResourcePackClientResponsePacket.STATUS_COMPLETED:
                            this.processLogin();
                            break;
                    }
                    break;
                case ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET:
                    ResourcePackChunkRequestPacket requestPacket = (ResourcePackChunkRequestPacket) packet;
                    ResourcePack resourcePack = this.server.getResourcePackManager().getPackById(requestPacket.packId);
                    if (resourcePack == null) {
                        this.close("", "disconnectionScreen.resourcePack");
                        break;
                    }

                    ResourcePackChunkDataPacket dataPacket = new ResourcePackChunkDataPacket();
                    dataPacket.packId = resourcePack.getPackId();
                    dataPacket.chunkIndex = requestPacket.chunkIndex;
                    dataPacket.data = resourcePack.getPackChunk(1048576 * requestPacket.chunkIndex, 1048576);
                    dataPacket.progress = 1048576 * requestPacket.chunkIndex;
                    this.dataPacket(dataPacket);
                    break;
                case ProtocolInfo.PLAYER_INPUT_PACKET:
                    if(!this.isAlive() || !this.spawned){
                        break;
                    }
                    PlayerInputPacket ipk = (PlayerInputPacket) packet;
                    if(riding instanceof EntityMinecartAbstract){
                        ((EntityMinecartEmpty) riding).setCurrentSpeed(ipk.motionY);
                    }
                    break;
                case ProtocolInfo.MOVE_PLAYER_PACKET:
                    if (this.teleportPosition != null) {
                        break;
                    }

                    MovePlayerPacket movePlayerPacket = (MovePlayerPacket) packet;
                    Vector3 newPos = new Vector3(movePlayerPacket.x, movePlayerPacket.y - this.getEyeHeight(), movePlayerPacket.z);

                    if (newPos.distanceSquared(this) < 0.01 && movePlayerPacket.yaw % 360 == this.yaw && movePlayerPacket.pitch % 360 == this.pitch) {
                        break;
                    }

                    boolean revert = false;
                    if (!this.isAlive() || !this.spawned) {
                        revert = true;
                        this.forceMovement = new Vector3(this.x, this.y, this.z);
                    }

                    if (this.forceMovement != null && (newPos.distanceSquared(this.forceMovement) > 0.1 || revert)) {
                        this.sendPosition(this.forceMovement, movePlayerPacket.yaw, movePlayerPacket.pitch, MovePlayerPacket.MODE_RESET);
                    } else {

                        movePlayerPacket.yaw %= 360;
                        movePlayerPacket.pitch %= 360;

                        if (movePlayerPacket.yaw < 0) {
                            movePlayerPacket.yaw += 360;
                        }

                        this.setRotation(movePlayerPacket.yaw, movePlayerPacket.pitch);
                        this.newPosition = newPos;
                        this.forceMovement = null;
                    }

                    if (riding != null) {
                        if (riding instanceof EntityBoat) {
                            riding.setPositionAndRotation(this.temporalVector.setComponents(movePlayerPacket.x, movePlayerPacket.y - 1, movePlayerPacket.z), (movePlayerPacket.headYaw + 90) % 360, 0);
                        }
                    }

                    break;
                case ProtocolInfo.ADVENTURE_SETTINGS_PACKET:
                    //TODO: player abilities, check for other changes
                    AdventureSettingsPacket adventureSettingsPacket = (AdventureSettingsPacket) packet;
                    if (adventureSettingsPacket.isFlying && !this.getAdventureSettings().canFly()) {
                        this.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server");
                        break;
                    }
                    PlayerToggleFlightEvent playerToggleFlightEvent = new PlayerToggleFlightEvent(this, adventureSettingsPacket.isFlying);
                    this.server.getPluginManager().callEvent(playerToggleFlightEvent);
                    if (playerToggleFlightEvent.isCancelled()) {
                        this.getAdventureSettings().update();
                    } else {
                        this.getAdventureSettings().setFlying(playerToggleFlightEvent.isFlying());
                    }
                    break;
                case ProtocolInfo.MOB_EQUIPMENT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    MobEquipmentPacket mobEquipmentPacket = (MobEquipmentPacket) packet;

                    if (mobEquipmentPacket.slot == 0x28 || mobEquipmentPacket.slot == 0 || mobEquipmentPacket.slot == 255) {
                        mobEquipmentPacket.slot = -1;
                    } else {
                        mobEquipmentPacket.slot -= 9;
                    }

                    Item item;
                    int slot;
                    if (this.isCreative()) {
                        item = mobEquipmentPacket.item;
                        slot = Item.getCreativeItemIndex(item);
                    } else {
                        item = this.inventory.getItem(mobEquipmentPacket.slot);
                        slot = mobEquipmentPacket.slot;
                    }

                    if (mobEquipmentPacket.slot == -1) {
                        if (this.isCreative()) {
                            boolean found = false;
                            for (int i = 0; i < this.inventory.getHotbarSize(); ++i) {
                                if (this.inventory.getHotbarSlotIndex(i) == -1) {
                                    this.inventory.setHeldItemIndex(i);
                                    found = true;
                                    break;
                                }

                            }
                            if (!found) {
                                this.inventory.sendContents(this);
                                break;
                            }
                        } else {
                            if (mobEquipmentPacket.selectedSlot >= 0 && mobEquipmentPacket.selectedSlot < 9) {
                                this.inventory.setHeldItemIndex(mobEquipmentPacket.selectedSlot);
                                this.inventory.setHeldItemSlot(mobEquipmentPacket.slot);
                            } else {
                                this.inventory.sendContents(this);
                                break;
                            }
                        }
                    } else if (item == null || slot == -1 || !item.deepEquals(mobEquipmentPacket.item)) {
                        this.inventory.sendContents(this);
                        break;
                    } else if (this.isCreative()) {
                        this.inventory.setHeldItemIndex(mobEquipmentPacket.selectedSlot);
                        this.inventory.setItem(mobEquipmentPacket.selectedSlot, item);
                        this.inventory.setHeldItemSlot(mobEquipmentPacket.selectedSlot);
                    } else {
                        if (mobEquipmentPacket.selectedSlot >= 0 && mobEquipmentPacket.selectedSlot < this.inventory.getHotbarSize()) {
                            this.inventory.setHeldItemIndex(mobEquipmentPacket.selectedSlot);
                            this.inventory.setHeldItemSlot(slot);
                        } else {
                            this.inventory.sendContents(this);
                            break;
                        }
                    }

                    this.inventory.sendHeldItem(this.hasSpawned.values());

                    this.setDataFlag(Player.DATA_FLAGS, Player.DATA_FLAG_ACTION, false);
                    break;
                case ProtocolInfo.USE_ITEM_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    UseItemPacket useItemPacket = (UseItemPacket) packet;

                    Vector3 blockVector = new Vector3(useItemPacket.x, useItemPacket.y, useItemPacket.z);

                    this.craftingType = CRAFTING_SMALL;

                    if (useItemPacket.face >= 0 && useItemPacket.face <= 5) {
                        BlockFace face = BlockFace.fromIndex(useItemPacket.face);
                        this.setDataFlag(Player.DATA_FLAGS, Player.DATA_FLAG_ACTION, false);

                        if (!this.canInteract(blockVector.add(0.5, 0.5, 0.5), this.isCreative() ? 13 : 7)) {
                        } else if (this.isCreative()) {
                            Item i = this.inventory.getItemInHand();
                            if (this.level.useItemOn(blockVector, i, face, useItemPacket.fx, useItemPacket.fy, useItemPacket.fz, this, true) != null) {
                                break;
                            }
                        } else if (!this.inventory.getItemInHand().deepEquals(useItemPacket.item)) {
                            this.inventory.sendHeldItem(this);
                        } else {
                            item = this.inventory.getItemInHand();
                            Item oldItem = item.clone();
                            //TODO: Implement adventure mode checks
                            if ((item = this.level.useItemOn(blockVector, item, face, useItemPacket.fx, useItemPacket.fy, useItemPacket.fz, this, true)) != null) {
                                if (!item.deepEquals(oldItem) || item.getCount() != oldItem.getCount()) {
                                    this.inventory.setItemInHand(item);
                                    this.inventory.sendHeldItem(this.hasSpawned.values());
                                }
                                break;
                            }
                        }

                        this.inventory.sendHeldItem(this);

                        if (blockVector.distanceSquared(this) > 10000) {
                            break;
                        }

                        Block target = this.level.getBlock(blockVector);
                        Block block = target.getSide(face);

                        if (target instanceof BlockDoor) {
                            BlockDoor door = (BlockDoor) target;

                            Block part;

                            if ((door.getDamage() & 0x08) > 0) { //up
                                part = target.down();

                                if (part.getId() == target.getId()) {
                                    target = part;
                                }
                            }
                        }

                        this.level.sendBlocks(new Player[]{this}, new Block[]{target, block}, UpdateBlockPacket.FLAG_ALL_PRIORITY);
                        break;
                    } else if (useItemPacket.face == -1) {
                        Vector3 aimPos = new Vector3(
                                -Math.sin(this.yaw / 180d * Math.PI) * Math.cos(this.pitch / 180d * Math.PI),
                                -Math.sin(this.pitch / 180d * Math.PI),
                                Math.cos(this.yaw / 180d * Math.PI) * Math.cos(this.pitch / 180d * Math.PI)
                        );

                        if (this.isCreative()) {
                            item = this.inventory.getItemInHand();
                        } else if (!this.inventory.getItemInHand().deepEquals(useItemPacket.item)) {
                            this.inventory.sendHeldItem(this);
                            break;
                        } else {
                            item = this.inventory.getItemInHand();
                        }

                        PlayerInteractEvent playerInteractEvent = new PlayerInteractEvent(this, item, aimPos, null, Action.RIGHT_CLICK_AIR);

                        this.server.getPluginManager().callEvent(playerInteractEvent);

                        if (playerInteractEvent.isCancelled()) {
                            this.inventory.sendHeldItem(this);
                            break;
                        }

                        if (item.getId() == Item.SNOWBALL) {
                            CompoundTag nbt = new CompoundTag()
                                    .putList(new ListTag<DoubleTag>("Pos")
                                            .add(new DoubleTag("", x))
                                            .add(new DoubleTag("", y + this.getEyeHeight()))
                                            .add(new DoubleTag("", z)))
                                    .putList(new ListTag<DoubleTag>("Motion")
                                            .add(new DoubleTag("", -Math.sin(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI)))
                                            .add(new DoubleTag("", -Math.sin(pitch / 180 * Math.PI)))
                                            .add(new DoubleTag("", Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI))))
                                    .putList(new ListTag<FloatTag>("Rotation")
                                            .add(new FloatTag("", (float) yaw))
                                            .add(new FloatTag("", (float) pitch)));

                            float f = 1.5f;
                            EntitySnowball snowball = new EntitySnowball(this.chunk, nbt, this);

                            snowball.setMotion(snowball.getMotion().multiply(f));
                            if (this.isSurvival()) {
                                item.setCount(item.getCount() - 1);
                                this.inventory.setItemInHand(item.getCount() > 0 ? item : Item.get(Item.AIR));
                            }
                            if (snowball instanceof EntityProjectile) {
                                ProjectileLaunchEvent projectileLaunchEvent = new ProjectileLaunchEvent(snowball);
                                this.server.getPluginManager().callEvent(projectileLaunchEvent);
                                if (projectileLaunchEvent.isCancelled()) {
                                    snowball.kill();
                                } else {
                                    snowball.spawnToAll();
                                    this.level.addSound(new LaunchSound(this), this.getViewers().values());
                                }
                            } else {
                                snowball.spawnToAll();
                            }
                        } else if (item.getId() == Item.EGG) {
                            CompoundTag nbt = new CompoundTag()
                                    .putList(new ListTag<DoubleTag>("Pos")
                                            .add(new DoubleTag("", x))
                                            .add(new DoubleTag("", y + this.getEyeHeight()))
                                            .add(new DoubleTag("", z)))
                                    .putList(new ListTag<DoubleTag>("Motion")
                                            .add(new DoubleTag("", -Math.sin(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI)))
                                            .add(new DoubleTag("", -Math.sin(pitch / 180 * Math.PI)))
                                            .add(new DoubleTag("", Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI))))
                                    .putList(new ListTag<FloatTag>("Rotation")
                                            .add(new FloatTag("", (float) yaw))
                                            .add(new FloatTag("", (float) pitch)));

                            float f = 1.5f;
                            EntityEgg egg = new EntityEgg(this.chunk, nbt, this);

                            egg.setMotion(egg.getMotion().multiply(f));
                            if (this.isSurvival()) {
                                item.setCount(item.getCount() - 1);
                                this.inventory.setItemInHand(item.getCount() > 0 ? item : Item.get(Item.AIR));
                            }
                            if (egg instanceof EntityProjectile) {
                                ProjectileLaunchEvent projectileLaunchEvent = new ProjectileLaunchEvent(egg);
                                this.server.getPluginManager().callEvent(projectileLaunchEvent);
                                if (projectileLaunchEvent.isCancelled()) {
                                    egg.kill();
                                } else {
                                    egg.spawnToAll();
                                    this.level.addSound(new LaunchSound(this), this.getViewers().values());
                                }
                            } else {
                                egg.spawnToAll();
                            }
                        } else if (item.getId() == Item.ENDER_PEARL && (this.server.getTick() - this.lastEnderPearl) >= 20) {
                            CompoundTag nbt = new CompoundTag()
                                    .putList(new ListTag<DoubleTag>("Pos")
                                            .add(new DoubleTag("", x))
                                            .add(new DoubleTag("", y + this.getEyeHeight()))
                                            .add(new DoubleTag("", z)))
                                    .putList(new ListTag<DoubleTag>("Motion")
                                            .add(new DoubleTag("", -Math.sin(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI)))
                                            .add(new DoubleTag("", -Math.sin(pitch / 180 * Math.PI)))
                                            .add(new DoubleTag("", Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI))))
                                    .putList(new ListTag<FloatTag>("Rotation")
                                            .add(new FloatTag("", (float) yaw))
                                            .add(new FloatTag("", (float) pitch)));

                            float f = 1.5f;
                            EntityEnderPearl enderPearl = new EntityEnderPearl(this.chunk, nbt, this);

                            enderPearl.setMotion(enderPearl.getMotion().multiply(f));
                            if (this.isSurvival()) {
                                item.setCount(item.getCount() - 1);
                                this.inventory.setItemInHand(item.getCount() > 0 ? item : Item.get(Item.AIR));
                            }
                            if (enderPearl instanceof EntityProjectile) {
                                ProjectileLaunchEvent projectileLaunchEvent = new ProjectileLaunchEvent(enderPearl);
                                this.server.getPluginManager().callEvent(projectileLaunchEvent);
                                if (projectileLaunchEvent.isCancelled()) {
                                    enderPearl.kill();
                                } else {
                                    enderPearl.spawnToAll();
                                    this.level.addSound(new LaunchSound(this), this.getViewers().values());
                                }
                            } else {
                                enderPearl.spawnToAll();
                            }
                            this.lastEnderPearl = this.server.getTick();
                        } else if (item.getId() == Item.EXPERIENCE_BOTTLE) {
                            CompoundTag nbt = new CompoundTag()
                                    .putList(new ListTag<DoubleTag>("Pos")
                                            .add(new DoubleTag("", x))
                                            .add(new DoubleTag("", y + this.getEyeHeight()))
                                            .add(new DoubleTag("", z)))
                                    .putList(new ListTag<DoubleTag>("Motion")
                                            .add(new DoubleTag("", -Math.sin(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI)))
                                            .add(new DoubleTag("", -Math.sin(pitch / 180 * Math.PI)))
                                            .add(new DoubleTag("", Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI))))
                                    .putList(new ListTag<FloatTag>("Rotation")
                                            .add(new FloatTag("", (float) yaw))
                                            .add(new FloatTag("", (float) pitch)))
                                    .putInt("Potion", item.getDamage());
                            double f = 1.5;
                            Entity bottle = new EntityExpBottle(this.chunk, nbt, this);
                            bottle.setMotion(bottle.getMotion().multiply(f));
                            if (this.isSurvival()) {
                                item.setCount(item.getCount() - 1);
                                this.inventory.setItemInHand(item.getCount() > 0 ? item : Item.get(Item.AIR));
                            }
                            if (bottle instanceof EntityProjectile) {
                                EntityProjectile bottleEntity = (EntityProjectile) bottle;
                                ProjectileLaunchEvent projectileEv = new ProjectileLaunchEvent(bottleEntity);
                                this.server.getPluginManager().callEvent(projectileEv);
                                if (projectileEv.isCancelled()) {
                                    bottle.kill();
                                } else {
                                    bottle.spawnToAll();
                                    this.level.addSound(new LaunchSound(this), this.getViewers().values());
                                }
                            } else {
                                bottle.spawnToAll();
                            }
                        } else if (item.getId() == Item.SPLASH_POTION) {
                            CompoundTag nbt = new CompoundTag()
                                    .putList(new ListTag<DoubleTag>("Pos")
                                            .add(new DoubleTag("", x))
                                            .add(new DoubleTag("", y + this.getEyeHeight()))
                                            .add(new DoubleTag("", z)))
                                    .putList(new ListTag<DoubleTag>("Motion")
                                            .add(new DoubleTag("", -Math.sin(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI)))
                                            .add(new DoubleTag("", -Math.sin(pitch / 180 * Math.PI)))
                                            .add(new DoubleTag("", Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI))))
                                    .putList(new ListTag<FloatTag>("Rotation")
                                            .add(new FloatTag("", (float) yaw))
                                            .add(new FloatTag("", (float) pitch)))
                                    .putShort("PotionId", item.getDamage());
                            double f = 1.5;
                            Entity bottle = new EntityPotion(this.chunk, nbt, this);
                            bottle.setMotion(bottle.getMotion().multiply(f));
                            if (this.isSurvival()) {
                                item.setCount(item.getCount() - 1);
                                this.inventory.setItemInHand(item.getCount() > 0 ? item : Item.get(Item.AIR));
                            }
                            if (bottle instanceof EntityPotion) {
                                EntityPotion bottleEntity = (EntityPotion) bottle;
                                ProjectileLaunchEvent projectileEv = new ProjectileLaunchEvent(bottleEntity);
                                this.server.getPluginManager().callEvent(projectileEv);
                                if (projectileEv.isCancelled()) {
                                    bottle.kill();
                                } else {
                                    bottle.spawnToAll();
                                    this.level.addSound(new LaunchSound(this), this.getViewers().values());
                                }
                            } else {
                                bottle.spawnToAll();
                            }
                        }

                        this.setDataFlag(Player.DATA_FLAGS, Player.DATA_FLAG_ACTION, true);
                        this.startAction = this.server.getTick();
                    }
                    break;
                case ProtocolInfo.PLAYER_ACTION_PACKET:
                    PlayerActionPacket playerActionPacket = (PlayerActionPacket) packet;
                    if (!this.spawned || (!this.isAlive() && playerActionPacket.action != PlayerActionPacket.ACTION_RESPAWN && playerActionPacket.action != PlayerActionPacket.ACTION_DIMENSION_CHANGE)) {
                        break;
                    }

                    playerActionPacket.entityId = this.id;
                    Vector3 pos = new Vector3(playerActionPacket.x, playerActionPacket.y, playerActionPacket.z);
                    BlockFace face = BlockFace.fromIndex(playerActionPacket.face);

                    switch (playerActionPacket.action) {
                        case PlayerActionPacket.ACTION_START_BREAK:
                            if (this.lastBreak != Long.MAX_VALUE || pos.distanceSquared(this) > 10000) {
                                break;
                            }
                            Block target = this.level.getBlock(pos);
                            PlayerInteractEvent playerInteractEvent = new PlayerInteractEvent(this, this.inventory.getItemInHand(), target, face, target.getId() == 0 ? Action.LEFT_CLICK_AIR : Action.LEFT_CLICK_BLOCK);
                            this.getServer().getPluginManager().callEvent(playerInteractEvent);
                            if (playerInteractEvent.isCancelled()) {
                                this.inventory.sendHeldItem(this);
                                break;
                            }
                            if (target.getId() == Block.NOTEBLOCK) {
                                ((BlockNoteblock)target).emitSound();
                                break;
                            }
                            Block block = target.getSide(face);
                            if (block.getId() == Block.FIRE) {
                                this.level.setBlock(block, new BlockAir(), true);
                                break;
                            }
                            if(!this.isCreative()){
                                //improved this to take stuff like swimming, ladders, enchanted tools into account, fix wrong tool break time calculations for bad tools (pmmp/PocketMine-MP#211)
                                //Done by lmlstarqaq
                                double breakTime = Math.ceil(target.getBreakTime(this.inventory.getItemInHand(), this) * 20);
                                if (breakTime > 0) {
                                    LevelEventPacket pk = new LevelEventPacket();
                                    pk.evid = LevelEventPacket.EVENT_BLOCK_START_BREAK;
                                    pk.x = (float)pos.x;
                                    pk.y = (float)pos.y;
                                    pk.z = (float)pos.z;
                                    pk.data = (int)(65535 / breakTime);
                                    this.getLevel().addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
                                }
                            }
                            this.lastBreak = System.currentTimeMillis();
                            break;

                        case PlayerActionPacket.ACTION_ABORT_BREAK:
                            this.lastBreak = Long.MAX_VALUE;

                        case PlayerActionPacket.ACTION_STOP_BREAK:
                            LevelEventPacket pk = new LevelEventPacket();
                            pk.evid = LevelEventPacket.EVENT_BLOCK_STOP_BREAK;
                            pk.x = (float)pos.x;
                            pk.y = (float)pos.y;
                            pk.z = (float)pos.z;
                            pk.data = 0;
                            this.getLevel().addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
                            break;

                        case PlayerActionPacket.ACTION_RELEASE_ITEM:
                            if (this.startAction > -1 && this.getDataFlag(Player.DATA_FLAGS, Player.DATA_FLAG_ACTION)) {
                                if (this.inventory.getItemInHand().getId() == Item.BOW) {

                                    Item bow = this.inventory.getItemInHand();
                                    ItemArrow itemArrow = new ItemArrow();
                                    if (this.isSurvival() && !this.inventory.contains(itemArrow)) {
                                        this.inventory.sendContents(this);
                                        break;
                                    }

                                    double damage = 2;
                                    boolean flame = false;

                                    if (bow.hasEnchantments()) {
                                        Enchantment bowDamage = bow.getEnchantment(Enchantment.ID_BOW_POWER);

                                        if (bowDamage != null && bowDamage.getLevel() > 0) {
                                            damage += 0.25 * (bowDamage.getLevel() + 1);
                                        }

                                        Enchantment flameEnchant = bow.getEnchantment(Enchantment.ID_BOW_FLAME);
                                        flame = flameEnchant != null && flameEnchant.getLevel() > 0;
                                    }

                                    CompoundTag nbt = new CompoundTag()
                                            .putList(new ListTag<DoubleTag>("Pos")
                                                    .add(new DoubleTag("", x))
                                                    .add(new DoubleTag("", y + this.getEyeHeight()))
                                                    .add(new DoubleTag("", z)))
                                            .putList(new ListTag<DoubleTag>("Motion")
                                                    .add(new DoubleTag("", -Math.sin(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI)))
                                                    .add(new DoubleTag("", -Math.sin(pitch / 180 * Math.PI)))
                                                    .add(new DoubleTag("", Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI))))
                                            .putList(new ListTag<FloatTag>("Rotation")
                                                    .add(new FloatTag("", (yaw > 180 ? 360 : 0) - (float) yaw))
                                                    .add(new FloatTag("", (float) -pitch)))
                                            .putShort("Fire", this.isOnFire() || flame ? 45 * 60 : 0)
                                            .putDouble("damage", damage);

                                    int diff = (this.server.getTick() - this.startAction);
                                    double p = (double) diff / 20;

                                    double f = Math.min((p * p + p * 2) / 3, 1) * 2;
                                    EntityShootBowEvent entityShootBowEvent = new EntityShootBowEvent(this, bow, new EntityArrow(this.chunk, nbt, this, f == 2), f);

                                    if (f < 0.1 || diff < 5) {
                                        entityShootBowEvent.setCancelled();
                                    }

                                    this.server.getPluginManager().callEvent(entityShootBowEvent);
                                    if (entityShootBowEvent.isCancelled()) {
                                        entityShootBowEvent.getProjectile().kill();
                                        this.inventory.sendContents(this);
                                    } else {
                                        entityShootBowEvent.getProjectile().setMotion(entityShootBowEvent.getProjectile().getMotion().multiply(entityShootBowEvent.getForce()));
                                        if (this.isSurvival()) {
                                            Enchantment infinity;

                                            if (!bow.hasEnchantments() || (infinity = bow.getEnchantment(Enchantment.ID_BOW_INFINITY)) == null || infinity.getLevel() <= 0)
                                                this.inventory.removeItem(itemArrow);

                                            if (!bow.isUnbreakable()) {
                                                Enchantment durability = bow.getEnchantment(Enchantment.ID_DURABILITY);
                                                if (!(durability != null && durability.getLevel() > 0 && (100 / (durability.getLevel() + 1)) <= new Random().nextInt(100))) {
                                                    bow.setDamage(bow.getDamage() + 1);
                                                    if (bow.getDamage() >= 385) {
                                                        this.inventory.setItemInHand(new ItemBlock(new BlockAir(), 0, 0));
                                                    } else {
                                                        this.inventory.setItemInHand(bow);
                                                    }
                                                }
                                            }
                                        }
                                        if (entityShootBowEvent.getProjectile() instanceof EntityProjectile) {
                                            ProjectileLaunchEvent projectev = new ProjectileLaunchEvent(entityShootBowEvent.getProjectile());
                                            this.server.getPluginManager().callEvent(projectev);
                                            if (projectev.isCancelled()) {
                                                entityShootBowEvent.getProjectile().kill();
                                            } else {
                                                entityShootBowEvent.getProjectile().spawnToAll();
                                                this.level.addSound(new LaunchSound(this), this.getViewers().values());
                                            }
                                        } else {
                                            entityShootBowEvent.getProjectile().spawnToAll();
                                        }
                                    }
                                }
                            }
                            //milk removed here, see the section of food

                        case PlayerActionPacket.ACTION_STOP_SLEEPING:
                            this.stopSleep();
                            break;

                        case PlayerActionPacket.ACTION_RESPAWN:
                            if (!this.spawned || this.isAlive() || !this.isOnline()) {
                                break;
                            }

                            if (this.server.isHardcore()) {
                                this.setBanned(true);
                                break;
                            }

                            this.craftingType = CRAFTING_SMALL;

                            PlayerRespawnEvent playerRespawnEvent = new PlayerRespawnEvent(this, this.getSpawn());
                            this.server.getPluginManager().callEvent(playerRespawnEvent);

                            Position respawnPos = playerRespawnEvent.getRespawnPosition();

                            this.teleport(respawnPos, null);

                            RespawnPacket respawnPacket = new RespawnPacket();
                            respawnPacket.x = (float) respawnPos.x;
                            respawnPacket.y = (float) respawnPos.y;
                            respawnPacket.z = (float) respawnPos.z;
                            this.dataPacket(respawnPacket);

                            this.setSprinting(false, true);
                            this.setSneaking(false);

                            this.extinguish();
                            this.setDataProperty(new ShortEntityData(Player.DATA_AIR, 400), false);
                            this.deadTicks = 0;
                            this.noDamageTicks = 60;

                            this.removeAllEffects();
                            this.setHealth(this.getMaxHealth());
                            this.getFoodData().setLevel(20, 20);

                            this.sendData(this);

                            this.setMovementSpeed(DEFAULT_SPEED);

                            this.getAdventureSettings().update();
                            this.inventory.sendContents(this);
                            this.inventory.sendArmorContents(this);

                            this.spawnToAll();
                            this.scheduleUpdate();
                            break;

                        case PlayerActionPacket.ACTION_JUMP:
                            break packetswitch;

                        case PlayerActionPacket.ACTION_START_SPRINT:
                            PlayerToggleSprintEvent playerToggleSprintEvent = new PlayerToggleSprintEvent(this, true);
                            this.server.getPluginManager().callEvent(playerToggleSprintEvent);
                            if (playerToggleSprintEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSprinting(true);
                            }
                            break packetswitch;

                        case PlayerActionPacket.ACTION_STOP_SPRINT:
                            playerToggleSprintEvent = new PlayerToggleSprintEvent(this, false);
                            this.server.getPluginManager().callEvent(playerToggleSprintEvent);
                            if (playerToggleSprintEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSprinting(false);
                            }
                            break packetswitch;

                        case PlayerActionPacket.ACTION_START_SNEAK:
                            PlayerToggleSneakEvent playerToggleSneakEvent = new PlayerToggleSneakEvent(this, true);
                            this.server.getPluginManager().callEvent(playerToggleSneakEvent);
                            if (playerToggleSneakEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSneaking(true);
                            }
                            break packetswitch;

                        case PlayerActionPacket.ACTION_STOP_SNEAK:
                            playerToggleSneakEvent = new PlayerToggleSneakEvent(this, false);
                            this.server.getPluginManager().callEvent(playerToggleSneakEvent);
                            if (playerToggleSneakEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSneaking(false);
                            }
                            break packetswitch;

                        case PlayerActionPacket.ACTION_START_GLIDE:
                            PlayerToggleGlideEvent playerToggleGlideEvent = new PlayerToggleGlideEvent(this, true);
                            this.server.getPluginManager().callEvent(playerToggleGlideEvent);
                            if (playerToggleGlideEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setGliding(true);
                            }
                            break packetswitch;

                        case PlayerActionPacket.ACTION_STOP_GLIDE:
                            playerToggleGlideEvent = new PlayerToggleGlideEvent(this, false);
                            this.server.getPluginManager().callEvent(playerToggleGlideEvent);
                            if (playerToggleGlideEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setGliding(false);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_WORLD_IMMUTABLE:
                            break; //TODO
                        case PlayerActionPacket.ACTION_CONTINUE_BREAK:
                            block = this.level.getBlock(pos);
                            this.level.addParticle(new PunchBlockParticle(pos, block, face));
                            break;
                    }

                    this.startAction = -1;
                    this.setDataFlag(Player.DATA_FLAGS, Player.DATA_FLAG_ACTION, false);
                    break;
                case ProtocolInfo.REMOVE_BLOCK_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }
                    this.craftingType = CRAFTING_SMALL;

                    Vector3 vector = new Vector3(((RemoveBlockPacket) packet).x, ((RemoveBlockPacket) packet).y, ((RemoveBlockPacket) packet).z);

                    if (this.isCreative()) {
                        item = this.inventory.getItemInHand();
                    } else {
                        item = this.inventory.getItemInHand();
                    }

                    Item oldItem = item.clone();

                    if (this.canInteract(vector.add(0.5, 0.5, 0.5), this.isCreative() ? 13 : 7) && (item = this.level.useBreakOn(vector, item, this, true)) != null) {
                        if (this.isSurvival()) {
                            this.getFoodData().updateFoodExpLevel(0.025);
                            if (!item.deepEquals(oldItem) || item.getCount() != oldItem.getCount()) {
                                this.inventory.setItemInHand(item);
                                this.inventory.sendHeldItem(this.hasSpawned.values());
                            }
                        }
                        break;
                    }

                    this.inventory.sendContents(this);
                    Block target = this.level.getBlock(vector);
                    BlockEntity blockEntity = this.level.getBlockEntity(vector);

                    this.level.sendBlocks(new Player[]{this}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY);

                    this.inventory.sendHeldItem(this);

                    if (blockEntity instanceof BlockEntitySpawnable) {
                        ((BlockEntitySpawnable) blockEntity).spawnTo(this);
                    }
                    break;

                case ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET:
                    break;

                case ProtocolInfo.INTERACT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    InteractPacket interactPacket = (InteractPacket) packet;
                    
                    Entity targetEntity = this.level.getEntity(interactPacket.target);

                    if (targetEntity == null || !this.isAlive() || !targetEntity.isAlive()) {
                        break;
                    }

                    this.craftingType = CRAFTING_SMALL;

                    if (targetEntity instanceof EntityItem || targetEntity instanceof EntityArrow || targetEntity instanceof EntityXPOrb) {
                        this.kick(PlayerKickEvent.Reason.INVALID_PVE, "Attempting to attack an invalid entity");
                        this.server.getLogger().warning(this.getServer().getLanguage().translateString("nukkit.player.invalidEntity", this.getName()));
                        break;
                    }
                    
                    item = this.inventory.getItemInHand();

                    switch (interactPacket.action) {
                        case InteractPacket.ACTION_MOUSEOVER:
                            this.getServer().getPluginManager().callEvent(new PlayerMouseOverEntityEvent(this, targetEntity));
                            break;
                        case InteractPacket.ACTION_LEFT_CLICK:
                            if (this.getGamemode() == Player.VIEW) {
                                break;
                            }

                            float itemDamage = item.getAttackDamage();

                            for (Enchantment enchantment : item.getEnchantments()) {
                                itemDamage += enchantment.getDamageBonus(targetEntity);
                            }

                            Map<DamageModifier, Float> damage = new EnumMap<>(DamageModifier.class);
                            damage.put(DamageModifier.BASE, itemDamage);

                            if (!this.canInteract(targetEntity, isCreative() ? 8 : 5)) {
                                break;
                            } else if (targetEntity instanceof Player) {
                                if ((((Player) targetEntity).getGamemode() & 0x01) > 0) {
                                    break;
                                } else if (!this.server.getPropertyBoolean("pvp") || this.server.getDifficulty() == 0) {
                                    break;
                                }
                            }

                            if (!targetEntity.attack(new EntityDamageByEntityEvent(this, targetEntity, DamageCause.ENTITY_ATTACK, damage))) {
                                if (item.isTool() && this.isSurvival()) {
                                    this.inventory.sendContents(this);
                                }
                                break;
                            }

                            for (Enchantment enchantment : item.getEnchantments()) {
                                enchantment.doPostAttack(this, targetEntity);
                            }

                            if (item.isTool() && this.isSurvival()) {
                                if (item.useOn(targetEntity) && item.getDamage() >= item.getMaxDurability()) {
                                    this.inventory.setItemInHand(new ItemBlock(new BlockAir()));
                                } else {
                                    this.inventory.setItemInHand(item);
                                }
                            }

                            break;
                        case InteractPacket.ACTION_RIGHT_CLICK:
                            PlayerInteractEntityEvent playerInteractEntityEvent = new PlayerInteractEntityEvent(this, targetEntity, item);
                            getServer().getPluginManager().callEvent(playerInteractEntityEvent);

                            if (playerInteractEntityEvent.isCancelled()) {
                                break;
                            }

                            if (targetEntity.onInteract(this, item) && this.isSurvival()) {
                                if (item.isTool()) {
                                    if (item.useOn(targetEntity) && item.getDamage() >= item.getMaxDurability()) {
                                        item = new ItemBlock(new BlockAir());
                                    }
                                } else {
                                    if (item.count > 1) {
                                        item.count--;
                                    } else {
                                        item = new ItemBlock(new BlockAir());
                                    }
                                }

                                this.inventory.setItemInHand(item);
                            }
                            break;
                        case InteractPacket.ACTION_VEHICLE_EXIT:
                            if (!(targetEntity instanceof EntityVehicle) || this.riding == null) {
                                break;
                            }

                            ((EntityVehicle) riding).mountEntity(this);
                            break;
                    }
                    break;
                case ProtocolInfo.BLOCK_PICK_REQUEST_PACKET:
                    BlockPickRequestPacket pickRequestPacket = (BlockPickRequestPacket) packet;
                    if (this.isCreative()) {
                        BlockEntity be = this.getLevel().getBlockEntity(new Vector3(pickRequestPacket.x, pickRequestPacket.y, pickRequestPacket.z));
                        if (be != null) {
                            CompoundTag nbt = be.getCleanedNBT();
                            if(nbt != null){
                                Item item1 = this.getInventory().getItemInHand();
                                item1.setCustomBlockData(nbt);
                                item1.setLore("+(DATA)");
                                this.getInventory().setItemInHand(item1);
                            }
                        }
                    }
                    break;
                case ProtocolInfo.ANIMATE_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    PlayerAnimationEvent animationEvent = new PlayerAnimationEvent(this, ((AnimatePacket) packet).action);
                    this.server.getPluginManager().callEvent(animationEvent);
                    if (animationEvent.isCancelled()) {
                        break;
                    }

                    AnimatePacket animatePacket = new AnimatePacket();
                    animatePacket.eid = this.getId();
                    animatePacket.action = animationEvent.getAnimationType();
                    Server.broadcastPacket(this.getViewers().values(), animatePacket);
                    break;
                case ProtocolInfo.SET_HEALTH_PACKET:
                    //use UpdateAttributePacket instead
                    break;

                case ProtocolInfo.ENTITY_EVENT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }
                    this.craftingType = CRAFTING_SMALL;

                    this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false); //TODO: check if this should be true
                    EntityEventPacket entityEventPacket = (EntityEventPacket) packet;

                    switch (entityEventPacket.event) {
                        case EntityEventPacket.USE_ITEM: //Eating
                            Item itemInHand = this.inventory.getItemInHand();
                            PlayerItemConsumeEvent consumeEvent = new PlayerItemConsumeEvent(this, itemInHand);
                            this.server.getPluginManager().callEvent(consumeEvent);
                            if (consumeEvent.isCancelled()) {
                                this.inventory.sendContents(this);
                                break;
                            }

                            if (itemInHand.getId() == Item.POTION) {
                                Potion potion = Potion.getPotion(itemInHand.getDamage()).setSplash(false);

                                if (this.getGamemode() == SURVIVAL) {
                                    if (itemInHand.getCount() > 1) {
                                        ItemGlassBottle bottle = new ItemGlassBottle();
                                        if (this.inventory.canAddItem(bottle)) {
                                            this.inventory.addItem(bottle);
                                        }
                                        --itemInHand.count;
                                    } else {
                                        itemInHand = new ItemGlassBottle();
                                    }
                                }

                                if (potion != null) {
                                    potion.applyPotion(this);
                                }

                            } else {
                                EntityEventPacket pk = new EntityEventPacket();
                                pk.eid = this.getId();
                                pk.event = EntityEventPacket.USE_ITEM;
                                this.dataPacket(pk);
                                Server.broadcastPacket(this.getViewers().values(), pk);

                                Food food = Food.getByRelative(itemInHand);
                                if (food != null) if (food.eatenBy(this)) --itemInHand.count;

                            }

                            this.inventory.setItemInHand(itemInHand);
                            this.inventory.sendHeldItem(this);

                            break;
                    }
                    break;
                case ProtocolInfo.DROP_ITEM_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }
                    DropItemPacket dropItem = (DropItemPacket) packet;

                    if (dropItem.item.getId() <= 0) {
                        break;
                    }

                    item = (this.isCreative() || this.inventory.contains(dropItem.item)) ? dropItem.item : this.inventory.getItemInHand();
                    PlayerDropItemEvent dropItemEvent = new PlayerDropItemEvent(this, item);
                    this.server.getPluginManager().callEvent(dropItemEvent);
                    if (dropItemEvent.isCancelled()) {
                        this.inventory.sendContents(this);
                        break;
                    }

                    if (!this.isCreative()) {
                        this.inventory.removeItem(item);
                    }
                    Vector3 motion = this.getDirectionVector().multiply(0.4);

                    this.level.dropItem(this.add(0, 1.3, 0), item, motion, 40);

                    this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false);
                    break;
                case ProtocolInfo.COMMAND_STEP_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }
                    this.craftingType = 0;
                    CommandStepPacket commandStepPacket = (CommandStepPacket) packet;
                    String commandText = commandStepPacket.command;
                    Command command = this.getServer().getCommandMap().getCommand(commandText);
                    if (command != null) {
                        if (commandStepPacket.args != null && commandStepPacket.args.size() > 0) {
                            CommandParameter[] pars = command.getCommandParameters(commandStepPacket.overload);
                            if (pars != null) {
                                for (CommandParameter par : pars) {
                                    JsonElement arg = commandStepPacket.args.get(par.name);
                                    if (arg != null) {
                                        switch (par.type) {
                                            case CommandParameter.ARG_TYPE_TARGET:
                                                CommandArg rules = new Gson().fromJson(arg, CommandArg.class);
                                                commandText += " " + rules.getRules()[0].getValue();
                                                break;
                                            case CommandParameter.ARG_TYPE_BLOCK_POS:
                                                CommandArgBlockVector bv = new Gson().fromJson(arg, CommandArgBlockVector.class);
                                                commandText += " " + bv.getX() + " " + bv.getY() + " " + bv.getZ();
                                                break;
                                            case CommandParameter.ARG_TYPE_STRING:
                                            case CommandParameter.ARG_TYPE_STRING_ENUM:
                                            case CommandParameter.ARG_TYPE_RAW_TEXT:
                                                String string = new Gson().fromJson(arg, String.class);
                                                commandText += " " + string;
                                                break;
                                            default:
                                                commandText += " " + arg.toString();
                                                break;
                                        }
                                    }
                                }
                            } else {
                                this.sendMessage(this.getServer().getLanguage().translateString(command.getUsage()));
                            }
                        }
                    }
                    PlayerCommandPreprocessEvent playerCommandPreprocessEvent = new PlayerCommandPreprocessEvent(this, "/" + commandText);
                    this.server.getPluginManager().callEvent(playerCommandPreprocessEvent);
                    if (playerCommandPreprocessEvent.isCancelled()) {
                        break;
                    }

                    Timings.playerCommandTimer.startTiming();
                    this.server.dispatchCommand(playerCommandPreprocessEvent.getPlayer(), playerCommandPreprocessEvent.getMessage().substring(1));
                    Timings.playerCommandTimer.stopTiming();
                    break;
                case ProtocolInfo.TEXT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    this.craftingType = CRAFTING_SMALL;
                    TextPacket textPacket = (TextPacket) packet;

                    if (textPacket.type == TextPacket.TYPE_CHAT) {
                        textPacket.message = this.removeFormat ? TextFormat.clean(textPacket.message) : textPacket.message;
                        for (String msg : textPacket.message.split("\n")) {
                            if (!"".equals(msg.trim()) && msg.length() <= 255 && this.messageCounter-- > 0) {
                                PlayerChatEvent chatEvent = new PlayerChatEvent(this, msg);
                                this.server.getPluginManager().callEvent(chatEvent);
                                if (!chatEvent.isCancelled()) {
                                    this.server.broadcastMessage(this.getServer().getLanguage().translateString(chatEvent.getFormat(), new String[]{chatEvent.getPlayer().getDisplayName(), chatEvent.getMessage()}), chatEvent.getRecipients());
                                }
                            }
                        }
                    }
                    break;
                case ProtocolInfo.CONTAINER_CLOSE_PACKET:
                    ContainerClosePacket containerClosePacket = (ContainerClosePacket) packet;
                    if (!this.spawned || containerClosePacket.windowid == 0) {
                        break;
                    }
                    this.craftingType = CRAFTING_SMALL;
                    this.currentTransaction = null;
                    if (this.windowIndex.containsKey(containerClosePacket.windowid)) {
                        this.server.getPluginManager().callEvent(new InventoryCloseEvent(this.windowIndex.get(containerClosePacket.windowid), this));
                        this.removeWindow(this.windowIndex.get(containerClosePacket.windowid));
                    } else {
                        this.windowIndex.remove(containerClosePacket.windowid);
                    }
                    break;

                case ProtocolInfo.CRAFTING_EVENT_PACKET:
                    CraftingEventPacket craftingEventPacket = (CraftingEventPacket) packet;

                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    Recipe recipe = this.server.getCraftingManager().getRecipe(craftingEventPacket.id);

                    if (this.craftingType == CRAFTING_ANVIL) {
                        Inventory inv = this.windowIndex.get(craftingEventPacket.windowId);
                        AnvilInventory anvilInventory = inv instanceof AnvilInventory ? (AnvilInventory) inv : null;

                        if (anvilInventory == null) {
                            anvilInventory = null;

                            for (Inventory window : this.windowIndex.values()) {
                                if (window instanceof AnvilInventory) {
                                    anvilInventory = (AnvilInventory) window;
                                    break;
                                }
                            }

                            if (anvilInventory == null) { //If it'sf _still_ null, then the player doesn't have a valid anvil window, cannot proceed.
                                this.getServer().getLogger().debug("Couldn't find an anvil window for " + this.getName() + ", exiting");
                                this.inventory.sendContents(this);
                                break;
                            }
                        }

                        if (recipe == null) {
                            //Item renamed

                            if (!anvilInventory.onRename(this, craftingEventPacket.output[0])) {
                                this.getServer().getLogger().debug(this.getName() + " failed to rename an item in an anvil");
                                this.inventory.sendContents(this);
                            }
                        } else {
                            //TODO: Anvil crafting recipes
                        }
                        break;
                    } else if (!this.windowIndex.containsKey(craftingEventPacket.windowId)) {
                        this.inventory.sendContents(this);
                        containerClosePacket = new ContainerClosePacket();
                        containerClosePacket.windowid = craftingEventPacket.windowId;
                        this.dataPacket(containerClosePacket);
                        break;
                    }

                    if ((recipe == null) || (((recipe instanceof BigShapelessRecipe) || (recipe instanceof BigShapedRecipe)) && this.craftingType == CRAFTING_SMALL)) {
                        this.inventory.sendContents(this);
                        break;
                    }

                    for (int i = 0; i < craftingEventPacket.input.length; i++) {
                        Item inputItem = craftingEventPacket.input[i];
                        if (inputItem.getDamage() == -1 || inputItem.getDamage() == 0xffff) {
                            inputItem.setDamage(null);
                        }

                        if (i < 9 && inputItem.getId() > 0) {
                            inputItem.setCount(1);
                        }
                    }

                    boolean canCraft = true;

                    if (craftingEventPacket.input.length == 0) {
                        Recipe[] recipes = getServer().getCraftingManager().getRecipesByResult(craftingEventPacket.output[0]);

                        recipe = null;

                        ArrayList<Item> ingredientz = new ArrayList<>();

                        recipeloop:
                        for (Recipe rec : recipes) {
                            ingredientz.clear();

                            if (rec instanceof ShapedRecipe) {
                                Map<Integer, Map<Integer, Item>> ingredients = ((ShapedRecipe) rec).getIngredientMap();
                                for (Map<Integer, Item> map : ingredients.values()) {
                                    for (Item ingredient : map.values()) {
                                        if (ingredient != null && ingredient.getId() != Item.AIR) {
                                            ingredientz.add(ingredient);
                                        }
                                    }
                                }
                            } else if (rec instanceof ShapelessRecipe) {
                                ShapelessRecipe recipe0 = (ShapelessRecipe) rec;

                                for (Item ingredient : recipe0.getIngredientList()) {
                                    if (ingredient != null && ingredient.getId() != Item.AIR) {
                                        ingredientz.add(ingredient);
                                    }
                                }
                            }

                            Map<String, Item> serialized = new HashMap<>();

                            for (Item ingredient : ingredientz) {
                                String hash = ingredient.getId() + ":" + ingredient.getDamage();
                                Item r = serialized.get(hash);

                                if (r != null) {
                                    r.count += ingredient.getCount();
                                    continue;
                                }

                                serialized.put(hash, ingredient);
                            }

                            for (Item ingredient : serialized.values()) {
                                if (!this.inventory.contains(ingredient)) {
                                    continue recipeloop;
                                }
                            }

                            recipe = rec;

                            CraftItemEvent craftItemEvent = new CraftItemEvent(this, serialized.values().stream().toArray(Item[]::new), recipe);
                            getServer().getPluginManager().callEvent(craftItemEvent);

                            if (craftItemEvent.isCancelled()) {
                                this.inventory.sendContents(this);
                                break packetswitch;
                            }

                            for (Item ingredient : serialized.values()) {
                                this.inventory.removeItem(ingredient);
                            }

                            this.inventory.addItem(recipe.getResult());
                            break;
                        }

                        if (recipe == null) {
                            this.server.getLogger().debug("(1) Unmatched desktop recipe " + craftingEventPacket.id + " from player " + this.getName());
                            this.inventory.sendContents(this);
                        }
                    } else {
                        ArrayList<Item> ingredientz = new ArrayList<>();

                        if (recipe instanceof ShapedRecipe) {

                            Map<Integer, Map<Integer, Item>> ingredients = ((ShapedRecipe) recipe).getIngredientMap();
                            for (Map<Integer, Item> map : ingredients.values()) {
                                for (Item ingredient : map.values()) {
                                    if (ingredient != null && ingredient.getId() != Item.AIR) {
                                        ingredientz.add(ingredient);
                                    }
                                }
                            }
                        } else if (recipe instanceof ShapelessRecipe) {
                            ShapelessRecipe recipe0 = (ShapelessRecipe) recipe;

                            for (Item ingredient : recipe0.getIngredientList()) {
                                if (ingredient != null && ingredient.getId() != Item.AIR) {
                                    ingredientz.add(ingredient);
                                }
                            }
                        }

                        Map<String, Item> serialized = new HashMap<>();

                        for (Item ingredient : ingredientz) {
                            String hash = ingredient.getId() + ":" + ingredient.getDamage();
                            Item r = serialized.get(hash);

                            if (r != null) {
                                r.count += ingredient.getCount();
                                continue;
                            }

                            serialized.put(hash, ingredient);
                        }

                        for (Item ingredient : serialized.values()) {
                            if (!this.inventory.contains(ingredient)) {
                                canCraft = false;
                                break;
                            }
                        }

                        if (!canCraft) {
                            this.server.getLogger().debug("(1) Unmatched recipe " + craftingEventPacket.id + " from player " + this.getName() + "  not anough ingredients");
                            return;
                        }

                        CraftItemEvent craftItemEvent = new CraftItemEvent(this, serialized.values().stream().toArray(Item[]::new), recipe);
                        getServer().getPluginManager().callEvent(craftItemEvent);

                        if (craftItemEvent.isCancelled()) {
                            this.inventory.sendContents(this);
                            break;
                        }

                        for (Item ingredient : serialized.values()) {
                            this.inventory.removeItem(ingredient);
                        }

                        this.inventory.addItem(recipe.getResult());

                        /*if (recipe instanceof ShapedRecipe) {
                            int offsetX = 0;
                            int offsetY = 0;

                            if (this.craftingType == CRAFTING_BIG) {
                                int minX = -1, minY = -1, maxX = 0, maxY = 0;
                                for (int x = 0; x < 3 && canCraft; ++x) {
                                    for (int y = 0; y < 3; ++y) {
                                        Item readItem = craftingEventPacket.input[y * 3 + x];
                                        if (readItem.getId() != Item.AIR) {
                                            if (minY == -1 || minY > y) {
                                                minY = y;
                                            }
                                            if (maxY < y) {
                                                maxY = y;
                                            }
                                            if (minX == -1) {
                                                minX = x;
                                            }
                                            if (maxX < x) {
                                                maxX = x;
                                            }
                                        }
                                    }
                                }
                                if (maxX == minX) {
                                    offsetX = minX;
                                }
                                if (maxY == minY) {
                                    offsetY = minY;
                                }
                            }

                            //To fix some items can't craft
                            for (int x = 0; x < 3 - offsetX && canCraft; ++x) {
                                for (int y = 0; y < 3 - offsetY; ++y) {
                                    item = craftingEventPacket.input[(y + offsetY) * 3 + (x + offsetX)];
                                    Item ingredient = ((ShapedRecipe) recipe).getIngredient(x, y);
                                    //todo: check this https://github.com/PocketMine/PocketMine-MP/commit/58709293cf4eee2e836a94226bbba4aca0f53908
                                    if (item.getCount() > 0) {
                                        if (ingredient == null || !ingredient.deepEquals(item, ingredient.hasMeta(), ingredient.getCompoundTag() != null)) {
                                            canCraft = false;
                                            break;
                                        }

                                    }
                                }
                            }

                            //If can't craft by auto resize, will try to craft this item in another way
                            if (!canCraft) {
                                canCraft = true;
                                for (int x = 0; x < 3 && canCraft; ++x) {
                                    for (int y = 0; y < 3; ++y) {
                                        item = craftingEventPacket.input[y * 3 + x];
                                        Item ingredient = ((ShapedRecipe) recipe).getIngredient(x, y);
                                        if (item.getCount() > 0) {
                                            if (ingredient == null || !ingredient.deepEquals(item, ingredient.hasMeta(), ingredient.getCompoundTag() != null)) {
                                                canCraft = false;
                                                break;
                                            }

                                        }
                                    }
                                }
                            }

                        } else if (recipe instanceof ShapelessRecipe) {
                            List<Item> needed = ((ShapelessRecipe) recipe).getIngredientList();

                            for (int x = 0; x < 3 && canCraft; ++x) {
                                for (int y = 0; y < 3; ++y) {
                                    item = craftingEventPacket.input[y * 3 + x].clone();

                                    for (Item n : new ArrayList<>(needed)) {
                                        if (n.deepEquals(item, n.hasMeta(), n.getCompoundTag() != null)) {
                                            int remove = Math.min(n.getCount(), item.getCount());
                                            n.setCount(n.getCount() - remove);
                                            item.setCount(item.getCount() - remove);

                                            if (n.getCount() == 0) {
                                                needed.remove(n);
                                            }
                                        }
                                    }

                                    if (item.getCount() > 0) {
                                        canCraft = false;
                                        break;
                                    }
                                }
                            }

                            if (!needed.isEmpty()) {
                                canCraft = false;
                            }
                        } else {
                            canCraft = false;
                        }

                        List<Item> ingredientsList = new ArrayList<>();
                        if (recipe instanceof ShapedRecipe) {
                            for (int x = 0; x < 3; x++) {
                                for (int y = 0; y < 3; y++) {
                                    Item need = ((ShapedRecipe) recipe).getIngredient(x, y);
                                    if (need.getId() == 0) {
                                        continue;
                                    }
                                    for (int count = need.getCount(); count > 0; count--) {
                                        Item needAdd = need.clone();
                                        //todo: check if there need to set item's count to 1, I'm too tired to check that today =w=
                                        needAdd.setCount(1);
                                        ingredientsList.add(needAdd);
                                    }
                                }
                            }
                        }
                        if (recipe instanceof ShapelessRecipe) {
                            List<Item> recipeItem = ((ShapelessRecipe) recipe).getIngredientList();
                            for (Item need : recipeItem) {
                                if (need.getId() == 0) {
                                    continue;
                                }
                                Item needAdd = need.clone();
                                //todo: check if there need to set item's count to 1, I'm too tired to check that today =w=
                                needAdd.setCount(1);
                                ingredientsList.add(needAdd);
                            }
                        }

                        Item[] ingredients = ingredientsList.stream().toArray(Item[]::new);

                        Item result = craftingEventPacket.output[0];

                        if (!canCraft || !recipe.getResult().deepEquals(result)) {
                            this.server.getLogger().debug("(2) Unmatched recipe " + recipe.getId() + " from player " + this.getName() + ": expected " + recipe.getResult() + ", got " + result + ", using: " + Arrays.asList(ingredients).toString());
                            this.inventory.sendContents(this);
                            break;
                        }

                        int[] used = new int[this.inventory.getSize()];

                        for (Item ingredient : ingredients) {
                            slot = -1;
                            for (int index : this.inventory.getContents().keySet()) {
                                Item i = this.inventory.getContents().get(index);
                                if (ingredient.getId() != 0 && ingredient.deepEquals(i, ingredient.hasMeta()) && (i.getCount() - used[index]) >= 1) {
                                    slot = index;
                                    used[index]++;
                                    break;
                                }
                            }

                            if (ingredient.getId() != 0 && slot == -1) {
                                canCraft = false;
                                break;
                            }
                        }

                        if (!canCraft) {
                            this.server.getLogger().debug("(3) Unmatched recipe " + recipe.getId() + " from player " + this.getName() + ": client does not have enough items, using: " + Arrays.asList(ingredients).toString());
                            this.inventory.sendContents(this);
                            break;
                        }
                        CraftItemEvent craftItemEvent;
                        this.server.getPluginManager().callEvent(craftItemEvent = new CraftItemEvent(this, ingredients, recipe));

                        if (craftItemEvent.isCancelled()) {
                            this.inventory.sendContents(this);
                            break;
                        }

                        for (int i = 0; i < used.length; i++) {
                            int count = used[i];
                            if (count == 0) {
                                continue;
                            }

                            item = this.inventory.getItem(i);

                            Item newItem;
                            if (item.getCount() > count) {
                                newItem = item.clone();
                                newItem.setCount(item.getCount() - count);
                            } else {
                                newItem = new ItemBlock(new BlockAir(), 0, 0);
                            }

                            this.inventory.setItem(i, newItem);
                        }

                        Item[] extraItem = this.inventory.addItem(recipe.getResult());
                        if (extraItem.length > 0) {
                            for (Item i : extraItem) {
                                this.level.dropItem(this, i);
                            }
                        }*/
                    }

                    if (recipe != null) {
                        switch (recipe.getResult().getId()) {
                            case Item.WORKBENCH:
                                this.awardAchievement("buildWorkBench");
                                break;
                            case Item.WOODEN_PICKAXE:
                                this.awardAchievement("buildPickaxe");
                                break;
                            case Item.FURNACE:
                                this.awardAchievement("buildFurnace");
                                break;
                            case Item.WOODEN_HOE:
                                this.awardAchievement("buildHoe");
                                break;
                            case Item.BREAD:
                                this.awardAchievement("makeBread");
                                break;
                            case Item.CAKE:
                                //TODO: detect complex recipes like cake that leave remains
                                this.awardAchievement("bakeCake");
                                this.inventory.addItem(new ItemBucket(0, 3));
                                break;
                            case Item.STONE_PICKAXE:
                            case Item.GOLD_PICKAXE:
                            case Item.IRON_PICKAXE:
                            case Item.DIAMOND_PICKAXE:
                                this.awardAchievement("buildBetterPickaxe");
                                break;
                            case Item.WOODEN_SWORD:
                                this.awardAchievement("buildSword");
                                break;
                            case Item.DIAMOND:
                                this.awardAchievement("diamond");
                                break;
                            default:
                                break;
                        }
                    }

                    break;
                case ProtocolInfo.CONTAINER_SET_SLOT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    ContainerSetSlotPacket containerSetSlotPacket = (ContainerSetSlotPacket) packet;
                    if (containerSetSlotPacket.slot < 0) {
                        break;
                    }

                    Inventory inv;
                    BaseTransaction transaction;
                    if (containerSetSlotPacket.windowid == 0) { //Our inventory
                        inv = this.inventory;
                        if (containerSetSlotPacket.slot >= this.inventory.getSize()) {
                            break;
                        }
                        if (this.isCreative()) {
                            if (Item.getCreativeItemIndex(containerSetSlotPacket.item) != -1) {
                                inv.setItem(containerSetSlotPacket.slot, containerSetSlotPacket.item);
                                this.inventory.setHotbarSlotIndex(containerSetSlotPacket.slot, containerSetSlotPacket.slot); //links hotbar[packet.slot] to slots[packet.slot]
                            }
                        }
                        transaction = new BaseTransaction(this.inventory, containerSetSlotPacket.slot, this.inventory.getItem(containerSetSlotPacket.slot), containerSetSlotPacket.item);
                    } else if (containerSetSlotPacket.windowid == ContainerSetContentPacket.SPECIAL_ARMOR) { //Our armor
                        inv = this.inventory;
                        if (containerSetSlotPacket.slot >= 4) {
                            break;
                        }
                        transaction = new BaseTransaction(this.inventory, containerSetSlotPacket.slot + this.inventory.getSize(), this.inventory.getArmorItem(containerSetSlotPacket.slot), containerSetSlotPacket.item);
                    } else if (this.windowIndex.containsKey(containerSetSlotPacket.windowid)) {
                        inv = this.windowIndex.get(containerSetSlotPacket.windowid);

                        if (!(inv instanceof AnvilInventory)) {
                            this.craftingType = CRAFTING_SMALL;
                        }

                        if (inv instanceof EnchantInventory && containerSetSlotPacket.item.hasEnchantments()) {
                            ((EnchantInventory) inv).onEnchant(this, inv.getItem(containerSetSlotPacket.slot), containerSetSlotPacket.item);
                        }

                        transaction = new BaseTransaction(inv, containerSetSlotPacket.slot, inv.getItem(containerSetSlotPacket.slot), containerSetSlotPacket.item);
                    } else {
                        break;
                    }

                    if (inv != null) {
                        Item sourceItem = inv.getItem(containerSetSlotPacket.slot);
                        Item heldItem = sourceItem.clone();
                        heldItem.setCount(sourceItem.getCount() - containerSetSlotPacket.item.getCount());
                        if (heldItem.getCount() > 0) { //In win10, click mouse and hold on item
                            InventoryClickEvent inventoryClickEvent = new InventoryClickEvent(inv, containerSetSlotPacket.slot, sourceItem, heldItem, containerSetSlotPacket.item);
                            this.getServer().getPluginManager().callEvent(inventoryClickEvent);
                            //TODO Fix hold on bug and support Cancellable
                        }
                    }

                    if (transaction.getSourceItem().deepEquals(transaction.getTargetItem()) && transaction.getTargetItem().getCount() == transaction.getSourceItem().getCount()) { //No changes!
                        //No changes, just a local inventory update sent by the server
                        break;
                    }


                    if (this.currentTransaction == null || this.currentTransaction.getCreationTime() < (System.currentTimeMillis() - 8 * 1000)) {
                        if (this.currentTransaction != null) {
                            for (Inventory inventory : this.currentTransaction.getInventories()) {
                                if (inventory instanceof PlayerInventory) {
                                    ((PlayerInventory) inventory).sendArmorContents(this);
                                }
                                inventory.sendContents(this);
                            }
                        }
                        this.currentTransaction = new SimpleTransactionGroup(this);
                    }

                    this.currentTransaction.addTransaction(transaction);

                    if (this.currentTransaction.canExecute() || this.isCreative()) {
                        HashSet<String> achievements = new HashSet<>();

                        for (Transaction tr : this.currentTransaction.getTransactions()) {
                            Inventory inv1 = tr.getInventory();

                            if (inv1 instanceof FurnaceInventory) {
                                if (tr.getSlot() == 2) {
                                    switch (((FurnaceInventory) inv1).getResult().getId()) {
                                        case Item.IRON_INGOT:
                                            achievements.add("acquireIron");
                                            break;
                                    }
                                }
                            }
                        }

                        if (this.currentTransaction.execute(this.isCreative())) {
                            for (String achievement : achievements) {
                                this.awardAchievement(achievement);
                            }
                        }

                        this.currentTransaction = null;
                    } else {
                        if (containerSetSlotPacket.item.getId() != 0) {
                            inventory.sendSlot(containerSetSlotPacket.hotbarSlot, this);
                            inventory.sendSlot(containerSetSlotPacket.slot, this);
                        }
                    }

                    break;
                case ProtocolInfo.BLOCK_ENTITY_DATA_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }
                    BlockEntityDataPacket blockEntityDataPacket = (BlockEntityDataPacket) packet;
                    this.craftingType = CRAFTING_SMALL;

                    pos = new Vector3(blockEntityDataPacket.x, blockEntityDataPacket.y, blockEntityDataPacket.z);
                    if (pos.distanceSquared(this) > 10000) {
                        break;
                    }

                    BlockEntity t = this.level.getBlockEntity(pos);
                    if (t instanceof BlockEntitySign) {
                        CompoundTag nbt;
                        try {
                            nbt = NBTIO.read(blockEntityDataPacket.namedTag, ByteOrder.LITTLE_ENDIAN, true);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        if (!BlockEntity.SIGN.equals(nbt.getString("id"))) {
                            ((BlockEntitySign) t).spawnTo(this);
                        } else {
                            SignChangeEvent signChangeEvent = new SignChangeEvent(t.getBlock(), this, new String[]{
                                    this.removeFormat ? TextFormat.clean(nbt.getString("Text1")) : nbt.getString("Text1"),
                                    this.removeFormat ? TextFormat.clean(nbt.getString("Text2")) : nbt.getString("Text2"),
                                    this.removeFormat ? TextFormat.clean(nbt.getString("Text3")) : nbt.getString("Text3"),
                                    this.removeFormat ? TextFormat.clean(nbt.getString("Text4")) : nbt.getString("Text4")
                            });

                            if (!t.namedTag.contains("Creator") || !Objects.equals(this.getUniqueId().toString(), t.namedTag.getString("Creator"))) {
                                signChangeEvent.setCancelled();
                            }

                            this.server.getPluginManager().callEvent(signChangeEvent);

                            if (!signChangeEvent.isCancelled()) {
                                ((BlockEntitySign) t).setText(signChangeEvent.getLine(0), signChangeEvent.getLine(1), signChangeEvent.getLine(2), signChangeEvent.getLine(3));
                            } else {
                                ((BlockEntitySign) t).spawnTo(this);
                            }

                        }
                    }
                    break;
                case ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET:
                    RequestChunkRadiusPacket requestChunkRadiusPacket = (RequestChunkRadiusPacket) packet;
                    ChunkRadiusUpdatedPacket chunkRadiusUpdatePacket = new ChunkRadiusUpdatedPacket();
                    this.chunkRadius = Math.max(5, Math.min(requestChunkRadiusPacket.radius, this.viewDistance));
                    chunkRadiusUpdatePacket.radius = this.chunkRadius;
                    this.dataPacket(chunkRadiusUpdatePacket);
                    break;
                case ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET:
                    SetPlayerGameTypePacket setPlayerGameTypePacket = (SetPlayerGameTypePacket) packet;
                    if (setPlayerGameTypePacket.gamemode != this.gamemode) {
                        if (!this.hasPermission("nukkit.command.gamemode")) {
                            SetPlayerGameTypePacket setPlayerGameTypePacket1 = new SetPlayerGameTypePacket();
                            setPlayerGameTypePacket1.gamemode = this.gamemode & 0x01;
                            this.dataPacket(setPlayerGameTypePacket1);
                            this.getAdventureSettings().update();
                            break;
                        }
                        this.setGamemode(setPlayerGameTypePacket.gamemode, true);
                        Command.broadcastCommandMessage(this, new TranslationContainer("commands.gamemode.success.self", Server.getGamemodeString(this.gamemode)));
                    }
                    break;
                case ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET:
                    ItemFrameDropItemPacket itemFrameDropItemPacket = (ItemFrameDropItemPacket) packet;
                    Vector3 vector3 = this.temporalVector.setComponents(itemFrameDropItemPacket.x, itemFrameDropItemPacket.y, itemFrameDropItemPacket.z);
                    BlockEntity blockEntityItemFrame = this.level.getBlockEntity(vector3);
                    BlockEntityItemFrame itemFrame = (BlockEntityItemFrame) blockEntityItemFrame;
                    if (itemFrame != null) {
                        Block block = itemFrame.getBlock();
                        Item itemDrop = itemFrame.getItem();
                        ItemFrameDropItemEvent itemFrameDropItemEvent = new ItemFrameDropItemEvent(this, block, itemFrame, itemDrop);
                        this.server.getPluginManager().callEvent(itemFrameDropItemEvent);
                        if (!itemFrameDropItemEvent.isCancelled()) {
                            if (itemDrop.getId() != Item.AIR) {
                                vector3 = this.temporalVector.setComponents(itemFrame.x + 0.5, itemFrame.y, itemFrame.z + 0.5);
                                this.level.dropItem(vector3, itemDrop);
                                itemFrame.setItem(new ItemBlock(new BlockAir()));
                                itemFrame.setItemRotation(0);
                                this.getLevel().addSound(new ItemFrameItemRemovedSound(this));
                            }
                        } else {
                            itemFrame.spawnTo(this);
                        }
                    }
                    break;
                case ProtocolInfo.MAP_INFO_REQUEST_PACKET:
                    MapInfoRequestPacket pk = (MapInfoRequestPacket) packet;
                    Item mapItem = null;

                    for (Item item1 : this.inventory.getContents().values()) {
                        if (item1 instanceof ItemMap && ((ItemMap) item1).getMapId() == pk.mapId) {
                            mapItem = item1;
                        }
                    }

                    if (mapItem == null) {
                        for (BlockEntity be : this.level.getBlockEntities().values()) {
                            if (be instanceof BlockEntityItemFrame) {
                                BlockEntityItemFrame itemFrame1 = (BlockEntityItemFrame) be;

                                if (itemFrame1.getItem() instanceof ItemMap && ((ItemMap) itemFrame1.getItem()).getMapId() == pk.mapId) {
                                    ((ItemMap) itemFrame1.getItem()).sendImage(this);
                                    break;
                                }
                            }
                        }
                    }

                    if (mapItem != null) {
                        PlayerMapInfoRequestEvent event;
                        getServer().getPluginManager().callEvent(event = new PlayerMapInfoRequestEvent(this, mapItem));

                        if (!event.isCancelled()) {
                            ((ItemMap) mapItem).sendImage(this);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public boolean kick() {
        return this.kick("");
    }

    public boolean kick(String reason, boolean isAdmin) {
        return this.kick(PlayerKickEvent.Reason.UNKNOWN, reason, isAdmin);
    }

    public boolean kick(String reason) {
        return kick(PlayerKickEvent.Reason.UNKNOWN, reason);
    }

    public boolean kick(PlayerKickEvent.Reason reason) {
        return this.kick(reason, true);
    }

    public boolean kick(PlayerKickEvent.Reason reason, String reasonString) {
        return this.kick(reason, reasonString, true);
    }

    public boolean kick(PlayerKickEvent.Reason reason, boolean isAdmin) {
        return this.kick(reason, reason.toString(), isAdmin);
    }

    public boolean kick(PlayerKickEvent.Reason reason, String reasonString, boolean isAdmin) {
        PlayerKickEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerKickEvent(this, reason, this.getLeaveMessage()));
        if (!ev.isCancelled()) {
            String message;
            if (isAdmin) {
                if (!this.isBanned()) {
                    message = "Kicked by admin." + (!"".equals(reasonString) ? " Reason: " + reasonString : "");
                } else {
                    message = reasonString;
                }
            } else {
                if ("".equals(reasonString)) {
                    message = "disconnectionScreen.noReason";
                } else {
                    message = reasonString;
                }
            }

            this.close(ev.getQuitMessage(), message);

            return true;
        }

        return false;
    }

    @Override
    public void sendMessage(String message) {
        // TODO: Remove this workaround (broken client MCPE 1.0.0)
        messageQueue.add(this.server.getLanguage().translateString(message));

        /*
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_RAW;
        pk.message = this.server.getLanguage().translateString(message);
        this.dataPacket(pk);
        */
    }

    @Override
    public void sendMessage(TextContainer message) {
        if (message instanceof TranslationContainer) {
            this.sendTranslation(message.getText(), ((TranslationContainer) message).getParameters());
            return;
        }
        this.sendMessage(message.getText());
    }

    public void sendTranslation(String message) {
        this.sendTranslation(message, new String[0]);
    }

    public void sendTranslation(String message, String[] parameters) {
        TextPacket pk = new TextPacket();
        if (!this.server.isLanguageForced()) {
            pk.type = TextPacket.TYPE_TRANSLATION;
            pk.message = this.server.getLanguage().translateString(message, parameters, "nukkit.");
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = this.server.getLanguage().translateString(parameters[i], parameters, "nukkit.");

            }
            pk.parameters = parameters;
        } else {
            pk.type = TextPacket.TYPE_RAW;
            pk.message = this.server.getLanguage().translateString(message, parameters);
        }
        this.dataPacket(pk);
    }

    public void sendPopup(String message) {
        this.sendPopup(message, "");
    }

    public void sendPopup(String message, String subtitle) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_POPUP;
        pk.source = message;
        pk.message = subtitle;
        this.dataPacket(pk);
    }

    public void sendTip(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_TIP;
        pk.message = message;
        this.dataPacket(pk);
    }

    public void clearTitle() {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_CLEAR;
        this.dataPacket(pk);
    }

    /**
     * Resets both title animation times and subtitle for the next shown title
     */
    public void resetTitleSettings() {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_RESET;
        this.dataPacket(pk);
    }

    public void sendTitle(String text) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_TITLE;
        pk.text = text;
        this.dataPacket(pk);
    }

    /**
     * Sets a subtitle for the next shown title
     * @param text Subtitle text
     */
    public void setSubtitle(String text) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_SUBTITLE;
        pk.text = text;
        this.dataPacket(pk);
    }

    public void sendActionBarTitle(String text) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_ACTION_BAR;
        pk.text = text;
        this.dataPacket(pk);
    }

    /**
     * Sets times for title animations
     * @param fadeInTime For how long title fades in
     * @param stayTime For how long title is shown
     * @param fadeOutTime For how long title fades out
     */
    public void setTitleAnimationTimes(int fadeInTime, int stayTime, int fadeOutTime) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_ANIMATION_TIMES;
        pk.fadeInTime = fadeInTime;
        pk.stayTime = stayTime;
        pk.fadeOutTime = fadeOutTime;
        this.dataPacket(pk);
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
        this.close(new TextContainer(message), reason, notify);
    }

    public void close(TextContainer message) {
        this.close(message, "generic");
    }

    public void close(TextContainer message, String reason) {
        this.close(message, reason, true);
    }

    public void close(TextContainer message, String reason, boolean notify) {
        if (this.connected && !this.closed) {
            if (notify && reason.length() > 0) {
                DisconnectPacket pk = new DisconnectPacket();
                pk.message = reason;
                this.directDataPacket(pk);
            }

            this.connected = false;
            PlayerQuitEvent ev = null;
            if (this.getName() != null && this.getName().length() > 0) {
                this.server.getPluginManager().callEvent(ev = new PlayerQuitEvent(this, message, true, reason));
                if (this.loggedIn && ev.getAutoSave()) {
                    this.save();
                }
            }

            for (Player player : new ArrayList<>(this.server.getOnlinePlayers().values())) {
                if (!player.canSee(this)) {
                    player.showPlayer(this);
                }
            }

            this.hiddenPlayers = new HashMap<>();

            for (Inventory window : new ArrayList<>(this.windowIndex.values())) {
                this.removeWindow(window);
            }

            for (long index : new ArrayList<>(this.usedChunks.keySet())) {
                int chunkX = Level.getHashX(index);
                int chunkZ = Level.getHashZ(index);
                this.level.unregisterChunkLoader(this, chunkX, chunkZ);
                this.usedChunks.remove(index);
            }

            super.close();

            this.interfaz.close(this, notify ? reason : "");

            if (this.loggedIn) {
                this.server.removeOnlinePlayer(this);
            }

            this.loggedIn = false;

            if (ev != null && !Objects.equals(this.username, "") && this.spawned && !Objects.equals(ev.getQuitMessage().toString(), "")) {
                this.server.broadcastMessage(ev.getQuitMessage());
            }

            this.server.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_USERS, this);
            this.spawned = false;
            this.server.getLogger().info(this.getServer().getLanguage().translateString("nukkit.player.logOut",
                    TextFormat.AQUA + (this.getName() == null ? "" : this.getName()) + TextFormat.WHITE,
                    this.ip,
                    String.valueOf(this.port),
                    this.getServer().getLanguage().translateString(reason)));
            this.windows = new HashMap<>();
            this.windowIndex = new HashMap<>();
            this.usedChunks = new HashMap<>();
            this.loadQueue = new HashMap<>();
            this.hasSpawned = new HashMap<>();
            this.spawnPosition = null;

            if (this.riding instanceof EntityVehicle) {
                ((EntityVehicle) this.riding).linkedEntity = null;
            }

            this.riding = null;
        }

        if (this.perm != null) {
            this.perm.clearPermissions();
            this.perm = null;
        }

        if (this.inventory != null) {
            this.inventory = null;
            this.currentTransaction = null;
        }

        this.chunk = null;

        this.server.removePlayer(this);
    }

    public void save() {
        this.save(false);
    }

    public void save(boolean async) {
        if (this.closed) {
            throw new IllegalStateException("Tried to save closed player");
        }

        super.saveNBT();

        if (this.level != null) {
            this.namedTag.putString("Level", this.level.getFolderName());
            if (this.spawnPosition != null && this.spawnPosition.getLevel() != null) {
                this.namedTag.putString("SpawnLevel", this.spawnPosition.getLevel().getFolderName());
                this.namedTag.putInt("SpawnX", (int) this.spawnPosition.x);
                this.namedTag.putInt("SpawnY", (int) this.spawnPosition.y);
                this.namedTag.putInt("SpawnZ", (int) this.spawnPosition.z);
            }

            CompoundTag achievements = new CompoundTag();
            for (String achievement : this.achievements) {
                achievements.putByte(achievement, 1);
            }

            this.namedTag.putCompound("Achievements", achievements);

            this.namedTag.putInt("playerGameType", this.gamemode);
            this.namedTag.putLong("lastPlayed", System.currentTimeMillis() / 1000);

            this.namedTag.putString("lastIP", this.getAddress());

            this.namedTag.putInt("EXP", this.getExperience());
            this.namedTag.putInt("expLevel", this.getExperienceLevel());

            this.namedTag.putInt("foodLevel", this.getFoodData().getLevel());
            this.namedTag.putFloat("foodSaturationLevel", this.getFoodData().getFoodSaturationLevel());

            if (!"".equals(this.username) && this.namedTag != null) {
                this.server.saveOfflinePlayerData(this.username, this.namedTag, async);
            }
        }
    }

    public String getName() {
        return this.username;
    }

    @Override
    public void kill() {
        if (!this.spawned) {
            return;
        }

        boolean showMessages = this.level.getGameRules().getBoolean("showDeathMessages");
        String message = "death.attack.generic";

        List<String> params = new ArrayList<>();
        params.add(this.getDisplayName());
        if (showMessages) {

            EntityDamageEvent cause = this.getLastDamageCause();

            switch (cause == null ? DamageCause.CUSTOM : cause.getCause()) {
                case ENTITY_ATTACK:
                    if (cause instanceof EntityDamageByEntityEvent) {
                        Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                        killer = e;
                        if (e instanceof Player) {
                            message = "death.attack.player";
                            params.add(((Player) e).getDisplayName());
                            break;
                        } else if (e instanceof EntityLiving) {
                            message = "death.attack.mob";
                            params.add(!Objects.equals(e.getNameTag(), "") ? e.getNameTag() : e.getName());
                            break;
                        } else {
                            params.add("Unknown");
                        }
                    }
                    break;
                case PROJECTILE:
                    if (cause instanceof EntityDamageByEntityEvent) {
                        Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                        killer = e;
                        if (e instanceof Player) {
                            message = "death.attack.arrow";
                            params.add(((Player) e).getDisplayName());
                        } else if (e instanceof EntityLiving) {
                            message = "death.attack.arrow";
                            params.add(!Objects.equals(e.getNameTag(), "") ? e.getNameTag() : e.getName());
                            break;
                        } else {
                            params.add("Unknown");
                        }
                    }
                    break;
                case SUICIDE:
                    message = "death.attack.generic";
                    break;
                case VOID:
                    message = "death.attack.outOfWorld";
                    break;
                case FALL:
                    if (cause != null) {
                        if (cause.getFinalDamage() > 2) {
                            message = "death.fell.accident.generic";
                            break;
                        }
                    }
                    message = "death.attack.fall";
                    break;

                case SUFFOCATION:
                    message = "death.attack.inWall";
                    break;

                case LAVA:
                    message = "death.attack.lava";
                    break;

                case FIRE:
                    message = "death.attack.onFire";
                    break;

                case FIRE_TICK:
                    message = "death.attack.inFire";
                    break;

                case DROWNING:
                    message = "death.attack.drown";
                    break;

                case CONTACT:
                    if (cause instanceof EntityDamageByBlockEvent) {
                        if (((EntityDamageByBlockEvent) cause).getDamager().getId() == Block.CACTUS) {
                            message = "death.attack.cactus";
                        }
                    }
                    break;

                case BLOCK_EXPLOSION:
                case ENTITY_EXPLOSION:
                    if (cause instanceof EntityDamageByEntityEvent) {
                        Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                        killer = e;
                        if (e instanceof Player) {
                            message = "death.attack.explosion.player";
                            params.add(((Player) e).getDisplayName());
                        } else if (e instanceof EntityLiving) {
                            message = "death.attack.explosion.player";
                            params.add(!Objects.equals(e.getNameTag(), "") ? e.getNameTag() : e.getName());
                            break;
                        }
                    } else {
                        message = "death.attack.explosion";
                    }
                    break;

                case MAGIC:
                    message = "death.attack.magic";
                    break;

                case CUSTOM:
                    break;

                default:
                    break;

            }
        } else {
            message = "";
            params.clear();
        }

        this.health = 0;
        this.scheduleUpdate();

        PlayerDeathEvent ev = new PlayerDeathEvent(this, this.getDrops(), new TranslationContainer(message, params.stream().toArray(String[]::new)), this.getExperienceLevel());

        ev.setKeepExperience(this.level.gameRules.getBoolean("keepInventory"));
        ev.setKeepInventory(ev.getKeepExperience());
        this.server.getPluginManager().callEvent(ev);

        if (!ev.getKeepInventory() && this.level.getGameRules().getBoolean("doEntityDrops")) {
            for (Item item : ev.getDrops()) {
                this.level.dropItem(this, item, null, true, 40);
            }

            if (this.inventory != null) {
                this.inventory.clearAll();
            }
        }

        if (!ev.getKeepExperience() && this.level.getGameRules().getBoolean("doEntityDrops")) {
            if (this.isSurvival() || this.isAdventure()) {
                int exp = ev.getExperience() * 7;
                if (exp > 100) exp = 100;
                int add = 1;
                for (int ii = 1; ii < exp; ii += add) {
                    this.getLevel().dropExpOrb(this, add);
                    add = new NukkitRandom().nextRange(1, 3);
                }
            }
            this.setExperience(0, 0);
        }

        if (showMessages && !ev.getDeathMessage().toString().isEmpty()) {
            this.server.broadcast(ev.getDeathMessage(), Server.BROADCAST_CHANNEL_USERS);
        }


        RespawnPacket pk = new RespawnPacket();
        Position pos = this.getSpawn();
        pk.x = (float) pos.x;
        pk.y = (float) pos.y;
        pk.z = (float) pos.z;
        this.dataPacket(pk);
    }

    @Override
    public void setHealth(float health) {
        if (health < 1) {
            health = 0;
        }

        super.setHealth(health);
        Attribute attr = Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(this.getMaxHealth()).setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0);
        if (this.spawned) {
            UpdateAttributesPacket pk = new UpdateAttributesPacket();
            pk.entries = new Attribute[]{attr};
            pk.entityId = this.id;
            this.dataPacket(pk);
        }
    }

    public int getExperience() {
        return this.exp;
    }

    public int getExperienceLevel() {
        return this.expLevel;
    }

    public void addExperience(int add) {
        if (add == 0) return;
        int now = this.getExperience();
        int added = now + add;
        int level = this.getExperienceLevel();
        int most = calculateRequireExperience(level);
        while (added >= most) {  //Level Up!
            added = added - most;
            level++;
            most = calculateRequireExperience(level);
        }
        this.setExperience(added, level);
    }

    public static int calculateRequireExperience(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else if (level >= 15) {
            return 37 + (level - 15) * 5;
        } else {
            return 7 + level * 2;
        }
    }

    public void setExperience(int exp) {
        setExperience(exp, this.getExperienceLevel());
    }

    //todo something on performance, lots of exp orbs then lots of packets, could crash client

    public void setExperience(int exp, int level) {
        this.exp = exp;
        this.expLevel = level;

        this.sendExperienceLevel(level);
        this.sendExperience(exp);
    }

    public void sendExperience() {
        sendExperience(this.getExperience());
    }

    public void sendExperience(int exp) {
        if (this.spawned) {
            float percent = ((float) exp) / calculateRequireExperience(this.getExperienceLevel());
            this.setAttribute(Attribute.getAttribute(Attribute.EXPERIENCE).setValue(percent));
        }
    }

    public void sendExperienceLevel() {
        sendExperienceLevel(this.getExperienceLevel());
    }

    public void sendExperienceLevel(int level) {
        if (this.spawned) {
            this.setAttribute(Attribute.getAttribute(Attribute.EXPERIENCE_LEVEL).setValue(level));
        }
    }

    public void setAttribute(Attribute attribute) {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entries = new Attribute[]{attribute};
        pk.entityId = this.id;
        this.dataPacket(pk);
    }

    @Override
    public void setMovementSpeed(float speed) {
        super.setMovementSpeed(speed);
        if (this.spawned) {
            Attribute attribute = Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue(speed);
            this.setAttribute(attribute);
        }
    }

    public Entity getKiller() {
        return killer;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (!this.isAlive()) {
            return false;
        }

        if (this.isCreative()
                && source.getCause() != DamageCause.MAGIC
                && source.getCause() != DamageCause.SUICIDE
                && source.getCause() != DamageCause.VOID
                ) {
            //source.setCancelled();
            return false;
        } else if (this.getAdventureSettings().canFly() && source.getCause() == DamageCause.FALL) {
            //source.setCancelled();
            return false;
        } else if (source.getCause() == DamageCause.FALL) {
            if (this.getLevel().getBlock(this.getPosition().floor().add(0.5, -1, 0.5)).getId() == Block.SLIME_BLOCK) {
                if (!this.isSneaking()) {
                    //source.setCancelled();
                    this.resetFallDistance();
                    return false;
                }
            }
        }

        if (source instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
            if (damager instanceof Player) {
                ((Player) damager).getFoodData().updateFoodExpLevel(0.3);
            }
            //Critical hit
            boolean add = false;
            if (!damager.onGround) {
                NukkitRandom random = new NukkitRandom();
                for (int i = 0; i < 5; i++) {
                    CriticalParticle par = new CriticalParticle(new Vector3(this.x + random.nextRange(-15, 15) / 10, this.y + random.nextRange(0, 20) / 10, this.z + random.nextRange(-15, 15) / 10));
                    this.getLevel().addParticle(par);
                }

                add = true;
            }
            if (add) source.setDamage((float) (source.getDamage() * 1.5));
        }

        if (super.attack(source)) { //!source.isCancelled()
            if (this.getLastDamageCause() == source && this.spawned) {
                this.getFoodData().updateFoodExpLevel(0.3);
                EntityEventPacket pk = new EntityEventPacket();
                pk.eid = this.id;
                pk.event = EntityEventPacket.HURT_ANIMATION;
                this.dataPacket(pk);
            }
            return true;
        } else {
            return false;
        }
    }

    public void sendPosition(Vector3 pos) {
        this.sendPosition(pos, this.yaw);
    }

    public void sendPosition(Vector3 pos, double yaw) {
        this.sendPosition(pos, yaw, this.pitch);
    }

    public void sendPosition(Vector3 pos, double yaw, double pitch) {
        this.sendPosition(pos, yaw, pitch, MovePlayerPacket.MODE_NORMAL);
    }

    public void sendPosition(Vector3 pos, double yaw, double pitch, byte mode) {
        this.sendPosition(pos, yaw, pitch, mode, null);
    }

    public void sendPosition(Vector3 pos, double yaw, double pitch, byte mode, Player[] targets) {
        MovePlayerPacket pk = new MovePlayerPacket();
        pk.eid = this.getId();
        pk.x = (float) pos.x;
        pk.y = (float) (pos.y + this.getEyeHeight());
        pk.z = (float) pos.z;
        pk.headYaw = (float) yaw;
        pk.pitch = (float) pitch;
        pk.yaw = (float) yaw;
        pk.mode = mode;

        if (targets != null) {
            Server.broadcastPacket(targets, pk);
        } else {
            pk.eid = this.id;
            this.dataPacket(pk);
        }
    }

    @Override
    protected void checkChunks() {
        if (this.chunk == null || (this.chunk.getX() != ((int) this.x >> 4) || this.chunk.getZ() != ((int) this.z >> 4))) {
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }
            this.chunk = this.level.getChunk((int) this.x >> 4, (int) this.z >> 4, true);

            if (!this.justCreated) {
                Map<Integer, Player> newChunk = this.level.getChunkPlayers((int) this.x >> 4, (int) this.z >> 4);
                newChunk.remove(this.getLoaderId());

                //List<Player> reload = new ArrayList<>();
                for (Player player : new ArrayList<>(this.hasSpawned.values())) {
                    if (!newChunk.containsKey(player.getLoaderId())) {
                        this.despawnFrom(player);
                    } else {
                        newChunk.remove(player.getLoaderId());
                        //reload.add(player);
                    }
                }

                for (Player player : newChunk.values()) {
                    this.spawnTo(player);
                }
            }

            if (this.chunk == null) {
                return;
            }

            this.chunk.addEntity(this);
        }
    }

    protected boolean checkTeleportPosition() {
        if (this.teleportPosition != null) {
            int chunkX = (int) this.teleportPosition.x >> 4;
            int chunkZ = (int) this.teleportPosition.z >> 4;

            for (int X = -1; X <= 1; ++X) {
                for (int Z = -1; Z <= 1; ++Z) {
                    long index = Level.chunkHash(chunkX + X, chunkZ + Z);
                    if (!this.usedChunks.containsKey(index) || !this.usedChunks.get(index)) {
                        return false;
                    }
                }
            }

            /*if (this.isLevelChange) { //TODO: remove this
                PlayStatusPacket statusPacket0 = new PlayStatusPacket();//Weather
                this.getLevel().sendWeather(this);
                //Update time
                this.getLevel().sendTime(this);
                statusPacket0.status = PlayStatusPacket.PLAYER_SPAWN;
                this.dataPacket(statusPacket0);

                //Weather
                this.getLevel().sendWeather(this);
                //Update time
                this.getLevel().sendTime(this);
            }*/

            this.spawnToAll();
            this.isLevelChange = false;
            this.forceMovement = this.teleportPosition;
            this.teleportPosition = null;
            return true;
        }

        return false;
    }

    private boolean isLevelChange = false;

    @Override
    public boolean teleport(Location location, TeleportCause cause) {
        if (!this.isOnline()) {
            return false;
        }

        Location from = this.getLocation();
        Location to = location;

        if (cause != null) {
            PlayerTeleportEvent event = new PlayerTeleportEvent(this, from, to, cause);
            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
            to = event.getTo();
            if (from.getLevel().getId() != to.getLevel().getId()) { //Different level, update compass position
                SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
                pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
                Position spawn = to.getLevel().getSpawnLocation();
                pk.x = spawn.getFloorX();
                pk.y = spawn.getFloorY();
                pk.z = spawn.getFloorZ();
                dataPacket(pk);
            }
        }

        if (super.teleport(to, null)) { // null to prevent fire of duplicate EntityTeleportEvent

            for (Inventory window : new ArrayList<>(this.windowIndex.values())) {
                if (window == this.inventory) {
                    continue;
                }
                this.removeWindow(window);
            }

            this.teleportPosition = new Vector3(this.x, this.y, this.z);
            this.forceMovement = this.teleportPosition;
            this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET);

            this.checkTeleportPosition();

            this.resetFallDistance();
            this.nextChunkOrderRun = 0;
            this.newPosition = null;

            //Weather
            this.getLevel().sendWeather(this);
            //Update time
            this.getLevel().sendTime(this);

            if (from.getLevel().getId() != to.level.getId()) {
                /*if (this.spawned) { //broken
                    //TODO: remove this in future version
                    this.isLevelChange = true;
                    this.nextChunkOrderRun = 10000;

                    ChangeDimensionPacket changeDimensionPacket1 = new ChangeDimensionPacket();
                    changeDimensionPacket1.dimension = 1;
                    changeDimensionPacket1.x = (float) this.getX();
                    changeDimensionPacket1.y = (float) this.getY();
                    changeDimensionPacket1.z = (float) this.getZ();
                    this.dataPacket(changeDimensionPacket1);

                    this.forceSendEmptyChunks();
                    this.getServer().getScheduler().scheduleDelayedTask(() -> {
                        PlayStatusPacket statusPacket0 = new PlayStatusPacket();
                        statusPacket0.status = PlayStatusPacket.PLAYER_SPAWN;
                        dataPacket(statusPacket0);
                    }, 8);

                    this.getServer().getScheduler().scheduleDelayedTask(() -> {
                        ChangeDimensionPacket changeDimensionPacket = new ChangeDimensionPacket();
                        changeDimensionPacket.dimension = 0;
                        changeDimensionPacket.x = (float) this.getX();
                        changeDimensionPacket.y = (float) this.getY();
                        changeDimensionPacket.z = (float) this.getZ();
                        dataPacket(changeDimensionPacket);
                        nextChunkOrderRun = 0;
                    }, 9);
                }*/
            }
            return true;
        }

        return false;
    }

    protected void forceSendEmptyChunks() {
        int chunkPositionX = this.getFloorX() >> 4;
        int chunkPositionZ = this.getFloorZ() >> 4;
        for (int x = -3; x < 3; x++) {
            for (int z = -3; z < 3; z++) {
                FullChunkDataPacket chunk = new FullChunkDataPacket();
                chunk.chunkX = chunkPositionX + x;
                chunk.chunkZ = chunkPositionZ + z;
                chunk.data = new byte[0];
                this.dataPacket(chunk);
            }
        }
    }

    public void teleportImmediate(Location location) {
        this.teleportImmediate(location, TeleportCause.PLUGIN);
    }

    public void teleportImmediate(Location location, TeleportCause cause) {
        Location from = this.getLocation();
        if (super.teleport(location, cause)) {

            for (Inventory window : new ArrayList<>(this.windowIndex.values())) {
                if (window == this.inventory) {
                    continue;
                }
                this.removeWindow(window);
            }

            if (from.getLevel().getId() != location.getLevel().getId()) { //Different level, update compass position
                SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
                pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
                Position spawn = location.getLevel().getSpawnLocation();
                pk.x = spawn.getFloorX();
                pk.y = spawn.getFloorY();
                pk.z = spawn.getFloorZ();
                dataPacket(pk);
            }

            this.forceMovement = new Vector3(this.x, this.y, this.z);
            this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET);

            this.resetFallDistance();
            this.orderChunks();
            this.nextChunkOrderRun = 0;
            this.newPosition = null;

            //Weather
            this.getLevel().sendWeather(this);
            //Update time
            this.getLevel().sendTime(this);
        }
    }

    /**
     * Creates and sends a BossBar to the player
     *
     * @param text  The BossBar message
     * @param length  The BossBar percentage
     * @return bossBarId  The BossBar ID, you should store it if you want to remove or update the BossBar later
     */
    public long createBossBar(String text, int length) {
        // First we spawn a entity
        long bossBarId = 1095216660480L + ThreadLocalRandom.current().nextLong(0, 0x7fffffffL);
        AddEntityPacket pkAdd = new AddEntityPacket();
        pkAdd.type = EntityCreeper.NETWORK_ID;
        pkAdd.entityUniqueId = bossBarId;
        pkAdd.entityRuntimeId = bossBarId;
        pkAdd.x = (float) this.x;
        pkAdd.y = (float) -10; // Below the bedrock
        pkAdd.z = (float) this.z;
        pkAdd.speedX = (float) this.motionX;
        pkAdd.speedY = (float) this.motionY;
        pkAdd.speedZ = (float) this.motionZ;
        EntityMetadata metadata = new EntityMetadata()
                // Default Metadata tags
                .putLong(DATA_FLAGS, 0)
                .putShort(DATA_AIR, 400)
                .putShort(DATA_MAX_AIR, 400)
                .putLong(DATA_LEAD_HOLDER_EID, -1)
                .putFloat(DATA_SCALE, 1f)
                .putString(Entity.DATA_NAMETAG, text) // Set the entity name
                .putInt(Entity.DATA_SCALE, 0); // And make it invisible
        pkAdd.metadata = metadata;
        this.dataPacket(pkAdd);

        // Now we send the entity attributes
        // TODO: Attributes should be sent on AddEntityPacket, however it doesn't work (client bug?)
        UpdateAttributesPacket pkAttributes = new UpdateAttributesPacket();
        pkAttributes.entityId = bossBarId;
        Attribute attr = Attribute.getAttribute(Attribute.MAX_HEALTH);
        attr.setMaxValue(100); // Max value - We need to change the max value first, or else the "setValue" will return a IllegalArgumentException
        attr.setValue(length); // Entity health
        pkAttributes.entries = new Attribute[] { attr };
        this.dataPacket(pkAttributes);

        // And now we send the bossbar packet
        BossEventPacket pkBoss = new BossEventPacket();
        pkBoss.eid = bossBarId;
        pkBoss.type = BossEventPacket.ADD;
        this.dataPacket(pkBoss);
        return bossBarId;
    }

    /**
     * Updates a BossBar
     *
     * @param text  The new BossBar message
     * @param length  The new BossBar length
     * @param bossBarId  The BossBar ID
     */
    public void updateBossBar(String text, int length, long bossBarId) {
        // First we update the boss bar length
        UpdateAttributesPacket pkAttributes = new UpdateAttributesPacket();
        pkAttributes.entityId = bossBarId;
        Attribute attr = Attribute.getAttribute(Attribute.MAX_HEALTH);
        attr.setMaxValue(100); // Max value - We need to change the max value first, or else the "setValue" will return a IllegalArgumentException
        attr.setValue(length); // Entity health
        pkAttributes.entries = new Attribute[] { attr };
        this.dataPacket(pkAttributes);
        // And then the boss bar text
        SetEntityDataPacket pkMetadata = new SetEntityDataPacket();
        pkMetadata.eid = bossBarId;
        pkMetadata.metadata = new EntityMetadata()
                // Default Metadata tags
                .putLong(DATA_FLAGS, 0)
                .putShort(DATA_AIR, 400)
                .putShort(DATA_MAX_AIR, 400)
                .putLong(DATA_LEAD_HOLDER_EID, -1)
                .putFloat(DATA_SCALE, 1f)
                .putString(Entity.DATA_NAMETAG, text) // Set the entity name
                .putInt(Entity.DATA_SCALE, 0); // And make it invisible
        this.dataPacket(pkMetadata);

        // And now we send the bossbar packet
        BossEventPacket pkBoss = new BossEventPacket();
        pkBoss.eid = bossBarId;
        pkBoss.type = BossEventPacket.UPDATE;
        this.dataPacket(pkBoss);
        return;
    }

    /**
     * Removes a BossBar
     *
     * @param bossBarId  The BossBar ID
     */
    public void removeBossBar(long bossBarId) {
        RemoveEntityPacket pkRemove = new RemoveEntityPacket();
        pkRemove.eid = bossBarId;
        this.dataPacket(pkRemove);
    }

    public int getWindowId(Inventory inventory) {
        if (this.windows.containsKey(inventory)) {
            return this.windows.get(inventory);
        }

        return -1;
    }

    public int addWindow(Inventory inventory) {
        return this.addWindow(inventory, null);
    }

    public int addWindow(Inventory inventory, Integer forceId) {
        if (this.windows.containsKey(inventory)) {
            return this.windows.get(inventory);
        }
        int cnt;
        if (forceId == null) {
            this.windowCnt = cnt = Math.max(2, ++this.windowCnt % 99);
        } else {
            cnt = forceId;
        }
        this.windowIndex.put(cnt, inventory);
        this.windows.put(inventory, cnt);
        if (inventory.open(this)) {
            return cnt;
        } else {
            this.removeWindow(inventory);

            return -1;
        }
    }

    public void removeWindow(Inventory inventory) {
        inventory.close(this);
        if (this.windows.containsKey(inventory)) {
            int id = this.windows.get(inventory);
            this.windows.remove(this.windowIndex.get(id));
            this.windowIndex.remove(id);
        }
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        this.server.getPlayerMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return this.server.getPlayerMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return this.server.getPlayerMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        this.server.getPlayerMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    @Override
    public void onChunkChanged(FullChunk chunk) {
        this.usedChunks.remove(Level.chunkHash(chunk.getX(), chunk.getZ()));
    }

    @Override
    public void onChunkLoaded(FullChunk chunk) {

    }

    @Override
    public void onChunkPopulated(FullChunk chunk) {

    }

    @Override
    public void onChunkUnloaded(FullChunk chunk) {

    }

    @Override
    public void onBlockChanged(Vector3 block) {

    }

    @Override
    public Integer getLoaderId() {
        return this.loaderId;
    }

    @Override
    public boolean isLoaderActive() {
        return this.isConnected();
    }


    public static BatchPacket getChunkCacheFromData(int chunkX, int chunkZ, byte[] payload) {
        FullChunkDataPacket pk = new FullChunkDataPacket();
        pk.chunkX = chunkX;
        pk.chunkZ = chunkZ;
        pk.data = payload;
        pk.encode();

        BatchPacket batch = new BatchPacket();
        byte[][] batchPayload = new byte[2][];
        byte[] buf = pk.getBuffer();
        batchPayload[0] = Binary.writeUnsignedVarInt(buf.length);
        batchPayload[1] = buf;
        byte[] data = Binary.appendBytes(batchPayload);
        try {
            batch.payload = Zlib.deflate(data, Server.getInstance().networkCompressionLevel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return batch;
    }

    private boolean foodEnabled = true;

    public boolean isFoodEnabled() {
        return !(this.isCreative() || this.isSpectator()) && this.foodEnabled;
    }

    public void setFoodEnabled(boolean foodEnabled) {
        this.foodEnabled = foodEnabled;
    }

    public PlayerFood getFoodData() {
        return this.foodData;
    }

    //todo a lot on dimension

    public void setDimension(int dimension) {
        ChangeDimensionPacket pk = new ChangeDimensionPacket();
        pk.dimension = getLevel().getDimension();
        this.dataPacket(pk);
    }

    public void setCheckMovement(boolean checkMovement) {
        this.checkMovement = checkMovement;
    }

    public synchronized void setLocale(Locale locale) {
        this.locale.set(locale);
    }

    public synchronized Locale getLocale() {
        return this.locale.get();
    }

    public void setSprinting(boolean value, boolean setDefault) {
        super.setSprinting(value);
        if (setDefault) {
            this.movementSpeed = DEFAULT_SPEED;
        } else {
            float sprintSpeedChange = DEFAULT_SPEED * 0.3f;
            if (!value) sprintSpeedChange *= -1;
            this.movementSpeed += sprintSpeedChange;
        }
        this.setMovementSpeed(this.movementSpeed);
    }

    public void transfer(InetSocketAddress address) {
        String hostName = address.getAddress().getHostAddress();
        int port = address.getPort();
        TransferPacket pk = new TransferPacket();
        pk.address = hostName;
        pk.port = port;
        this.dataPacket(pk);
        String message = "Transferred to " + hostName + ":" + port;
        this.close(message, message, false);
    }

    public ClientChainData getLoginChainData() {
        return this.loginChainData;
    }

    public boolean pickupEntity(Entity entity, boolean near) {
        if (!this.spawned || !this.isAlive() || !this.isOnline()) {
            return false;
        }

        if (near) {
            if (entity instanceof EntityArrow && ((EntityArrow) entity).hadCollision) {
                ItemArrow item = new ItemArrow();
                if (this.isSurvival() && !this.inventory.canAddItem(item)) {
                    return false;
                }

                InventoryPickupArrowEvent ev;
                this.server.getPluginManager().callEvent(ev = new InventoryPickupArrowEvent(this.inventory, (EntityArrow) entity));
                if (ev.isCancelled()) {
                    return false;
                }

                TakeItemEntityPacket pk = new TakeItemEntityPacket();
                pk.entityId = this.getId();
                pk.target = entity.getId();
                Server.broadcastPacket(entity.getViewers().values(), pk);

                pk = new TakeItemEntityPacket();
                pk.entityId = this.id;
                pk.target = entity.getId();
                this.dataPacket(pk);

                this.inventory.addItem(item.clone());
                entity.kill();
                return true;
            } else if (entity instanceof EntityItem) {
                if (((EntityItem) entity).getPickupDelay() <= 0) {
                    Item item = ((EntityItem) entity).getItem();

                    if (item != null) {
                        if (this.isSurvival() && !this.inventory.canAddItem(item)) {
                            return false;
                        }

                        InventoryPickupItemEvent ev;
                        this.server.getPluginManager().callEvent(ev = new InventoryPickupItemEvent(this.inventory, (EntityItem) entity));
                        if (ev.isCancelled()) {
                            return false;
                        }

                        switch (item.getId()) {
                            case Item.WOOD:
                            case Item.WOOD2:
                                this.awardAchievement("mineWood");
                                break;
                            case Item.DIAMOND:
                                this.awardAchievement("diamond");
                                break;
                        }
                        /*switch (item.getId()) {
                            case Item.WOOD:
                                this.awardAchievement("mineWood");
                                break;
                            case Item.DIAMOND:
                                this.awardAchievement("diamond");
                                break;
                        }*/

                        TakeItemEntityPacket pk = new TakeItemEntityPacket();
                        pk.entityId = this.getId();
                        pk.target = entity.getId();
                        Server.broadcastPacket(entity.getViewers().values(), pk);

                        pk = new TakeItemEntityPacket();
                        pk.entityId = this.id;
                        pk.target = entity.getId();
                        this.dataPacket(pk);

                        this.inventory.addItem(item.clone());
                        entity.kill();
                        return true;
                    }
                }
            }
        }

        int tick = this.getServer().getTick();
        if (pickedXPOrb < tick && entity instanceof EntityXPOrb && this.boundingBox.isVectorInside(entity)) {
            EntityXPOrb xpOrb = (EntityXPOrb) entity;
            if (xpOrb.getPickupDelay() <= 0) {
                int exp = xpOrb.getExp();
                this.addExperience(exp);
                entity.kill();
                this.getLevel().addSound(new ExperienceOrbSound(this));
                pickedXPOrb = tick;
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        if ((this.hash == 0) || (this.hash == 485)) {
            this.hash = (485 + (getUniqueId() != null ? getUniqueId().hashCode() : 0));
        }

        return this.hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Player)) {
            return false;
        }
        Player other = (Player) obj;
        return Objects.equals(this.getUniqueId(), other.getUniqueId()) && this.getId() == other.getId();
    }

    /**
     * Notifies an ACK response from the client
     *
     * @param identification
     */
    public void notifyACK(int identification) {
        needACK.put(identification, true);
    }
}
