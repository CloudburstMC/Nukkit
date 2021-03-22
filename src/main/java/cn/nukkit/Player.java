package cn.nukkit;

import cn.nukkit.AdventureSettings.Type;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.blockentity.BlockEntityLectern;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.entity.*;
import cn.nukkit.entity.data.*;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.event.block.LecternPageChangeEvent;
import cn.nukkit.event.block.WaterFrostEvent;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityDamageEvent.DamageModifier;
import cn.nukkit.event.entity.EntityPortalEnterEvent.PortalType;
import cn.nukkit.event.inventory.*;
import cn.nukkit.event.player.*;
import cn.nukkit.event.player.PlayerAsyncPreLoginEvent.LoginResult;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.inventory.*;
import cn.nukkit.inventory.transaction.*;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.data.ReleaseItemData;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.inventory.transaction.data.UseItemOnEntityData;
import cn.nukkit.item.*;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.*;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.particle.PunchBlockParticle;
import cn.nukkit.math.*;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.Network;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.positiontracking.PositionTracking;
import cn.nukkit.positiontracking.PositionTrackingService;
import cn.nukkit.potion.Effect;
import cn.nukkit.resourcepacks.ResourcePack;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.*;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @author MagicDroidX &amp; Box (Nukkit Project)
 */
@Log4j2
public class Player extends EntityHuman implements CommandSender, InventoryHolder, ChunkLoader, IPlayer {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final Player[] EMPTY_ARRAY = new Player[0];
    
    private static final int NO_SHIELD_DELAY = 10;

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
    public static final int CRAFTING_BEACON = 4;
    public static final @PowerNukkitOnly int CRAFTING_GRINDSTONE = 1000;
    public static final @PowerNukkitOnly int CRAFTING_STONECUTTER = 1001;
    public static final @PowerNukkitOnly int CRAFTING_CARTOGRAPHY = 1002;

    public static final float DEFAULT_SPEED = 0.1f;
    public static final float MAXIMUM_SPEED = 0.5f;

    public static final int PERMISSION_CUSTOM = 3;
    public static final int PERMISSION_OPERATOR = 2;
    public static final int PERMISSION_MEMBER = 1;
    public static final int PERMISSION_VISITOR = 0;

    public static final int ANVIL_WINDOW_ID = 2;
    public static final int ENCHANT_WINDOW_ID = 3;
    public static final int BEACON_WINDOW_ID = 4;
    public static final int GRINDSTONE_WINDOW_ID = 2;

    protected final SourceInterface interfaz;

    public boolean playedBefore;
    public boolean spawned = false;
    public boolean loggedIn = false;
    public boolean locallyInitialized = false;
    public int gamemode;
    public long lastBreak;
    private BlockVector3 lastBreakPosition = new BlockVector3();

    protected int windowCnt = 4;

    protected final BiMap<Inventory, Integer> windows = HashBiMap.create();

    protected final BiMap<Integer, Inventory> windowIndex = windows.inverse();
    protected final Set<Integer> permanentWindows = new IntOpenHashSet();
    private boolean inventoryOpen;
    @Since("1.3.2.0-PN") protected int closingWindowId = Integer.MIN_VALUE;

    protected int messageCounter = 2;

    private String clientSecret;

    public Vector3 speed = null;

    public final HashSet<String> achievements = new HashSet<>();

    public int craftingType = CRAFTING_SMALL;

    protected PlayerUIInventory playerUIInventory;
    protected CraftingGrid craftingGrid;
    protected CraftingTransaction craftingTransaction;
    @Since("1.3.1.0-PN") protected EnchantTransaction enchantTransaction;
    @Since("1.3.2.0-PN") protected RepairItemTransaction repairItemTransaction;
    @Since("1.4.0.0-PN") @PowerNukkitOnly protected GrindstoneTransaction grindstoneTransaction;

    public long creationTime = 0;

    protected long randomClientId;

    protected Vector3 forceMovement = null;

    protected Vector3 teleportPosition = null;

    protected boolean connected = true;
    protected final InetSocketAddress socketAddress;
    protected boolean removeFormat = true;

    protected String username;
    protected String iusername;
    protected String displayName;

    protected int startAction = -1;

    protected Vector3 sleeping = null;
    protected Long clientID = null;

    private int loaderId;

    protected float stepHeight = 0.6f;

    public final Map<Long, Boolean> usedChunks = new Long2ObjectOpenHashMap<>();

    protected int chunkLoadCount = 0;
    protected final Long2ObjectLinkedOpenHashMap<Boolean> loadQueue = new Long2ObjectLinkedOpenHashMap<>();
    protected int nextChunkOrderRun = 1;

    protected final Map<UUID, Player> hiddenPlayers = new HashMap<>();

    protected Vector3 newPosition = null;

    protected int chunkRadius;
    protected int viewDistance;
    protected final int chunksPerTick;
    protected final int spawnThreshold;

    protected Position spawnPosition = null;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected Vector3 spawnBlockPosition;

    protected int inAirTicks = 0;
    protected int startAirTicks = 5;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private int noShieldTicks;

    protected AdventureSettings adventureSettings;

    protected boolean checkMovement = true;

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

    protected int lastEnderPearl = 20;
    protected int lastChorusFruitTeleport = 20;

    private LoginChainData loginChainData;

    public Block breakingBlock = null;

    public int pickedXPOrb = 0;

    protected int formWindowCount = 0;
    protected Map<Integer, FormWindow> formWindows = new Int2ObjectOpenHashMap<>();
    protected Map<Integer, FormWindow> serverSettings = new Int2ObjectOpenHashMap<>();

    protected Map<Long, DummyBossBar> dummyBossBars = new Long2ObjectLinkedOpenHashMap<>();

    private AsyncTask preLoginEventTask = null;
    protected boolean shouldLogin = false;

    public EntityFishingHook fishing = null;

    public long lastSkinChange;

    protected double lastRightClickTime = 0.0;
    protected Vector3 lastRightClickPos = null;

    private int timeSinceRest;

    protected int lastPlayerdLevelUpSoundTime = 0;
    
    private TaskHandler delayedPosTrackingUpdate;

    private float soulSpeedMultiplier = 1;
    private boolean wasInSoulSandCompatible;

    public float getSoulSpeedMultiplier() {
        return this.soulSpeedMultiplier;
    }

    public int getStartActionTick() {
        return startAction;
    }

    public void startAction() {
        this.startAction = this.server.getTick();
    }

    public void stopAction() {
        this.startAction = -1;
    }

    public int getLastEnderPearlThrowingTick() {
        return lastEnderPearl;
    }

    public void onThrowEnderPearl() {
        this.lastEnderPearl = this.server.getTick();
    }

    public int getLastChorusFruitTeleport() {
        return lastChorusFruitTeleport;
    }

    public void onChorusFruitTeleport() {
        this.lastChorusFruitTeleport = this.server.getTick();
    }

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
     * @return random client id
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
        this.getAdventureSettings().set(Type.ALLOW_FLIGHT, value);
        this.getAdventureSettings().update();
    }

    @Deprecated
    public boolean getAllowFlight() {
        return this.getAdventureSettings().get(Type.ALLOW_FLIGHT);
    }

    public void setAllowModifyWorld(boolean value) {
        this.getAdventureSettings().set(Type.WORLD_IMMUTABLE, !value);
        this.getAdventureSettings().set(Type.BUILD_AND_MINE, value);
        this.getAdventureSettings().set(Type.WORLD_BUILDER, value);
        this.getAdventureSettings().update();
    }

    public void setAllowInteract(boolean value) {
        setAllowInteract(value, value);
    }

    public void setAllowInteract(boolean value, boolean containers) {
        this.getAdventureSettings().set(Type.WORLD_IMMUTABLE, !value);
        this.getAdventureSettings().set(Type.DOORS_AND_SWITCHED, value);
        this.getAdventureSettings().set(Type.OPEN_CONTAINERS, containers);
        this.getAdventureSettings().update();
    }

    @Deprecated
    public void setAutoJump(boolean value) {
        this.getAdventureSettings().set(Type.AUTO_JUMP, value);
        this.getAdventureSettings().update();
    }

    @Deprecated
    public boolean hasAutoJump() {
        return this.getAdventureSettings().get(Type.AUTO_JUMP);
    }

    @Override
    public void spawnTo(Player player) {
        if (this.spawned && player.spawned && this.isAlive() && player.getLevel() == this.level && player.canSee(this) && !this.isSpectator()) {
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

        if (this.isEnableClientCommand() && spawned) this.sendCommandData();
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
        if (!spawned) {
            return;
        }
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
            pk.commands = data;
            this.dataPacket(pk);
        }
    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return this.perm.getEffectivePermissions();
    }
    
    private static InetSocketAddress uncheckedNewInetSocketAddress(String ip, int port) {
        try {
            return new InetSocketAddress(InetAddress.getByName(ip), port);
        } catch (UnknownHostException exception) {
            throw new IllegalArgumentException(exception);
        }
    }
    
    public Player(SourceInterface interfaz, Long clientID, String ip, int port) {
        this(interfaz, clientID, uncheckedNewInetSocketAddress(ip, port));
    }
    
    @PowerNukkitOnly
    public Player(SourceInterface interfaz, Long clientID, InetSocketAddress socketAddress) {
        super(null, new CompoundTag());
        this.interfaz = interfaz;
        this.perm = new PermissibleBase(this);
        this.server = Server.getInstance();
        this.lastBreak = -1;
        this.socketAddress = socketAddress;
        this.clientID = clientID;
        this.loaderId = Level.generateChunkLoaderId(this);
        this.chunksPerTick = this.server.getConfig("chunk-sending.per-tick", 4);
        this.spawnThreshold = this.server.getConfig("chunk-sending.spawn-threshold", 56);
        this.spawnPosition = null;
        this.gamemode = this.server.getGamemode();
        this.setLevel(this.server.getDefaultLevel());
        this.viewDistance = this.server.getViewDistance();
        this.chunkRadius = viewDistance;
        //this.newPosition = new Vector3(0, 0, 0);
        this.boundingBox = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);
        this.lastSkinChange = -1;

        this.uuid = null;
        this.rawUUID = null;

        this.creationTime = System.currentTimeMillis();
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.addDefaultWindows();
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
            this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getDisplayName(), this.getSkin(), this.getLoginChainData().getXUID());
        }
    }

    @Override
    public void setSkin(Skin skin) {
        super.setSkin(skin);
        if (this.spawned) {
            this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getDisplayName(), skin, this.getLoginChainData().getXUID());
        }
    }

    public String getAddress() {
        return this.socketAddress.getAddress().getHostAddress();
    }

    public int getPort() {
        return this.socketAddress.getPort();
    }

    public InetSocketAddress getSocketAddress() {
        return this.socketAddress;
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

    /**
     * Returns whether the player is currently using an item (right-click and hold).
     *
     * @return bool
     */
    public boolean isUsingItem() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_ACTION) && this.startAction > -1;
    }

    public void setUsingItem(boolean value) {
        this.startAction = value ? this.server.getTick() : -1;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, value);
    }

    public String getButtonText() {
        return this.buttonText;
    }

    public void setButtonText(String text) {
        this.buttonText = text;
        this.setDataProperty(new StringEntityData(Entity.DATA_INTERACT_TEXT, this.buttonText));
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

    /**
     * The block that holds the player respawn position. May be null when unknown.
     * @return The position of a bed, respawn anchor, or null when unknown.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public Vector3 getSpawnBlock() {
        return spawnBlockPosition;
    }

    /**
     * Sets the position of the block that holds the player respawn position. May be null when unknown.
     * @param spawnBlock The position of a bed or respawn anchor
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setSpawnBlock(@Nullable Vector3 spawnBlock) {
        if (spawnBlock == null) {
            this.spawnBlockPosition = null;
        } else {
            this.spawnBlockPosition = new Vector3(spawnBlock.x, spawnBlock.y, spawnBlock.z);
        }
    }

    public void sendChunk(int x, int z, DataPacket packet) {
        if (!this.connected) {
            return;
        }

        this.usedChunks.put(Level.chunkHash(x, z), Boolean.TRUE);
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

    public void sendChunk(int x, int z, int subChunkCount, byte[] payload) {
        if (!this.connected) {
            return;
        }

        this.usedChunks.put(Level.chunkHash(x, z), true);
        this.chunkLoadCount++;

        LevelChunkPacket pk = new LevelChunkPacket();
        pk.chunkX = x;
        pk.chunkZ = z;
        pk.subChunkCount = subChunkCount;
        pk.data = payload;

        this.dataPacket(pk);

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

        if (!loadQueue.isEmpty()) {
            int count = 0;
            ObjectIterator<Long2ObjectMap.Entry<Boolean>> iter = loadQueue.long2ObjectEntrySet().fastIterator();
            while (iter.hasNext()) {
                Long2ObjectMap.Entry<Boolean> entry = iter.next();
                long index = entry.getLongKey();

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

                iter.remove();

                PlayerChunkRequestEvent ev = new PlayerChunkRequestEvent(this, chunkX, chunkZ);
                this.server.getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    this.level.requestChunk(chunkX, chunkZ, this);
                }
            }
        }
        if (this.chunkLoadCount >= this.spawnThreshold && !this.spawned && this.teleportPosition == null) {
            this.doFirstSpawn();
        }
        Timings.playerChunkSendTimer.stopTiming();
    }

    protected void doFirstSpawn() {
        this.spawned = true;

        this.inventory.sendContents(this);
        this.inventory.sendHeldItem(this);
        this.inventory.sendArmorContents(this);
        this.offhandInventory.sendContents(this);
        this.setEnableClientCommand(true);

        SetTimePacket setTimePacket = new SetTimePacket();
        setTimePacket.time = this.level.getTime();
        this.dataPacket(setTimePacket);

        Location pos;
        if(this.server.isSafeSpawn()) {
            pos = this.level.getSafeSpawn(this).getLocation();
            pos.yaw = this.yaw;
            pos.pitch = this.pitch;
        } else {
            pos = new Location(this.forceMovement.x,this.forceMovement.y,this.forceMovement.z, this.yaw, this.pitch, this.level);
        }


        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(this, pos, true);

        this.server.getPluginManager().callEvent(respawnEvent);

        Position fromEvent = respawnEvent.getRespawnPosition();
        if (fromEvent instanceof Location) {
            pos = fromEvent.getLocation();
        } else {
            pos = fromEvent.getLocation();
            pos.yaw = this.yaw;
            pos.pitch = this.pitch;
        }

        this.teleport(pos, null);
        lastYaw = yaw;
        lastPitch = pitch;

        this.sendPlayStatus(PlayStatusPacket.PLAYER_SPAWN);

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

        this.getServer().sendRecipeList(this);


        for (long index : this.usedChunks.keySet()) {
            int chunkX = Level.getHashX(index);
            int chunkZ = Level.getHashZ(index);
            for (Entity entity : this.level.getChunkEntities(chunkX, chunkZ).values()) {
                if (this != entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this);
                }
            }
        }

        int experience = this.getExperience();
        if (experience != 0) {
            this.sendExperience(experience);
        }

        int level = this.getExperienceLevel();
        if (level != 0) {
            this.sendExperienceLevel(this.getExperienceLevel());
        }

        if (!this.isSpectator()) {
            this.spawnToAll();
        }

        //todo Updater

        //Weather
        this.getLevel().sendWeather(this);

        //FoodLevel
        PlayerFood food = this.getFoodData();
        if (food.getLevel() != food.getMaxLevel()) {
            food.sendFoodLevel();
        }

        if (this.getHealth() < 1) {
            this.respawn();
        } else {
            updateTrackingPositions(false);
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void updateTrackingPositions() {
        updateTrackingPositions(false);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void updateTrackingPositions(boolean delayed) {
        Server server = getServer();
        if (delayed) {
            if (delayedPosTrackingUpdate != null) {
                delayedPosTrackingUpdate.cancel();
            }
            delayedPosTrackingUpdate = server.getScheduler().scheduleDelayedTask(null, this::updateTrackingPositions, 10);
            return;
        }
        PositionTrackingService positionTrackingService = server.getPositionTrackingService();
        positionTrackingService.forceRecheck(this);
    }

    protected boolean orderChunks() {
        if (!this.connected) {
            return false;
        }

        Timings.playerChunkOrderTimer.startTiming();

        this.nextChunkOrderRun = 200;

        loadQueue.clear();
        Long2ObjectOpenHashMap<Boolean> lastChunk = new Long2ObjectOpenHashMap<>(this.usedChunks);

        int centerX = (int) this.x >> 4;
        int centerZ = (int) this.z >> 4;

        int radius = spawned ? this.chunkRadius : (int) Math.ceil(Math.sqrt(spawnThreshold));
        int radiusSqr = radius * radius;



        long index;
        for (int x = 0; x <= radius; x++) {
            int xx = x * x;
            for (int z = 0; z <= x; z++) {
                int distanceSqr = xx + z * z;
                if (distanceSqr > radiusSqr) continue;

                /* Top right quadrant */
                if(this.usedChunks.get(index = Level.chunkHash(centerX + x, centerZ + z)) != Boolean.TRUE) {
                    this.loadQueue.put(index, Boolean.TRUE);
                }
                lastChunk.remove(index);
                /* Top left quadrant */
                if(this.usedChunks.get(index = Level.chunkHash(centerX - x - 1, centerZ + z)) != Boolean.TRUE) {
                    this.loadQueue.put(index, Boolean.TRUE);
                }
                lastChunk.remove(index);
                /* Bottom right quadrant */
                if(this.usedChunks.get(index = Level.chunkHash(centerX + x, centerZ - z - 1)) != Boolean.TRUE) {
                    this.loadQueue.put(index, Boolean.TRUE);
                }
                lastChunk.remove(index);
                /* Bottom left quadrant */
                if(this.usedChunks.get(index = Level.chunkHash(centerX - x - 1, centerZ - z - 1)) != Boolean.TRUE) {
                    this.loadQueue.put(index, Boolean.TRUE);
                }
                lastChunk.remove(index);
                if(x != z){
                    /* Top right quadrant mirror */
                    if(this.usedChunks.get(index = Level.chunkHash(centerX + z, centerZ + x)) != Boolean.TRUE) {
                        this.loadQueue.put(index, Boolean.TRUE);
                    }
                    lastChunk.remove(index);
                    /* Top left quadrant mirror */
                    if(this.usedChunks.get(index = Level.chunkHash(centerX - z - 1, centerZ + x)) != Boolean.TRUE) {
                        this.loadQueue.put(index, Boolean.TRUE);
                    }
                    lastChunk.remove(index);
                    /* Bottom right quadrant mirror */
                    if(this.usedChunks.get(index = Level.chunkHash(centerX + z, centerZ - x - 1)) != Boolean.TRUE) {
                        this.loadQueue.put(index, Boolean.TRUE);
                    }
                    lastChunk.remove(index);
                    /* Bottom left quadrant mirror */
                    if(this.usedChunks.get(index = Level.chunkHash(centerX - z - 1, centerZ - x - 1)) != Boolean.TRUE) {
                        this.loadQueue.put(index, Boolean.TRUE);
                    }
                    lastChunk.remove(index);
                }
            }
        }

        LongIterator keys = lastChunk.keySet().iterator();
        while (keys.hasNext()) {
            index = keys.nextLong();
            this.unloadChunk(Level.getHashX(index), Level.getHashZ(index));
        }

        if (!loadQueue.isEmpty()) {
            NetworkChunkPublisherUpdatePacket packet = new NetworkChunkPublisherUpdatePacket();
            packet.position = this.asBlockVector3();
            packet.radius = viewDistance << 4;
            this.dataPacket(packet);
        }

        Timings.playerChunkOrderTimer.stopTiming();
        return true;
    }

    @DeprecationDetails(by = "Cloudburst Nukkit", since = "1.3.2.0-PN", replaceWith = "dataPacket(DataPacket)",
            reason = "Batching packet is now handled near the RakNet layer")
    @Deprecated
    public boolean batchDataPacket(DataPacket packet) {
        return this.dataPacket(packet);
    }

    /**
     * 0 is true
     * -1 is false
     * other is identifer
     * @param packet packet to send
     * @return packet successfully sent
     */
    public boolean dataPacket(DataPacket packet) {
        if (!this.connected) {
            return false;
        }

        try (Timing ignored = Timings.getSendDataPacketTiming(packet)) {
            DataPacketSendEvent ev = new DataPacketSendEvent(this, packet);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }

            if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
                log.trace("Outbound {}: {}", this.getName(), packet);
            }

            this.interfaz.putPacket(this, packet, false, true);
        }
        return true;
    }

    @DeprecationDetails(by = "Cloudburst Nukkit", since = "2019-05-08", replaceWith = "dataPacket(DataPacket)",
            reason = "ACKs are handled by the RakNet layer only")
    @PowerNukkitDifference(since = "1.3.2.0-PN", 
            info = "Cloudburst changed the return values from 0/-1 to 1/0, breaking backward compatibility for no reason, " +
                    "we reversed that.")
    @Deprecated
    public int dataPacket(DataPacket packet, boolean needACK) {
        return dataPacket(packet) ? 0 : -1;
    }

    /**
     * 0 is true
     * -1 is false
     * other is identifer
     * @param packet packet to send
     * @return packet successfully sent
     */
    @Deprecated
    @DeprecationDetails(by = "Cloudburst Nukkit", since = "1.3.2.0-PN", replaceWith = "dataPacket(DataPacket)",
            reason = "Direct packets are no longer allowed")
    public boolean directDataPacket(DataPacket packet) {
        return this.dataPacket(packet);
    }

    @DeprecationDetails(by = "Cloudburst Nukkit", since = "2019-05-08", replaceWith = "dataPacket(DataPacket)",
            reason = "ACK are handled by the RakNet layer and direct packets are no longer allowed")
    @PowerNukkitDifference(since = "1.3.2.0-PN",
            info = "Cloudburst changed the return values from 0/-1 to 1/0, breaking backward compatibility for no reason, " +
                    "we reversed that.")
    @Deprecated
    public int directDataPacket(DataPacket packet, boolean needACK) {
        return this.dataPacket(packet) ? 0 : -1;
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

        this.timeSinceRest = 0;

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
        pk.dimension = this.spawnPosition.level.getDimension();
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
            pk.action = AnimatePacket.Action.WAKE_UP;
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
     * Returns a client-friendly gamemode of the specified real gamemode
     * This function takes care of handling gamemodes known to MCPE (as of 1.1.0.3, that includes Survival, Creative and Adventure)
     * <p>
     * TODO: remove this when Spectator Mode gets added properly to MCPE
     */
    private static int getClientFriendlyGamemode(int gamemode) {
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
            newSettings.set(Type.WORLD_IMMUTABLE, (gamemode & 0x02) > 0);
            newSettings.set(Type.BUILD_AND_MINE, (gamemode & 0x02) <= 0);
            newSettings.set(Type.WORLD_BUILDER, (gamemode & 0x02) <= 0);
            newSettings.set(Type.ALLOW_FLIGHT, (gamemode & 0x01) > 0);
            newSettings.set(Type.NO_CLIP, gamemode == 0x03);
            newSettings.set(Type.FLYING, gamemode == 0x03);
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
            this.getAdventureSettings().set(Type.FLYING, true);
            this.teleport(this.temporalVector.setComponents(this.x, this.y + 0.1, this.z));

            /*InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
            inventoryContentPacket.inventoryId = InventoryContentPacket.SPECIAL_CREATIVE;
            this.dataPacket(inventoryContentPacket);*/
        } else {
            if (this.isSurvival()) {
                this.getAdventureSettings().set(Type.FLYING, false);
            }
            /*InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
            inventoryContentPacket.inventoryId = InventoryContentPacket.SPECIAL_CREATIVE;
            inventoryContentPacket.slots = Item.getCreativeItems().toArray(new Item[0]);
            this.dataPacket(inventoryContentPacket);*/
        }

        this.resetFallDistance();

        this.inventory.sendContents(this);
        this.inventory.sendContents(this.getViewers().values());
        this.inventory.sendHeldItem(this.hasSpawned.values());
        this.offhandInventory.sendContents(this);
        this.offhandInventory.sendContents(this.getViewers().values());

        this.inventory.sendCreativeContents();
        return true;
    }

    @Deprecated
    public void sendSettings() {
        this.getAdventureSettings().update();
    }

    public boolean isSurvival() {
        return this.gamemode == SURVIVAL;
    }

    public boolean isCreative() {
        return this.gamemode == CREATIVE;
    }

    public boolean isSpectator() {
        return this.gamemode == SPECTATOR;
    }

    public boolean isAdventure() {
        return this.gamemode == ADVENTURE;
    }

    @Override
    public Item[] getDrops() {
        if (!this.isCreative() && !this.isSpectator()) {
            return super.getDrops();
        }

        return Item.EMPTY_ARRAY;
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
            bb.setMaxY(bb.getMinY() + 0.5);
            bb.setMinY(bb.getMinY() - 1);

            AxisAlignedBB realBB = this.boundingBox.clone();
            realBB.setMaxY(realBB.getMinY() + 0.1);
            realBB.setMinY(realBB.getMinY() - 0.2);

            int minX = NukkitMath.floorDouble(bb.getMinX());
            int minY = NukkitMath.floorDouble(bb.getMinY());
            int minZ = NukkitMath.floorDouble(bb.getMinZ());
            int maxX = NukkitMath.ceilDouble(bb.getMaxX());
            int maxY = NukkitMath.ceilDouble(bb.getMaxY());
            int maxZ = NukkitMath.ceilDouble(bb.getMaxZ());

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
        boolean scaffolding = false;
        boolean endPortal = false;
        for (Block block : this.getCollisionBlocks()) {
            switch (block.getId()) {
                case BlockID.NETHER_PORTAL:
                    portal = true;
                    break;
                case BlockID.SCAFFOLDING:
                    scaffolding = true;
                    break;
                case BlockID.END_PORTAL:
                    endPortal = true;
                    break;
            }

            block.onEntityCollide(this);
            block.getLevelBlockAtLayer(1).onEntityCollide(this);
        }

        setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_IN_SCAFFOLDING, scaffolding);

        AxisAlignedBB scanBoundingBox = boundingBox.getOffsetBoundingBox(0, -0.125, 0);
        scanBoundingBox.setMaxY(boundingBox.getMinY());
        Block[] scaffoldingUnder = level.getCollisionBlocks(
                scanBoundingBox,
                true, true,
                b-> b.getId() == BlockID.SCAFFOLDING
        );
        setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_OVER_SCAFFOLDING, scaffoldingUnder.length > 0);
        
        if (endPortal) {
            if (!inEndPortal) {
                inEndPortal = true;
                EntityPortalEnterEvent ev = new EntityPortalEnterEvent(this, PortalType.END);
                getServer().getPluginManager().callEvent(ev);
            }
        } else {
            inEndPortal = false;
        }
        
        if (portal) {
            if (this.isCreative() && this.inPortalTicks < 80) {
                this.inPortalTicks = 80;
            } else {
                this.inPortalTicks++;
            }
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
            this.positionChanged = false;
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
            if (this.newPosition == null) {
                return; //maybe solve that in better way
            }

            double diffX = this.x - newPos.x;
            double diffY = this.y - newPos.y;
            double diffZ = this.z - newPos.z;

            double yS = 0.5 + this.ySize;
            if (diffY >= -yS || diffY <= yS) {
                diffY = 0;
            }

            if (diffX != 0 || diffY != 0 || diffZ != 0) {
                if (this.checkMovement && !isOp() && !server.getAllowFlight() && (this.isSurvival() || this.isAdventure())) {
                    // Some say: I cant move my head when riding because the server
                    // blocked my movement
                    if (!this.isSleeping() && this.riding == null && !this.hasEffect(Effect.LEVITATION) && !this.hasEffect(Effect.SLOW_FALLING)) {
                        double diffHorizontalSqr = (diffX * diffX + diffZ * diffZ) / ((double) (tickDiff * tickDiff));
                        if (diffHorizontalSqr > 0.5) {
                            PlayerInvalidMoveEvent ev;
                            this.getServer().getPluginManager().callEvent(ev = new PlayerInvalidMoveEvent(this, true));
                            if (!ev.isCancelled()) {
                                revert = ev.isRevert();

                                if (revert) {
                                    log.warn(this.getServer().getLanguage().translateString("nukkit.player.invalidMove", this.getName()));
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
                        this.addMovement(this.x, this.y, this.z, this.yaw, this.pitch, this.yaw);
                    }
                    //Biome biome = Biome.biomes[level.getBiomeId(this.getFloorX(), this.getFloorZ())];
                    //sendTip(biome.getName() + " (" + biome.doesOverhang() + " " + biome.getBaseHeight() + "-" + biome.getHeightVariation() + ")");
                } else {
                    this.blocksAround = blocksAround;
                    this.collisionBlocks = collidingBlocks;
                }
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
                        this.getFoodData().updateFoodExpLevel(0.06 * distance + jump + swimming);
                    } else {
                        if (this.inAirTicks == 3 && swimming == 0) {
                            jump = 0.2;
                        }
                        this.getFoodData().updateFoodExpLevel(0.01 * distance + jump + swimming);
                    }
                }
            }
        }

        if (!revert && delta > 0.0001d) {
            Enchantment frostWalker = inventory.getBoots().getEnchantment(Enchantment.ID_FROST_WALKER);
            if (frostWalker != null && frostWalker.getLevel() > 0 && !this.isSpectator() && this.y >= 1 && this.y <= 255) {
                int radius = 2 + frostWalker.getLevel();
                for (int coordX = this.getFloorX() - radius; coordX < this.getFloorX() + radius + 1; coordX++) {
                    for (int coordZ = this.getFloorZ() - radius; coordZ < this.getFloorZ() + radius + 1; coordZ++) {
                        Block block = level.getBlock(coordX, this.getFloorY() - 1, coordZ);
                        int layer = 0;
                        if ((block.getId() != Block.STILL_WATER && (block.getId() != Block.WATER || block.getDamage() != 0)) || block.up().getId() != Block.AIR) {
                            block = block.getLevelBlockAtLayer(1);
                            layer = 1;
                            if ((block.getId() != Block.STILL_WATER && (block.getId() != Block.WATER || block.getDamage() != 0)) || block.up().getId() != Block.AIR) {
                                continue;
                            }
                        }
                        WaterFrostEvent ev = new WaterFrostEvent(block, this);
                        server.getPluginManager().callEvent(ev);
                        if (!ev.isCancelled()) {
                            level.setBlock(block, layer, Block.get(Block.ICE_FROSTED), true, false);
                            level.scheduleUpdate(level.getBlock(block, layer), ThreadLocalRandom.current().nextInt(20, 40));
                        }
                    }
                }
            }
        }

        if (!revert) {
            int soulSpeedLevel = this.getInventory().getBoots().getEnchantmentLevel(Enchantment.ID_SOUL_SPEED);

            if (soulSpeedLevel > 0) {
                Block downBlock = this.getLevelBlock().down();

                if (this.wasInSoulSandCompatible && !downBlock.isSoulSpeedCompatible()) {
                    this.wasInSoulSandCompatible = false;

                    this.soulSpeedMultiplier = 1;

                    this.sendMovementSpeed(this.movementSpeed);
                } else if (!this.wasInSoulSandCompatible && downBlock.isSoulSpeedCompatible()) {
                    this.wasInSoulSandCompatible = true;

                    this.soulSpeedMultiplier = (soulSpeedLevel * 0.105f) + 1.3f;

                    this.sendMovementSpeed(this.movementSpeed * this.soulSpeedMultiplier);
                }

            }
        }

        if (revert) {

            this.lastX = from.x;
            this.lastY = from.y;
            this.lastZ = from.z;

            this.lastYaw = from.yaw;
            this.lastPitch = from.pitch;

            // We have to send slightly above otherwise the player will fall into the ground.
            this.sendPosition(from.add(0, 0.00001, 0), from.yaw, from.pitch, MovePlayerPacket.MODE_RESET);
            //this.sendSettings();
            this.forceMovement = new Vector3(from.x, from.y + 0.00001, from.z);
        } else {
            this.forceMovement = null;
            if (distanceSquared != 0 && this.nextChunkOrderRun > 20) {
                this.nextChunkOrderRun = 20;
            }
        }

        this.newPosition = null;
    }

    @Override
    public void addMovement(double x, double y, double z, double yaw, double pitch, double headYaw) {
        this.sendPosition(new Vector3(x, y, z), yaw, pitch, MovePlayerPacket.MODE_NORMAL, this.getViewers().values().toArray(EMPTY_ARRAY));
    }

    @Override
    public boolean setMotion(Vector3 motion) {
        if (super.setMotion(motion)) {
            if (this.chunk != null) {
                this.addMotion(this.motionX, this.motionY, this.motionZ);  //Send to others
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

    public void sendMovementSpeed(float movementSpeed) {
        Attribute attribute = Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue(movementSpeed);

        this.setAttribute(attribute);
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

        if (this.fishing != null && this.server.getTick() % 20 == 0) {
            if (this.distance(fishing) > 33) {
                this.stopFishing(false);
            }
        }

        if (!this.isAlive() && this.spawned) {
            ++this.deadTicks;
            if (this.deadTicks >= 10) {
                this.despawnFromAll();
            }
            return true;
        }

        if (this.spawned) {
            this.processMovement(tickDiff);

            if (!this.isSpectator()) {
                this.checkNearEntities();
            }

            this.entityBaseTick(tickDiff);

            if (this.getServer().getDifficulty() == 0 && this.level.getGameRules().getBoolean(GameRule.NATURAL_REGENERATION)) {
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
                    if (this.checkMovement && !this.isGliding() && !server.getAllowFlight() && !this.getAdventureSettings().get(Type.ALLOW_FLIGHT) && this.inAirTicks > 20 && !this.isSleeping() && !this.isImmobile() && !this.isSwimming() && this.riding == null && !this.hasEffect(Effect.LEVITATION) && !this.hasEffect(Effect.SLOW_FALLING)) {
                        double expectedVelocity = (-this.getGravity()) / ((double) this.getDrag()) - ((-this.getGravity()) / ((double) this.getDrag())) * Math.exp(-((double) this.getDrag()) * ((double) (this.inAirTicks - this.startAirTicks)));
                        double diff = (this.speed.y - expectedVelocity) * (this.speed.y - expectedVelocity);

                        Block block = level.getBlock(this);
                        int blockId = block.getId();
                        boolean ignore = blockId == Block.LADDER || blockId == Block.VINES || blockId == Block.COBWEB
                                || blockId == Block.SCAFFOLDING;// || (blockId == Block.SWEET_BERRY_BUSH && block.getDamage() > 0);

                        if (!this.hasEffect(Effect.JUMP) && diff > 0.6 && expectedVelocity < this.speed.y && !ignore) {
                            if (this.inAirTicks < 150) {
                                this.setMotion(new Vector3(0, expectedVelocity, 0));
                            } else if (this.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server")) {
                                return false;
                            }
                        }
                        if (ignore) {
                            this.resetFallDistance();
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

            if (!this.isSleeping()) {
                this.timeSinceRest++;
            }
        }

        this.checkTeleportPosition();

        if (currentTick % 10 == 0) {
            this.checkInteractNearby();
        }

        if (this.spawned && this.dummyBossBars.size() > 0 && currentTick % 100 == 0) {
            this.dummyBossBars.values().forEach(DummyBossBar::updateBossEntityPosition);
        }

        updateBlockingFlag();
        return true;
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean hasUpdated = false;
        if (isUsingItem()) {
            if (noShieldTicks < NO_SHIELD_DELAY) {
                noShieldTicks = NO_SHIELD_DELAY;
                hasUpdated = true;
            }
        } else {
            if (noShieldTicks > 0) {
                noShieldTicks -= tickDiff;
                hasUpdated = true;
            }
            if (noShieldTicks < 0) {
                noShieldTicks = 0;
                hasUpdated = true;
            }
        }
        return super.entityBaseTick(tickDiff) || hasUpdated;
    }

    public void checkInteractNearby() {
        int interactDistance = isCreative() ? 5 : 3;
        if (canInteract(this, interactDistance)) {
            if (getEntityPlayerLookingAt(interactDistance) != null) {
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
    }

    public boolean canInteract(Vector3 pos, double maxDistance) {
        return this.canInteract(pos, maxDistance, 6.0);
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
            String reason = this.server.getNameBans().getEntires().get(this.getName().toLowerCase()).getReason();
            this.kick(PlayerKickEvent.Reason.NAME_BANNED, !reason.isEmpty() ? "You are banned. Reason: " + reason : "You are banned");
            return;
        } else if (this.server.getIPBans().isBanned(this.getAddress())) {
            String reason = this.server.getIPBans().getEntires().get(this.getAddress()).getReason();
            this.kick(PlayerKickEvent.Reason.IP_BANNED, !reason.isEmpty() ? "You are banned. Reason: " + reason : "You are banned");
            return;
        }

        if (this.hasPermission(Server.BROADCAST_CHANNEL_USERS)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_USERS, this);
        }
        if (this.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this);
        }

        Player oldPlayer = null;
        for (Player p : new ArrayList<>(this.server.getOnlinePlayers().values())) {
            if (p != this && p.getName() != null && p.getName().equalsIgnoreCase(this.getName()) ||
                    this.getUniqueId().equals(p.getUniqueId())) {
                oldPlayer = p;
                break;
            }
        }
        CompoundTag nbt;
        if (oldPlayer != null) {
            oldPlayer.saveNBT();
            nbt = oldPlayer.namedTag;
            oldPlayer.close("", "disconnectionScreen.loggedinOtherLocation");
        } else {
            File legacyDataFile = new File(server.getDataPath() + "players/" + this.username.toLowerCase() + ".dat");
            File dataFile = new File(server.getDataPath() + "players/" + this.uuid.toString() + ".dat");
            if (legacyDataFile.exists() && !dataFile.exists()) {
                nbt = this.server.getOfflinePlayerData(this.username, false);

                if (!legacyDataFile.delete()) {
                    log.warn("Could not delete legacy player data for {}", this.username);
                }
            } else {
                nbt = this.server.getOfflinePlayerData(this.uuid, true);
            }
        }

        if (nbt == null) {
            this.close(this.getLeaveMessage(), "Invalid data");
            return;
        }

        if (loginChainData.isXboxAuthed() && server.getPropertyBoolean("xbox-auth") || !server.getPropertyBoolean("xbox-auth")) {
            server.updateName(this.uuid, this.username);
        }

        this.playedBefore = (nbt.getLong("lastPlayed") - nbt.getLong("firstPlayed")) > 1;


        nbt.putString("NameTag", this.username);

        int exp = nbt.getInt("EXP");
        int expLevel = nbt.getInt("expLevel");
        this.setExperience(exp, expLevel);

        this.gamemode = nbt.getInt("playerGameType") & 0x03;
        if (this.server.getForceGamemode()) {
            this.gamemode = this.server.getGamemode();
            nbt.putInt("playerGameType", this.gamemode);
        }

        this.adventureSettings = new AdventureSettings(this)
                .set(Type.WORLD_IMMUTABLE, isAdventure() || isSpectator())
                .set(Type.WORLD_BUILDER, !isAdventure() && !isSpectator())
                .set(Type.AUTO_JUMP, true)
                .set(Type.ALLOW_FLIGHT, isCreative())
                .set(Type.NO_CLIP, isSpectator());

        Level level;
        if ((level = this.server.getLevelByName(nbt.getString("Level"))) == null) {
            this.setLevel(this.server.getDefaultLevel());
            nbt.putString("Level", this.level.getName());
            Position spawnLocation = this.level.getSafeSpawn();
            nbt.getList("Pos", DoubleTag.class)
                    .add(new DoubleTag("0", spawnLocation.x))
                    .add(new DoubleTag("1", spawnLocation.y))
                    .add(new DoubleTag("2", spawnLocation.z));
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

        UUID uuid = getUniqueId();
        nbt.putLong("UUIDLeast", uuid.getLeastSignificantBits());
        nbt.putLong("UUIDMost", uuid.getMostSignificantBits());

        if (this.server.getAutoSave()) {
            this.server.saveOfflinePlayerData(this.uuid, nbt, true);
        }

        this.sendPlayStatus(PlayStatusPacket.LOGIN_SUCCESS);
        this.server.onPlayerLogin(this);

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

        if (this.isSpectator()) this.keepMovement = true;

        this.forceMovement = this.teleportPosition = this.getPosition();

        if (!this.namedTag.contains("TimeSinceRest")) {
            this.namedTag.putInt("TimeSinceRest", 0);
        }
        this.timeSinceRest = this.namedTag.getInt("TimeSinceRest");

        if (!this.server.isCheckMovement()) {
            this.checkMovement = false;
        }

        ResourcePacksInfoPacket infoPacket = new ResourcePacksInfoPacket();
        infoPacket.resourcePackEntries = this.server.getResourcePackManager().getResourceStack();
        infoPacket.mustAccept = this.server.getForceResources();
        this.dataPacket(infoPacket);
    }

    protected void completeLoginSequence() {
        PlayerLoginEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerLoginEvent(this, "Plugin reason"));
        if (ev.isCancelled()) {
            this.close(this.getLeaveMessage(), ev.getKickMessage());
            return;
        }

        Level level = this.server.getLevelByName(this.namedTag.getString("SpawnLevel"));
        if(level != null){
            this.spawnPosition = new Position(this.namedTag.getInt("SpawnX"), this.namedTag.getInt("SpawnY"), this.namedTag.getInt("SpawnZ"), level);
            if (this.namedTag.containsInt("SpawnBlockPositionX") && this.namedTag.containsInt("SpawnBlockPositionY") && this.namedTag.containsInt("SpawnBlockPositionZ")) {
                this.spawnBlockPosition = new Vector3(namedTag.getInt("SpawnBlockPositionX"), namedTag.getInt("SpawnBlockPositionY"), namedTag.getInt("SpawnBlockPositionZ"));
            } else {
                this.spawnBlockPosition = null;
            }
        }else{
            this.spawnPosition = this.level.getSafeSpawn();
            this.spawnBlockPosition = null;
        }

        spawnPosition = this.getSpawn();

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
        startGamePacket.dimension = /*(byte) (this.level.getDimension() & 0xff)*/0;
        startGamePacket.worldGamemode = getClientFriendlyGamemode(this.gamemode);
        startGamePacket.difficulty = this.server.getDifficulty();
        startGamePacket.spawnX = spawnPosition.getFloorX();
        startGamePacket.spawnY = spawnPosition.getFloorY();
        startGamePacket.spawnZ = spawnPosition.getFloorZ();
        startGamePacket.hasAchievementsDisabled = true;
        startGamePacket.dayCycleStopTime = -1;
        startGamePacket.rainLevel = 0;
        startGamePacket.lightningLevel = 0;
        startGamePacket.commandsEnabled = this.isEnableClientCommand();
        startGamePacket.gameRules = getLevel().getGameRules();
        startGamePacket.levelId = "";
        startGamePacket.worldName = this.getServer().getNetwork().getName();
        startGamePacket.generator = 1; //0 old, 1 infinite, 2 flat
        startGamePacket.dimension = (byte) getLevel().getDimension();
        //startGamePacket.isInventoryServerAuthoritative = true;
        this.dataPacket(startGamePacket);

        this.dataPacket(new BiomeDefinitionListPacket());
        this.dataPacket(new AvailableEntityIdentifiersPacket());
        this.inventory.sendCreativeContents();
        this.getAdventureSettings().update();

        this.sendAttributes();

        this.sendPotionEffects(this);
        this.sendData(this);

        this.loggedIn = true;

        this.level.sendTime(this);

        this.sendAttributes();
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setCanClimb(true);

        log.info(this.getServer().getLanguage().translateString("nukkit.player.logIn",
                TextFormat.AQUA + this.username + TextFormat.WHITE,
                this.getAddress(),
                String.valueOf(this.getPort()),
                String.valueOf(this.id),
                this.level.getName(),
                String.valueOf(NukkitMath.round(this.x, 4)),
                String.valueOf(NukkitMath.round(this.y, 4)),
                String.valueOf(NukkitMath.round(this.z, 4))));

        if (this.isOp() || this.hasPermission("nukkit.textcolor")) {
            this.setRemoveFormat(false);
        }

        this.server.addOnlinePlayer(this);
        this.server.onPlayerCompleteLoginSequence(this);
    }

    public void handleDataPacket(DataPacket packet) {
        if (!connected) {
            return;
        }

        try (Timing ignored = Timings.getReceiveDataPacketTiming(packet)) {
            DataPacketReceiveEvent ev = new DataPacketReceiveEvent(this, packet);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return;
            }

            if (packet.pid() == ProtocolInfo.BATCH_PACKET) {
                this.server.getNetwork().processBatch((BatchPacket) packet, this);
                return;
            }

            if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
                log.trace("Inbound {}: {}", this.getName(), packet);
            }

            packetswitch:
            switch (packet.pid()) {
                case ProtocolInfo.LOGIN_PACKET:
                    if (this.loggedIn) {
                        break;
                    }

                    LoginPacket loginPacket = (LoginPacket) packet;

                    String message;
                    if (!ProtocolInfo.SUPPORTED_PROTOCOLS.contains(loginPacket.getProtocol())) {
                        if (loginPacket.getProtocol() < ProtocolInfo.CURRENT_PROTOCOL) {
                            message = "disconnectionScreen.outdatedClient";

                            this.sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_CLIENT, true);
                        } else {
                            message = "disconnectionScreen.outdatedServer";

                            this.sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_SERVER, true);
                        }
                        if (((LoginPacket) packet).protocol < 137) {
                            DisconnectPacket disconnectPacket = new DisconnectPacket();
                            disconnectPacket.message = message;
                            disconnectPacket.encode();
                            BatchPacket batch = new BatchPacket();
                            batch.payload = disconnectPacket.getBuffer();
                            this.dataPacket(batch);
                            // Still want to run close() to allow the player to be removed properly
                        }
                        this.close("", message, false);
                        break;
                    }

                    this.username = TextFormat.clean(loginPacket.username);
                    this.displayName = this.username;
                    this.iusername = this.username.toLowerCase();
                    this.setDataProperty(new StringEntityData(DATA_NAMETAG, this.username), false);

                    this.loginChainData = ClientChainData.read(loginPacket);

                    if (!loginChainData.isXboxAuthed() && server.getPropertyBoolean("xbox-auth")) {
                        this.close("", "disconnectionScreen.notAuthenticated");
                        break;
                    }

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
                        Skin skin = loginPacket.skin;
                        if (this.server.isForceSkinTrusted()) {
                            skin.setTrusted(true);
                        }
                        this.setSkin(skin);
                    }

                    PlayerPreLoginEvent playerPreLoginEvent;
                    this.server.getPluginManager().callEvent(playerPreLoginEvent = new PlayerPreLoginEvent(this, "Plugin reason"));
                    if (playerPreLoginEvent.isCancelled()) {
                        this.close("", playerPreLoginEvent.getKickMessage());

                        break;
                    }

                    Player playerInstance = this;
                    this.preLoginEventTask = new AsyncTask() {
                        private PlayerAsyncPreLoginEvent event;

                        @Override
                        public void onRun() {
                            this.event = new PlayerAsyncPreLoginEvent(username, uuid, loginChainData, playerInstance.getSkin(), playerInstance.getAddress(), playerInstance.getPort());
                            server.getPluginManager().callEvent(this.event);
                        }

                        @Override
                        public void onCompletion(Server server) {
                            if (playerInstance.closed) {
                                return;
                            }

                            if (this.event.getLoginResult() == LoginResult.KICK) {
                                playerInstance.close(this.event.getKickMessage(), this.event.getKickMessage());
                            } else if (playerInstance.shouldLogin) {
                                playerInstance.setSkin(this.event.getSkin());
                                playerInstance.completeLoginSequence();
                                for (Consumer<Server> action : this.event.getScheduledActions()) {
                                    action.accept(server);
                                }
                            }
                        }
                    };

                    this.server.getScheduler().scheduleAsyncTask(this.preLoginEventTask);
                    this.processLogin();
                    break;
                case ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET:
                    ResourcePackClientResponsePacket responsePacket = (ResourcePackClientResponsePacket) packet;
                    switch (responsePacket.responseStatus) {
                        case ResourcePackClientResponsePacket.STATUS_REFUSED:
                            this.close("", "disconnectionScreen.noReason");
                            break;
                        case ResourcePackClientResponsePacket.STATUS_SEND_PACKS:
                            for (ResourcePackClientResponsePacket.Entry entry : responsePacket.packEntries) {
                                ResourcePack resourcePack = this.server.getResourcePackManager().getPackById(entry.uuid);
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
                            this.shouldLogin = true;

                            if (this.preLoginEventTask.isFinished()) {
                                this.preLoginEventTask.onCompletion(server);
                            }
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
                case ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET:
                    if (this.locallyInitialized) {
                        break;
                    }
                    this.locallyInitialized = true;
                    PlayerLocallyInitializedEvent locallyInitializedEvent = new PlayerLocallyInitializedEvent(this);
                    this.server.getPluginManager().callEvent(locallyInitializedEvent);
                    break;
                case ProtocolInfo.PLAYER_SKIN_PACKET:
                    PlayerSkinPacket skinPacket = (PlayerSkinPacket) packet;
                    Skin skin = skinPacket.skin;

                    if (!skin.isValid()) {
                        break;
                    }

                    if (this.server.isForceSkinTrusted()) {
                        skin.setTrusted(true);
                    }

                    PlayerChangeSkinEvent playerChangeSkinEvent = new PlayerChangeSkinEvent(this, skin);
                    playerChangeSkinEvent.setCancelled(TimeUnit.SECONDS.toMillis(this.server.getPlayerSkinChangeCooldown()) > System.currentTimeMillis() - this.lastSkinChange);
                    this.server.getPluginManager().callEvent(playerChangeSkinEvent);
                    if (!playerChangeSkinEvent.isCancelled()) {
                        this.lastSkinChange = System.currentTimeMillis();
                        this.setSkin(skin);
                    }

                    break;
                case ProtocolInfo.PACKET_VIOLATION_WARNING_PACKET:
                    Optional<String> packetName = Arrays.stream(ProtocolInfo.class.getDeclaredFields())
                            .filter(field -> field.getType() == Byte.TYPE)
                            .filter(field -> {
                                try {
                                    return field.getByte(null) == ((PacketViolationWarningPacket) packet).packetId;
                                } catch (IllegalAccessException e) {
                                    return false;
                                }
                            }).map(Field::getName).findFirst();
                    log.warn("Violation warning from {}{}", this.getName(), packetName.map(name-> " for packet "+name).orElse("")+": " + packet.toString());
                    break;
                case ProtocolInfo.EMOTE_PACKET:
                    for (Player viewer : this.getViewers().values()) {
                        viewer.dataPacket(packet);
                    }
                    return;
                case ProtocolInfo.PLAYER_INPUT_PACKET:
                    if (!this.isAlive() || !this.spawned) {
                        break;
                    }
                    PlayerInputPacket ipk = (PlayerInputPacket) packet;
                    if (riding instanceof EntityMinecartAbstract) {
                        ((EntityMinecartAbstract) riding).setCurrentSpeed(ipk.motionY);
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

                    if (newPos.distanceSquared(this) > 100) {
                        this.sendPosition(this, movePlayerPacket.yaw, movePlayerPacket.pitch, MovePlayerPacket.MODE_RESET);
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
                        this.positionChanged = true;
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
                    if (!server.getAllowFlight() && adventureSettingsPacket.getFlag(AdventureSettingsPacket.FLYING) && !this.getAdventureSettings().get(Type.ALLOW_FLIGHT)) {
                        this.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server");
                        break;
                    }
                    PlayerToggleFlightEvent playerToggleFlightEvent = new PlayerToggleFlightEvent(this, adventureSettingsPacket.getFlag(AdventureSettingsPacket.FLYING));
                    this.server.getPluginManager().callEvent(playerToggleFlightEvent);
                    if (playerToggleFlightEvent.isCancelled()) {
                        this.getAdventureSettings().update();
                    } else {
                        this.getAdventureSettings().set(Type.FLYING, playerToggleFlightEvent.isFlying());
                    }
                    break;
                case ProtocolInfo.MOB_EQUIPMENT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    MobEquipmentPacket mobEquipmentPacket = (MobEquipmentPacket) packet;

                    Inventory inv = this.getWindowById(mobEquipmentPacket.windowId);

                    if (inv == null) {
                        log.debug("Player {} has no open container with window ID {}", this.getName(),  mobEquipmentPacket.windowId);
                        return;
                    }

                    Item item = inv.getItem(mobEquipmentPacket.hotbarSlot);

                    if (!item.equals(mobEquipmentPacket.item)) {
                        log.debug("Tried to equip {} but have {} in target slot", mobEquipmentPacket.item, item);
                        inv.sendContents(this);
                        return;
                    }

                    if (inv instanceof PlayerInventory) {
                        ((PlayerInventory) inv).equipItem(mobEquipmentPacket.hotbarSlot);
                    }

                    this.setDataFlag(Player.DATA_FLAGS, Player.DATA_FLAG_ACTION, false);

                    break;
                case ProtocolInfo.PLAYER_ACTION_PACKET:
                    PlayerActionPacket playerActionPacket = (PlayerActionPacket) packet;
                    if (!this.spawned || (!this.isAlive() && playerActionPacket.action != PlayerActionPacket.ACTION_RESPAWN && playerActionPacket.action != PlayerActionPacket.ACTION_DIMENSION_CHANGE_REQUEST)) {
                        break;
                    }

                    playerActionPacket.entityId = this.id;
                    Vector3 pos = new Vector3(playerActionPacket.x, playerActionPacket.y, playerActionPacket.z);
                    BlockFace face = BlockFace.fromIndex(playerActionPacket.face);

                    actionswitch:
                    switch (playerActionPacket.action) {
                        case PlayerActionPacket.ACTION_START_BREAK:
                            long currentBreak = System.currentTimeMillis();
                            BlockVector3 currentBreakPosition = new BlockVector3(playerActionPacket.x, playerActionPacket.y, playerActionPacket.z);
                            // HACK: Client spams multiple left clicks so we need to skip them.
                            if ((lastBreakPosition.equals(currentBreakPosition) && (currentBreak - this.lastBreak) < 10) || pos.distanceSquared(this) > 100) {
                                break;
                            }
                            Block target = this.level.getBlock(pos);
                            PlayerInteractEvent playerInteractEvent = new PlayerInteractEvent(this, this.inventory.getItemInHand(), target, face, target.getId() == 0 ? Action.LEFT_CLICK_AIR : Action.LEFT_CLICK_BLOCK);
                            this.getServer().getPluginManager().callEvent(playerInteractEvent);
                            if (playerInteractEvent.isCancelled()) {
                                this.inventory.sendHeldItem(this);
                                break;
                            }
                            target.onTouch(this, playerInteractEvent.getAction());
                            Block block = target.getSide(face);
                            if (block.getId() == BlockID.FIRE || block.getId() == BlockID.SOUL_FIRE) {
                                this.level.setBlock(block, Block.get(BlockID.AIR), true);
                                this.level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_EXTINGUISH_FIRE);
                                break;
                            }
                            if (block.getId() == BlockID.SWEET_BERRY_BUSH && block.getDamage() == 0) {
                                Item oldItem = playerInteractEvent.getItem();
                                Item i = this.level.useBreakOn(block, oldItem, this, true);
                                if (this.isSurvival()) {
                                    this.getFoodData().updateFoodExpLevel(0.025);
                                    if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                        inventory.setItemInHand(i);
                                        inventory.sendHeldItem(this.getViewers().values());
                                    }
                                }
                                break;
                            }
                            
                            if (!block.isBlockChangeAllowed(this)) {
                                break;
                            }
                            
                            if (!this.isCreative()) {
                                
                                //improved this to take stuff like swimming, ladders, enchanted tools into account, fix wrong tool break time calculations for bad tools (pmmp/PocketMine-MP#211)
                                //Done by lmlstarqaq
                                double breakTime = Math.ceil(target.calculateBreakTime(this.inventory.getItemInHand(), this) * 20);
                                if (breakTime > 0) {
                                    LevelEventPacket pk = new LevelEventPacket();
                                    pk.evid = LevelEventPacket.EVENT_BLOCK_START_BREAK;
                                    pk.x = (float) pos.x;
                                    pk.y = (float) pos.y;
                                    pk.z = (float) pos.z;
                                    pk.data = (int) (65535 / breakTime);
                                    this.getLevel().addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
                                }
                            }

                            this.breakingBlock = target;
                            this.lastBreak = currentBreak;
                            this.lastBreakPosition = currentBreakPosition;
                            break;

                        case PlayerActionPacket.ACTION_ABORT_BREAK:
                        case PlayerActionPacket.ACTION_STOP_BREAK:
                            LevelEventPacket pk = new LevelEventPacket();
                            pk.evid = LevelEventPacket.EVENT_BLOCK_STOP_BREAK;
                            pk.x = (float) pos.x;
                            pk.y = (float) pos.y;
                            pk.z = (float) pos.z;
                            pk.data = 0;
                            this.getLevel().addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
                            this.breakingBlock = null;
                            break;
                        case PlayerActionPacket.ACTION_GET_UPDATED_BLOCK:
                            break; //TODO
                        case PlayerActionPacket.ACTION_DROP_ITEM:
                            break; //TODO
                        case PlayerActionPacket.ACTION_STOP_SLEEPING:
                            this.stopSleep();
                            break;
                        case PlayerActionPacket.ACTION_RESPAWN:
                            if (!this.spawned || this.isAlive() || !this.isOnline()) {
                                break;
                            }

                            this.respawn();
                            break;
                        case PlayerActionPacket.ACTION_JUMP:
                            PlayerJumpEvent playerJumpEvent = new PlayerJumpEvent(this);
                            this.server.getPluginManager().callEvent(playerJumpEvent);
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
                        case PlayerActionPacket.ACTION_DIMENSION_CHANGE_ACK:
                            this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_NORMAL);
                            break; //TODO
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
                        case PlayerActionPacket.ACTION_CONTINUE_BREAK:
                            if (this.isBreakingBlock()) {
                                block = this.level.getBlock(pos);
                                this.level.addParticle(new PunchBlockParticle(pos, block, face));
                            }
                            break;
                        case PlayerActionPacket.ACTION_START_SWIMMING:
                            PlayerToggleSwimEvent ptse = new PlayerToggleSwimEvent(this, true);
                            this.server.getPluginManager().callEvent(ptse);

                            if (ptse.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSwimming(true);
                            }
                            break;
                        case PlayerActionPacket.ACTION_STOP_SWIMMING:
                            ptse = new PlayerToggleSwimEvent(this, false);
                            this.server.getPluginManager().callEvent(ptse);

                            if (ptse.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSwimming(false);
                            }
                            break;
                        case PlayerActionPacket.ACTION_START_SPIN_ATTACK:
                            if (this.inventory.getItemInHand().getId() != ItemID.TRIDENT) {
                                this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET);
                                break;
                            }
                            
                            int riptideLevel = this.inventory.getItemInHand().getEnchantmentLevel(Enchantment.ID_TRIDENT_RIPTIDE);
                            if (riptideLevel < 1) {
                                this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET);
                                break;
                            }
                            
                            if (!(this.isTouchingWater() || (this.getLevel().isRaining() && this.getLevel().canBlockSeeSky(this)))) {
                                this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET);
                                break;
                            }
                            
                            PlayerToggleSpinAttackEvent playerToggleSpinAttackEvent = new PlayerToggleSpinAttackEvent(this, true);
                            this.server.getPluginManager().callEvent(playerToggleSpinAttackEvent);

                            if (playerToggleSpinAttackEvent.isCancelled()) {
                                this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET);
                            } else {
                                this.setSpinAttacking(true);
                                
                                Sound riptideSound;
                                if (riptideLevel >= 3) {
                                    riptideSound = Sound.ITEM_TRIDENT_RIPTIDE_3;
                                } else if (riptideLevel == 2) {
                                    riptideSound = Sound.ITEM_TRIDENT_RIPTIDE_2;
                                } else {
                                    riptideSound = Sound.ITEM_TRIDENT_RIPTIDE_1;
                                }
                                this.level.addSound(this, riptideSound);
                            }
                            break packetswitch;
                        case PlayerActionPacket.ACTION_STOP_SPIN_ATTACK:
                            playerToggleSpinAttackEvent = new PlayerToggleSpinAttackEvent(this, false);
                            this.server.getPluginManager().callEvent(playerToggleSpinAttackEvent);

                            if (playerToggleSpinAttackEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSpinAttacking(false);
                            }
                            break;
                    }

                    this.setUsingItem(false);
                    break;
                case ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET:
                    break;

                case ProtocolInfo.MODAL_FORM_RESPONSE_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    ModalFormResponsePacket modalFormPacket = (ModalFormResponsePacket) packet;

                    if (formWindows.containsKey(modalFormPacket.formId)) {
                        FormWindow window = formWindows.remove(modalFormPacket.formId);
                        window.setResponse(modalFormPacket.data.trim());

                        PlayerFormRespondedEvent event = new PlayerFormRespondedEvent(this, modalFormPacket.formId, window);
                        getServer().getPluginManager().callEvent(event);
                    } else if (serverSettings.containsKey(modalFormPacket.formId)) {
                        FormWindow window = serverSettings.get(modalFormPacket.formId);
                        window.setResponse(modalFormPacket.data.trim());

                        PlayerSettingsRespondedEvent event = new PlayerSettingsRespondedEvent(this, modalFormPacket.formId, window);
                        getServer().getPluginManager().callEvent(event);

                        //Set back new settings if not been cancelled
                        if (!event.isCancelled() && window instanceof FormWindowCustom)
                            ((FormWindowCustom) window).setElementsFromResponse();
                    }

                    break;

                case ProtocolInfo.INTERACT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    InteractPacket interactPacket = (InteractPacket) packet;

                    if (interactPacket.action != InteractPacket.ACTION_MOUSEOVER || interactPacket.target != 0) {
                        this.craftingType = CRAFTING_SMALL;
                        //this.resetCraftingGridType();
                    }


                    Entity targetEntity = this.level.getEntity(interactPacket.target);

                    if (targetEntity == null || !this.isAlive() || !targetEntity.isAlive()) {
                        break;
                    }

                    if (targetEntity instanceof EntityItem || targetEntity instanceof EntityArrow || targetEntity instanceof EntityXPOrb) {
                        this.kick(PlayerKickEvent.Reason.INVALID_PVE, "Attempting to interact with an invalid entity");
                        log.warn(this.getServer().getLanguage().translateString("nukkit.player.invalidEntity", this.getName()));
                        break;
                    }

                    item = this.inventory.getItemInHand();

                    switch (interactPacket.action) {
                        case InteractPacket.ACTION_MOUSEOVER:
                            if (interactPacket.target == 0) {
                                break packetswitch;
                            }
                            this.getServer().getPluginManager().callEvent(new PlayerMouseOverEntityEvent(this, targetEntity));
                            break;
                        case InteractPacket.ACTION_VEHICLE_EXIT:
                            if (!(targetEntity instanceof EntityRideable) || this.riding == null) {
                                break;
                            }

                            ((EntityRideable) riding).mountEntity(this);
                            break;
                        case InteractPacket.ACTION_OPEN_INVENTORY:
                            if (targetEntity.getId() != this.getId()) break;
                            if (!this.inventoryOpen) {
                                this.inventory.open(this);
                                this.inventoryOpen = true;
                            }
                            break;
                    }
                    break;
                case ProtocolInfo.BLOCK_PICK_REQUEST_PACKET:
                    BlockPickRequestPacket pickRequestPacket = (BlockPickRequestPacket) packet;
                    Block block = this.level.getBlock(this.temporalVector.setComponents(pickRequestPacket.x, pickRequestPacket.y, pickRequestPacket.z));
                    item = block.toItem();

                    if (pickRequestPacket.addUserData) {
                        BlockEntity blockEntity = this.getLevel().getBlockEntity(new Vector3(pickRequestPacket.x, pickRequestPacket.y, pickRequestPacket.z));
                        if (blockEntity != null) {
                            CompoundTag nbt = blockEntity.getCleanedNBT();
                            if (nbt != null) {
                                item.setCustomBlockData(nbt);
                                item.setLore("+(DATA)");
                            }
                        }
                    }

                    PlayerBlockPickEvent pickEvent = new PlayerBlockPickEvent(this, block, item);
                    if (this.isSpectator()) {
                        log.debug("Got block-pick request from {} when in spectator mode", this.getName());
                        pickEvent.setCancelled();
                    }

                    this.server.getPluginManager().callEvent(pickEvent);

                    if (!pickEvent.isCancelled()) {
                        boolean itemExists = false;
                        int itemSlot = -1;
                        for (int slot = 0; slot < this.inventory.getSize(); slot++) {
                            if (this.inventory.getItem(slot).equals(pickEvent.getItem())) {
                                if (slot < this.inventory.getHotbarSize()) {
                                    this.inventory.setHeldItemSlot(slot);
                                } else {
                                    itemSlot = slot;
                                }
                                itemExists = true;
                                break;
                            }
                        }

                        for (int slot = 0; slot < this.inventory.getHotbarSize(); slot++) {
                            if (this.inventory.getItem(slot).isNull()) {
                                if (!itemExists && this.isCreative()) {
                                    this.inventory.setHeldItemSlot(slot);
                                    this.inventory.setItemInHand(pickEvent.getItem());
                                    break packetswitch;
                                } else if (itemSlot > -1) {
                                    this.inventory.setHeldItemSlot(slot);
                                    this.inventory.setItemInHand(this.inventory.getItem(itemSlot));
                                    this.inventory.clear(itemSlot, true);
                                    break packetswitch;
                                }
                            }
                        }

                        if (!itemExists && this.isCreative()) {
                            Item itemInHand = this.inventory.getItemInHand();
                            this.inventory.setItemInHand(pickEvent.getItem());
                            if (!this.inventory.isFull()) {
                                for (int slot = 0; slot < this.inventory.getSize(); slot++) {
                                    if (this.inventory.getItem(slot).isNull()) {
                                        this.inventory.setItem(slot, itemInHand);
                                        break;
                                    }
                                }
                            }
                        } else if (itemSlot > -1) {
                            Item itemInHand = this.inventory.getItemInHand();
                            this.inventory.setItemInHand(this.inventory.getItem(itemSlot));
                            this.inventory.setItem(itemSlot, itemInHand);
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

                    AnimatePacket.Action animation = animationEvent.getAnimationType();

                    switch (animation) {
                        case ROW_RIGHT:
                        case ROW_LEFT:
                            if (this.riding instanceof EntityBoat) {
                                ((EntityBoat) this.riding).onPaddle(animation, ((AnimatePacket) packet).rowingTime);
                            }
                            break;
                    }
                    
                    if (animationEvent.getAnimationType() == AnimatePacket.Action.SWING_ARM) {
                        setNoShieldTicks(NO_SHIELD_DELAY);
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
                    EntityEventPacket entityEventPacket = (EntityEventPacket) packet;
                    if (craftingType != CRAFTING_ANVIL && entityEventPacket.event != EntityEventPacket.ENCHANT) {
                        this.craftingType = CRAFTING_SMALL;
                        //this.resetCraftingGridType();
                    }


                    if (entityEventPacket.event == EntityEventPacket.EATING_ITEM) {
                        if (entityEventPacket.data == 0 || entityEventPacket.eid != this.id) {
                            break;
                        }

                        entityEventPacket.eid = this.id;
                        entityEventPacket.isEncoded = false;

                        this.dataPacket(entityEventPacket);
                        Server.broadcastPacket(this.getViewers().values(), entityEventPacket);
                    } else if (entityEventPacket.event == EntityEventPacket.ENCHANT) {
                        if (entityEventPacket.eid != this.id) {
                            break;
                        }

                        Inventory inventory = this.getWindowById(ANVIL_WINDOW_ID);
                        if (inventory instanceof AnvilInventory) {
                            ((AnvilInventory) inventory).setCost(-entityEventPacket.data);
                        }
                    }
                    break;
                case ProtocolInfo.COMMAND_REQUEST_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }
                    this.craftingType = CRAFTING_SMALL;
                    CommandRequestPacket commandRequestPacket = (CommandRequestPacket) packet;
                    PlayerCommandPreprocessEvent playerCommandPreprocessEvent = new PlayerCommandPreprocessEvent(this, commandRequestPacket.command);
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

                    TextPacket textPacket = (TextPacket) packet;

                    if (textPacket.type == TextPacket.TYPE_CHAT) {
                        String chatMessage = textPacket.message;
                        int breakLine = chatMessage.indexOf('\n');
                        // Chat messages shouldn't contain break lines so ignore text afterwards
                        if (breakLine != -1) {
                            chatMessage = chatMessage.substring(0, breakLine);
                        }
                        this.chat(chatMessage);
                    }
                    break;
                case ProtocolInfo.CONTAINER_CLOSE_PACKET:
                    ContainerClosePacket containerClosePacket = (ContainerClosePacket) packet;
                    if (!this.spawned || containerClosePacket.windowId == ContainerIds.INVENTORY && !inventoryOpen) {
                        break;
                    }

                    if (this.windowIndex.containsKey(containerClosePacket.windowId)) {
                        this.server.getPluginManager().callEvent(new InventoryCloseEvent(this.windowIndex.get(containerClosePacket.windowId), this));
                        if (containerClosePacket.windowId == ContainerIds.INVENTORY) this.inventoryOpen = false;
                        this.closingWindowId = containerClosePacket.windowId;
                        this.removeWindow(this.windowIndex.get(containerClosePacket.windowId), true);
                        this.closingWindowId = Integer.MIN_VALUE;
                    }
                    if (containerClosePacket.windowId == -1) {
                        this.craftingType = CRAFTING_SMALL;
                        this.resetCraftingGridType();
                        this.addWindow(this.craftingGrid, ContainerIds.NONE);
                        ContainerClosePacket pk = new ContainerClosePacket();
                        pk.wasServerInitiated = false;
                        pk.windowId = -1;
                        this.dataPacket(pk);
                    }
                    break;
                case ProtocolInfo.CRAFTING_EVENT_PACKET:
                    CraftingEventPacket craftingEventPacket = (CraftingEventPacket) packet;
                    if (craftingType == CRAFTING_BIG && craftingEventPacket.type == CraftingEventPacket.TYPE_WORKBENCH 
                            || craftingType == CRAFTING_SMALL && craftingEventPacket.type == CraftingEventPacket.TYPE_INVENTORY) {
                        if (craftingTransaction != null) {
                            craftingTransaction.setReadyToExecute(true);
                            if (craftingTransaction.getPrimaryOutput() == null) {
                                craftingTransaction.setPrimaryOutput(craftingEventPacket.output[0]);
                            }
                        }
                    }
                    break;
                case ProtocolInfo.BLOCK_ENTITY_DATA_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    BlockEntityDataPacket blockEntityDataPacket = (BlockEntityDataPacket) packet;
                    this.craftingType = CRAFTING_SMALL;
                    this.resetCraftingGridType();

                    pos = new Vector3(blockEntityDataPacket.x, blockEntityDataPacket.y, blockEntityDataPacket.z);
                    if (pos.distanceSquared(this) > 10000) {
                        break;
                    }

                    BlockEntity t = this.level.getBlockEntity(pos);
                    if (t instanceof BlockEntitySpawnable) {
                        CompoundTag nbt;
                        try {
                            nbt = NBTIO.read(blockEntityDataPacket.namedTag, ByteOrder.LITTLE_ENDIAN, true);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        if (!((BlockEntitySpawnable) t).updateCompoundTag(nbt, this)) {
                            ((BlockEntitySpawnable) t).spawnTo(this);
                        }
                    }
                    break;
                case ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET:
                    RequestChunkRadiusPacket requestChunkRadiusPacket = (RequestChunkRadiusPacket) packet;
                    ChunkRadiusUpdatedPacket chunkRadiusUpdatePacket = new ChunkRadiusUpdatedPacket();
                    this.chunkRadius = Math.max(3, Math.min(requestChunkRadiusPacket.radius, this.viewDistance));
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
                    
                // PowerNukkit Note: This packed is not being sent anymore since 1.16.210
                case ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET:
                    ItemFrameDropItemPacket itemFrameDropItemPacket = (ItemFrameDropItemPacket) packet;
                    Vector3 vector3 = this.temporalVector.setComponents(itemFrameDropItemPacket.x, itemFrameDropItemPacket.y, itemFrameDropItemPacket.z);
                    BlockEntity blockEntityItemFrame = this.level.getBlockEntity(vector3);
                    BlockEntityItemFrame itemFrame = (BlockEntityItemFrame) blockEntityItemFrame;
                    if (itemFrame != null) {
                        block = itemFrame.getBlock();
                        Item itemDrop = itemFrame.getItem();
                        ItemFrameDropItemEvent itemFrameDropItemEvent = new ItemFrameDropItemEvent(this, block, itemFrame, itemDrop);
                        this.server.getPluginManager().callEvent(itemFrameDropItemEvent);
                        if (!itemFrameDropItemEvent.isCancelled()) {
                            if (itemDrop.getId() != Item.AIR) {
                                vector3 = this.temporalVector.setComponents(itemFrame.x + 0.5, itemFrame.y, itemFrame.z + 0.5);
                                this.level.dropItem(vector3, itemDrop);
                                itemFrame.setItem(new ItemBlock(Block.get(BlockID.AIR)));
                                itemFrame.setItemRotation(0);
                                this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_ITEM_REMOVED);
                            }
                        } else {
                            itemFrame.spawnTo(this);
                        }
                    }
                    break;
                    
                    
                case ProtocolInfo.LECTERN_UPDATE_PACKET:
                    LecternUpdatePacket lecternUpdatePacket = (LecternUpdatePacket) packet;
                    BlockVector3 blockPosition = lecternUpdatePacket.blockPosition;
                    this.temporalVector.setComponents(blockPosition.x, blockPosition.y, blockPosition.z);
                    if (lecternUpdatePacket.dropBook) {
                        Block blockLectern = this.getLevel().getBlock(temporalVector);
                        if (blockLectern instanceof BlockLectern) {
                            ((BlockLectern) blockLectern).dropBook(this);
                        }
                    } else {
                        BlockEntity blockEntityLectern = this.level.getBlockEntity(this.temporalVector);
                        if (blockEntityLectern instanceof BlockEntityLectern) {
                            BlockEntityLectern lectern = (BlockEntityLectern) blockEntityLectern;
                            LecternPageChangeEvent lecternPageChangeEvent = new LecternPageChangeEvent(this, lectern, lecternUpdatePacket.page);
                            this.server.getPluginManager().callEvent(lecternPageChangeEvent);
                            if (!lecternPageChangeEvent.isCancelled()) {
                                lectern.setRawPage(lecternPageChangeEvent.getNewRawPage());
                                lectern.spawnToAll();
                                Block blockLectern = lectern.getBlock();
                                if (blockLectern instanceof BlockLectern) {
                                    ((BlockLectern) blockLectern).executeRedstonePulse();
                                }
                            }
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
                case ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V1:
                case ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V2:
                case ProtocolInfo.LEVEL_SOUND_EVENT_PACKET:
                    if (!this.isSpectator() || (((LevelSoundEventPacket) packet).sound != LevelSoundEventPacket.SOUND_HIT && ((LevelSoundEventPacket) packet).sound != LevelSoundEventPacket.SOUND_ATTACK_NODAMAGE)) {
                        this.level.addChunkPacket(this.getChunkX(), this.getChunkZ(), packet);
                    }
                    break;
                case ProtocolInfo.INVENTORY_TRANSACTION_PACKET:
                    if (this.isSpectator()) {
                        this.sendAllInventories();
                        break;
                    }

                    InventoryTransactionPacket transactionPacket = (InventoryTransactionPacket) packet;

                    List<InventoryAction> actions = new ArrayList<>();
                    for (NetworkInventoryAction networkInventoryAction : transactionPacket.actions) {
                        if (craftingType == CRAFTING_STONECUTTER && craftingTransaction != null
                                && networkInventoryAction.sourceType == NetworkInventoryAction.SOURCE_TODO) {
                            networkInventoryAction.windowId = NetworkInventoryAction.SOURCE_TYPE_CRAFTING_RESULT;
                        } else if (craftingType == CRAFTING_CARTOGRAPHY && craftingTransaction != null
                                && transactionPacket.actions.length == 2 && transactionPacket.actions[1].windowId == ContainerIds.UI
                                && networkInventoryAction.inventorySlot == 0) {
                            int slot = transactionPacket.actions[1].inventorySlot;
                            if (slot == 50) {
                                networkInventoryAction.windowId = NetworkInventoryAction.SOURCE_TYPE_CRAFTING_RESULT;
                            } else {
                                networkInventoryAction.inventorySlot = slot - 12;
                            }
                        }
                        InventoryAction a = networkInventoryAction.createInventoryAction(this);

                        if (a == null) {
                            log.debug("Unmatched inventory action from {}: {}", this.getName(), networkInventoryAction);
                            this.sendAllInventories();
                            break packetswitch;
                        }

                        actions.add(a);
                    }

                    if (transactionPacket.isCraftingPart) {
                        if (this.craftingTransaction == null) {
                            this.craftingTransaction = new CraftingTransaction(this, actions);
                        } else {
                            for (InventoryAction action : actions) {
                                this.craftingTransaction.addAction(action);
                            }
                        }

                        if (this.craftingTransaction.getPrimaryOutput() != null && (this.craftingTransaction.isReadyToExecute() || this.craftingTransaction.canExecute())) {
                            //we get the actions for this in several packets, so we can't execute it until we get the result

                            if (this.craftingTransaction.execute()) {
                                Sound sound = null;
                                switch (craftingType) {
                                    case CRAFTING_STONECUTTER:
                                        sound = Sound.BLOCK_STONECUTTER_USE;
                                        break;
                                    case CRAFTING_GRINDSTONE:
                                        sound = Sound.BLOCK_GRINDSTONE_USE;
                                        break;
                                    case CRAFTING_CARTOGRAPHY:
                                        sound = Sound.BLOCK_CARTOGRAPHY_TABLE_USE;
                                        break;
                                }

                                if (sound != null) {
                                    Collection<Player> players = level.getChunkPlayers(getChunkX(), getChunkZ()).values();
                                    players.remove(this);
                                    if (!players.isEmpty()) {
                                        level.addSound(this, sound, 1f, 1f, players);
                                    }
                                }
                            }
                            this.craftingTransaction = null;
                        }

                        return;
                    } else if (transactionPacket.isEnchantingPart) {
                        if (this.enchantTransaction == null) {
                            this.enchantTransaction = new EnchantTransaction(this, actions);
                        } else {
                            for (InventoryAction action : actions) {
                                this.enchantTransaction.addAction(action);
                            }
                        }
                        if (this.enchantTransaction.canExecute()) {
                            this.enchantTransaction.execute();
                            this.enchantTransaction = null;
                        }
                        return;
                    } else if (transactionPacket.isRepairItemPart) {
                        if (GrindstoneTransaction.checkForItemPart(actions)) {
                            if (this.grindstoneTransaction == null) {
                                this.grindstoneTransaction = new GrindstoneTransaction(this, actions);
                            } else {
                                for (InventoryAction action : actions) {
                                    this.grindstoneTransaction.addAction(action);
                                }
                            }
                            if (this.grindstoneTransaction.canExecute()) {
                                this.grindstoneTransaction.execute();
                                this.grindstoneTransaction = null;
                            }
                        } else {
                            if (this.repairItemTransaction == null) {
                                this.repairItemTransaction = new RepairItemTransaction(this, actions);
                            } else {
                                for (InventoryAction action : actions) {
                                    this.repairItemTransaction.addAction(action);
                                }
                            }
                            if (this.repairItemTransaction.canExecute()) {
                                this.repairItemTransaction.execute();
                                this.repairItemTransaction = null;
                            }
                        }
                        return;
                    } else if (this.craftingTransaction != null) {
                        if (craftingTransaction.checkForCraftingPart(actions)) {
                            for (InventoryAction action : actions) {
                                craftingTransaction.addAction(action);
                            }
                            return;
                        } else {
                            log.debug("Got unexpected normal inventory action with incomplete crafting transaction from {}, refusing to execute crafting", this.getName());
                            this.removeAllWindows(false);
                            this.sendAllInventories();
                            this.craftingTransaction = null;
                        }
                    } else if (this.enchantTransaction != null) {
                        if (enchantTransaction.checkForEnchantPart(actions)) {
                            for (InventoryAction action : actions) {
                                enchantTransaction.addAction(action);
                            }
                            return;
                        } else {
                            log.debug("Got unexpected normal inventory action with incomplete enchanting transaction from {}, refusing to execute enchant {}", this.getName(), transactionPacket.toString());
                            this.removeAllWindows(false);
                            this.sendAllInventories();
                            this.enchantTransaction = null;
                        }
                    } else if (this.repairItemTransaction != null) {
                        if (GrindstoneTransaction.checkForItemPart(actions)) {
                            for (InventoryAction action : actions) {
                                this.grindstoneTransaction.addAction(action);
                            }
                            return;
                        } else if (RepairItemTransaction.checkForRepairItemPart(actions)) {
                            for (InventoryAction action : actions) {
                                this.repairItemTransaction.addAction(action);
                            }
                            return;
                        } else {
                            this.server.getLogger().debug("Got unexpected normal inventory action with incomplete repair item transaction from " + this.getName() + ", refusing to execute repair item " + transactionPacket.toString());
                            this.removeAllWindows(false);
                            this.sendAllInventories();
                            this.repairItemTransaction = null;
                        }
                    }

                    switch (transactionPacket.transactionType) {
                        case InventoryTransactionPacket.TYPE_NORMAL:
                            InventoryTransaction transaction = new InventoryTransaction(this, actions);

                            if (!transaction.execute()) {
                                log.debug("Failed to execute inventory transaction from {} with actions: {}", this.getName(), Arrays.toString(transactionPacket.actions));
                                break packetswitch; //oops!
                            }

                            //TODO: fix achievement for getting iron from furnace

                            break packetswitch;
                        case InventoryTransactionPacket.TYPE_MISMATCH:
                            if (transactionPacket.actions.length > 0) {
                                log.debug("Expected 0 actions for mismatch, got {}, {}", transactionPacket.actions.length, Arrays.toString(transactionPacket.actions));
                            }
                            this.sendAllInventories();

                            break packetswitch;
                        case InventoryTransactionPacket.TYPE_USE_ITEM:
                            UseItemData useItemData = (UseItemData) transactionPacket.transactionData;

                            BlockVector3 blockVector = useItemData.blockPos;
                            face = useItemData.face;

                            int type = useItemData.actionType;
                            switch (type) {
                                case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_BLOCK:
                                    // Remove if client bug is ever fixed
                                    boolean spamBug = (lastRightClickPos != null && System.currentTimeMillis() - lastRightClickTime < 100.0 && blockVector.distanceSquared(lastRightClickPos) < 0.00001);
                                    lastRightClickPos = blockVector.asVector3();
                                    lastRightClickTime = System.currentTimeMillis();
                                    if (spamBug && this.getInventory().getItemInHand().getBlockId() == BlockID.AIR) {
                                        return;
                                    }

                                    this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false);

                                    if (this.canInteract(blockVector.add(0.5, 0.5, 0.5), this.isCreative() ? 13 : 7)) {
                                        if (this.isCreative()) {
                                            Item i = inventory.getItemInHand();
                                            if (this.level.useItemOn(blockVector.asVector3(), i, face, useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, this) != null) {
                                                break packetswitch;
                                            }
                                        } else if (inventory.getItemInHand().equals(useItemData.itemInHand)) {
                                            Item i = inventory.getItemInHand();
                                            Item oldItem = i.clone();
                                            //TODO: Implement adventure mode checks
                                            if ((i = this.level.useItemOn(blockVector.asVector3(), i, face, useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, this)) != null) {
                                                if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                                    inventory.setItemInHand(i);
                                                    inventory.sendHeldItem(this.getViewers().values());
                                                }
                                                break packetswitch;
                                            }
                                        }
                                    }

                                    inventory.sendHeldItem(this);

                                    if (blockVector.distanceSquared(this) > 10000) {
                                        break packetswitch;
                                    }

                                    Block target = this.level.getBlock(blockVector.asVector3());
                                    block = target.getSide(face);

                                    this.level.sendBlocks(new Player[]{this}, new Block[]{target, block}, UpdateBlockPacket.FLAG_NOGRAPHIC);
                                    this.level.sendBlocks(new Player[]{this}, new Block[]{target.getLevelBlockAtLayer(1), block.getLevelBlockAtLayer(1)}, UpdateBlockPacket.FLAG_NOGRAPHIC, 1);
                                    break packetswitch;
                                case InventoryTransactionPacket.USE_ITEM_ACTION_BREAK_BLOCK:
                                    if (!this.spawned || !this.isAlive()) {
                                        break packetswitch;
                                    }

                                    this.resetCraftingGridType();

                                    Item i = this.getInventory().getItemInHand();

                                    Item oldItem = i.clone();

                                    if (this.canInteract(blockVector.add(0.5, 0.5, 0.5), this.isCreative() ? 13 : 7) && (i = this.level.useBreakOn(blockVector.asVector3(), face, i, this, true)) != null) {
                                        if (this.isSurvival()) {
                                            this.getFoodData().updateFoodExpLevel(0.025);
                                            if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                                inventory.setItemInHand(i);
                                                inventory.sendHeldItem(this.getViewers().values());
                                            }
                                        }
                                        break packetswitch;
                                    }

                                    inventory.sendContents(this);
                                    target = this.level.getBlock(blockVector.asVector3());
                                    BlockEntity blockEntity = this.level.getBlockEntity(blockVector.asVector3());

                                    this.level.sendBlocks(new Player[]{this}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY);

                                    inventory.sendHeldItem(this);

                                    if (blockEntity instanceof BlockEntitySpawnable) {
                                        ((BlockEntitySpawnable) blockEntity).spawnTo(this);
                                    }

                                    break packetswitch;
                                case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_AIR:
                                    Vector3 directionVector = this.getDirectionVector();

                                    if (this.isCreative()) {
                                        item = this.inventory.getItemInHand();
                                    } else if (!this.inventory.getItemInHand().equals(useItemData.itemInHand)) {
                                        this.inventory.sendHeldItem(this);
                                        break packetswitch;
                                    } else {
                                        item = this.inventory.getItemInHand();
                                    }

                                    PlayerInteractEvent interactEvent = new PlayerInteractEvent(this, item, directionVector, face, Action.RIGHT_CLICK_AIR);

                                    this.server.getPluginManager().callEvent(interactEvent);

                                    if (interactEvent.isCancelled()) {
                                        this.inventory.sendHeldItem(this);
                                        break packetswitch;
                                    }

                                    if (item.onClickAir(this, directionVector)) {
                                        if (!this.isCreative()) {
                                            this.inventory.setItemInHand(item);
                                        }

                                        if (!this.isUsingItem()) {
                                            this.setUsingItem(true);
                                            break packetswitch;
                                        }

                                        // Used item
                                        int ticksUsed = this.server.getTick() - this.startAction;
                                        this.setUsingItem(false);

                                        if (!item.onUse(this, ticksUsed)) {
                                            this.inventory.sendContents(this);
                                        }
                                    }

                                    break packetswitch;
                                default:
                                    //unknown
                                    break;
                            }
                            break;
                        case InventoryTransactionPacket.TYPE_USE_ITEM_ON_ENTITY:
                            UseItemOnEntityData useItemOnEntityData = (UseItemOnEntityData) transactionPacket.transactionData;

                            Entity target = this.level.getEntity(useItemOnEntityData.entityRuntimeId);
                            if (target == null) {
                                return;
                            }

                            type = useItemOnEntityData.actionType;

                            if (!useItemOnEntityData.itemInHand.equalsExact(this.inventory.getItemInHand())) {
                                this.inventory.sendHeldItem(this);
                            }

                            item = this.inventory.getItemInHand();

                            switch (type) {
                                case InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_INTERACT:
                                    PlayerInteractEntityEvent playerInteractEntityEvent = new PlayerInteractEntityEvent(this, target, item, useItemOnEntityData.clickPos);
                                    if (this.isSpectator()) playerInteractEntityEvent.setCancelled();
                                    getServer().getPluginManager().callEvent(playerInteractEntityEvent);

                                    if (playerInteractEntityEvent.isCancelled()) {
                                        break;
                                    }
                                    if (target.onInteract(this, item, useItemOnEntityData.clickPos) && (this.isSurvival() || this.isAdventure())) {
                                        if (item.isTool()) {
                                            if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                                                level.addSound(this, Sound.RANDOM_BREAK);
                                                item = new ItemBlock(Block.get(BlockID.AIR));
                                            }
                                        } else {
                                            if (item.count > 1) {
                                                item.count--;
                                            } else {
                                                item = new ItemBlock(Block.get(BlockID.AIR));
                                            }
                                        }

                                        this.inventory.setItemInHand(item);
                                    }
                                    break;
                                case InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_ATTACK:
                                    if (target.getId() == this.getId()) {
                                        this.kick(PlayerKickEvent.Reason.INVALID_PVP, "Attempting to attack yourself");
                                        log.warn(this.getName() + " tried to attack oneself");
                                        break;
                                    }
                                    
                                    float itemDamage = item.getAttackDamage();

                                    for (Enchantment enchantment : item.getEnchantments()) {
                                        itemDamage += enchantment.getDamageBonus(target);
                                    }

                                    Map<DamageModifier, Float> damage = new EnumMap<>(DamageModifier.class);
                                    damage.put(DamageModifier.BASE, itemDamage);

                                    if (!this.canInteract(target, isCreative() ? 8 : 5)) {
                                        break;
                                    } else if (target instanceof Player) {
                                        if ((((Player) target).getGamemode() & 0x01) > 0) {
                                            break;
                                        } else if (!this.server.getPropertyBoolean("pvp") || this.server.getDifficulty() == 0) {
                                            break;
                                        }
                                    }

                                    EntityDamageByEntityEvent entityDamageByEntityEvent = new EntityDamageByEntityEvent(this, target, DamageCause.ENTITY_ATTACK, damage);
                                    if (this.isSpectator()) entityDamageByEntityEvent.setCancelled();
                                    if ((target instanceof Player) && !this.level.getGameRules().getBoolean(GameRule.PVP)) {
                                        entityDamageByEntityEvent.setCancelled();
                                    }
                                    
                                    
                                    if (target instanceof EntityLiving) {
                                        ((EntityLiving) target).preAttack(this);
                                    }
                                    
                                    try {
                                        if (!target.attack(entityDamageByEntityEvent)) {
                                            if (item.isTool() && this.isSurvival()) {
                                                this.inventory.sendContents(this);
                                            }
                                            break;
                                        }
                                    } finally {
                                        if (target instanceof EntityLiving) {
                                            ((EntityLiving) target).postAttack(this);
                                        }
                                    }

                                    for (Enchantment enchantment : item.getEnchantments()) {
                                        enchantment.doPostAttack(this, target);
                                    }

                                    if (item.isTool() && this.isSurvival()) {
                                        if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                                            level.addSound(this, Sound.RANDOM_BREAK);
                                            this.inventory.setItemInHand(new ItemBlock(Block.get(BlockID.AIR)));
                                        } else {
                                            this.inventory.setItemInHand(item);
                                        }
                                    }
                                    return;
                                default:
                                    break; //unknown
                            }

                            break;
                        case InventoryTransactionPacket.TYPE_RELEASE_ITEM:
                            if (this.isSpectator()) {
                                this.sendAllInventories();
                                break packetswitch;
                            }
                            ReleaseItemData releaseItemData = (ReleaseItemData) transactionPacket.transactionData;

                            try {
                                type = releaseItemData.actionType;
                                switch (type) {
                                    case InventoryTransactionPacket.RELEASE_ITEM_ACTION_RELEASE:
                                        if (this.isUsingItem()) {
                                            item = this.inventory.getItemInHand();

                                            int ticksUsed = this.server.getTick() - this.startAction;
                                            if (!item.onRelease(this, ticksUsed)) {
                                                this.inventory.sendContents(this);
                                            }

                                            this.setUsingItem(false);
                                        } else {
                                            this.inventory.sendContents(this);
                                        }
                                        return;
                                    case InventoryTransactionPacket.RELEASE_ITEM_ACTION_CONSUME:
                                        log.debug("Unexpected release item action consume from {}", this::getName);
                                        return;
                                    default:
                                        break;
                                }
                            } finally {
                                this.setUsingItem(false);
                            }
                            break;
                        default:
                            this.inventory.sendContents(this);
                            break;
                    }
                    break;
                case ProtocolInfo.PLAYER_HOTBAR_PACKET:
                    PlayerHotbarPacket hotbarPacket = (PlayerHotbarPacket) packet;

                    if (hotbarPacket.windowId != ContainerIds.INVENTORY) {
                        return; //In PE this should never happen
                    }

                    this.inventory.equipItem(hotbarPacket.selectedHotbarSlot);
                    break;
                case ProtocolInfo.SERVER_SETTINGS_REQUEST_PACKET:
                    PlayerServerSettingsRequestEvent settingsRequestEvent = new PlayerServerSettingsRequestEvent(this, new HashMap<>(this.serverSettings));
                    this.getServer().getPluginManager().callEvent(settingsRequestEvent);

                    if (!settingsRequestEvent.isCancelled()) {
                        settingsRequestEvent.getSettings().forEach((id, window) -> {
                            ServerSettingsResponsePacket re = new ServerSettingsResponsePacket();
                            re.formId = id;
                            re.data = window.getJSONData();
                            this.dataPacket(re);
                        });
                    }
                    break;
                case ProtocolInfo.RESPAWN_PACKET:
                    if (this.isAlive()) {
                        break;
                    }
                    RespawnPacket respawnPacket = (RespawnPacket) packet;
                    if (respawnPacket.respawnState == RespawnPacket.STATE_CLIENT_READY_TO_SPAWN) {
                        RespawnPacket respawn1 = new RespawnPacket();
                        respawn1.x = (float) this.getX();
                        respawn1.y = (float) this.getY();
                        respawn1.z = (float) this.getZ();
                        respawn1.respawnState = RespawnPacket.STATE_READY_TO_SPAWN;
                        this.dataPacket(respawn1);
                    }
                    break;
                case ProtocolInfo.BOOK_EDIT_PACKET:
                    BookEditPacket bookEditPacket = (BookEditPacket) packet;
                    Item oldBook = this.inventory.getItem(bookEditPacket.inventorySlot);
                    if (oldBook.getId() != Item.BOOK_AND_QUILL) {
                        return;
                    }

                    if (bookEditPacket.text == null || bookEditPacket.text.length() > 256) {
                        return;
                    }

                    Item newBook = oldBook.clone();
                    boolean success;
                    switch (bookEditPacket.action) {
                        case REPLACE_PAGE:
                            success = ((ItemBookAndQuill) newBook).setPageText(bookEditPacket.pageNumber, bookEditPacket.text);
                            break;
                        case ADD_PAGE:
                            success = ((ItemBookAndQuill) newBook).insertPage(bookEditPacket.pageNumber, bookEditPacket.text);
                            break;
                        case DELETE_PAGE:
                            success = ((ItemBookAndQuill) newBook).deletePage(bookEditPacket.pageNumber);
                            break;
                        case SWAP_PAGES:
                            success = ((ItemBookAndQuill) newBook).swapPages(bookEditPacket.pageNumber, bookEditPacket.secondaryPageNumber);
                            break;
                        case SIGN_BOOK:
                            newBook = Item.get(Item.WRITTEN_BOOK, 0, 1, oldBook.getCompoundTag());
                            success = ((ItemBookWritten) newBook).signBook(bookEditPacket.title, bookEditPacket.author, bookEditPacket.xuid, ItemBookWritten.GENERATION_ORIGINAL);
                            break;
                        default:
                            return;
                    }

                    if (success) {
                        PlayerEditBookEvent editBookEvent = new PlayerEditBookEvent(this, oldBook, newBook, bookEditPacket.action);
                        this.server.getPluginManager().callEvent(editBookEvent);
                        if (!editBookEvent.isCancelled()) {
                            this.inventory.setItem(bookEditPacket.inventorySlot, editBookEvent.getNewBook());
                        }
                    }
                    break;
                case ProtocolInfo.FILTER_TEXT_PACKET:
                    FilterTextPacket filterTextPacket = (FilterTextPacket) packet;

                    FilterTextPacket textResponsePacket = new FilterTextPacket();

                    if (craftingType == CRAFTING_ANVIL) {
                        AnvilInventory anvilInventory = (AnvilInventory) getWindowById(ANVIL_WINDOW_ID);
                        if (anvilInventory != null) {
                            PlayerTypingAnvilInventoryEvent playerTypingAnvilInventoryEvent = new PlayerTypingAnvilInventoryEvent(
                                    this, anvilInventory, anvilInventory.getNewItemName(), filterTextPacket.getText()
                            );
                            getServer().getPluginManager().callEvent(playerTypingAnvilInventoryEvent);
                            anvilInventory.setNewItemName(playerTypingAnvilInventoryEvent.getTypedName());
                        }
                    }
                    
                    textResponsePacket.text = filterTextPacket.text;
                    textResponsePacket.fromServer = true;
                    this.dataPacket(textResponsePacket);
                    break;
                case ProtocolInfo.POS_TRACKING_CLIENT_REQUEST_PACKET:
                    PositionTrackingDBClientRequestPacket posTrackReq = (PositionTrackingDBClientRequestPacket) packet;
                    try {
                        PositionTracking positionTracking = this.server.getPositionTrackingService().startTracking(this, posTrackReq.getTrackingId(), true);
                        if (positionTracking != null) {
                            break;
                        }
                    } catch (IOException e) {
                        log.warn("Failed to track the trackingHandler {}", posTrackReq.getTrackingId(), e);
                    }
                    PositionTrackingDBServerBroadcastPacket notFound = new PositionTrackingDBServerBroadcastPacket();
                    notFound.setAction(PositionTrackingDBServerBroadcastPacket.Action.NOT_FOUND);
                    notFound.setTrackingId(posTrackReq.getTrackingId());
                    dataPacket(notFound);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Sends a chat message as this player. If the message begins with a / (forward-slash) it will be treated
     * as a command.
     * @param message message to send
     * @return successful
     */
    public boolean chat(String message) {
        if (!this.spawned || !this.isAlive()) {
            return false;
        }

        this.resetCraftingGridType();
        this.craftingType = CRAFTING_SMALL;

        if (this.removeFormat) {
            message = TextFormat.clean(message, true);
        }

        for (String msg : message.split("\n")) {
            if (!msg.trim().isEmpty() && msg.length() <= 255 && this.messageCounter-- > 0) {
                PlayerChatEvent chatEvent = new PlayerChatEvent(this, msg);
                this.server.getPluginManager().callEvent(chatEvent);
                if (!chatEvent.isCancelled()) {
                    this.server.broadcastMessage(this.getServer().getLanguage().translateString(chatEvent.getFormat(), new String[]{chatEvent.getPlayer().getDisplayName(), chatEvent.getMessage()}), chatEvent.getRecipients());
                }
            }
        }

        return true;
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
                    message = "Kicked by admin." + (!reasonString.isEmpty() ? " Reason: " + reasonString : "");
                } else {
                    message = reasonString;
                }
            } else {
                if (reasonString.isEmpty()) {
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

    public void setViewDistance(int distance) {
        this.chunkRadius = distance;

        ChunkRadiusUpdatedPacket pk = new ChunkRadiusUpdatedPacket();
        pk.radius = distance;

        this.dataPacket(pk);
    }

    public int getViewDistance() {
        return this.chunkRadius;
    }

    @Override
    public void sendMessage(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_RAW;
        pk.message = this.server.getLanguage().translateString(message);
        this.dataPacket(pk);
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
        this.sendTranslation(message, EmptyArrays.EMPTY_STRINGS);
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

    public void sendChat(String message) {
        this.sendChat("", message);
    }

    public void sendChat(String source, String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_CHAT;
        pk.source = source;
        pk.message = this.server.getLanguage().translateString(message);
        this.dataPacket(pk);
    }

    public void sendPopup(String message) {
        this.sendPopup(message, "");
    }

    // TODO: Support Translation Parameters
    public void sendPopup(String message, String subtitle) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_POPUP;
        pk.message = message;
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

    public void setSubtitle(String subtitle) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_SUBTITLE;
        pk.text = subtitle;
        this.dataPacket(pk);
    }

    public void setTitleAnimationTimes(int fadein, int duration, int fadeout) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_ANIMATION_TIMES;
        pk.fadeInTime = fadein;
        pk.stayTime = duration;
        pk.fadeOutTime = fadeout;
        this.dataPacket(pk);
    }


    private void setTitle(String text) {
        SetTitlePacket packet = new SetTitlePacket();
        packet.text = text;
        packet.type = SetTitlePacket.TYPE_TITLE;
        this.dataPacket(packet);
    }

    public void sendTitle(String title) {
        this.sendTitle(title, null, 20, 20, 5);
    }

    public void sendTitle(String title, String subtitle) {
        this.sendTitle(title, subtitle, 20, 20, 5);
    }

    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        this.setTitleAnimationTimes(fadeIn, stay, fadeOut);
        if (!Strings.isNullOrEmpty(subtitle)) {
            this.setSubtitle(subtitle);
        }
        // title won't send if an empty string is used.
        this.setTitle(Strings.isNullOrEmpty(title) ? " " : title);
    }

    public void sendActionBar(String title) {
        this.sendActionBar(title, 1, 0, 1);
    }

    public void sendActionBar(String title, int fadein, int duration, int fadeout) {
        SetTitlePacket pk = new SetTitlePacket();
        pk.type = SetTitlePacket.TYPE_ACTION_BAR;
        pk.text = title;
        pk.fadeInTime = fadein;
        pk.stayTime = duration;
        pk.fadeOutTime = fadeout;
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
                this.dataPacket(pk);
            }

            this.connected = false;
            PlayerQuitEvent ev = null;
            if (this.getName() != null && this.getName().length() > 0) {
                this.server.getPluginManager().callEvent(ev = new PlayerQuitEvent(this, message, true, reason));
                if (this.fishing != null) {
                    this.stopFishing(false);
                }
            }
            
            // Close the temporary windows first, so they have chance to change all inventories before being disposed 
            this.removeAllWindows(false);
            resetCraftingGridType();

            if (ev != null && this.loggedIn && ev.getAutoSave()) {
                this.save();
            }

            for (Player player : new ArrayList<>(this.server.getOnlinePlayers().values())) {
                if (!player.canSee(this)) {
                    player.showPlayer(this);
                }
            }

            this.hiddenPlayers.clear();

            this.removeAllWindows(true);

            for (long index : new ArrayList<>(this.usedChunks.keySet())) {
                int chunkX = Level.getHashX(index);
                int chunkZ = Level.getHashZ(index);
                this.level.unregisterChunkLoader(this, chunkX, chunkZ);
                this.usedChunks.remove(index);

                for (Entity entity : level.getChunkEntities(chunkX, chunkZ).values()) {
                    if (entity != this) {
                        entity.getViewers().remove(getLoaderId());
                    }
                }
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
            log.info(this.getServer().getLanguage().translateString("nukkit.player.logOut",
                    TextFormat.AQUA + (this.getName() == null ? "" : this.getName()) + TextFormat.WHITE,
                    this.getAddress(),
                    String.valueOf(this.getPort()),
                    this.getServer().getLanguage().translateString(reason)));
            this.windows.clear();
            this.usedChunks.clear();
            this.loadQueue.clear();
            this.hasSpawned.clear();
            this.spawnPosition = null;

            if (this.riding instanceof EntityRideable) {
                this.riding.passengers.remove(this);
            }

            this.riding = null;
        }

        if (this.perm != null) {
            this.perm.clearPermissions();
            this.perm = null;
        }

        if (this.inventory != null) {
            this.inventory = null;
        }

        this.chunk = null;

        this.server.removePlayer(this);
    }

    public void save() {
        this.save(false);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        
        if (spawnBlockPosition == null) {
            namedTag.remove("SpawnBlockPositionX").remove("SpawnBlockPositionY").remove("SpawnBlockPositionZ");
        } else {
            namedTag.putInt("SpawnBlockPositionX", spawnBlockPosition.getFloorX())
                    .putInt("SpawnBlockPositionY", spawnBlockPosition.getFloorY())
                    .putInt("SpawnBlockPositionZ", spawnBlockPosition.getFloorZ());
        }
    }

    public void save(boolean async) {
        if (this.closed) {
            throw new IllegalStateException("Tried to save closed player");
        }

        saveNBT();

        if (this.level != null) {
            this.namedTag.putString("Level", this.level.getFolderName());
            if (this.spawnPosition != null && this.spawnPosition.getLevel() != null) {
                this.namedTag.putString("SpawnLevel", this.spawnPosition.getLevel().getFolderName());
                this.namedTag.putInt("SpawnX", this.spawnPosition.getFloorX());
                this.namedTag.putInt("SpawnY", this.spawnPosition.getFloorY());
                this.namedTag.putInt("SpawnZ", this.spawnPosition.getFloorZ());
                this.namedTag.putInt("SpawnDimension", this.spawnPosition.getLevel().getDimension());
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

            this.namedTag.putInt("TimeSinceRest", this.timeSinceRest);

            if (!this.username.isEmpty() && this.namedTag != null) {
                this.server.saveOfflinePlayerData(this.uuid, this.namedTag, async);
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

        boolean showMessages = this.level.getGameRules().getBoolean(GameRule.SHOW_DEATH_MESSAGE);
        String message = "";
        List<String> params = new ArrayList<>();
        EntityDamageEvent cause = this.getLastDamageCause();

        if (showMessages) {
            params.add(this.getDisplayName());

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
                case VOID:
                    message = "death.attack.outOfWorld";
                    break;
                case FALL:
                    if (cause.getFinalDamage() > 2) {
                        message = "death.fell.accident.generic";
                        break;
                    }
                    message = "death.attack.fall";
                    break;

                case SUFFOCATION:
                    message = "death.attack.inWall";
                    break;

                case LAVA:
                    Block block = this.level.getBlock(new Vector3(this.x, this.y - 1, this.z));
                    if (block.getId() == Block.MAGMA) {
                        message = "death.attack.lava.magma";
                        break;
                    }
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
                        } else {
                            message = "death.attack.explosion";
                        }
                    } else {
                        message = "death.attack.explosion";
                    }
                    break;
                case MAGIC:
                    message = "death.attack.magic";
                    break;
                case LIGHTNING:
                    message = "death.attack.lightningBolt";
                    break;
                case HUNGER:
                    message = "death.attack.starve";
                    break;
                default:
                    message = "death.attack.generic";
                    break;
            }
        }

        PlayerDeathEvent ev = new PlayerDeathEvent(this, this.getDrops(), new TranslationContainer(message, params.toArray(EmptyArrays.EMPTY_STRINGS)), this.expLevel);
        ev.setKeepExperience(this.level.gameRules.getBoolean(GameRule.KEEP_INVENTORY));
        ev.setKeepInventory(ev.getKeepExperience());

        // Note for diff comparison: Totem code was moved to Entity 
        this.server.getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            if (this.fishing != null) {
                this.stopFishing(false);
            }

            this.health = 0;
            this.extinguish();
            this.scheduleUpdate();

            if (!ev.getKeepInventory() && this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
                for (Item item : ev.getDrops()) {
                    if (!item.hasEnchantment(Enchantment.ID_VANISHING_CURSE)) {
                        this.level.dropItem(this, item, null, true, 40);
                    }
                }

                if (this.inventory != null) {
                    this.inventory.clearAll();
                }
                if (this.offhandInventory != null) {
                    this.offhandInventory.clearAll();
                }
            }

            if (!ev.getKeepExperience() && this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
                if (this.isSurvival() || this.isAdventure()) {
                    int exp = ev.getExperience() * 7;
                    if (exp > 100) exp = 100;
                    this.getLevel().dropExpOrb(this, exp);
                }
                this.setExperience(0, 0);
            }

            this.timeSinceRest = 0;

            if (showMessages && !ev.getDeathMessage().toString().isEmpty()) {
                this.server.broadcast(ev.getDeathMessage(), Server.BROADCAST_CHANNEL_USERS);
            }

            RespawnPacket pk = new RespawnPacket();
            Position pos = this.getSpawn();
            pk.x = (float) pos.x;
            pk.y = (float) pos.y;
            pk.z = (float) pos.z;
            pk.respawnState = RespawnPacket.STATE_SEARCHING_FOR_SPAWN;

            this.dataPacket(pk);
        }
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected boolean isValidRespawnBlock(Block block) {
        if (block.getId() == BlockID.RESPAWN_ANCHOR && block.getLevel().getDimension() == Level.DIMENSION_NETHER) {
            BlockRespawnAnchor anchor = (BlockRespawnAnchor) block;
            return anchor.getCharge() > 0;
        }
        if (block.getId() == BlockID.BED_BLOCK && block.getLevel().getDimension() == Level.DIMENSION_OVERWORLD) {
            BlockBed bed = (BlockBed) block;
            return bed.isBedValid();
        }
        
        return false;
    }

    protected void respawn() {
        if (this.server.isHardcore()) {
            this.setBanned(true);
            return;
        }

        this.craftingType = CRAFTING_SMALL;
        this.resetCraftingGridType();

        Vector3 spawnBlock = getSpawnBlock();
        if (spawnBlock == null) {
            spawnBlock = spawnPosition;
        }
        
        PlayerRespawnEvent playerRespawnEvent = new PlayerRespawnEvent(this, this.getSpawn());
        Block respawnBlock;
        int respawnBlockDim = Level.DIMENSION_OVERWORLD;
        if (spawnBlock != null) {
            Position spawnBlockPos = new Position(spawnBlock.x, spawnBlock.y, spawnBlock.z, playerRespawnEvent.getRespawnPosition().getLevel());
            respawnBlockDim = spawnBlockPos.level.getDimension();
            playerRespawnEvent.setRespawnBlockPosition(spawnBlockPos);
            respawnBlock = spawnBlockPos.getLevelBlock();
            if (isValidRespawnBlock(respawnBlock)) {
                playerRespawnEvent.setRespawnBlockAvailable(true);
                playerRespawnEvent.setConsumeCharge(respawnBlock.getId() == BlockID.RESPAWN_ANCHOR);
            } else {
                playerRespawnEvent.setRespawnBlockAvailable(false);
                playerRespawnEvent.setConsumeCharge(false);
                playerRespawnEvent.setOriginalRespawnPosition(playerRespawnEvent.getRespawnPosition());
                playerRespawnEvent.setRespawnPosition(this.server.getDefaultLevel().getSafeSpawn());
            }
        }
        
        this.server.getPluginManager().callEvent(playerRespawnEvent);
        
        if (!playerRespawnEvent.isRespawnBlockAvailable()) {
            if (!playerRespawnEvent.isKeepRespawnBlockPosition()) {
                this.spawnBlockPosition = null;
            }
            if (!playerRespawnEvent.isKeepRespawnPosition()) {
                this.spawnPosition = null;
                if (playerRespawnEvent.isSendInvalidRespawnBlockMessage()) {
                    sendMessage(new TranslationContainer(TextFormat.GRAY + 
                            "%tile."+(respawnBlockDim==Level.DIMENSION_OVERWORLD?"bed":"respawn_anchor")+".notValid"));
                }
            }
        }

        if (playerRespawnEvent.isConsumeCharge()) {
            Position pos = playerRespawnEvent.getRespawnBlockPosition();
            if (pos != null && pos.isValid()) {
                respawnBlock = pos.getLevelBlock();
                if (respawnBlock.getId() == BlockID.RESPAWN_ANCHOR) {
                    BlockRespawnAnchor respawnAnchor = (BlockRespawnAnchor) respawnBlock;
                    int charge = respawnAnchor.getCharge();
                    if (charge > 0) {
                        respawnAnchor.setCharge(charge-1);
                        respawnAnchor.getLevel().setBlock(respawnAnchor, respawnBlock);
                        respawnAnchor.getLevel().scheduleUpdate(respawnAnchor, 10);
                    }
                }
            }
        }

        Position respawnPos = playerRespawnEvent.getRespawnPosition();

        this.sendExperience();
        this.sendExperienceLevel();

        this.setSprinting(false);
        this.setSneaking(false);

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
        this.offhandInventory.sendContents(this);

        this.teleport(respawnPos, null);
        this.spawnToAll();
        this.scheduleUpdate();

        if (playerRespawnEvent.isConsumeCharge()) {
            getLevel().addSound(this, Sound.RESPAWN_ANCHOR_DEPLETE, 1, 1, this);
        }
    }

    @Override
    public void setHealth(float health) {
        if (health < 1) {
            health = 0;
        }

        super.setHealth(health);
        //TODO: Remove it in future! This a hack to solve the client-side absorption bug! WFT Mojang (Half a yellow heart cannot be shown, we can test it in local gaming)
        Attribute attr = Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(this.getAbsorption() % 2 != 0 ? this.getMaxHealth() + 1 : this.getMaxHealth()).setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0);
        if (this.spawned) {
            UpdateAttributesPacket pk = new UpdateAttributesPacket();
            pk.entries = new Attribute[]{attr};
            pk.entityId = this.id;
            this.dataPacket(pk);
        }
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        super.setMaxHealth(maxHealth);

        Attribute attr = Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(this.getAbsorption() % 2 != 0 ? this.getMaxHealth() + 1 : this.getMaxHealth()).setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0);
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
        addExperience(add, false);
    }

    public void addExperience(int add, boolean playLevelUpSound) {
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
        this.setExperience(added, level, playLevelUpSound);
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

    public void setExperience(int exp, int level) {
        setExperience(exp, level, false);
    }

    //todo something on performance, lots of exp orbs then lots of packets, could crash client

    public void setExperience(int exp, int level, boolean playLevelUpSound) {
        int levelBefore = this.expLevel;
        this.exp = exp;
        this.expLevel = level;

        this.sendExperienceLevel(level);
        this.sendExperience(exp);
        if (playLevelUpSound && levelBefore < level && levelBefore / 5 != level / 5 && this.lastPlayerdLevelUpSoundTime < this.age - 100) {
            this.lastPlayerdLevelUpSoundTime = this.age;
            this.level.addLevelSoundEvent(
                    this, 
                    LevelSoundEventPacketV2.SOUND_LEVELUP,
                    Math.min(7, level / 5) << 28,
                    "",
                    false, false
            );
        }
    }

    public void sendExperience() {
        sendExperience(this.getExperience());
    }

    public void sendExperience(int exp) {
        if (this.spawned) {
            float percent = ((float) exp) / calculateRequireExperience(this.getExperienceLevel());
            percent = Math.max(0f, Math.min(1f, percent));
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
        setMovementSpeed(speed, true);
    }

    public void setMovementSpeed(float speed, boolean send) {
        super.setMovementSpeed(speed);
        if (this.spawned && send) {
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

        if (this.isSpectator() || (this.isCreative() && source.getCause() != DamageCause.SUICIDE)) {
            //source.setCancelled();
            return false;
        } else if (this.getAdventureSettings().get(Type.ALLOW_FLIGHT) && source.getCause() == DamageCause.FALL) {
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

        if (super.attack(source)) { //!source.isCancelled()
            if (this.getLastDamageCause() == source && this.spawned) {
                if (source instanceof EntityDamageByEntityEvent) {
                    Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
                    if (damager instanceof Player) {
                        ((Player) damager).getFoodData().updateFoodExpLevel(0.3);
                    }
                }
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

    /**
     * Drops an item on the ground in front of the player. Returns if the item drop was successful.
     *
     * @param item to drop
     * @return bool if the item was dropped or if the item was null
     */
    public boolean dropItem(Item item) {
        if (!this.spawned || !this.isAlive()) {
            return false;
        }

        if (item.isNull()) {
            log.debug("{} attempted to drop a null item ({})", this.getName(), item);
            return true;
        }

        Vector3 motion = this.getDirectionVector().multiply(0.4);

        this.level.dropItem(this.add(0, 1.3, 0), item, motion, 40);

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false);
        return true;
    }

    /**
     * Drops an item on the ground in front of the player. Returns the dropped item.
     *
     * @param item to drop
     * @return EntityItem if the item was dropped or null if the item was null
     */
    @Since("1.3.2.0-PN")
    @Nullable
    public EntityItem dropAndGetItem(@Nonnull Item item) {
        if (!this.spawned || !this.isAlive()) {
            return null;
        }

        if (item.isNull()) {
            log.debug("{} attempted to drop a null item ({})", this.getName(), item);
            return null;
        }

        Vector3 motion = this.getDirectionVector().multiply(0.4);

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, false);

        return this.level.dropAndGetItem(this.add(0, 1.3, 0), item, motion, 40);
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

    public void sendPosition(Vector3 pos, double yaw, double pitch, int mode) {
        this.sendPosition(pos, yaw, pitch, mode, null);
    }

    public void sendPosition(Vector3 pos, double yaw, double pitch, int mode, Player[] targets) {
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

            this.spawnToAll();
            this.forceMovement = this.teleportPosition;
            this.teleportPosition = null;
            return true;
        }

        return false;
    }

    protected void sendPlayStatus(int status) {
        sendPlayStatus(status, false);
    }

    protected void sendPlayStatus(int status, boolean immediate) {
        PlayStatusPacket pk = new PlayStatusPacket();
        pk.status = status;

        this.dataPacket(pk);
    }

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
        }

        //TODO Remove it! A hack to solve the client-side teleporting bug! (inside into the block)
        if (super.teleport(to.getY() == to.getFloorY() ? to.add(0, 0.00001, 0) : to, null)) { // null to prevent fire of duplicate EntityTeleportEvent
            this.removeAllWindows();

            this.teleportPosition = new Vector3(this.x, this.y, this.z);
            this.forceMovement = this.teleportPosition;
            this.yaw = to.yaw;
            this.pitch = to.pitch;
            this.sendPosition(this, to.yaw, to.pitch, MovePlayerPacket.MODE_TELEPORT);

            this.checkTeleportPosition();

            this.resetFallDistance();
            this.nextChunkOrderRun = 0;
            this.newPosition = null;

            //DummyBossBar
            this.getDummyBossBars().values().forEach(DummyBossBar::reshow);
            //Weather
            this.getLevel().sendWeather(this);
            //Update time
            this.getLevel().sendTime(this);
            updateTrackingPositions(true);
            return true;
        }

        return false;
    }

    protected void forceSendEmptyChunks() {
        int chunkPositionX = this.getFloorX() >> 4;
        int chunkPositionZ = this.getFloorZ() >> 4;
        for (int x = -chunkRadius; x < chunkRadius; x++) {
            for (int z = -chunkRadius; z < chunkRadius; z++) {
                LevelChunkPacket chunk = new LevelChunkPacket();
                chunk.chunkX = chunkPositionX + x;
                chunk.chunkZ = chunkPositionZ + z;
                chunk.data = EmptyArrays.EMPTY_BYTES;
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

            for (Inventory window : new ArrayList<>(this.windows.keySet())) {
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
                pk.dimension = spawn.getLevel().getDimension();
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
            updateTrackingPositions(true);
        }
    }

    /**
     * Shows a new FormWindow to the player
     * You can find out FormWindow result by listening to PlayerFormRespondedEvent
     *
     * @param window to show
     * @return form id to use in {@link PlayerFormRespondedEvent}
     */
    public int showFormWindow(FormWindow window) {
        return showFormWindow(window, this.formWindowCount++);
    }

    /**
     * Shows a new FormWindow to the player
     * You can find out FormWindow result by listening to PlayerFormRespondedEvent
     *
     * @param window to show
     * @param id form id
     * @return form id to use in {@link PlayerFormRespondedEvent}
     */
    public int showFormWindow(FormWindow window, int id) {
        ModalFormRequestPacket packet = new ModalFormRequestPacket();
        packet.formId = id;
        packet.data = window.getJSONData();
        this.formWindows.put(packet.formId, window);

        this.dataPacket(packet);
        return id;
    }

    /**
     * Shows a new setting page in game settings
     * You can find out settings result by listening to PlayerFormRespondedEvent
     *
     * @param window to show on settings page
     * @return form id to use in {@link PlayerFormRespondedEvent}
     */
    public int addServerSettings(FormWindow window) {
        int id = this.formWindowCount++;

        this.serverSettings.put(id, window);
        return id;
    }

    /**
     * Creates and sends a BossBar to the player
     *
     * @param text   The BossBar message
     * @param length The BossBar percentage
     * @return bossBarId  The BossBar ID, you should store it if you want to remove or update the BossBar later
     */
    @Deprecated
    public long createBossBar(String text, int length) {
        DummyBossBar bossBar = new DummyBossBar.Builder(this).text(text).length(length).build();
        return this.createBossBar(bossBar);
    }

    /**
     * Creates and sends a BossBar to the player
     *
     * @param dummyBossBar DummyBossBar Object (Instantiate it by the Class Builder)
     * @return bossBarId  The BossBar ID, you should store it if you want to remove or update the BossBar later
     * @see DummyBossBar.Builder
     */
    public long createBossBar(DummyBossBar dummyBossBar) {
        this.dummyBossBars.put(dummyBossBar.getBossBarId(), dummyBossBar);
        dummyBossBar.create();
        return dummyBossBar.getBossBarId();
    }

    /**
     * Get a DummyBossBar object
     *
     * @param bossBarId The BossBar ID
     * @return DummyBossBar object
     * @see DummyBossBar#setText(String) Set BossBar text
     * @see DummyBossBar#setLength(float) Set BossBar length
     * @see DummyBossBar#setColor(BlockColor) Set BossBar color
     */
    public DummyBossBar getDummyBossBar(long bossBarId) {
        return this.dummyBossBars.getOrDefault(bossBarId, null);
    }

    /**
     * Get all DummyBossBar objects
     *
     * @return DummyBossBars Map
     */
    public Map<Long, DummyBossBar> getDummyBossBars() {
        return dummyBossBars;
    }

    /**
     * Updates a BossBar
     *
     * @param text      The new BossBar message
     * @param length    The new BossBar length
     * @param bossBarId The BossBar ID
     */
    @Deprecated
    public void updateBossBar(String text, int length, long bossBarId) {
        if (this.dummyBossBars.containsKey(bossBarId)) {
            DummyBossBar bossBar = this.dummyBossBars.get(bossBarId);
            bossBar.setText(text);
            bossBar.setLength(length);
        }
    }

    /**
     * Removes a BossBar
     *
     * @param bossBarId The BossBar ID
     */
    public void removeBossBar(long bossBarId) {
        if (this.dummyBossBars.containsKey(bossBarId)) {
            this.dummyBossBars.get(bossBarId).destroy();
            this.dummyBossBars.remove(bossBarId);
        }
    }

    public int getWindowId(Inventory inventory) {
        if (this.windows.containsKey(inventory)) {
            return this.windows.get(inventory);
        }

        return -1;
    }

    public Inventory getWindowById(int id) {
        return this.windowIndex.get(id);
    }

    public int addWindow(Inventory inventory) {
        return this.addWindow(inventory, null);
    }

    public int addWindow(Inventory inventory, Integer forceId) {
        return addWindow(inventory, forceId, false);
    }

    public int addWindow(Inventory inventory, Integer forceId, boolean isPermanent) {
        return addWindow(inventory, forceId, isPermanent, false);
    }

    public int addWindow(Inventory inventory, Integer forceId, boolean isPermanent, boolean alwaysOpen) {
        if (this.windows.containsKey(inventory)) {
            return this.windows.get(inventory);
        }
        int cnt;
        if (forceId == null) {
            this.windowCnt = cnt = Math.max(4, ++this.windowCnt % 99);
        } else {
            cnt = forceId;
        }
        this.windows.forcePut(inventory, cnt);

        if (isPermanent) {
            this.permanentWindows.add(cnt);
        }

        if (this.spawned && inventory.open(this)) {
            if (!isPermanent) {
                updateTrackingPositions(true);
            }
            return cnt;
        } else if (!alwaysOpen) {
            this.removeWindow(inventory);

            return -1;
        } else {
            inventory.getViewers().add(this);
        }
        
        if (!isPermanent) {
            updateTrackingPositions(true);
        }

        return cnt;
    }

    public Optional<Inventory> getTopWindow() {
        for (Entry<Inventory, Integer> entry : this.windows.entrySet()) {
            if (!this.permanentWindows.contains(entry.getValue())) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    public void removeWindow(Inventory inventory) {
        this.removeWindow(inventory, false);
    }

    @Since("1.3.2.0-PN")
    protected void removeWindow(Inventory inventory, boolean isResponse) {
        inventory.close(this);
        if (isResponse && !this.permanentWindows.contains(this.getWindowId(inventory))) {
            this.windows.remove(inventory);
            updateTrackingPositions(true);
        }
    }

    public void sendAllInventories() {
        for (Inventory inv : this.windows.keySet()) {
            inv.sendContents(this);

            if (inv instanceof PlayerInventory) {
                ((PlayerInventory) inv).sendArmorContents(this);
            }
        }
    }

    protected void addDefaultWindows() {
        this.addWindow(this.getInventory(), ContainerIds.INVENTORY, true, true);

        this.playerUIInventory = new PlayerUIInventory(this);
        this.addWindow(this.playerUIInventory, ContainerIds.UI, true);
        this.addWindow(this.offhandInventory, ContainerIds.OFFHAND, true, true);

        this.craftingGrid = this.playerUIInventory.getCraftingGrid();
        this.addWindow(this.craftingGrid, ContainerIds.NONE, true);

        //TODO: more windows
    }

    public PlayerUIInventory getUIInventory() {
        return playerUIInventory;
    }

    public PlayerCursorInventory getCursorInventory() {
        return this.playerUIInventory.getCursorInventory();
    }

    public CraftingGrid getCraftingGrid() {
        return this.craftingGrid;
    }

    public void setCraftingGrid(CraftingGrid grid) {
        this.craftingGrid = grid;
        this.addWindow(grid, ContainerIds.NONE);
    }

    public void resetCraftingGridType() {
        if (this.craftingGrid != null) {
            Item[] drops = this.inventory.addItem(this.craftingGrid.getContents().values().toArray(Item.EMPTY_ARRAY));

            if (drops.length > 0) {
                for (Item drop : drops) {
                    this.dropItem(drop);
                }
            }

            drops = this.inventory.addItem(this.getCursorInventory().getItem(0));
            if (drops.length > 0) {
                for (Item drop : drops) {
                    this.dropItem(drop);
                }
            }

            this.playerUIInventory.clearAll();

            if (this.craftingGrid instanceof BigCraftingGrid) {
                this.craftingGrid = this.playerUIInventory.getCraftingGrid();
                this.addWindow(this.craftingGrid, ContainerIds.NONE);
//
//                ContainerClosePacket pk = new ContainerClosePacket(); //be sure, big crafting is really closed
//                pk.windowId = ContainerIds.NONE;
//                this.dataPacket(pk);
            }

            this.craftingType = CRAFTING_SMALL;
        }
    }

    public void removeAllWindows() {
        removeAllWindows(false);
    }

    public void removeAllWindows(boolean permanent) {
        for (Entry<Integer, Inventory> entry : new ArrayList<>(this.windowIndex.entrySet())) {
            if (!permanent && this.permanentWindows.contains(entry.getKey())) {
                continue;
            }
            this.removeWindow(entry.getValue());
        }
    }

    @Since("1.3.2.0-PN")
    public int getClosingWindowId() {
        return this.closingWindowId;
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
    public int getLoaderId() {
        return this.loaderId;
    }

    @Override
    public boolean isLoaderActive() {
        return this.isConnected();
    }


    public static BatchPacket getChunkCacheFromData(int chunkX, int chunkZ, int subChunkCount, byte[] payload) {
        LevelChunkPacket pk = new LevelChunkPacket();
        pk.chunkX = chunkX;
        pk.chunkZ = chunkZ;
        pk.subChunkCount = subChunkCount;
        pk.data = payload;
        pk.encode();

        BatchPacket batch = new BatchPacket();
        byte[][] batchPayload = new byte[2][];
        byte[] buf = pk.getBuffer();
        batchPayload[0] = Binary.writeUnsignedVarInt(buf.length);
        batchPayload[1] = buf;
        byte[] data = Binary.appendBytes(batchPayload);
        try {
            batch.payload = Network.deflateRaw(data, Server.getInstance().networkCompressionLevel);
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

    private void setDimension(int dimension) {
        ChangeDimensionPacket pk = new ChangeDimensionPacket();
        pk.dimension = dimension;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        this.dataPacket(pk);
    }

    @Override
    public boolean switchLevel(Level level) {
        Level oldLevel = this.level;
        if (super.switchLevel(level)) {
            SetSpawnPositionPacket spawnPosition = new SetSpawnPositionPacket();
            spawnPosition.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
            Position spawn = level.getSpawnLocation();
            spawnPosition.x = spawn.getFloorX();
            spawnPosition.y = spawn.getFloorY();
            spawnPosition.z = spawn.getFloorZ();
            spawnPosition.dimension = spawn.getLevel().getDimension();
            this.dataPacket(spawnPosition);

            // Remove old chunks
            for (long index : new ArrayList<>(this.usedChunks.keySet())) {
                int chunkX = Level.getHashX(index);
                int chunkZ = Level.getHashZ(index);
                this.unloadChunk(chunkX, chunkZ, oldLevel);
            }
            this.usedChunks.clear();

            SetTimePacket setTime = new SetTimePacket();
            setTime.time = level.getTime();
            this.dataPacket(setTime);

            GameRulesChangedPacket gameRulesChanged = new GameRulesChangedPacket();
            gameRulesChanged.gameRules = level.getGameRules();
            this.dataPacket(gameRulesChanged);

            if (oldLevel.getDimension() != level.getDimension()) {
                this.setDimension(level.getDimension());
            }
            updateTrackingPositions(true);
            return true;
        }

        return false;
    }

    public void setCheckMovement(boolean checkMovement) {
        this.checkMovement = checkMovement;
    }

    /**
     * @since 1.2.1.0-PN
     */
    public boolean isCheckingMovement() {
        return this.checkMovement;
    }

    public synchronized void setLocale(Locale locale) {
        this.locale.set(locale);
    }

    public synchronized Locale getLocale() {
        return this.locale.get();
    }

    @Override
    public void setSprinting(boolean value) {
        if (isSprinting() != value) {
            super.setSprinting(value);
            this.setMovementSpeed(value ? getMovementSpeed() * 1.3f : getMovementSpeed() / 1.3f);
        }
    }

    public void transfer(InetSocketAddress address) {
        String hostName = address.getAddress().getHostAddress();
        int port = address.getPort();
        TransferPacket pk = new TransferPacket();
        pk.address = hostName;
        pk.port = port;
        this.dataPacket(pk);
    }

    public LoginChainData getLoginChainData() {
        return this.loginChainData;
    }

    public boolean pickupEntity(Entity entity, boolean near) {
        if (!this.spawned || !this.isAlive() || !this.isOnline() || this.isSpectator() || entity.isClosed()) {
            return false;
        }

        if (near) {
            Inventory inventory = this.inventory;
            if (entity instanceof EntityArrow && ((EntityArrow) entity).hadCollision) {
                ItemArrow item = new ItemArrow();
                if (this.isSurvival()) {
                    // Should only collect to the offhand slot if the item matches what is already there
                    if (this.offhandInventory.getItem(0).getId() == item.getId() && this.offhandInventory.canAddItem(item)) {
                        inventory = this.offhandInventory;
                    } else if (!inventory.canAddItem(item)) {
                        return false;
                    }
                }

                InventoryPickupArrowEvent ev = new InventoryPickupArrowEvent(inventory, (EntityArrow) entity);

                int pickupMode = ((EntityArrow) entity).getPickupMode();
                if (pickupMode == EntityArrow.PICKUP_NONE || pickupMode == EntityArrow.PICKUP_CREATIVE && !this.isCreative()) {
                    ev.setCancelled();
                }

                this.server.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return false;
                }

                TakeItemEntityPacket pk = new TakeItemEntityPacket();
                pk.entityId = this.getId();
                pk.target = entity.getId();
                Server.broadcastPacket(entity.getViewers().values(), pk);
                this.dataPacket(pk);

                if (!this.isCreative()) {
                    inventory.addItem(item.clone());
                }
                entity.close();
                return true;
            } else if (entity instanceof EntityThrownTrident) {
                // Check Trident is returning to shooter
                if (!((EntityThrownTrident) entity).hadCollision) {
                    if (entity.isNoClip()) {
                        if (!((EntityProjectile) entity).shootingEntity.equals(this)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
                
                if (!((EntityThrownTrident) entity).isPlayer()) {
                    return false;
                }
                
                Item item = ((EntityThrownTrident) entity).getItem();
                if (this.isSurvival() && !inventory.canAddItem(item)) {
                    return false;
                }

                InventoryPickupTridentEvent ev = new InventoryPickupTridentEvent(this.inventory, (EntityThrownTrident) entity);
                this.server.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return false;
                }

                TakeItemEntityPacket pk = new TakeItemEntityPacket();
                pk.entityId = this.getId();
                pk.target = entity.getId();
                Server.broadcastPacket(entity.getViewers().values(), pk);
                this.dataPacket(pk);

                if (!((EntityThrownTrident) entity).isCreative()) {
                    if (inventory.getItem(((EntityThrownTrident) entity).getFavoredSlot()).getId() == Item.AIR) {
                        inventory.setItem(((EntityThrownTrident) entity).getFavoredSlot(), item.clone());
                    } else {
                        inventory.addItem(item.clone());
                    }
                }
                entity.close();
                return true;
            } else if (entity instanceof EntityItem) {
                if (((EntityItem) entity).getPickupDelay() <= 0) {
                    Item item = ((EntityItem) entity).getItem();

                    if (item != null) {
                        if (this.isSurvival() && !inventory.canAddItem(item)) {
                            return false;
                        }

                        InventoryPickupItemEvent ev;
                        this.server.getPluginManager().callEvent(ev = new InventoryPickupItemEvent(inventory, (EntityItem) entity));
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

                        TakeItemEntityPacket pk = new TakeItemEntityPacket();
                        pk.entityId = this.getId();
                        pk.target = entity.getId();
                        Server.broadcastPacket(entity.getViewers().values(), pk);
                        this.dataPacket(pk);

                        entity.close();
                        inventory.addItem(item.clone());
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
                entity.kill();
                this.getLevel().addLevelEvent(LevelEventPacket.EVENT_SOUND_EXPERIENCE_ORB, 0, this);
                pickedXPOrb = tick;

                //Mending
                ArrayList<Integer> itemsWithMending = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    if (inventory.getArmorItem(i).getEnchantment((short)Enchantment.ID_MENDING) != null) {
                        itemsWithMending.add(inventory.getSize() + i);
                    }
                }
                if (inventory.getItemInHand().getEnchantment((short)Enchantment.ID_MENDING) != null) {
                    itemsWithMending.add(inventory.getHeldItemIndex());
                }
                if (itemsWithMending.size() > 0) {
                    Random rand = new Random();
                    Integer itemToRepair = itemsWithMending.get(rand.nextInt(itemsWithMending.size()));
                    Item toRepair = inventory.getItem(itemToRepair);
                    if (toRepair instanceof ItemTool || toRepair instanceof ItemArmor) {
                        if (toRepair.getDamage() > 0) {
                            int dmg = toRepair.getDamage() - 2;
                            if (dmg < 0)
                                dmg = 0;
                            toRepair.setDamage(dmg);
                            inventory.setItem(itemToRepair, toRepair);
                            return true;
                        }
                    }
                }

                this.addExperience(exp, true);
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

    public boolean isBreakingBlock() {
        return this.breakingBlock != null;
    }

    /**
     * Show a window of a XBOX account's profile
     * @param xuid XUID
     */
    public void showXboxProfile(String xuid) {
        ShowProfilePacket pk = new ShowProfilePacket();
        pk.xuid = xuid;
        this.dataPacket(pk);
    }

    public void startFishing(Item fishingRod) {
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", x))
                        .add(new DoubleTag("", y + this.getEyeHeight()))
                        .add(new DoubleTag("", z)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", -Math.sin(yaw / 180 + Math.PI) * Math.cos(pitch / 180 * Math.PI)))
                        .add(new DoubleTag("", -Math.sin(pitch / 180 * Math.PI)))
                        .add(new DoubleTag("", Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI))))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (float) yaw))
                        .add(new FloatTag("", (float) pitch)));
        double f = 1;
        EntityFishingHook fishingHook = new EntityFishingHook(chunk, nbt, this);
        fishingHook.setMotion(new Vector3(-Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f, -Math.sin(Math.toRadians(pitch)) * f * f,
                Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f));
        ProjectileLaunchEvent ev = new ProjectileLaunchEvent(fishingHook);
        this.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            fishingHook.kill();
        } else {
            fishingHook.spawnToAll();
            this.fishing = fishingHook;
            fishingHook.rod = fishingRod;
        }
    }

    public void stopFishing(boolean click) {
        if (click) {
            fishing.reelLine();
        } else if (this.fishing != null) {
            this.fishing.kill();
            this.fishing.close();
        }

        this.fishing = null;
    }

    @Override
    public boolean doesTriggerPressurePlate() {
        return this.gamemode != SPECTATOR;
    }

    private void updateBlockingFlag() {
        boolean shouldBlock = getNoShieldTicks() == 0
                && (this.isSneaking() || getRiding() != null)
                && (this.getInventory().getItemInHand().getId() == ItemID.SHIELD || this.getOffhandInventory().getItem(0).getId() == ItemID.SHIELD);
        
        if (isBlocking() != shouldBlock) {
            this.setBlocking(shouldBlock);
        }
    }

    @Override
    protected void onBlock(Entity entity, boolean animate) {
        super.onBlock(entity, animate);
        if (animate) {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_BLOCKED_USING_DAMAGED_SHIELD, true);
            this.getServer().getScheduler().scheduleTask(null, ()-> {
                if (this.isOnline()) {
                    this.setDataFlag(DATA_FLAGS, DATA_FLAG_BLOCKED_USING_DAMAGED_SHIELD, false);
                }
            });
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getNoShieldTicks() {
        return noShieldTicks;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setNoShieldTicks(int noShieldTicks) {
        this.noShieldTicks = noShieldTicks;
    }

    @Override
    public String toString() {
        return "Player(name='" + getName() +
                "', location=" + super.toString() +
                ')';
    }

    /**
     * Adds the items to the main player inventory and drops on the floor any excess. 
     * @param items The items to give to the player.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void giveItem(Item... items) {
        for(Item failed: getInventory().addItem(items)) {
            getLevel().dropItem(this, failed);
        }
    }

    @Since("1.3.2.0-PN")
    public int getTimeSinceRest() {
        return timeSinceRest;
    }

    @Since("1.3.2.0-PN")
    public void setTimeSinceRest(int timeSinceRest) {
        this.timeSinceRest = timeSinceRest;
    }

    // TODO: Support Translation Parameters
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void sendPopupJukebox(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_JUKEBOX_POPUP;
        pk.message = message;
        this.dataPacket(pk);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void sendSystem(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_SYSTEM;
        pk.message = message;
        this.dataPacket(pk);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void sendWhisper(String message) {
        this.sendWhisper("", message);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void sendWhisper(String source, String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_WHISPER;
        pk.source = source;
        pk.message = message;
        this.dataPacket(pk);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void sendAnnouncement(String message) {
        this.sendAnnouncement("", message);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void sendAnnouncement(String source, String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_ANNOUNCEMENT;
        pk.source = source;
        pk.message = message;
        this.dataPacket(pk);
    }
}
