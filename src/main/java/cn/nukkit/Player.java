package cn.nukkit;

import cn.nukkit.AdventureSettings.Type;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.blockentity.BlockEntityLectern;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.entity.*;
import cn.nukkit.entity.custom.EntityManager;
import cn.nukkit.entity.data.*;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.event.block.WaterFrostEvent;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityDamageEvent.DamageModifier;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.inventory.InventoryPickupArrowEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.inventory.InventoryPickupTridentEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.event.player.PlayerAsyncPreLoginEvent.LoginResult;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.inventory.*;
import cn.nukkit.inventory.transaction.*;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.data.ReleaseItemData;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.inventory.transaction.data.UseItemOnEntityData;
import cn.nukkit.item.*;
import cn.nukkit.item.custom.CustomItemManager;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.*;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.particle.ItemBreakParticle;
import cn.nukkit.level.particle.PunchBlockParticle;
import cn.nukkit.math.*;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.CompressionProvider;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.encryption.PrepareEncryptionTask;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.*;
import cn.nukkit.network.session.NetworkPlayerSession;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;
import cn.nukkit.resourcepacks.ResourcePack;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.*;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.util.internal.PlatformDependent;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * The Player class
 *
 * @author MagicDroidX &amp; Box
 * Nukkit Project
 */
@Log4j2
public class Player extends EntityHuman implements CommandSender, InventoryHolder, ChunkLoader, IPlayer {

    public static final int SURVIVAL = 0;
    public static final int CREATIVE = 1;
    public static final int ADVENTURE = 2;
    public static final int SPECTATOR = 3;

    public static final int CRAFTING_SMALL = 0;
    public static final int CRAFTING_BIG = 1;
    public static final int ANVIL_WINDOW_ID = 2;
    public static final int ENCHANT_WINDOW_ID = 3;
    public static final int BEACON_WINDOW_ID = 4;
    public static final int LOOM_WINDOW_ID = 5;
    public static final int SMITHING_WINDOW_ID = 6;
    public static final int GRINDSTONE_WINDOW_ID = 7;

    public static final float DEFAULT_SPEED = 0.1f;
    public static final float MAXIMUM_SPEED = 6f; // TODO: Decrease when block collisions are fixed
    public static final float DEFAULT_FLY_SPEED = 0.05f;
    public static final float DEFAULT_VERTICAL_FLY_SPEED = 1f;

    protected static final int RESOURCE_PACK_CHUNK_SIZE = 8192; // 8KB

    protected final SourceInterface interfaz;
    protected final NetworkPlayerSession networkSession;

    @Deprecated
    public long creationTime;
    public boolean playedBefore;
    public boolean spawned;
    public boolean loggedIn;
    private boolean loginVerified;
    private boolean loginPacketReceived;
    protected boolean networkSettingsRequested;
    public int gamemode;
    protected long randomClientId;
    private String unverifiedUsername = "";

    protected final BiMap<Inventory, Integer> windows = HashBiMap.create();
    protected final BiMap<Integer, Inventory> windowIndex = windows.inverse();
    protected final Set<Integer> permanentWindows = new IntOpenHashSet();
    private boolean inventoryOpen;
    protected int windowCnt = 10;
    protected int closingWindowId = Integer.MIN_VALUE;

    public final HashSet<String> achievements = new HashSet<>();

    public int craftingType = CRAFTING_SMALL;

    protected PlayerUIInventory playerUIInventory;
    protected CraftingGrid craftingGrid;
    protected CraftingTransaction craftingTransaction;
    protected EnchantTransaction enchantTransaction;
    protected RepairItemTransaction repairItemTransaction;
    protected LoomTransaction loomTransaction;
    protected SmithingTransaction smithingTransaction;
    protected GrindstoneTransaction grindstoneTransaction;

    public Vector3 speed;
    protected Vector3 forceMovement;
    protected Vector3 teleportPosition;
    protected Vector3 newPosition;
    protected Vector3 sleeping;
    private BlockVector3 lastRightClickPos;
    private final Queue<Vector3> clientMovements = PlatformDependent.newMpscQueue(4);

    protected boolean connected = true;
    protected final InetSocketAddress socketAddress;
    protected boolean removeFormat = true;

    protected String username;
    protected String iusername;
    protected String displayName;

    private boolean hasSpawnChunks;
    private final int loaderId;
    private int chunksSent;
    protected int nextChunkOrderRun = 1;
    protected int chunkRadius;
    protected int viewDistance;
    public final Map<Long, Boolean> usedChunks = new Long2ObjectOpenHashMap<>();
    protected final LongLinkedOpenHashSet loadQueue = new LongLinkedOpenHashSet();

    protected final Map<UUID, Player> hiddenPlayers = new HashMap<>();

    protected Position spawnPosition;

    protected int inAirTicks;
    protected int startAirTicks = 10;

    protected AdventureSettings adventureSettings;
    protected Color locatorBarColor;

    private PermissibleBase perm;

    /**
     * Option not to update shield blocking status.
     */
    @Getter
    @Setter
    private boolean canTickShield = true;
    /**
     * Player's client-side walk speed. Remember to call getAdventureSettings().update() if changed.
     */
    @Getter
    @Setter
    private float walkSpeed = DEFAULT_SPEED;
    /**
     * Player's client-side fly speed. Remember to call getAdventureSettings().update() if changed.
     */
    @Getter
    @Setter
    private float flySpeed = DEFAULT_FLY_SPEED;
    /**
     * Player's client-side vertical fly speed. Remember to call getAdventureSettings().update() if changed.
     */
    @Getter
    @Setter
    private float verticalFlySpeed = DEFAULT_VERTICAL_FLY_SPEED;

    private int exp;
    private int expLevel;

    protected PlayerFood foodData;

    private Entity killer;

    private final AtomicReference<Locale> locale = new AtomicReference<>(null);

    private int hash;

    private String buttonText = "";

    protected boolean enableClientCommand = true;

    private BlockEnderChest viewingEnderChest;

    private LoginChainData loginChainData;

    public int pickedXPOrb;
    private boolean canPickupXP = true;

    protected int formWindowCount;
    protected Map<Integer, FormWindow> formWindows = new Int2ObjectOpenHashMap<>();
    protected Map<Integer, FormWindow> serverSettings = new Int2ObjectOpenHashMap<>();

    protected Map<Long, DummyBossBar> dummyBossBars = new Long2ObjectLinkedOpenHashMap<>();

    private AsyncTask preLoginEventTask;
    protected boolean shouldLogin;

    protected int startAction = -1;
    private int lastEmote;
    protected int lastEnderPearl = 20;
    protected int lastChorusFruitTeleport = 20;
    protected int lastFireworkBoost = 20;
    public long lastSkinChange = -1;
    private double lastRightClickTime;
    public long lastBreak = -1; // When last block break was started
    private BlockVector3 lastBreakPosition = new BlockVector3();
    public Block breakingBlock; // Block player is breaking currently
    private BlockFace breakingBlockFace; // Block face player is breaking currently
    private PlayerBlockActionData lastBlockAction;
    public EntityFishingHook fishing;
    @Getter
    private boolean formOpen;
    private boolean flySneaking;
    public boolean locallyInitialized;
    private boolean foodEnabled = true;
    protected boolean checkMovement = true;
    private int timeSinceRest;
    private boolean inSoulSand;
    private boolean dimensionChangeInProgress;
    private boolean awaitingDimensionAck;
    private boolean awaitingEncryptionHandshake;
    private int riderJumpTick;
    private int riptideTicks;
    private int blockingDelay;
    private int fireworkBoostTicks;
    private int fireworkBoostLevel;

    @Setter
    private boolean needSendData;
    private boolean needSendAdventureSettings;
    private boolean needSendFoodLevel;
    @Setter
    private boolean needSendInventory;
    private boolean needSendHeldItem;
    private boolean needSendRotation;
    private boolean dimensionFix560;

    /**
     * Save last crossbow load tick (used to prevent loading a crossbow launching it immediately afterward)
     */
    private int crossbowLoadTick;
    /**
     * Packets that can be received before the player has logged in
     */
    private static final ByteOpenHashSet PRE_LOGIN_PACKETS = new ByteOpenHashSet(new byte[]{ProtocolInfo.BATCH_PACKET, ProtocolInfo.LOGIN_PACKET, ProtocolInfo.REQUEST_NETWORK_SETTINGS_PACKET, ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET, ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET, ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET, ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET, ProtocolInfo.CLIENT_CACHE_STATUS_PACKET, ProtocolInfo.PACKET_VIOLATION_WARNING_PACKET, ProtocolInfo.CLIENT_TO_SERVER_HANDSHAKE_PACKET});
    /**
     * Default kick message for flying
     */
    private static final String MSG_FLYING_NOT_ENABLED = "Flying is not enabled on this server";

    /**
     * Get action start tick
     * @return action start tick, -1 = no action in progress
     */
    public int getStartActionTick() {
        return startAction;
    }

    /**
     * Set action start tick to current tick
     */
    public void startAction() {
        this.startAction = this.server.getTick();
    }

    /**
     * Reset action start tick
     */
    public void stopAction() {
        this.startAction = -1;
    }

    /**
     * Get last tick an ender pearl was used
     * @return last ender pearl used tick
     */
    public int getLastEnderPearlThrowingTick() {
        return lastEnderPearl;
    }

    /**
     * Set last ender pearl throw tick to current tick
     */
    public void onThrowEnderPearl() {
        this.lastEnderPearl = this.server.getTick();
    }

    /**
     * Get last chorus fruit teleport tick
     * @return last chorus fruit teleport tick
     */
    public int getLastChorusFruitTeleport() {
        return lastChorusFruitTeleport;
    }

    /**
     * Set last chorus fruit teleport tick to current tick
     */
    public void onChorusFruitTeleport() {
        this.lastChorusFruitTeleport = this.server.getTick();
    }

    /**
     * Get last tick firework boost for elytra was used
     * @return last firework boost tick
     */
    public int getLastFireworkBoostTick() {
        return lastFireworkBoost;
    }

    /**
     * Set last firework boost tick to current tick
     */
    public void onFireworkBoost(int boostLevel) {
        this.lastFireworkBoost = this.server.getTick();
        this.fireworkBoostLevel = boostLevel;
        this.fireworkBoostTicks = boostLevel == 3 ? 44 : boostLevel == 2 ? 29 : 23;
    }

    /**
     * Set last spin attack tick to current tick
     */
    public void onSpinAttack(int riptideLevel) {
        this.riptideTicks = 50 + (riptideLevel << 5);
    }

    /**
     * Get ender chest the player is viewing
     * @return the ender chest player is viewing or null if player is not viewing an ender chest
     */
    public BlockEnderChest getViewingEnderChest() {
        return viewingEnderChest;
    }

    /**
     * Add player to ender chest viewers
     */
    public void setViewingEnderChest(BlockEnderChest chest) {
        if (chest == null && this.viewingEnderChest != null) {
            this.viewingEnderChest.getViewers().remove(this);
        } else if (chest != null) {
            chest.getViewers().add(this);
        }
        this.viewingEnderChest = chest;
    }

    /**
     * Get player quit message
     * @return quit message
     */
    public TranslationContainer getLeaveMessage() {
        return new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.left", this.displayName);
    }

    @Deprecated
    public String getClientSecret() {
        return null;
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
        return this.server.getNameBans().isBanned(this.username);
    }

    @Override
    public void setBanned(boolean value) {
        if (value) {
            this.server.getNameBans().addBan(this.username, null, null, null);
            this.kick(PlayerKickEvent.Reason.NAME_BANNED, "You are banned!");
        } else {
            this.server.getNameBans().remove(this.username);
        }
    }

    @Override
    public boolean isWhitelisted() {
        return this.server.isWhitelisted(this.iusername);
    }

    @Override
    public void setWhitelisted(boolean value) {
        if (value) {
            this.server.addWhitelist(this.iusername);
        } else {
            this.server.removeWhitelist(this.iusername);
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

    /**
     * Get current adventure settings
     * @return adventure settings
     */
    public AdventureSettings getAdventureSettings() {
        return adventureSettings;
    }

    /**
     * Set and send adventure settings
     * @param adventureSettings new adventure settings
     */
    public void setAdventureSettings(AdventureSettings adventureSettings) {
        this.adventureSettings = adventureSettings.clone(this);
        this.adventureSettings.update();
    }

    /**
     * Reset in air ticks
     */
    public void resetInAirTicks() {
        if (this.inAirTicks != 0) {
            this.startAirTicks = 10;
        }
        this.inAirTicks = 0;
    }

    /**
     * Set allow flight adventure setting
     * @param value allow flight enabled
     */
    @Deprecated
    public void setAllowFlight(boolean value) {
        this.adventureSettings.set(Type.ALLOW_FLIGHT, value);
        this.adventureSettings.update();
    }

    /**
     * Check wether allow flight adventure setting is enabled
     * @return allow flight enabled
     */
    @Deprecated
    public boolean getAllowFlight() {
        return this.adventureSettings.get(Type.ALLOW_FLIGHT);
    }

    /**
     * Set can modify world adventure setting(s)
     * @param value can modify world
     */
    public void setAllowModifyWorld(boolean value) {
        this.adventureSettings.set(Type.WORLD_IMMUTABLE, !value);
        this.adventureSettings.set(Type.MINE, value);
        this.adventureSettings.set(Type.BUILD, value);
        this.adventureSettings.update();
    }

    /**
     * Set can interact adventure setting(s)
     * @param value can interact
     */
    public void setAllowInteract(boolean value) {
        this.setAllowInteract(value, value);
    }

    /**
     * Set can interact adventure setting(s)
     * @param value can interact
     * @param containers can open containers
     */
    public void setAllowInteract(boolean value, boolean containers) {
        this.adventureSettings.set(Type.WORLD_IMMUTABLE, !value);
        this.adventureSettings.set(Type.DOORS_AND_SWITCHED, value);
        this.adventureSettings.set(Type.OPEN_CONTAINERS, containers);
        this.adventureSettings.update();
    }

    /**
     * Set auto jump adventure setting (adventureSettings.set(Type.AUTO_JUMP) + adventureSettings.update())
     * @param value auto jump enabled
     */
    @Deprecated
    public void setAutoJump(boolean value) {
        this.adventureSettings.set(Type.AUTO_JUMP, value);
        this.adventureSettings.update();
    }

    /**
     * Check whether auto jump adventure setting is enabled (adventureSettings.get(Type.AUTO_JUMP))
     * @return auto jump enabled
     */
    @Deprecated
    public boolean hasAutoJump() {
        return this.adventureSettings.get(Type.AUTO_JUMP);
    }

    @Override
    public void spawnTo(Player player) {
        if (this.spawned && player.spawned && this.isAlive() && player.isAlive() && player.getLevel() == this.level && player.canSee(this) && !this.isSpectator()) {
            super.spawnTo(player);
        }
    }

    /**
     * Check whether player can use text formatting
     * @return player can use text formatting
     */
    public boolean getRemoveFormat() {
        return removeFormat;
    }

    /**
     * Make player unable to use text formatting (color codes etc.)
     */
    public void setRemoveFormat() {
        this.setRemoveFormat(true);
    }

    /**
     * Set whether player can use text formatting (color codes etc.)
     * @param remove remove formatting from received texts
     */
    public void setRemoveFormat(boolean remove) {
        this.removeFormat = remove;
    }

    /**
     * Check whether player can see another player (not hidden)
     * @param player target player
     * @return player can see the target player
     */
    public boolean canSee(Player player) {
        return !this.hiddenPlayers.containsKey(player.getUniqueId());
    }

    /**
     * Hide this player from target player
     * @param player target player
     */
    public void hidePlayer(Player player) {
        if (this == player) {
            return;
        }
        this.hiddenPlayers.put(player.getUniqueId(), player);
        player.despawnFrom(this);
    }

    /**
     * Allow target player to see this player
     * @param player target player
     */
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

    /**
     * Check whether player can pick up xp orbs
     * @return can pick up xp orbs
     */
    public boolean canPickupXP() {
        return this.canPickupXP;
    }

    /**
     * Set whether player can pick up xp orbs
     * @param canPickupXP can pick up xp orbs
     */
    public void setCanPickupXP(boolean canPickupXP) {
        this.canPickupXP = canPickupXP;
    }

    @Override
    public void resetFallDistance() {
        super.resetFallDistance();
        this.resetInAirTicks();
    }

    @Override
    public boolean isOnline() {
        return this.connected && this.loggedIn;
    }

    @Override
    public boolean isOp() {
        return this.server.isOp(this.username);
    }

    @Override
    public void setOp(boolean value) {
        if (value == this.isOp()) {
            return;
        }

        if (value) {
            this.server.addOp(this.username);
        } else {
            this.server.removeOp(this.username);
        }

        this.recalculatePermissions();
        this.adventureSettings.update();
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

        if (this.enableClientCommand && spawned) this.sendCommandData();
    }

    /**
     * Are commands enabled for this player on the client side
     * @return commands enabled
     */
    public boolean isEnableClientCommand() {
        return this.enableClientCommand;
    }

    /**
     * Set commands enabled client side. This does not necessarily prevent commands from being used.
     * @param enable can use commands
     */
    public void setEnableClientCommand(boolean enable) {
        this.enableClientCommand = enable;
        SetCommandsEnabledPacket pk = new SetCommandsEnabledPacket();
        pk.enabled = enable;
        this.dataPacket(pk);
        if (enable) this.sendCommandData();
    }

    /**
     * Send updated command data (AvailableCommandsPacket)
     */
    public void sendCommandData() {
        AvailableCommandsPacket pk = new AvailableCommandsPacket();
        Map<String, CommandDataVersions> data = new HashMap<>();

        for (Command command : this.server.getCommandMap().getCommands().values()) {
            if (command.isRegistered()) {
                if ("help".equals(command.getName())) {
                    continue; // The client will add this
                }
                CommandDataVersions commandData = command.generateCustomCommandData(this);
                if (commandData != null) { // No permission
                    data.put(command.getName(), commandData);
                }
            }
        }

        if (!data.isEmpty()) {
            pk.commands = data;
            this.dataPacket(pk);
        }
    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return this.perm.getEffectivePermissions();
    }

    public Player(SourceInterface interfaz, Long clientID, InetSocketAddress socketAddress) {
        super(null, new CompoundTag());
        this.interfaz = interfaz;
        this.networkSession = interfaz.getSession(socketAddress);
        this.perm = new PermissibleBase(this);
        this.server = Server.getInstance();
        this.socketAddress = socketAddress;
        this.loaderId = Level.generateChunkLoaderId(this);
        this.gamemode = this.server.getGamemode();
        this.setLevel(this.server.getDefaultLevel());
        this.viewDistance = this.server.getViewDistance();
        this.chunkRadius = this.viewDistance;
        this.boundingBox = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.addDefaultWindows();
    }

    public boolean isPlayer() {
        return true;
    }

    /**
     * Remove achievement from player if the player has it
     * @param achievementId achievement id
     */
    public void removeAchievement(String achievementId) {
        achievements.remove(achievementId);
    }

    /**
     * Check whether player has an achievement
     * @param achievementId achievement id
     * @return has achievement
     */
    public boolean hasAchievement(String achievementId) {
        return achievements.contains(achievementId);
    }

    /**
     * Check whether player is still connected
     * @return connected
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Get player's display name. Default value is player's username.
     * @return display name
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Set player's display name
     * @param displayName display name
     */
    public void setDisplayName(String displayName) {
        if (displayName == null) {
            displayName = "";
            server.getLogger().debug("Warning: setDisplayName: argument is null", new Throwable(""));
        }
        this.displayName = displayName;
        updatePlayerListData(true);
    }

    @Override
    public void setSkin(Skin skin) {
        super.setSkin(skin);
        updatePlayerListData(true);
    }

    public Color getLocatorBarColor() {
        if (this.locatorBarColor == null) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            this.locatorBarColor = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
        }
        return this.locatorBarColor;
    }

    public void setLocatorBarColor(Color locatorBarColor) {
        this.locatorBarColor = locatorBarColor;
        updatePlayerListData(true);
    }

    void updatePlayerListData(boolean onlyWhenSpawned) {
        if (this.spawned || !onlyWhenSpawned) {
            this.server.updatePlayerListData(
                    new PlayerListPacket.Entry(this.getUniqueId(), this.getId(), this.displayName, this.getSkin(), this.loginChainData.getXUID(), this.getLocatorBarColor()),
                    this.server.playerList.values().toArray(new Player[0]));
        }
    }


    /**
     * Get player's host address
     * @return host address
     */
    public String getAddress() {
        return this.socketAddress.getAddress().getHostAddress();
    }

    /**
     * Get the port of player's connection
     * @return port
     */
    public int getPort() {
        return this.socketAddress.getPort();
    }

    /**
     * Get player's socket address
     * @return socket address
     */
    public InetSocketAddress getSocketAddress() {
        return this.socketAddress;
    }

    /**
     * Get most recent position of received movements
     * @return next position or current position if no next position has been received
     */
    public Position getNextPosition() {
        return this.newPosition != null ? new Position(this.newPosition.x, this.newPosition.y, this.newPosition.z, this.level) : this.getPosition();
    }

    public AxisAlignedBB getNextPositionBB() {
        if (this.newPosition == null) {
            return this.boundingBox;
        }
        Vector3 diff = this.newPosition.subtract(this);
        return this.boundingBox.getOffsetBoundingBox(diff.x, diff.y, diff.z);
    }

    /**
     * Check whether player is sleeping
     * @return is sleeping
     */
    public boolean isSleeping() {
        return this.sleeping != null;
    }

    /**
     * Get in air ticks
     * @return in air ticks
     */
    public int getInAirTicks() {
        return this.inAirTicks;
    }

    /**
     * Returns whether the player is currently using an item (right-click and hold).
     *
     * @return whether the player is currently using an item
     */
    public boolean isUsingItem() {
        return this.startAction > -1 && this.getDataFlag(DATA_FLAGS, DATA_FLAG_ACTION);
    }

    /**
     * Set using item flag
     * @param value is using item
     */
    public void setUsingItem(boolean value) {
        this.startAction = value ? this.server.getTick() : -1;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ACTION, value);
    }

    /**
     * Get interaction button text
     * @return button text
     */
    public String getButtonText() {
        return this.buttonText;
    }

    /**
     * Set interaction button text
     * @param text button text
     */
    public void setButtonText(String text) {
        if (text == null) {
            text = "";
            server.getLogger().debug("Warning: setButtonText: argument is null", new Throwable(""));
        }
        if (!text.equals(buttonText)) {
            this.buttonText = text;
            this.setDataPropertyAndSendOnlyToSelf(new StringEntityData(Entity.DATA_INTERACTIVE_TAG, this.buttonText));
        }
    }

    /**
     * Unload a chunk on current level
     * @param x chunk x
     * @param z chunk z
     */
    public void unloadChunk(int x, int z) {
        this.unloadChunk(x, z, null);
    }

    /**
     * Unload a chunk on given level
     * @param x chunk x
     * @param z chunk z
     */
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

    /**
     * Unload all loaded chunks
     * @param online player is online; send entity despawn packets
     */
    private void unloadChunks(boolean online) {
        for (long index : this.usedChunks.keySet()) {
            int chunkX = Level.getHashX(index);
            int chunkZ = Level.getHashZ(index);
            this.level.unregisterChunkLoader(this, chunkX, chunkZ);

            for (Entity entity : level.getChunkEntities(chunkX, chunkZ).values()) {
                if (entity != this) {
                    if (online) {
                        entity.despawnFrom(this);
                    } else {
                        entity.hasSpawned.remove(loaderId);
                    }
                }
            }
        }

        this.usedChunks.clear();
        this.loadQueue.clear();
    }

    /**
     * Get player's spawn position
     * @return player's spawn position or server's default (safe) spawn position if not set
     */
    public Position getSpawn() {
        if (this.spawnPosition != null && this.spawnPosition.getLevel() != null && this.spawnPosition.getLevel().getProvider() != null) {
            return this.spawnPosition.add(0.5, 0, 0.5);
        } else {
            return this.server.getDefaultLevel().getSafeSpawn();
        }
    }

    /**
     * Get player's spawn position
     * @return player's spawn position or null if not set
     */
    @Nullable
    public Position getSpawnPosition() {
        return this.spawnPosition;
    }

    /**
     * Send a chunk packet
     * @param x chunk x
     * @param z xhunk z
     * @param packet chunk packet
     */
    public void sendChunk(int x, int z, DataPacket packet) {
        if (!this.connected) {
            return;
        }

        this.usedChunks.put(Level.chunkHash(x, z), true);

        this.dataPacket(packet);

        this.chunksSent++;

        if (this.spawned) {
            for (Entity entity : this.level.getChunkEntities(x, z).values()) {
                if (this != entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this);
                }
            }
        }

        // Hack: Fix dimension screen issues
        if (this.dimensionFix560) {
            this.dimensionFix560 = false;
            PlayerActionPacket pap = new PlayerActionPacket();
            pap.action = PlayerActionPacket.ACTION_DIMENSION_CHANGE_ACK;
            pap.entityId = this.getId();
            this.dataPacket(pap);
        }
    }

    /**
     * Send a chunk packet
     * @param x chunk x
     * @param z xhunk z
     * @param subChunkCount sub chunk count
     * @param payload packet payload
     */
    public void sendChunk(int x, int z, int subChunkCount, byte[] payload, int dimension) {
        if (this.connected) {
            LevelChunkPacket pk = new LevelChunkPacket();
            pk.chunkX = x;
            pk.chunkZ = z;
            pk.dimension = dimension;
            pk.subChunkCount = subChunkCount;
            pk.data = payload;
            this.sendChunk(x, z, pk);
        }
    }

    protected void sendNextChunk() {
        if (!this.connected) {
            return;
        }

        if (!loadQueue.isEmpty()) {
            int count = 0;
            LongIterator iter = loadQueue.longIterator();
            while (iter.hasNext()) {
                if (count >= server.chunksPerTick) {
                    break;
                }

                long index = iter.nextLong();
                int chunkX = Level.getHashX(index);
                int chunkZ = Level.getHashZ(index);

                ++count;

                try {
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
                } catch (Exception ex) {
                    server.getLogger().logException(ex);
                    return;
                }

                PlayerChunkRequestEvent ev = new PlayerChunkRequestEvent(this, chunkX, chunkZ);
                this.server.getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    this.level.requestChunk(chunkX, chunkZ, this);
                }
            }
        }

        if (!this.hasSpawnChunks && this.chunksSent >= server.spawnThreshold) {
            this.hasSpawnChunks = true;

            this.sendPlayStatus(PlayStatusPacket.PLAYER_SPAWN);
        }
    }

    protected void doFirstSpawn() {
        if (this.spawned) {
            return;
        }

        this.noDamageTicks = 60;
        this.setAirTicks(400);

        if (this.hasPermission(Server.BROADCAST_CHANNEL_USERS)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_USERS, this);
        }

        if (this.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)) {
            this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this);
        }

        boolean dead = this.getHealth() < 1;
        Position spawn = dead ? this.getSpawn() : this;
        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(this, spawn.level.getSafeSpawn(spawn), true);
        this.server.getPluginManager().callEvent(respawnEvent);

        this.spawned = true;

        if (dead) {
            if (this.server.isHardcore()) {
                this.setBanned(true);
                return;
            }

            this.teleport(respawnEvent.getRespawnPosition(), null);

            // TODO: should probably respawn() here
            this.setHealth(this.getMaxHealth());
            this.foodData.setLevel(20, 20);
            this.sendData(this);
        } else {
            this.setPosition(respawnEvent.getRespawnPosition());
            this.sendPosition(respawnEvent.getRespawnPosition(), yaw, pitch, MovePlayerPacket.MODE_TELEPORT);
            this.forceMovement = this.teleportPosition = respawnEvent.getRespawnPosition();

            this.getLevel().sendTime(this);
            this.getLevel().sendWeather(this);
        }

        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(this,
                new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.joined", new String[]{this.displayName})
        );

        this.server.getPluginManager().callEvent(playerJoinEvent);

        if (!playerJoinEvent.getJoinMessage().toString().trim().isEmpty()) {
            this.server.broadcastMessage(playerJoinEvent.getJoinMessage());
        }

        for (long index : this.usedChunks.keySet()) {
            int chunkX = Level.getHashX(index);
            int chunkZ = Level.getHashZ(index);
            for (Entity entity : this.level.getChunkEntities(chunkX, chunkZ).values()) {
                if (this != entity && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this);
                }
            }
        }

        // Prevent PlayerTeleportEvent during player spawn
        //this.teleport(pos, null);

        if (!this.isSpectator()) {
            this.spawnToAll();
        }

        if (!this.locallyInitialized) {
            // Not really needed anymore but it's here for plugin compatibility
            this.server.getPluginManager().callEvent(new PlayerLocallyInitializedEvent(this));
            this.locallyInitialized = true;
        }
    }

    protected boolean orderChunks() {
        if (!this.connected) {
            return false;
        }

        this.nextChunkOrderRun = 20;

        loadQueue.clear();
        Long2ObjectOpenHashMap<Boolean> lastChunk = new Long2ObjectOpenHashMap<>(this.usedChunks);

        int centerX = this.getChunkX();
        int centerZ = this.getChunkZ();

        int radius = spawned ? this.chunkRadius : server.spawnThresholdRadius;
        int radiusSqr = radius * radius;

        long index;
        for (int x = 0; x <= radius; x++) {
            int xx = x * x;
            for (int z = 0; z <= x; z++) {
                int distanceSqr = xx + z * z;
                if (distanceSqr > radiusSqr) continue;

                /* Top right quadrant */
                if (this.usedChunks.get(index = Level.chunkHash(centerX + x, centerZ + z)) != Boolean.TRUE) {
                    this.loadQueue.add(index);
                }
                lastChunk.remove(index);
                /* Top left quadrant */
                if (this.usedChunks.get(index = Level.chunkHash(centerX - x - 1, centerZ + z)) != Boolean.TRUE) {
                    this.loadQueue.add(index);
                }
                lastChunk.remove(index);
                /* Bottom right quadrant */
                if (this.usedChunks.get(index = Level.chunkHash(centerX + x, centerZ - z - 1)) != Boolean.TRUE) {
                    this.loadQueue.add(index);
                }
                lastChunk.remove(index);
                /* Bottom left quadrant */
                if (this.usedChunks.get(index = Level.chunkHash(centerX - x - 1, centerZ - z - 1)) != Boolean.TRUE) {
                    this.loadQueue.add(index);
                }
                lastChunk.remove(index);
                if (x != z) {
                    /* Top right quadrant mirror */
                    if (this.usedChunks.get(index = Level.chunkHash(centerX + z, centerZ + x)) != Boolean.TRUE) {
                        this.loadQueue.add(index);
                    }
                    lastChunk.remove(index);
                    /* Top left quadrant mirror */
                    if (this.usedChunks.get(index = Level.chunkHash(centerX - z - 1, centerZ + x)) != Boolean.TRUE) {
                        this.loadQueue.add(index);
                    }
                    lastChunk.remove(index);
                    /* Bottom right quadrant mirror */
                    if (this.usedChunks.get(index = Level.chunkHash(centerX + z, centerZ - x - 1)) != Boolean.TRUE) {
                        this.loadQueue.add(index);
                    }
                    lastChunk.remove(index);
                    /* Bottom left quadrant mirror */
                    if (this.usedChunks.get(index = Level.chunkHash(centerX - z - 1, centerZ - x - 1)) != Boolean.TRUE) {
                        this.loadQueue.add(index);
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
            packet.radius = this.chunkRadius << 4;
            this.dataPacket(packet);
        }

        return true;
    }

    /**
     * This method no longer has special function. Calls dataPacket().
     * @param packet data packet
     * @return return value of dataPacket()
     */
    @Deprecated
    public boolean batchDataPacket(DataPacket packet) {
        return this.dataPacket(packet);
    }

    /**
     * Send a data packet
     * @param packet data packet
     * @return sent
     */
    public boolean dataPacket(DataPacket packet) {
        if (!this.connected) {
            return false;
        }

        DataPacket dataPacket = packet.clone();

        DataPacketSendEvent ev = new DataPacketSendEvent(this, dataPacket);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }

        if (Nukkit.DEBUG > 2 && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Outbound {}: {}", this.getName(), dataPacket);
        }

        if (dataPacket instanceof BatchPacket) {
            this.networkSession.sendPacket(dataPacket);
        } else {
            // Make sure packets always go to BatchingHelper so they are not reordered
            this.server.batchPackets(new Player[]{this}, new DataPacket[]{dataPacket});
        }
        return true;
    }

    @Deprecated
    public int dataPacket(DataPacket packet, boolean needACK) {
        return this.dataPacket(packet) ? 1 : 0;
    }

    @Deprecated
    public boolean directDataPacket(DataPacket packet) {
        return this.dataPacket(packet);
    }

    @Deprecated
    public int directDataPacket(DataPacket packet, boolean needACK) {
        return this.directDataPacket(packet) ? 1 : 0;
    }

    public void forceDataPacket(DataPacket packet, Runnable callback) {
        DataPacketSendEvent ev = new DataPacketSendEvent(this, packet);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }

        if (Nukkit.DEBUG > 2 && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Outbound {}: {}", this.getName(), packet);
        }

        this.networkSession.sendImmediatePacket(packet, (callback == null ? () -> {} : callback));
    }

    /**
     * Get network latency
     * @return network latency in milliseconds
     */
    public int getPing() {
        return this.interfaz.getNetworkLatency(this);
    }

    /**
     * Attempt to sleep at position
     * @param pos position
     * @return successfully set sleeping
     */
    public boolean sleepOn(Vector3 pos) {
        if (!this.isOnline()) {
            return false;
        }

        Entity[] e = this.level.getNearbyEntities(this.boundingBox.grow(2, 1, 2), this);
        for (Entity p : e) {
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

        if (!pos.equals(this.getSpawnPosition())) {
            this.setSpawn(pos);
            this.sendMessage("§7%tile.bed.respawnSet", true);
        }

        this.level.sleepTicks = 60;
        this.timeSinceRest = 0;

        return true;
    }

    /**
     * Set player's spawn position
     * @param pos spawn position
     */
    public void setSpawn(Vector3 pos) {
        Level level;
        if (!(pos instanceof Position)) {
            level = this.level;
        } else {
            level = ((Position) pos).getLevel();
        }
        this.spawnPosition = pos instanceof Block ? ((Block) pos).clone().setLevel(level) : new Position(pos.x, pos.y, pos.z, level);
        this.sendSpawnPos((int) pos.x, (int) pos.y, (int) pos.z, level.getDimension());
    }

    /**
     * Internal: Send player spawn position
     */
    private void sendSpawnPos(int x, int y, int z, int dimension) {
        SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
        pk.spawnType = SetSpawnPositionPacket.TYPE_PLAYER_SPAWN;
        pk.x = x;
        pk.y = y;
        pk.z = z;
        pk.dimension = dimension;
        this.dataPacket(pk);
    }

    /**
     * Stop sleeping. Does nothing if the player is not sleeping.
     */
    public void stopSleep() {
        if (this.sleeping != null) {
            this.server.getPluginManager().callEvent(new PlayerBedLeaveEvent(this, this.level.getBlock(this.sleeping)));

            this.sleeping = null;
            this.removeDataProperty(DATA_PLAYER_BED_POSITION);
            this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, false);

            this.level.sleepTicks = 0;

            AnimatePacket pk = new AnimatePacket();
            pk.eid = this.id;
            pk.action = AnimatePacket.Action.WAKE_UP;
            this.dataPacket(pk);
        }
    }

    /**
     * Get sleeping position
     * @return current sleeping position or null if not sleeping
     */
    public Vector3 getSleepingPos() {
        return this.sleeping;
    }

    /**
     * Attempts to award an achievement
     * @param achievementId achievement id
     * @return new achievement awarded
     */
    public boolean awardAchievement(String achievementId) {
        if (!server.achievementsEnabled) {
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

    /**
     * Get player's gamemode:
     * 0 = survival
     * 1 = creative
     * 2 = adventure
     * 3 = spectator
     *
     * @return gamemode (number)
     */
    public int getGamemode() {
        return gamemode;
    }

    /**
     * Returns a client-friendly gamemode of the specified real gamemode
     * This function takes care of handling gamemodes known to MCPE (as of 1.1.0.3, that includes Survival, Creative and Adventure)
     */
    private static int getClientFriendlyGamemode(int gamemode) {
        gamemode &= 0x03;
        if (gamemode == Player.SPECTATOR) {
            return Player.CREATIVE;
        }
        return gamemode;
    }

    /**
     * Set player's gamemode
     * @param gamemode new gamemode
     * @return gamemode changed
     */
    public boolean setGamemode(int gamemode) {
        return this.setGamemode(gamemode, false, null);
    }

    /**
     * Set player's gamemode
     * @param gamemode new gamemode
     * @param clientSide whether change was client initiated
     * @return gamemode changed
     */
    public boolean setGamemode(int gamemode, boolean clientSide) {
        return this.setGamemode(gamemode, clientSide, null);
    }

    /**
     * Set player's gamemode
     * @param gamemode new gamemode
     * @param clientSide whether change was client initiated
     * @param newSettings updated adventure settings for the new gamemode; calculated automatically if null
     * @return gamemode changed
     */
    public boolean setGamemode(int gamemode, boolean clientSide, AdventureSettings newSettings) {
        if (gamemode < 0 || gamemode > 3 || this.gamemode == gamemode) {
            return false;
        }

        if (newSettings == null) {
            newSettings = this.adventureSettings.clone(this);
            newSettings.set(Type.WORLD_IMMUTABLE, (gamemode & 0x02) > 0);
            newSettings.set(Type.MINE, (gamemode & 0x02) <= 0);
            newSettings.set(Type.BUILD, (gamemode & 0x02) <= 0);
            newSettings.set(Type.NO_PVM, gamemode == SPECTATOR);
            newSettings.set(Type.ALLOW_FLIGHT, (gamemode & 0x01) > 0);
            newSettings.set(Type.NO_CLIP, gamemode == SPECTATOR);
            newSettings.set(Type.FLYING, (gamemode & 0x1) == 1);
        }

        PlayerGameModeChangeEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerGameModeChangeEvent(this, gamemode, newSettings));

        if (ev.isCancelled()) {
            return false;
        }

        this.gamemode = gamemode;

        if (this.isSpectator()) {
            this.keepMovement = true;
            this.onGround = false;
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
            this.teleport(this, null);
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_SILENT, true, false);
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_HAS_COLLISION, false); // Sends both
        } else {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_SILENT, false, false);
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_HAS_COLLISION, true); // Sends both
        }

        this.resetFallDistance();

        this.inventory.sendContents(this);
        this.inventory.sendHeldItem(this.hasSpawned.values());
        this.offhandInventory.sendContents(this);
        this.offhandInventory.sendContents(this.getViewers().values());

        this.inventory.sendCreativeContents();
        return true;
    }

    /**
     * Send adventure settings (adventureSettings.update())
     */
    @Deprecated
    public void sendSettings() {
        this.adventureSettings.update();
    }

    /**
     * Check player game mode
     * @return whether player is in survival mode
     */
    public boolean isSurvival() {
        return this.gamemode == SURVIVAL;
    }

    /**
     * Check player game mode
     * @return whether player is in creative mode
     */
    public boolean isCreative() {
        return this.gamemode == CREATIVE;
    }

    /**
     * Check player game mode
     * @return whether player is in spectator mode
     */
    public boolean isSpectator() {
        return this.gamemode == SPECTATOR;
    }

    /**
     * Check player game mode
     * @return whether player is in adventure mode
     */
    public boolean isAdventure() {
        return this.gamemode == ADVENTURE;
    }

    @Override
    public Item[] getDrops() {
        if (!this.isCreative() && !this.isSpectator()) {
            if (this.inventory != null) {
                List<Item> drops = new ArrayList<>(this.inventory.getContents().values());
                drops.addAll(this.offhandInventory.getContents().values());
                //drops.addAll(this.playerUIInventory.getContents().values()); // handled in resetCraftingGridType
                return drops.toArray(new Item[0]);
            }
            return new Item[0];
        }

        return new Item[0];
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
                        Block block = this.level.getBlock(chunk, x, y, z, true);

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
        if (this.isSpectator()) {
            return;
        }

        boolean netherPortal = false;
        boolean endPortal = false;
        Block powderSnow = null;

        for (Block block : this.getCollisionBlocks()) {
            if (block.getLevel().getProvider() == null) {
                break;
            }

            if (block.getId() == Block.NETHER_PORTAL) {
                netherPortal = true;
            } else if (block.getId() == Block.END_PORTAL) {
                endPortal = true;
            } else if (block.getId() == Block.POWDER_SNOW) {
                powderSnow = block;
            }

            block.onEntityCollide(this);
        }

        if (powderSnow != null) {
            this.inPowderSnowTicks++;

            if (this.inPowderSnowTicks <= 140) {
                this.setDataPropertyAndSendOnlyToSelf(new FloatEntityData(DATA_FREEZING_EFFECT_STRENGTH, this.inPowderSnowTicks / 140f));
            }

            if (this.inPowderSnowTicks >= 140 && server.getTick() % 40 == 0 && level.getGameRules().getBoolean(GameRule.FREEZE_DAMAGE)) {
                this.attack(new EntityDamageByBlockEvent(powderSnow, this, EntityDamageEvent.DamageCause.CONTACT, 1f));
            }
        } else if (this.inPowderSnowTicks != 0) {
            this.inPowderSnowTicks = 0;

            this.setDataPropertyAndSendOnlyToSelf(new FloatEntityData(DATA_FREEZING_EFFECT_STRENGTH, 0f));
        }

        if (endPortal) {
            this.inEndPortalTicks++;
        } else {
            this.inEndPortalTicks = 0;
        }

        if (this.inEndPortalTicks == 1 && EnumLevel.THE_END.getLevel() != null) {
            EntityPortalEnterEvent ev = new EntityPortalEnterEvent(this, EntityPortalEnterEvent.PortalType.END);
            this.getServer().getPluginManager().callEvent(ev);

            if (!ev.isCancelled()) {
                int oldDimension = this.getLevel().getDimension();
                if (oldDimension == Level.DIMENSION_THE_END) {
                    Position spawn;
                    if ((spawn = this.getSpawn()).getLevel().getDimension() == Level.DIMENSION_OVERWORLD) {
                        if (this.teleport(spawn, TeleportCause.END_PORTAL)) {
                            this.awardAchievement("theEnd2");
                        }
                    } else {
                        if (this.teleport(this.getServer().getDefaultLevel().getSafeSpawn(), TeleportCause.END_PORTAL)) {
                            this.awardAchievement("theEnd2");
                        }
                    }
                } else {
                    Level end = this.getServer().getLevelByName("the_end");
                    if (end != null) {
                        Position pos = new Position(100.5, 49, 0.5, end);

                        FullChunk chunk = end.getChunk(pos.getChunkX(), pos.getChunkZ(), false);
                        if (chunk == null || !(chunk.isGenerated() || chunk.isPopulated())) {
                            end.populateChunk(pos.getChunkX(), pos.getChunkZ(), true);

                            int x = pos.getFloorX();
                            int y = pos.getFloorY();
                            int z = pos.getFloorZ();
                            for (int xx = x - 2; xx < x + 3; xx++) {
                                for (int zz = z - 2; zz < z + 3; zz++)  {
                                    end.setBlockAt(xx, y - 1, zz, BlockID.OBSIDIAN);
                                    for (int yy = y; yy < y + 4; yy++) {
                                        end.setBlockAt(xx, yy, zz, BlockID.AIR);
                                    }
                                }
                            }
                        }

                        if (this.teleport(pos, TeleportCause.END_PORTAL) && oldDimension == Level.DIMENSION_OVERWORLD) {
                            this.awardAchievement("theEnd");
                        }
                    }
                }
            }
        }

        if (netherPortal) {
            this.inPortalTicks++;
        } else {
            this.inPortalTicks = 0;
            this.portalPos = null;
        }

        if (this.server.isNetherAllowed()) {
            if (this.inPortalTicks == (this.gamemode == CREATIVE ? 1 : 40) && this.portalPos == null) {
                Position portalPos = this.level.calculatePortalMirror(this);
                if (portalPos == null) {
                    return;
                }

                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++) {
                        int chunkX = (portalPos.getChunkX()) + x, chunkZ = (portalPos.getChunkZ()) + z;
                        FullChunk chunk = portalPos.level.getChunk(chunkX, chunkZ, false);
                        if (chunk == null || !(chunk.isGenerated() || chunk.isPopulated())) {
                            portalPos.level.populateChunk(chunkX, chunkZ, true);
                        }
                    }
                }
                this.portalPos = portalPos;
            }

            if (this.inPortalTicks == (this.gamemode == CREATIVE ? 1 : 80)) {
                EntityPortalEnterEvent ev = new EntityPortalEnterEvent(this, EntityPortalEnterEvent.PortalType.NETHER);

                if (this.portalPos == null) {
                    ev.setCancelled();
                }

                this.getServer().getPluginManager().callEvent(ev);

                if (ev.isCancelled()) {
                    this.portalPos = null;
                    return;
                }

                int oldDimension = this.getLevel().getDimension();
                Position foundPortal = BlockNetherPortal.findNearestPortal(this.portalPos);
                if (foundPortal == null) {
                    BlockNetherPortal.spawnPortal(this.portalPos);
                    if (this.teleport(this.portalPos.add(1.5, 1, 0.5), TeleportCause.NETHER_PORTAL) && oldDimension == Level.DIMENSION_OVERWORLD) {
                        this.awardAchievement("portal");
                    }
                } else {
                    if (this.teleport(BlockNetherPortal.getSafePortal(foundPortal), TeleportCause.NETHER_PORTAL) && oldDimension == Level.DIMENSION_OVERWORLD) {
                        this.awardAchievement("portal");
                    }
                }
                this.portalPos = null;
            }
        }
    }

    /**
     * Internal: Check nearby entities and try to pick them up
     */
    protected void checkNearEntities() {
        Entity[] e = this.level.getNearbyEntities(this.boundingBox.grow(1, 0.5, 1), this);
        for (Entity entity : e) {
            if (!entity.isAlive()) {
                continue;
            }

            // Update pickup delay
            if (entity.updateMode % 3 == 2) {
                entity.scheduleUpdate();
            }

            this.pickupEntity(entity, true);
        }
    }

    /**
     * Internal: Process player movement
     *
     * @param newPos client position
     */
    protected void handleMovement(Vector3 newPos) {
        if (!this.isAlive() || !this.spawned || this.teleportPosition != null || this.isSleeping()) {
            return;
        }

        double distanceSquared = newPos.distanceSquared(this);
        if (distanceSquared == 0) {
            if (this.lastYaw != this.yaw || this.lastPitch != this.pitch) {
                if (!this.firstMove) {
                    Location from = new Location(this.x, this.y, this.z, this.lastYaw, this.lastPitch, this.level);
                    Location to = this.getLocation();

                    PlayerMoveEvent moveEvent = new PlayerMoveEvent(this, from, to);
                    this.server.getPluginManager().callEvent(moveEvent);

                    if (moveEvent.isCancelled()) {
                        this.teleport(from, null);
                        return;
                    }

                    this.lastYaw = to.yaw;
                    this.lastPitch = to.pitch;

                    if (!to.equals(moveEvent.getTo())) { // If plugins modify the destination
                        this.teleport(moveEvent.getTo(), null);
                    } else {
                        this.needSendRotation = true;
                    }
                } else {
                    this.lastYaw = this.yaw;
                    this.lastPitch = this.pitch;
                    this.needSendRotation = true;
                    this.firstMove = false;
                }
            }

            if (this.speed == null) speed = new Vector3(0, 0, 0);
            else this.speed.setComponents(0, 0, 0);
            return;
        }

        int maxDist = 9;
        if (this.riptideTicks > 95 || newPos.y - this.y < 2 || this.isOnLadder()) { // TODO: Remove ladder/vines check when block collisions are fixed
            maxDist = 49;
        }

        if (distanceSquared > maxDist) {
            this.revertClientMotion(this);
            server.getLogger().debug(username + ": distanceSquared=" + distanceSquared +  " > maxDist=" + maxDist);
            return;
        }

        if (this.chunk == null || !this.chunk.isGenerated()) {
            BaseFullChunk chunk = this.level.getChunk(newPos.getChunkX(), newPos.getChunkZ(), false);
            if (chunk == null || !chunk.isGenerated()) {
                this.nextChunkOrderRun = 0;
                this.revertClientMotion(this);
                return;
            } else {
                if (this.chunk != null) {
                    this.chunk.removeEntity(this);
                }
                this.chunk = chunk;
            }
        }

        double dx = newPos.x - this.x;
        double dy = newPos.y - this.y;
        double dz = newPos.z - this.z;

        //the client likes to clip into blocks like stairs, but we do full server-side prediction of that without
        //help from the client's position changes, so we deduct the expected clip height from the moved distance.
        dy += this.ySize * (1 - STEP_CLIP_MULTIPLIER); // FIXME: ySize is always 0

        if (this.checkMovement && this.riptideTicks <= 0 && this.riding == null && !this.isGliding() && !this.getAllowFlight()) {
            double hSpeed = dx * dx + dz * dz;
            if (hSpeed > MAXIMUM_SPEED) {
                PlayerInvalidMoveEvent ev;
                this.getServer().getPluginManager().callEvent(ev = new PlayerInvalidMoveEvent(this, true));
                if (!ev.isCancelled()) {
                    server.getLogger().debug(username + ": hSpeed=" + hSpeed + " > MAXIMUM_SPEED=" + MAXIMUM_SPEED);
                    this.revertClientMotion(this);
                    return;
                }
            }
        }

        // Replacement for this.fastMove(dx, dy, dz) start
        if (this.isSpectator() || !this.level.hasCollision(this, this.boundingBox.getOffsetBoundingBox(dx, dy, dz).shrink(0.1, this.getStepHeight(), 0.1), false)) {
            this.x = newPos.x;
            this.y = newPos.y;
            this.z = newPos.z;

            this.boundingBox.setBounds(this.x - 0.3, this.y, this.z - 0.3, this.x + 0.3, this.y + this.getHeight(), this.z + 0.3);
        }

        this.checkChunks();

        if (!this.isSpectator() && (!this.onGround || dy != 0)) {
            AxisAlignedBB bb = this.boundingBox.clone();
            bb.setMinY(bb.getMinY() - 0.75);

            // Hack: fix fall damage from walls while falling
            if (Math.abs(dy) > 0.01) {
                bb.setMinX(bb.getMinX() + 0.1);
                bb.setMaxX(bb.getMaxX() - 0.1);
                bb.setMinZ(bb.getMinZ() + 0.1);
                bb.setMaxZ(bb.getMaxZ() - 0.1);
            }

            this.onGround = this.level.hasCollisionBlocks(this, bb);
        }

        this.isCollided = this.onGround;
        this.updateFallState(this.onGround);
        // Replacement for this.fastMove(dx, dy, dz) end

        Location from = new Location(this.lastX, this.lastY, this.lastZ, this.lastYaw, this.lastPitch, this.level);
        Location to = this.getLocation();

        if (!this.firstMove) {
            PlayerMoveEvent moveEvent = new PlayerMoveEvent(this, from, to);
            this.server.getPluginManager().callEvent(moveEvent);

            if (moveEvent.isCancelled()) {
                this.teleport(from, null);
                return;
            }

            this.lastX = to.x;
            this.lastY = to.y;
            this.lastZ = to.z;

            this.lastYaw = to.yaw;
            this.lastPitch = to.pitch;

            this.blocksAround = null;
            this.collisionBlocks = null;

            if (!to.equals(moveEvent.getTo())) { // If plugins modify the destination
                this.teleport(moveEvent.getTo(), null);
            } else {
                this.addMovement(this.x, this.y, this.z, this.yaw, this.pitch, this.yaw);
            }
        } else {
            this.lastX = to.x;
            this.lastY = to.y;
            this.lastZ = to.z;

            this.lastYaw = to.yaw;
            this.lastPitch = to.pitch;

            this.firstMove = false;
        }

        if (this.speed == null) speed = new Vector3(from.x - to.x, from.y - to.y, from.z - to.z);
        else this.speed.setComponents(from.x - to.x, from.y - to.y, from.z - to.z);

        if (this.riding == null && this.inventory != null) {
            if (this.isFoodEnabled() && this.getServer().getDifficulty() > 0) {
                if (distanceSquared >= 0.05) {
                    double jump = 0;
                    double swimming = this.isInsideOfWater() ? 0.01 * distanceSquared : 0;
                    double dd = distanceSquared;
                    if (swimming != 0) dd = 0;
                    if (this.isSprinting()) {
                        if (this.inAirTicks == 3 && swimming == 0) {
                            jump = 0.2;
                        }
                        this.foodData.updateFoodExpLevel(0.1 * dd + jump + swimming);
                    } else {
                        if (this.inAirTicks == 3 && swimming == 0) {
                            jump = 0.05;
                        }
                        this.foodData.updateFoodExpLevel(jump + swimming);
                    }
                }
            }

            Item boots = this.inventory.getBootsFast();

            Enchantment frostWalker = boots.getEnchantment(Enchantment.ID_FROST_WALKER);
            if (frostWalker != null && frostWalker.getLevel() > 0 && !this.isSpectator() && this.y > this.level.getMinBlockY() && this.y <= this.level.getMaxBlockY()) {
                int radius = 2 + frostWalker.getLevel();
                for (int coordX = this.getFloorX() - radius; coordX < this.getFloorX() + radius + 1; coordX++) {
                    for (int coordZ = this.getFloorZ() - radius; coordZ < this.getFloorZ() + radius + 1; coordZ++) {
                        Block block = level.getBlock(this.chunk, coordX, this.getFloorY() - 1, coordZ, true);
                        if ((block.getId() == Block.STILL_WATER || block.getId() == Block.WATER && block.getDamage() == 0) && block.up().getId() == Block.AIR) {
                            WaterFrostEvent waterFrostEvent = new WaterFrostEvent(block);
                            server.getPluginManager().callEvent(waterFrostEvent);
                            if (!waterFrostEvent.isCancelled()) {
                                level.setBlockAt((int) block.x, (int) block.y, (int) block.z, Block.ICE_FROSTED, 0);
                                level.scheduleUpdate(level.getBlock(this.chunk, (int) block.getX(), (int) block.getY(), (int) block.getZ(), true), ThreadLocalRandom.current().nextInt(20, 40));
                            }
                        }
                    }
                }
            }

            Enchantment soulSpeedEnchantment = boots.getEnchantment(Enchantment.ID_SOUL_SPEED);
            if (soulSpeedEnchantment != null && soulSpeedEnchantment.getLevel() > 0) {
                int down = this.getLevel().getBlockIdAt(chunk, getFloorX(), getFloorY() - 1, getFloorZ());
                if (this.inSoulSand && down != BlockID.SOUL_SAND) {
                    this.inSoulSand = false;
                    this.setMovementSpeed(DEFAULT_SPEED, true);
                } else if (!this.inSoulSand && down == BlockID.SOUL_SAND) {
                    this.inSoulSand = true;
                    float soulSpeed = (soulSpeedEnchantment.getLevel() * 0.105f) + 1.3f;
                    this.setMovementSpeed(DEFAULT_SPEED * soulSpeed, true);
                }
            }
        }

        this.forceMovement = null;
        if (distanceSquared != 0 && this.nextChunkOrderRun > 20) {
            this.nextChunkOrderRun = 20;
        }
        this.needSendRotation = false; // Sent with movement

        this.resetClientMovement();
    }

    @Override
    public void recalculateBoundingBox(boolean send) {
        double height = isSwimming() || isGliding() || isCrawling() ? 0.6 : isSneaking() ? 1.5 : 1.8;
        this.boundingBox.setBounds(
                this.x - 0.3,
                this.y + this.ySize,
                z - 0.3,
                x + 0.3,
                y + height + this.ySize,
                z + 0.3
        );

        if (send) {
            FloatEntityData bbH = new FloatEntityData(DATA_BOUNDING_BOX_HEIGHT, (float) height);
            FloatEntityData bbW = new FloatEntityData(DATA_BOUNDING_BOX_WIDTH, this.getWidth());
            this.dataProperties.put(bbH);
            this.dataProperties.put(bbW);
            sendData(this.hasSpawned.values().toArray(new Player[0]), new EntityMetadata().put(bbH).put(bbW));
        }
    }

    protected void resetClientMovement() {
        this.newPosition = null;
    }

    protected void revertClientMotion(Location originalPos) {
        this.lastX = originalPos.getX();
        this.lastY = originalPos.getY();
        this.lastZ = originalPos.getZ();
        this.lastYaw = originalPos.getYaw();
        this.lastPitch = originalPos.getPitch();
        Vector3 syncPos = originalPos.add(0, 0.00001, 0);
        this.needSendRotation = false;
        this.sendPosition(syncPos, originalPos.getYaw(), originalPos.getPitch(), MovePlayerPacket.MODE_RESET);
        this.forceMovement = syncPos;
        if (this.speed == null) {
            this.speed = new Vector3(0, 0, 0);
        } else {
            this.speed.setComponents(0, 0, 0);
        }
    }

    @Override
    public void addMovement(double x, double y, double z, double yaw, double pitch, double headYaw) {
        this.sendPositionToViewers(x, y, z, yaw, pitch, headYaw);
    }

    @Override
    public boolean setMotion(Vector3 motion) {
        if (super.setMotion(motion)) {
            if (this.chunk != null && this.spawned) {
                this.addMotion(this.motionX, this.motionY, this.motionZ); // Send to others
                SetEntityMotionPacket pk = new SetEntityMotionPacket();
                pk.eid = this.id;
                pk.motionX = (float) motion.x;
                pk.motionY = (float) motion.y;
                pk.motionZ = (float) motion.z;
                this.dataPacket(pk);
            }

            if (this.motionY > 0) {
                this.startAirTicks = (int) ((-(Math.log(this.getGravity() / (this.getGravity() + this.getDrag() * this.motionY))) / this.getDrag()) * 2 + 5);
            }

            return true;
        }

        return false;
    }

    /**
     * Set player's server side motion. Does not send updated motion to client.
     * @param motion new motion vector
     */
    public void setMotionLocally(Vector3 motion) {
        if (!this.justCreated) {
            EntityMotionEvent ev = new EntityMotionEvent(this, motion);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return;
            }
        }

        this.motionX = motion.x;
        this.motionY = motion.y;
        this.motionZ = motion.z;

        if (this.motionY > 0) {
            this.startAirTicks = (int) ((-(Math.log(this.getGravity() / (this.getGravity() + this.getDrag() * this.motionY))) / this.getDrag()) * 2 + 5);
        }
    }

    /**
     * Send all default attributes
     */
    public void sendAttributes() {
        int healthMax = this.getMaxHealth();
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.entityId = this.getId();
        pk.entries = new Attribute[]{
                Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(healthMax).setValue(health > 0 ? (health < healthMax ? health : healthMax) : 0),
                Attribute.getAttribute(Attribute.MAX_HUNGER).setValue(this.foodData.getLevel()).setDefaultValue(this.foodData.getMaxLevel()),
                Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue(this.getMovementSpeed()).setDefaultValue(this.getMovementSpeed()),
                Attribute.getAttribute(Attribute.EXPERIENCE_LEVEL).setValue(this.expLevel),
                Attribute.getAttribute(Attribute.EXPERIENCE).setValue(((float) this.exp) / calculateRequireExperience(this.expLevel))
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

        this.lastUpdate = currentTick;

        if (this.riptideTicks > 0) {
            this.riptideTicks -= tickDiff;
        }

        if (this.fishing != null && this.age % 10 == 0) {
            if (this.distanceSquared(fishing) > 1089) { // 33 blocks
                this.stopFishing(false);
            }
        }

        if (!this.isAlive() && this.spawned) {
            //++this.deadTicks;
            //if (this.deadTicks >= 10) {
            this.despawnFromAll(); // HACK: fix "dead" players
            //}
            return true;
        }

        if (this.spawned) {
            while (!this.clientMovements.isEmpty()) {
                this.handleMovement(this.clientMovements.poll());
            }

            if (this.needSendRotation) {
                this.addMovement(this.x, this.y, this.z, this.yaw, this.pitch, this.yaw);
                this.needSendRotation = false;
            }

            this.motionX = this.motionY = this.motionZ = 0; // HACK: fix player knockback being messed up

            if (!this.isSpectator() && this.isAlive()) {
                this.checkNearEntities();
            }

            this.entityBaseTick(tickDiff);

            if (this.getServer().getDifficulty() == 0 && this.level.getGameRules().getBoolean(GameRule.NATURAL_REGENERATION)) {
                if (this.getHealth() < this.getRealMaxHealth() && this.age % 20 == 0) {
                    this.heal(1);
                }

                if (this.foodData.getLevel() < 20 && this.age % 10 == 0) {
                    this.foodData.addFoodLevel(1, 0);
                }
            }

            if (this.isOnFire() && this.age % 10 == 0) {
                if (this.isCreative() && !this.isInsideOfFire()) {
                    this.extinguish();
                } else if (this.getLevel().isRaining() && this.canSeeSky()) {
                    this.extinguish();
                }
            }

            if (!this.isSpectator() && this.speed != null) {
                if (this.onGround) {

                    // 1.20.10 doesn't stop it automatically
                    if (this.isGliding()) {
                        this.setGliding(false);
                    }

                    this.resetFallDistance();
                } else {
                    if (this.checkMovement && this.riptideTicks < 1 && !this.isGliding() && !server.getAllowFlight() && this.inAirTicks > 20 && !this.getAllowFlight() && !this.isSleeping() && !this.isImmobile() && !this.isSwimming() && this.riding == null && !this.hasEffect(Effect.LEVITATION) && !this.hasEffect(Effect.SLOW_FALLING) && this.speed != null && !(this.speed.x == 0 && this.speed.y == 0 && this.speed.z == 0)) {
                        double expectedVelocity = (-this.getGravity()) / ((double) this.getDrag()) - ((-this.getGravity()) / ((double) this.getDrag())) * Math.exp(-((double) this.getDrag()) * ((double) (this.inAirTicks - this.startAirTicks)));
                        double diff = Math.abs(Math.abs(expectedVelocity) - Math.abs(this.speed.y));

                        if (diff > 1 && expectedVelocity < 0) {
                            if (this.inAirTicks < 200) {
                                PlayerInvalidMoveEvent ev = new PlayerInvalidMoveEvent(this, true);
                                this.getServer().getPluginManager().callEvent(ev);
                                if (!ev.isCancelled()) {
                                    this.startAirTicks = this.inAirTicks - 10;
                                    this.setMotion(new Vector3(0, expectedVelocity, 0));
                                }
                            } else if (this.kick(PlayerKickEvent.Reason.FLYING_DISABLED, MSG_FLYING_NOT_ENABLED, true)) {
                                return false;
                            }
                        }
                    }

                    if (this.y > highestPosition) {
                        this.highestPosition = this.y;
                    }

                    if (this.isSwimming() || this.isOnLadder() || (this.isGliding() && this.getPitch() <= 40 && Math.abs(this.speed.y) < 0.5)) {
                        this.resetFallDistance();
                    } else if (this.isGliding()) {
                        this.resetInAirTicks();
                    } else {
                        ++this.inAirTicks;
                    }
                }

                if (this.foodData != null) {
                    this.foodData.update(tickDiff);
                }
            }
        }

        if (this.age % 20 == 0) {
            if (this.isGliding()) {
                PlayerInventory inv = this.getInventory();
                if (inv != null) {
                    Item elytra = inv.getChestplate();
                    if (elytra == null || elytra.getId() != ItemID.ELYTRA) {
                        this.setGliding(false);
                    } else if ((this.gamemode & 0x01) == 0 && this.age % (20 * (elytra.getEnchantmentLevel(Enchantment.ID_DURABILITY) + 1)) == 0 && !elytra.isUnbreakable()) {
                        elytra.setDamage(elytra.getDamage() + 1);
                        if (elytra.getDamage() >= elytra.getMaxDurability()) {
                            this.setGliding(false);
                        }
                        inv.setChestplate(elytra);
                    }
                }
            }
        }

        if (this.fireworkBoostTicks > 0) {
            this.fireworkBoostTicks -= tickDiff;

            double multiplier = 1 + 0.25 * (this.fireworkBoostLevel < 1 ? 0.25 : this.fireworkBoostLevel);
            this.setMotion(new Vector3(
                    -Math.sin(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch)) * multiplier,
                    -Math.sin(Math.toRadians(this.pitch)) * multiplier,
                    Math.cos(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch)) * multiplier));
        }

        if (this.age % 5 == 0 && this.isBreakingBlock() && !this.isCreative()) {
            //this.level.addLevelSoundEvent(this.breakingBlock, LevelSoundEventPacket.SOUND_HIT, blockRuntimeId);
            this.level.addParticle(new PunchBlockParticle(this.breakingBlock, this.breakingBlock, this.breakingBlockFace));
        }

        this.checkTeleportPosition();

        if (this.spawned && !this.dummyBossBars.isEmpty() && currentTick % 100 == 0) {
            this.dummyBossBars.values().forEach(DummyBossBar::updateBossEntityPosition);
        }

        this.tickShield(tickDiff);

        if (!this.isSleeping()) {
            this.timeSinceRest += tickDiff;
        }

        return true;
    }

    /**
     * Update shield blocking status
     */
    private void tickShield(int tickDiff) {
        if (!this.canTickShield) {
            return;
        }
        if (this.blockingDelay > 0) {
            this.blockingDelay -= tickDiff;
        }
        boolean shieldInHand = this.getInventory().getItemInHandFast().getId() == ItemID.SHIELD;
        boolean shieldInOffhand = this.getOffhandInventory().getItemFast(0).getId() == ItemID.SHIELD;
        if (this.isBlocking()) {
            if (!this.isSneaking() || (!shieldInHand && !shieldInOffhand)) {
                this.setBlocking(false);
            }
        } else if (this.blockingDelay <= 0 && this.isSneaking() && (shieldInHand || shieldInOffhand)) {
            this.setBlocking(true);
        }
    }

    /**
     * Update interaction button text
     */
    public void checkInteractNearby() {
        int interactDistance = isCreative() ? 5 : 3;
        if (canInteract(this, interactDistance)) {
            EntityInteractable e = getEntityPlayerLookingAt(interactDistance);
            if (e != null) {
                String buttonText = e.getInteractButtonText(this);
                if (buttonText == null) {
                    buttonText = "";
                }
                setButtonText(buttonText);
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
                        entity = getEntityAtPosition(nearbyEntities, (int) block.getX(), (int) block.getY(), (int) block.getZ());
                        if (entity != null) {
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        return entity;
    }

    private static EntityInteractable getEntityAtPosition(Entity[] nearbyEntities, int x, int y, int z) {
        for (Entity nearestEntity : nearbyEntities) {
            if (nearestEntity.getFloorX() == x && nearestEntity.getFloorY() == y && nearestEntity.getFloorZ() == z
                    && nearestEntity instanceof EntityInteractable
                    && ((EntityInteractable) nearestEntity).canDoInteraction()) {
                return (EntityInteractable) nearestEntity;
            }
        }
        return null;
    }

    /**
     * Internal: Process chunk sending
     */
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

    /**
     * Check whether target is too far away to be interacted with
     * @param pos target position
     * @param maxDistance maximum distance
     * @return can interact
     */
    public boolean canInteract(Vector3 pos, double maxDistance) {
        return this.canInteract(pos, maxDistance, 6.0);
    }

    /**
     * Check whether target is too far away to be interacted with
     * @param pos target position
     * @param maxDistance maximum distance
     * @param maxDiff maximum diff
     * @return can interact
     */
    public boolean canInteract(Vector3 pos, double maxDistance, double maxDiff) {
        if (this.distanceSquared(pos) > maxDistance * maxDistance) {
            return false;
        }

        Vector2 dV = this.getDirectionPlane();
        return (dV.dot(new Vector2(pos.x, pos.z)) - dV.dot(new Vector2(this.x, this.z))) >= -maxDiff;
    }

    private boolean canInteractEntity(Vector3 pos, double maxDistanceSquared) {
        if (this.distanceSquared(pos) > maxDistanceSquared) {
            return false;
        }

        Vector2 dV = this.getDirectionPlane();
        return (dV.dot(new Vector2(pos.x, pos.z)) - dV.dot(new Vector2(this.x, this.z))) >= -0.87;
    }

    protected void processPreLogin() {
        this.loginVerified = true;
        final Player playerInstance = this;

        this.preLoginEventTask = new AsyncTask() {
            private PlayerAsyncPreLoginEvent event;

            @Override
            public void onRun() {
                this.event = new PlayerAsyncPreLoginEvent(username, uuid, loginChainData, skin, playerInstance.getAddress(), playerInstance.getPort());
                server.getPluginManager().callEvent(this.event);
            }

            @Override
            public void onCompletion(Server server) {
                if (!playerInstance.connected) {
                    return;
                }

                if (this.event.getLoginResult() == LoginResult.KICK) {
                    playerInstance.close(this.event.getKickMessage(), this.event.getKickMessage());
                } else if (playerInstance.shouldLogin) {
                    try {
                        playerInstance.setSkin(this.event.getSkin());
                        playerInstance.completeLoginSequence();
                        for (Consumer<Server> action : this.event.getScheduledActions()) {
                            action.accept(server);
                        }
                    } catch (Exception ex) {
                        server.getLogger().logException(ex);
                        playerInstance.close("", "Internal Server Error");
                    }
                }
            }
        };

        this.server.getScheduler().scheduleAsyncTask(null, this.preLoginEventTask);

        try {
            this.processLogin();
        } catch (Exception ex) {
            this.server.getLogger().logException(ex);
            this.close("", "Internal Server Error");
        }
    }

    protected void processLogin() {
        String lowerName = this.iusername;
        if (!this.server.isWhitelisted(lowerName)) {
            this.kick(PlayerKickEvent.Reason.NOT_WHITELISTED, "Server is white-listed");
            return;
        } else if (this.isBanned()) {
            String reason = this.server.getNameBans().getEntires().get(lowerName).getReason();
            this.kick(PlayerKickEvent.Reason.NAME_BANNED, "You are banned!" + (reason.isEmpty() ? "" : (" Reason: " + reason)));
            return;
        } else if (this.server.getIPBans().isBanned(this.getAddress())) {
            this.kick(PlayerKickEvent.Reason.IP_BANNED, "Your IP is banned!");
            return;
        }

        for (Player p : new ArrayList<>(this.server.playerList.values())) {
            if (p != this && p.username != null) {
                if (p.username.equalsIgnoreCase(this.username) || this.getUniqueId().equals(p.getUniqueId())) {
                    p.close("", "disconnectionScreen.loggedinOtherLocation");
                    break;
                }
            }
        }

        CompoundTag nbt;
        File legacyDataFile = new File(server.getDataPath() + "players/" + lowerName + ".dat");
        File dataFile = new File(server.getDataPath() + "players/" + this.uuid.toString() + ".dat");

        boolean dataFound = dataFile.exists();
        if (!dataFound && legacyDataFile.exists()) {
            nbt = this.server.getOfflinePlayerData(lowerName, false);
            if (!legacyDataFile.delete()) {
                this.server.getLogger().warning("Could not delete legacy player data for " + this.username);
            }
        } else {
            nbt = this.server.getOfflinePlayerData(this.uuid, !dataFound);
        }

        if (nbt == null) {
            this.close(this.getLeaveMessage(), "Invalid data");
            return;
        }

        if (loginChainData.isXboxAuthed() || !server.xboxAuth) {
            server.updateName(this.uuid, this.username);
        }

        this.playedBefore = (nbt.getLong("lastPlayed") - nbt.getLong("firstPlayed")) > 1;

        nbt.putString("NameTag", this.username);

        this.setExperience(nbt.getInt("EXP"), nbt.getInt("expLevel"));

        if (this.server.getForceGamemode()) {
            this.gamemode = this.server.getGamemode();
            nbt.putInt("playerGameType", this.gamemode);
        } else {
            this.gamemode = nbt.getInt("playerGameType") & 0x03;
        }

        this.adventureSettings = new AdventureSettings(this)
                .set(Type.WORLD_IMMUTABLE, isAdventure() || isSpectator())
                .set(Type.MINE, !isAdventure() && !isSpectator())
                .set(Type.BUILD, !isAdventure() && !isSpectator())
                .set(Type.NO_PVM, isSpectator())
                .set(Type.AUTO_JUMP, true)
                .set(Type.ALLOW_FLIGHT, isCreative() || isSpectator())
                .set(Type.NO_CLIP, isSpectator())
                .set(Type.FLYING, isSpectator());

        Level level;
        if ((level = this.server.getLevelByName(nbt.getString("Level"))) == null || nbt.getShort("Health") < 1) {
            this.setLevel(this.server.getDefaultLevel());
            nbt.putString("Level", this.level.getName());
            Vector3 sp = this.level.getProvider().getSpawn();
            nbt.getList("Pos", DoubleTag.class)
                    .add(new DoubleTag("0", sp.x))
                    .add(new DoubleTag("1", sp.y))
                    .add(new DoubleTag("2", sp.z));
        } else {
            this.setLevel(level);
        }

        if (nbt.contains("SpawnLevel")) {
            Level spawnLevel = server.getLevelByName(nbt.getString("SpawnLevel"));
            if (spawnLevel != null) {
                this.spawnPosition = new Position(
                        nbt.getInt("SpawnX"),
                        nbt.getInt("SpawnY"),
                        nbt.getInt("SpawnZ"),
                        spawnLevel
                );
            }
        }

        this.timeSinceRest = nbt.getInt("TimeSinceRest");

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

        ListTag<DoubleTag> posList = nbt.getList("Pos", DoubleTag.class);

        super.init(this.level.getChunk(NukkitMath.floorDouble(posList.get(0).data) >> 4, NukkitMath.floorDouble(posList.get(2).data) >> 4, true), nbt);

        if (!this.namedTag.contains("foodLevel")) {
            this.namedTag.putInt("foodLevel", 20);
        }

        if (!this.namedTag.contains("foodSaturationLevel")) {
            this.namedTag.putFloat("foodSaturationLevel", 20);
        }

        this.foodData = new PlayerFood(this, this.namedTag.getInt("foodLevel"), this.namedTag.getFloat("foodSaturationLevel"));

        if (this.isSpectator()) {
            this.keepMovement = true;
            this.onGround = false;
        }

        this.forceMovement = this.teleportPosition = this.getPosition();

        ResourcePacksInfoPacket infoPacket = new ResourcePacksInfoPacket();
        infoPacket.resourcePackEntries = this.server.getResourcePackManager().getResourceStack();
        infoPacket.mustAccept = this.server.getForceResources();
        this.dataPacket(infoPacket);
    }

    protected void completeLoginSequence() {
        if (this.loggedIn) {
            this.server.getLogger().warning("Tried to call completeLoginSequence but player is already logged in: " + this.username);
            return;
        }

        PlayerLoginEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerLoginEvent(this, "Plugin reason"));
        if (ev.isCancelled()) {
            this.close(this.getLeaveMessage(), ev.getKickMessage());
            return;
        }

        if (this.isClosed() || !this.isConnected()) {
            return; // Player was probably disconnected by a plugin
        }

        StartGamePacket startGamePacket = new StartGamePacket();
        startGamePacket.entityUniqueId = this.id;
        startGamePacket.entityRuntimeId = this.id;
        startGamePacket.playerGamemode = getClientFriendlyGamemode(this.gamemode);
        startGamePacket.x = (float) this.x;
        startGamePacket.y = (float) this.y;
        startGamePacket.z = (float) this.z;
        startGamePacket.yaw = (float) this.yaw;
        startGamePacket.pitch = (float) this.pitch;
        startGamePacket.dimension = (byte) (this.level.getDimension() & 0xff);
        startGamePacket.worldGamemode = getClientFriendlyGamemode(this.gamemode);
        startGamePacket.difficulty = this.server.getDifficulty();
        if (this.level.getProvider() == null || this.level.getProvider().getSpawn() == null) {
            startGamePacket.spawnX = (int) this.x;
            startGamePacket.spawnY = (int) this.y;
            startGamePacket.spawnZ = (int) this.z;
        } else {
            Vector3 spawn = this.level.getProvider().getSpawn();
            startGamePacket.spawnX = (int) spawn.x;
            startGamePacket.spawnY = (int) spawn.y;
            startGamePacket.spawnZ = (int) spawn.z;
        }
        startGamePacket.commandsEnabled = this.enableClientCommand;
        startGamePacket.gameRules = this.getLevel().getGameRules();
        startGamePacket.worldName = this.getServer().getNetwork().getName();
        if (this.getLevel().isRaining()) {
            startGamePacket.rainLevel = this.getLevel().getRainTime();
            if (this.getLevel().isThundering()) {
                startGamePacket.lightningLevel = this.getLevel().getThunderTime();
            }
        }

        this.forceDataPacket(startGamePacket, null);

        this.loggedIn = true;

        this.server.getLogger().info(this.getServer().getLanguage().translateString("nukkit.player.logIn",
                TextFormat.AQUA + this.username + TextFormat.WHITE,
                this.getAddress(),
                String.valueOf(this.getPort()),
                String.valueOf(this.id),
                this.level.getName(),
                String.valueOf(this.getFloorX()),
                String.valueOf(this.getFloorY()),
                String.valueOf(this.getFloorZ())));

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_CAN_CLIMB, true, false);
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_CAN_SHOW_NAMETAG, true, false);
        this.setDataProperty(new ByteEntityData(DATA_ALWAYS_SHOW_NAMETAG, 1), false);

        if (this.isSpectator()) {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_SILENT, true, false);
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_HAS_COLLISION, false, false);
        }

        this.dataPacket(CustomItemManager.get().getCachedPacket());
        this.dataPacket(BiomeDefinitionListPacket.getCachedPacket());
        this.dataPacket(EntityManager.get().getCachedPacket());

        this.sendSpawnPos((int) this.x, (int) this.y, (int) this.z, this.level.getDimension());
        this.getLevel().sendTime(this);

        SetDifficultyPacket difficultyPacket = new SetDifficultyPacket();
        difficultyPacket.difficulty = this.server.getDifficulty();
        this.dataPacket(difficultyPacket);

        SetCommandsEnabledPacket commandsPacket = new SetCommandsEnabledPacket();
        commandsPacket.enabled = this.isEnableClientCommand();
        this.dataPacket(commandsPacket);

        this.adventureSettings.update();

        GameRulesChangedPacket gameRulesPK = new GameRulesChangedPacket();
        gameRulesPK.gameRulesMap = level.getGameRules().getGameRules();
        this.dataPacket(gameRulesPK);

        this.server.sendFullPlayerListData(this);
        this.sendAttributes();

        this.inventory.sendCreativeContents();
        this.sendAllInventories();
        this.inventory.sendHeldItem(this);
        this.dataPacket(TrimDataPacket.getCachedPacket());
        this.server.sendRecipeList(this);

        if (this.isEnableClientCommand()) {
            this.sendCommandData();
        }

        this.sendPotionEffects(this);
        this.sendData(this);

        if (this.isOp() || this.hasPermission("nukkit.textcolor")) {
            this.setRemoveFormat(false);
        }

        this.server.addOnlinePlayer(this);
    }

    /**
     * Handling received data packets
     * @param packet packet
     */
    public void handleDataPacket(DataPacket packet) {
        if (!connected) {
            return;
        }

        byte pid = packet.pid();
        if (!loginVerified && pid != ProtocolInfo.LOGIN_PACKET && pid != ProtocolInfo.REQUEST_NETWORK_SETTINGS_PACKET && pid != ProtocolInfo.BATCH_PACKET && pid != ProtocolInfo.CLIENT_TO_SERVER_HANDSHAKE_PACKET) {
            server.getLogger().warning("Ignoring " + packet.getClass().getSimpleName() + " from " + getAddress() + " due to player not verified yet");
            return;
        }

        if (!loggedIn && !PRE_LOGIN_PACKETS.contains(pid)) {
            server.getLogger().warning("Ignoring " + packet.getClass().getSimpleName() + " from " + username + " due to player not logged in yet");
            return;
        }

        DataPacketReceiveEvent ev = new DataPacketReceiveEvent(this, packet);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }

        if (Nukkit.DEBUG > 2 && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Inbound {}: {}", this.getName(), packet);
        }

        switch (pid) {
            case ProtocolInfo.REQUEST_NETWORK_SETTINGS_PACKET:
                this.networkSettingsRequested = true;

                if (this.getNetworkSession().getCompression() != CompressionProvider.NONE) {
                    this.getServer().getLogger().debug((this.username == null ? this.unverifiedUsername : this.username) +
                            ": got a RequestNetworkSettingsPacket but compression is already set");
                    return;
                }

                RequestNetworkSettingsPacket networkSettingsRequest = (RequestNetworkSettingsPacket) packet;

                if (!ProtocolInfo.SUPPORTED_PROTOCOLS.contains(networkSettingsRequest.protocolVersion)) {
                    String message;
                    if (networkSettingsRequest.protocolVersion < ProtocolInfo.CURRENT_PROTOCOL) {
                        message = "disconnectionScreen.outdatedClient";
                    } else {
                        message = "disconnectionScreen.outdatedServer";
                    }
                    this.close("", message, true);
                    this.server.getLogger().debug(getAddress() + " disconnected with unsupported protocol " + networkSettingsRequest.protocolVersion);
                    return;
                }

                NetworkSettingsPacket settingsPacket = new NetworkSettingsPacket();
                settingsPacket.compressionAlgorithm = server.useSnappy ? PacketCompressionAlgorithm.SNAPPY : PacketCompressionAlgorithm.ZLIB;
                settingsPacket.compressionThreshold = server.networkCompressionThreshold;
                this.forceDataPacket(settingsPacket, () -> this.networkSession.setCompression(server.useSnappy ? CompressionProvider.SNAPPY : CompressionProvider.ZLIB_RAW));
                return;
            case ProtocolInfo.LOGIN_PACKET:
                if (this.loginPacketReceived) {
                    this.close("", "Invalid login packet");
                    return;
                }

                this.loginPacketReceived = true;

                LoginPacket loginPacket = (LoginPacket) packet;
                this.unverifiedUsername = TextFormat.clean(loginPacket.username);

                if (!this.networkSettingsRequested) {
                    this.close("", "Invalid login sequence: login packet before network settings");
                    return;
                }

                if (!ProtocolInfo.SUPPORTED_PROTOCOLS.contains(loginPacket.getProtocol())) {
                    String message;
                    if (loginPacket.getProtocol() < ProtocolInfo.CURRENT_PROTOCOL) {
                        message = "disconnectionScreen.outdatedClient";
                    } else {
                        message = "disconnectionScreen.outdatedServer";
                    }
                    this.close("", message, true);
                    this.server.getLogger().debug(getAddress() + " disconnected with unsupported protocol " + loginPacket.getProtocol());
                    return;
                }

                if (loginPacket.skin == null) {
                    this.close("", "disconnectionScreen.invalidSkin");
                    return;
                }

                if (this.server.getOnlinePlayersCount() >= this.server.getMaxPlayers() && this.kick(PlayerKickEvent.Reason.SERVER_FULL, "disconnectionScreen.serverFull", false)) {
                    return;
                }

                try {
                    // TODO: Why do we read this separately?
                    this.loginChainData = ClientChainData.read(loginPacket);
                } catch (ClientChainData.TooBigSkinException ex) {
                    this.close("", "disconnectionScreen.invalidSkin");
                    return;
                }

                server.getLogger().debug("Name: " + this.unverifiedUsername + " Protocol: " + loginPacket.getProtocol() + " Version: " + loginChainData.getGameVersion());

                if (!loginChainData.isXboxAuthed() && server.xboxAuth) {
                    this.close("", "disconnectionScreen.notAuthenticated");
                    return;
                }

                // Do not set username before the user is authenticated
                this.username = this.unverifiedUsername;
                this.unverifiedUsername = null;
                this.displayName = this.username;
                this.iusername = this.username.toLowerCase(Locale.ROOT);
                this.setDataProperty(new StringEntityData(DATA_NAMETAG, this.username), false);

                this.randomClientId = loginPacket.clientId;
                this.uuid = loginPacket.clientUUID;
                this.rawUUID = Binary.writeUUID(this.uuid);

                boolean valid = true;
                int len = loginPacket.username.length();
                if (len > 16 || len < 3 || loginPacket.username.trim().isEmpty()) {
                    valid = false;
                }

                if (valid) {
                    for (int i = 0; i < len; i++) {
                        char c = loginPacket.username.charAt(i);
                        if ((c >= 'a' && c <= 'z') ||
                                (c >= 'A' && c <= 'Z') ||
                                (c >= '0' && c <= '9') ||
                                c == '_' || (c == ' ' && i != 0 && i != len - 1)
                        ) {
                            continue;
                        }

                        valid = false;
                        break;
                    }
                }

                if (!valid || Objects.equals(this.iusername, "rcon") || Objects.equals(this.iusername, "console")) {
                    this.close("", "disconnectionScreen.invalidName");
                    return;
                }

                Skin skin = loginPacket.skin;
                if (!skin.isValid()) {
                    this.close("", "disconnectionScreen.invalidSkin");
                    return;
                }
                this.setSkin(skin);

                PlayerPreLoginEvent playerPreLoginEvent;
                this.server.getPluginManager().callEvent(playerPreLoginEvent = new PlayerPreLoginEvent(this, "Plugin reason"));
                if (playerPreLoginEvent.isCancelled()) {
                    this.close("", playerPreLoginEvent.getKickMessage());
                    return;
                }

                if (server.encryptionEnabled) {
                    this.getServer().getScheduler().scheduleAsyncTask(null, new PrepareEncryptionTask(this) {

                        @Override
                        public void onCompletion(Server server) {
                            if (!Player.this.connected) {
                                return;
                            }

                            if (this.getHandshakeJwt() == null || this.getEncryptionKey() == null || this.getEncryptionCipher() == null || this.getDecryptionCipher() == null) {
                                Player.this.close("Failed to enable encryption");
                                return;
                            }

                            ServerToClientHandshakePacket handshakePacket = new ServerToClientHandshakePacket();
                            handshakePacket.jwt = this.getHandshakeJwt();
                            Player.this.forceDataPacket(handshakePacket, () -> {
                                Player.this.awaitingEncryptionHandshake = true;
                                Player.this.networkSession.setEncryption(this.getEncryptionKey(), this.getEncryptionCipher(), this.getDecryptionCipher());
                            });
                        }
                    });
                } else {
                    this.processPreLogin();
                }
                return;
            case ProtocolInfo.CLIENT_TO_SERVER_HANDSHAKE_PACKET:
                if (!this.awaitingEncryptionHandshake) {
                    this.close("Invalid encryption handshake");
                    return;
                }

                this.awaitingEncryptionHandshake = false;
                this.processPreLogin();
                return;
            case ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET:
                if (this.spawned) {
                    this.getServer().getLogger().debug(username + ": ResourcePackClientResponsePacket after player spawned");
                    return;
                }
                ResourcePackClientResponsePacket responsePacket = (ResourcePackClientResponsePacket) packet;
                switch (responsePacket.responseStatus) {
                    case ResourcePackClientResponsePacket.STATUS_REFUSED:
                        this.close("", "disconnectionScreen.noReason");
                        return;
                    case ResourcePackClientResponsePacket.STATUS_SEND_PACKS:
                        for (ResourcePackClientResponsePacket.Entry entry : responsePacket.packEntries) {
                            ResourcePack resourcePack = this.server.getResourcePackManager().getPackById(entry.uuid);
                            if (resourcePack == null) {
                                this.close("", "disconnectionScreen.resourcePack");
                                return;
                            }

                            ResourcePackDataInfoPacket dataInfoPacket = new ResourcePackDataInfoPacket();
                            dataInfoPacket.packId = resourcePack.getPackId();
                            dataInfoPacket.maxChunkSize = RESOURCE_PACK_CHUNK_SIZE;
                            dataInfoPacket.chunkCount = MathHelper.ceil(resourcePack.getPackSize() / (float) RESOURCE_PACK_CHUNK_SIZE);
                            dataInfoPacket.compressedPackSize = resourcePack.getPackSize();
                            dataInfoPacket.sha256 = resourcePack.getSha256();
                            this.dataPacket(dataInfoPacket);
                        }
                        return;
                    case ResourcePackClientResponsePacket.STATUS_HAVE_ALL_PACKS:
                        ResourcePackStackPacket stackPacket = new ResourcePackStackPacket();
                        stackPacket.mustAccept = this.server.getForceResources() && !this.server.forceResourcesAllowOwnPacks; // Option not to disable client's own packs
                        stackPacket.resourcePackStack = this.server.getResourcePackManager().getResourceStack();
                        this.dataPacket(stackPacket);
                        return;
                    case ResourcePackClientResponsePacket.STATUS_COMPLETED:
                        this.shouldLogin = true;

                        if (this.preLoginEventTask.isFinished()) {
                            this.preLoginEventTask.onCompletion(server);
                        }
                        return;
                }
                return;
            case ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET:
                ResourcePackChunkRequestPacket requestPacket = (ResourcePackChunkRequestPacket) packet;
                ResourcePack resourcePack = this.server.getResourcePackManager().getPackById(requestPacket.packId);
                if (resourcePack == null) {
                    this.close("", "disconnectionScreen.resourcePack");
                    return;
                }

                ResourcePackChunkDataPacket dataPacket = new ResourcePackChunkDataPacket();
                dataPacket.packId = resourcePack.getPackId();
                dataPacket.chunkIndex = requestPacket.chunkIndex;
                dataPacket.data = resourcePack.getPackChunk(RESOURCE_PACK_CHUNK_SIZE * requestPacket.chunkIndex, RESOURCE_PACK_CHUNK_SIZE);
                dataPacket.progress = (long) RESOURCE_PACK_CHUNK_SIZE * requestPacket.chunkIndex;
                this.dataPacket(dataPacket);
                return;
            case ProtocolInfo.PLAYER_SKIN_PACKET:
                PlayerSkinPacket skinPacket = (PlayerSkinPacket) packet;
                skin = skinPacket.skin;

                if (!skin.isValid()) {
                    this.close("", "disconnectionScreen.invalidSkin");
                    return;
                }

                PlayerChangeSkinEvent playerChangeSkinEvent = new PlayerChangeSkinEvent(this, skin);
                playerChangeSkinEvent.setCancelled(TimeUnit.SECONDS.toMillis(this.server.getPlayerSkinChangeCooldown()) > System.currentTimeMillis() - this.lastSkinChange);
                this.server.getPluginManager().callEvent(playerChangeSkinEvent);
                if (!playerChangeSkinEvent.isCancelled()) {
                    this.lastSkinChange = System.currentTimeMillis();
                    this.setSkin(skin);
                }
                return;
            case ProtocolInfo.PLAYER_AUTH_INPUT_PACKET:
                if (!this.spawned) {
                    return;
                }

                PlayerAuthInputPacket authPacket = (PlayerAuthInputPacket) packet;
                if (!authPacket.getBlockActionData().isEmpty()) {
                    for (PlayerBlockActionData action : authPacket.getBlockActionData().values()) {
                        BlockVector3 blockPos = action.getPosition();
                        BlockFace blockFace = BlockFace.fromIndex(action.getFacing());
                        if (this.lastBlockAction != null && this.lastBlockAction.getAction() == PlayerActionType.PREDICT_DESTROY_BLOCK &&
                                action.getAction() == PlayerActionType.CONTINUE_DESTROY_BLOCK) {
                            this.onBlockBreakStart(blockPos, blockFace);
                        }

                        BlockVector3 lastBreakPos = this.lastBlockAction == null ? null : this.lastBlockAction.getPosition();
                        if (lastBreakPos != null && (lastBreakPos.getX() != blockPos.getX() ||
                                lastBreakPos.getY() != blockPos.getY() || lastBreakPos.getZ() != blockPos.getZ())) {
                            this.onBlockBreakAbort(lastBreakPos, BlockFace.DOWN);
                            this.onBlockBreakStart(blockPos, blockFace);
                        }

                        switch (action.getAction()) {
                            case START_DESTROY_BLOCK:
                                this.onBlockBreakStart(blockPos, blockFace);
                                break;
                            case ABORT_DESTROY_BLOCK:
                            //case STOP_DESTROY_BLOCK:
                                this.onBlockBreakAbort(blockPos, blockFace);
                                break;
                            case CONTINUE_DESTROY_BLOCK:
                                // When player moves cursor to another block
                                break;
                            case PREDICT_DESTROY_BLOCK:
                                //this.onBlockBreakAbort(blockPos, blockFace);
                                this.onBlockBreakComplete(blockPos, blockFace);
                                break;
                        }
                        this.lastBlockAction = action;
                    }
                }

                if (this.teleportPosition != null) {
                    return;
                }

                if (this.riding instanceof EntityControllable && riding.isControlling(this)) {
                    boolean jumping = authPacket.getInputData().contains(AuthInputAction.JUMPING);
                    if (jumping && this.riderJumpTick <= 0) {
                        this.riderJumpTick = server.getTick();
                    } else if (!jumping && this.riderJumpTick > 0) {
                        ((EntityControllable) riding).onJump(this, server.getTick() - this.riderJumpTick);
                        this.riderJumpTick = 0;
                    }
                    double inputX = authPacket.getMotion().getX();
                    double inputY = authPacket.getMotion().getY();
                    if (inputX >= -1.001 && inputX <= 1.001 && inputY >= -1.001 && inputY <= 1.001) {
                        ((EntityControllable) riding).onPlayerInput(this, inputX, inputY);
                    }
                } else if (this.riding instanceof EntityBoat && authPacket.getInputData().contains(AuthInputAction.IN_CLIENT_PREDICTED_IN_VEHICLE)) {
                    if (this.riding.getId() == authPacket.getPredictedVehicle() && this.riding.isControlling(this)) {
                        if (this.temporalVector.setComponents(authPacket.getPosition().getX(), authPacket.getPosition().getY(), authPacket.getPosition().getZ()).distanceSquared(this.riding) < 16) {
                            ((EntityBoat) this.riding).onInput(authPacket.getPosition().getX(), authPacket.getPosition().getY(), authPacket.getPosition().getZ(), authPacket.getHeadYaw());
                        }
                    }
                }

                if (!this.isSpectator() && authPacket.getInputData().contains(AuthInputAction.MISSED_SWING)) {
                    level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ATTACK_NODAMAGE, -1, "minecraft:player", false, false);
                }

                if (authPacket.getInputData().contains(AuthInputAction.START_SPRINTING)) {
                    PlayerToggleSprintEvent playerToggleSprintEvent = new PlayerToggleSprintEvent(this, true);
                    if ((this.foodData.getLevel() <= 6 && !this.getAdventureSettings().get(Type.FLYING)) ||
                            this.riding != null || this.sleeping != null || this.hasEffect(Effect.BLINDNESS) ||
                            (this.isSneaking() && !authPacket.getInputData().contains(AuthInputAction.STOP_SNEAKING))) {
                        playerToggleSprintEvent.setCancelled(true);
                    }
                    this.server.getPluginManager().callEvent(playerToggleSprintEvent);
                    if (playerToggleSprintEvent.isCancelled()) {
                        this.needSendData = true;
                    } else {
                        this.setSprinting(true, false);
                    }
                    this.setUsingItem(false);
                }

                if (authPacket.getInputData().contains(AuthInputAction.STOP_SPRINTING)) {
                    PlayerToggleSprintEvent playerToggleSprintEvent = new PlayerToggleSprintEvent(this, false);
                    this.server.getPluginManager().callEvent(playerToggleSprintEvent);
                    if (playerToggleSprintEvent.isCancelled()) {
                        this.needSendData = true;
                    } else {
                        this.setSprinting(false, false);
                    }
                }

                if (authPacket.getInputData().contains(AuthInputAction.START_SNEAKING)) {
                    PlayerToggleSneakEvent playerToggleSneakEvent = new PlayerToggleSneakEvent(this, true);
                    if (this.riding != null || this.sleeping != null) {
                        playerToggleSneakEvent.setCancelled(true);
                    }
                    this.server.getPluginManager().callEvent(playerToggleSneakEvent);
                    if (playerToggleSneakEvent.isCancelled()) {
                        this.needSendData = true;
                    } else {
                        this.setSneaking(true);
                    }
                }

                if (authPacket.getInputData().contains(AuthInputAction.STOP_SNEAKING)) {
                    PlayerToggleSneakEvent playerToggleSneakEvent = new PlayerToggleSneakEvent(this, false);
                    this.server.getPluginManager().callEvent(playerToggleSneakEvent);
                    if (playerToggleSneakEvent.isCancelled()) {
                        this.needSendData = true;
                    } else {
                        this.setSneaking(false);
                    }
                }

                if (authPacket.getInputData().contains(AuthInputAction.START_CRAWLING)) {
                    PlayerToggleCrawlEvent playerToggleCrawlEvent = new PlayerToggleCrawlEvent(this, true);
                    if (this.riding != null || this.sleeping != null) {
                        playerToggleCrawlEvent.setCancelled(true);
                    }
                    this.server.getPluginManager().callEvent(playerToggleCrawlEvent);
                    if (playerToggleCrawlEvent.isCancelled()) {
                        this.needSendData = true;
                    } else {
                        this.setCrawling(true);
                    }
                }

                if (authPacket.getInputData().contains(AuthInputAction.STOP_CRAWLING)) {
                    PlayerToggleCrawlEvent playerToggleCrawlEvent = new PlayerToggleCrawlEvent(this, false);
                    this.server.getPluginManager().callEvent(playerToggleCrawlEvent);
                    if (playerToggleCrawlEvent.isCancelled()) {
                        this.needSendData = true;
                    } else {
                        this.setCrawling(false);
                    }
                }

                if (authPacket.getInputData().contains(AuthInputAction.START_JUMPING)) {
                    this.server.getPluginManager().callEvent(new PlayerJumpEvent(this));
                }

                if (authPacket.getInputData().contains(AuthInputAction.START_GLIDING)) {
                    boolean withoutElytra = false;
                    Item chestplate = this.getInventory().getChestplateFast();
                    if (chestplate == null || chestplate.getId() != ItemID.ELYTRA || chestplate.getDamage() >= chestplate.getMaxDurability()) {
                        withoutElytra = true;
                    }
                    if (withoutElytra && !server.getAllowFlight()) {
                        this.kick(PlayerKickEvent.Reason.FLYING_DISABLED, MSG_FLYING_NOT_ENABLED, true);
                        return;
                    }
                    PlayerToggleGlideEvent playerToggleGlideEvent = new PlayerToggleGlideEvent(this, true);
                    if (this.riding != null || this.sleeping != null || withoutElytra) {
                        playerToggleGlideEvent.setCancelled(true);
                    }
                    this.server.getPluginManager().callEvent(playerToggleGlideEvent);
                    if (playerToggleGlideEvent.isCancelled()) {
                        this.needSendData = true;
                    } else {
                        this.setGliding(true);
                    }
                }

                if (authPacket.getInputData().contains(AuthInputAction.STOP_GLIDING)) {
                    PlayerToggleGlideEvent playerToggleGlideEvent = new PlayerToggleGlideEvent(this, false);
                    this.server.getPluginManager().callEvent(playerToggleGlideEvent);
                    if (playerToggleGlideEvent.isCancelled()) {
                        this.needSendData = true;
                    } else {
                        this.setGliding(false);
                    }
                }

                if (authPacket.getInputData().contains(AuthInputAction.START_SWIMMING)) {
                    PlayerToggleSwimEvent ptse = new PlayerToggleSwimEvent(this, true);
                    if (this.riding != null || this.sleeping != null || !this.isInsideOfWater()) {
                        ptse.setCancelled(true);
                    }
                    this.server.getPluginManager().callEvent(ptse);
                    if (ptse.isCancelled()) {
                        this.needSendData = true;
                    } else {
                        this.setSwimming(true);
                    }
                    this.setUsingItem(false);
                }

                if (authPacket.getInputData().contains(AuthInputAction.STOP_SWIMMING)) {
                    PlayerToggleSwimEvent ptse = new PlayerToggleSwimEvent(this, false);
                    this.server.getPluginManager().callEvent(ptse);
                    if (ptse.isCancelled()) {
                        this.needSendData = true;
                    } else {
                        this.setSwimming(false);
                    }
                }

                if (authPacket.getInputData().contains(AuthInputAction.START_SPIN_ATTACK)) {
                    Enchantment riptide = this.getInventory().getItemInHandFast().getEnchantment(Enchantment.ID_TRIDENT_RIPTIDE);
                    if (riptide != null) {
                        PlayerToggleSpinAttackEvent playerToggleSpinAttackEvent = new PlayerToggleSpinAttackEvent(this, true);

                        if (riptide.getLevel() < 1) {
                            playerToggleSpinAttackEvent.setCancelled(true);
                        } else {
                            boolean inWater = false;
                            for (Block block : this.getCollisionBlocks()) {
                                if (block instanceof BlockWater || block.level.isBlockWaterloggedAt(this.chunk, (int) block.x, (int) block.y, (int) block.z)) {
                                    inWater = true;
                                    break;
                                }
                            }
                            if (!(inWater || (this.getLevel().isRaining() && this.canSeeSky()))) {
                                playerToggleSpinAttackEvent.setCancelled(true);
                            }
                        }

                        server.getPluginManager().callEvent(playerToggleSpinAttackEvent);

                        if (playerToggleSpinAttackEvent.isCancelled()) {
                            this.setNeedSendData(true);
                        } else {
                            this.onSpinAttack(riptide.getLevel());
                            this.setSpinAttack(true);
                            this.setUsingItem(false);
                            this.resetFallDistance();
                            int riptideSound;
                            if (riptide.getLevel() >= 3) {
                                riptideSound = LevelSoundEventPacket.SOUND_ITEM_TRIDENT_RIPTIDE_3;
                            } else if (riptide.getLevel() == 2) {
                                riptideSound = LevelSoundEventPacket.SOUND_ITEM_TRIDENT_RIPTIDE_2;
                            } else {
                                riptideSound = LevelSoundEventPacket.SOUND_ITEM_TRIDENT_RIPTIDE_1;
                            }
                            this.getLevel().addLevelSoundEvent(this, riptideSound);
                        }
                    }
                }

                if (authPacket.getInputData().contains(AuthInputAction.STOP_SPIN_ATTACK)) {
                    PlayerToggleSpinAttackEvent playerToggleSpinAttackEvent = new PlayerToggleSpinAttackEvent(this, false);
                    this.server.getPluginManager().callEvent(playerToggleSpinAttackEvent);
                    if (playerToggleSpinAttackEvent.isCancelled()) {
                        this.needSendData = true;
                    } else {
                        this.setSpinAttack(false);
                    }
                }

                if (authPacket.getInputData().contains(AuthInputAction.START_FLYING)) {
                    if (!server.getAllowFlight() && !this.adventureSettings.get(Type.ALLOW_FLIGHT)) {
                        this.kick(PlayerKickEvent.Reason.FLYING_DISABLED, MSG_FLYING_NOT_ENABLED, true);
                        break;
                    }
                    PlayerToggleFlightEvent playerToggleFlightEvent = new PlayerToggleFlightEvent(this, true);
                    server.getPluginManager().callEvent(playerToggleFlightEvent);
                    if (playerToggleFlightEvent.isCancelled()) {
                        this.needSendAdventureSettings = true;
                    } else {
                        this.adventureSettings.set(AdventureSettings.Type.FLYING, playerToggleFlightEvent.isFlying());
                    }
                }

                if (authPacket.getInputData().contains(AuthInputAction.STOP_FLYING)) {
                    PlayerToggleFlightEvent playerToggleFlightEvent = new PlayerToggleFlightEvent(this, false);
                    if (this.isSpectator()) {
                        playerToggleFlightEvent.setCancelled(true);
                    }
                    server.getPluginManager().callEvent(playerToggleFlightEvent);
                    if (playerToggleFlightEvent.isCancelled()) {
                        this.needSendAdventureSettings = true;
                    } else {
                        this.adventureSettings.set(AdventureSettings.Type.FLYING, playerToggleFlightEvent.isFlying());
                    }
                }

                if (this.adventureSettings.get(AdventureSettings.Type.FLYING)) {
                    this.flySneaking = authPacket.getInputData().contains(AuthInputAction.SNEAKING);
                }

                Vector3 clientPosition = authPacket.getPosition().subtract(0, this.riding == null ? this.getBaseOffset() : this.riding.getMountedOffset(this).getY(), 0).asVector3();

                double distSqrt = clientPosition.distanceSquared(this);
                if (distSqrt > 100) { // Notice: This is the distance to player's position on server side. There are likely still unhandled previous movements when next move packet is received.
                    this.sendPosition(this, authPacket.getYaw(), authPacket.getPitch(), MovePlayerPacket.MODE_RESET);
                    server.getLogger().debug(username + ": move " + distSqrt + " > 100");
                    return;
                }

                boolean revertMotion = false;
                if (!this.isAlive() || !this.spawned) {
                    revertMotion = true;
                    this.forceMovement = this;
                }

                if (this.forceMovement != null && (revertMotion || clientPosition.distanceSquared(this.forceMovement) > 0.1)) {
                    this.sendPosition(this.forceMovement, authPacket.getYaw(), authPacket.getPitch(), MovePlayerPacket.MODE_RESET);
                } else {
                    float yaw = authPacket.getYaw() % 360;
                    float pitch = authPacket.getPitch() % 360;
                    if (yaw < 0) {
                        yaw += 360;
                    }

                    this.setRotation(yaw, pitch);
                    this.newPosition = clientPosition;
                    this.clientMovements.offer(clientPosition);
                    this.forceMovement = null;
                }
                return;
            case ProtocolInfo.MOB_EQUIPMENT_PACKET:
                if (!this.spawned || !this.isAlive()) {
                    return;
                }

                MobEquipmentPacket mobEquipmentPacket = (MobEquipmentPacket) packet;

                if (mobEquipmentPacket.windowId != ContainerIds.INVENTORY) {
                    return;
                }

                if (this.inventory == null) {
                    return;
                }

                this.inventory.equipItem(mobEquipmentPacket.hotbarSlot);
                this.setUsingItem(false);
                return;
            case ProtocolInfo.PLAYER_ACTION_PACKET:
                PlayerActionPacket playerActionPacket = (PlayerActionPacket) packet;
                if (!this.spawned || (!this.isAlive() && playerActionPacket.action != PlayerActionPacket.ACTION_RESPAWN)) {
                    return;
                }

                playerActionPacket.entityId = this.id;

                stopItemHold:
                switch (playerActionPacket.action) {
                    case PlayerActionPacket.ACTION_START_BREAK:
                        break stopItemHold;
                    case PlayerActionPacket.ACTION_ABORT_BREAK:
                    //case PlayerActionPacket.ACTION_STOP_BREAK: // This could be used instead of inventory transaction when the breaking is done?
                        return;
                    case PlayerActionPacket.ACTION_STOP_SLEEPING:
                        this.stopSleep();
                        return;
                    case PlayerActionPacket.ACTION_RESPAWN:
                        if (!this.spawned || this.isAlive() || !this.isOnline()) {
                            return;
                        }
                        this.respawn();
                        break stopItemHold;
                    case PlayerActionPacket.ACTION_JUMP:
                        return;
                    case PlayerActionPacket.ACTION_START_SPRINT:
                        break stopItemHold;
                    case PlayerActionPacket.ACTION_STOP_SPRINT:
                        return;
                    case PlayerActionPacket.ACTION_START_SNEAK:
                        return;
                    case PlayerActionPacket.ACTION_STOP_SNEAK:
                        return;
                    case PlayerActionPacket.ACTION_DIMENSION_CHANGE_ACK:
                        if (this.awaitingDimensionAck) {
                            this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET);
                            this.dummyBossBars.values().forEach(DummyBossBar::reshow);
                            this.awaitingDimensionAck = false;
                        } else {
                            this.getServer().getLogger().debug(username + ": got a dimension change ack but no dimension change is in progress");
                        }
                        return;
                    case PlayerActionPacket.ACTION_START_GLIDE:
                        return;
                    case PlayerActionPacket.ACTION_STOP_GLIDE:
                        return;
                    case PlayerActionPacket.ACTION_CONTINUE_BREAK:
                        // When player moves cursor to another block
                        return;
                    case PlayerActionPacket.ACTION_START_SWIMMING:
                        break stopItemHold;
                    case PlayerActionPacket.ACTION_STOP_SWIMMING:
                        return;
                }

                this.setUsingItem(false);
                return;
            case ProtocolInfo.MODAL_FORM_RESPONSE_PACKET:
                this.formOpen = false;

                if (!this.spawned || !this.isAlive()) {
                    return;
                }

                ModalFormResponsePacket modalFormPacket = (ModalFormResponsePacket) packet;

                if (formWindows.containsKey(modalFormPacket.formId)) {
                    FormWindow window = formWindows.remove(modalFormPacket.formId);
                    window.setResponse(modalFormPacket.data.trim());

                    for (FormResponseHandler handler : window.getHandlers()) {
                        handler.handle(this, modalFormPacket.formId);
                    }

                    PlayerFormRespondedEvent event = new PlayerFormRespondedEvent(this, modalFormPacket.formId, window);
                    getServer().getPluginManager().callEvent(event);
                } else if (serverSettings.containsKey(modalFormPacket.formId)) {
                    FormWindow window = serverSettings.get(modalFormPacket.formId);
                    window.setResponse(modalFormPacket.data.trim());

                    for (FormResponseHandler handler : window.getHandlers()) {
                        handler.handle(this, modalFormPacket.formId);
                    }

                    PlayerSettingsRespondedEvent event = new PlayerSettingsRespondedEvent(this, modalFormPacket.formId, window);
                    getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled() && window instanceof FormWindowCustom)
                        ((FormWindowCustom) window).setElementsFromResponse();
                }

                return;
            case ProtocolInfo.INTERACT_PACKET:
                if (!this.spawned || !this.isAlive()) {
                    return;
                }

                InteractPacket interactPacket = (InteractPacket) packet;

                if (interactPacket.target == 0 && interactPacket.action == InteractPacket.ACTION_MOUSEOVER) {
                    this.setButtonText("");
                    return;
                }

                Entity targetEntity = interactPacket.target == this.getId() ? this : this.level.getEntity(interactPacket.target);

                if (targetEntity == null || !this.isAlive() || !targetEntity.isAlive()) {
                    if (targetEntity != null || interactPacket.action != InteractPacket.ACTION_OPEN_INVENTORY) {
                        return;
                    }
                }

                if (targetEntity instanceof EntityItem || targetEntity instanceof EntityArrow || targetEntity instanceof EntityXPOrb) {
                    this.kick(PlayerKickEvent.Reason.INVALID_PVE);
                    return;
                }

                switch (interactPacket.action) {
                    case InteractPacket.ACTION_OPEN_INVENTORY:
                        if (!this.inventoryOpen) {
                            if (this.riding instanceof EntityChestBoat && this.riding == targetEntity) {
                                this.addWindow(((InventoryHolder) targetEntity).getInventory());
                            } else if (this.inventory.open(this)) {
                                this.inventoryOpen = true;
                                this.awardAchievement("openInventory");
                            }
                        } else if (Nukkit.DEBUG > 1) {
                            server.getLogger().debug(this.username + " tried to open inventory but one is already open");
                        }
                        return;
                    case InteractPacket.ACTION_MOUSEOVER:
                        String buttonText = "";
                        if (targetEntity instanceof EntityInteractable) {
                            buttonText = ((EntityInteractable) targetEntity).getInteractButtonText(this);
                            if (buttonText == null) {
                                buttonText = "";
                            }
                        }
                        this.setButtonText(buttonText);
                        this.getServer().getPluginManager().callEvent(new PlayerMouseOverEntityEvent(this, targetEntity));
                        return;
                    case InteractPacket.ACTION_VEHICLE_EXIT:
                        if (!(targetEntity instanceof EntityRideable) || this.riding != targetEntity) {
                            return;
                        }

                        this.riderJumpTick = 0;
                        ((EntityRideable) riding).dismountEntity(this);
                        return;
                }
                return;
            case ProtocolInfo.BLOCK_PICK_REQUEST_PACKET:
                if (!this.spawned || !this.isAlive() || this.inventory == null || this.inventoryOpen) {
                    return;
                }

                BlockPickRequestPacket pickRequestPacket = (BlockPickRequestPacket) packet;
                Block block = this.level.getBlock(chunk, pickRequestPacket.x, pickRequestPacket.y, pickRequestPacket.z, false);
                if (block.distanceSquared(this) > 1000) {
                    this.getServer().getLogger().debug(username + ": block pick request for a block too far away");
                    return;
                }
                Item item = block.toItem();
                if (pickRequestPacket.addUserData && this.isCreative()) {
                    BlockEntity blockEntity = this.getLevel().getBlockEntityIfLoaded(this.chunk, this.temporalVector.setComponents(pickRequestPacket.x, pickRequestPacket.y, pickRequestPacket.z));
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

                    if (!itemExists && !this.isCreative()) {
                        return;
                    }

                    for (int slot = 0; slot < this.inventory.getHotbarSize(); slot++) {
                        if (this.inventory.getItem(slot).isNull()) {
                            if (!itemExists && this.isCreative()) {
                                this.inventory.setHeldItemSlot(slot);
                                this.inventory.setItemInHand(pickEvent.getItem());
                                return;
                            } else if (itemSlot > -1) {
                                this.inventory.setHeldItemSlot(slot);
                                this.inventory.setItemInHand(this.inventory.getItem(itemSlot));
                                this.inventory.clear(itemSlot, true);
                                return;
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
                                    return;
                                }
                            }
                        }
                    } else if (itemSlot > -1) {
                        Item itemInHand = this.inventory.getItemInHand();
                        this.inventory.setItemInHand(this.inventory.getItem(itemSlot));
                        this.inventory.setItem(itemSlot, itemInHand);
                    }
                }
                return;
            case ProtocolInfo.ANIMATE_PACKET:
                if (!this.spawned || !this.isAlive()) {
                    return;
                }

                AnimatePacket animatePacket = (AnimatePacket) packet;

                if (animatePacket.action != AnimatePacket.Action.SWING_ARM &&
                        !(this.riding != null && (animatePacket.action == AnimatePacket.Action.ROW_LEFT || animatePacket.action == AnimatePacket.Action.ROW_RIGHT))) {
                    return;
                }

                PlayerAnimationEvent animationEvent = new PlayerAnimationEvent(this, animatePacket.action);
                this.server.getPluginManager().callEvent(animationEvent);
                if (animationEvent.isCancelled()) {
                    return;
                }

                AnimatePacket.Action animation = animationEvent.getAnimationType();

                switch (animation) {
                    case ROW_RIGHT:
                    case ROW_LEFT:
                        if (this.riding instanceof EntityBoat) {
                            ((EntityBoat) this.riding).onPaddle(animation, animatePacket.rowingTime);
                        }
                        break;
                }

                animatePacket = new AnimatePacket();
                animatePacket.eid = this.getId();
                animatePacket.action = animationEvent.getAnimationType();
                Server.broadcastPacket(this.getViewers().values(), animatePacket);
                return;
            case ProtocolInfo.ENTITY_EVENT_PACKET:
                if (!this.spawned || !this.isAlive()) {
                    return;
                }

                EntityEventPacket entityEventPacket = (EntityEventPacket) packet;

                if (entityEventPacket.event != EntityEventPacket.ENCHANT) {
                    this.craftingType = CRAFTING_SMALL;
                }

                switch (entityEventPacket.event) {
                    case EntityEventPacket.EATING_ITEM:
                        if (entityEventPacket.data == 0 || entityEventPacket.eid != this.id) {
                            this.getServer().getLogger().debug(username + ": entity event eid mismatch");
                            return;
                        }

                        entityEventPacket.eid = this.id;
                        entityEventPacket.isEncoded = false;
                        this.dataPacket(entityEventPacket);
                        Server.broadcastPacket(this.getViewers().values(), entityEventPacket);
                        return;
                    case EntityEventPacket.ENCHANT:
                        if (entityEventPacket.eid != this.id) {
                            this.getServer().getLogger().debug(username + ": entity event eid mismatch");
                            return;
                        }

                        Inventory inventory = this.getWindowById(ANVIL_WINDOW_ID);
                        if (inventory instanceof AnvilInventory) {
                            ((AnvilInventory) inventory).setCost(-entityEventPacket.data);
                        }
                        return;
                }
                return;
            case ProtocolInfo.COMMAND_REQUEST_PACKET:
                if (!this.spawned || !this.isAlive()) {
                    return;
                }

                this.resetCraftingGridType();

                CommandRequestPacket commandRequestPacket = (CommandRequestPacket) packet;
                PlayerCommandPreprocessEvent playerCommandPreprocessEvent = new PlayerCommandPreprocessEvent(this, commandRequestPacket.command + ' ');
                this.server.getPluginManager().callEvent(playerCommandPreprocessEvent);
                if (playerCommandPreprocessEvent.isCancelled()) {
                    return;
                }

                this.server.dispatchCommand(playerCommandPreprocessEvent.getPlayer(), playerCommandPreprocessEvent.getMessage().substring(1));
                return;
            case ProtocolInfo.TEXT_PACKET:
                if (!this.spawned || !this.isAlive()) {
                    return;
                }

                TextPacket textPacket = (TextPacket) packet;

                if (textPacket.type == TextPacket.TYPE_CHAT && textPacket.message.length() < 512) {
                    String chatMessage = textPacket.message;
                    int breakLine = chatMessage.indexOf('\n');
                    // Chat messages shouldn't contain break lines so ignore text afterwards
                    if (breakLine != -1) {
                        chatMessage = chatMessage.substring(0, breakLine);
                    }
                    this.chat(chatMessage);
                }
                return;
            case ProtocolInfo.CONTAINER_CLOSE_PACKET:
                ContainerClosePacket containerClosePacket = (ContainerClosePacket) packet;

                if (!this.spawned) {
                    return;
                }

                if (containerClosePacket.windowId == -1) {
                    // At least 1.21 does sometimes send windowId -1 when opening and closing containers quickly
                    if (this.inventoryOpen) {
                        this.inventoryOpen = false;

                        if (this.craftingType == CRAFTING_SMALL) {
                            for (Entry<Inventory, Integer> open : new ArrayList<>(this.windows.entrySet())) {
                                if (open.getKey() instanceof ContainerInventory || open.getKey() instanceof PlayerEnderChestInventory) {
                                    this.server.getPluginManager().callEvent(new InventoryCloseEvent(open.getKey(), this));
                                    this.closingWindowId = Integer.MAX_VALUE;
                                    this.removeWindow(open.getKey(), true);
                                    this.closingWindowId = Integer.MIN_VALUE;
                                }
                            }
                            return;
                        }
                    }

                    this.resetCraftingGridType();
                    this.addWindow(this.craftingGrid, ContainerIds.NONE);
                    ContainerClosePacket pk = new ContainerClosePacket();
                    pk.windowId = -1;
                    pk.wasServerInitiated = false;
                    this.dataPacket(pk);
                } else if (this.windowIndex.containsKey(containerClosePacket.windowId)) {
                    this.inventoryOpen = false;
                    Inventory inn = this.windowIndex.get(containerClosePacket.windowId);
                    this.server.getPluginManager().callEvent(new InventoryCloseEvent(inn, this));
                    this.closingWindowId = containerClosePacket.windowId;
                    this.removeWindow(inn, true);
                    this.closingWindowId = Integer.MIN_VALUE;
                } else { // Close the bugged inventory client refused with id -1 above
                    ContainerClosePacket pk = new ContainerClosePacket();
                    pk.windowId = containerClosePacket.windowId;
                    pk.wasServerInitiated = false;
                    this.dataPacket(pk);
                }
                return;
            case ProtocolInfo.BLOCK_ENTITY_DATA_PACKET:
                if (!this.spawned || !this.isAlive()) {
                    return;
                }

                BlockEntityDataPacket blockEntityDataPacket = (BlockEntityDataPacket) packet;
                this.resetCraftingGridType();

                Vector3 pos = this.temporalVector.setComponents(blockEntityDataPacket.x, blockEntityDataPacket.y, blockEntityDataPacket.z);
                if (pos.distanceSquared(this) > 2500) {
                    if (Nukkit.DEBUG > 1) {
                        server.getLogger().debug(username + ": BlockEntityDataPacket target too far " + pos);
                    }
                    return;
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
                return;
            case ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET:
                RequestChunkRadiusPacket requestChunkRadiusPacket = (RequestChunkRadiusPacket) packet;
                ChunkRadiusUpdatedPacket chunkRadiusUpdatePacket = new ChunkRadiusUpdatedPacket();
                this.chunkRadius = Math.max(3, Math.min(requestChunkRadiusPacket.radius, this.viewDistance));
                chunkRadiusUpdatePacket.radius = this.chunkRadius;
                this.dataPacket(chunkRadiusUpdatePacket);
                return;
            case ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET:
                if (!this.spawned) {
                    return;
                }

                SetPlayerGameTypePacket setPlayerGameTypePacket = (SetPlayerGameTypePacket) packet;
                if (setPlayerGameTypePacket.gamemode != this.gamemode) {
                    if (!this.hasPermission("nukkit.command.gamemode")) {
                        if (!this.isOp()) {
                            this.kick(PlayerKickEvent.Reason.INVALID_PACKET, "Invalid SetPlayerGameTypePacket", true);
                        }
                        return;
                    }
                    this.setGamemode(setPlayerGameTypePacket.gamemode, true);
                    Command.broadcastCommandMessage(this, new TranslationContainer("commands.gamemode.success.self", Server.getGamemodeString(this.gamemode)));
                }
                return;
            case ProtocolInfo.MAP_INFO_REQUEST_PACKET:
                if (this.inventory == null) {
                    return;
                }

                MapInfoRequestPacket pk = (MapInfoRequestPacket) packet;

                Item mapItem = null;

                for (Item item1 : this.offhandInventory.getContents().values()) {
                    if (item1 instanceof ItemMap && ((ItemMap) item1).getMapId() == pk.mapId) {
                        mapItem = item1;
                    }
                }

                if (mapItem == null) {
                    for (Item item1 : this.inventory.getContents().values()) {
                        if (item1 instanceof ItemMap && ((ItemMap) item1).getMapId() == pk.mapId) {
                            mapItem = item1;
                        }
                    }
                }

                if (mapItem == null) {
                    for (BlockEntity be : this.level.getBlockEntities().values()) {
                        if (be instanceof BlockEntityItemFrame) {
                            BlockEntityItemFrame itemFrame1 = (BlockEntityItemFrame) be;

                            if (itemFrame1.getItem() instanceof ItemMap && ((ItemMap) itemFrame1.getItem()).getMapId() == pk.mapId) {
                                ((ItemMap) itemFrame1.getItem()).sendImage(this);
                                return;
                            }
                        }
                    }
                } else {
                    PlayerMapInfoRequestEvent event = new PlayerMapInfoRequestEvent(this, mapItem);
                    getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        ItemMap map = (ItemMap) mapItem;
                        if (map.trySendImage(this)) {
                            return;
                        }
                        try {
                            BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
                            Graphics2D graphics = image.createGraphics();

                            int worldX = (Math.floorDiv(this.getFloorX(), 128)) << 7;
                            int worldZ = (Math.floorDiv(this.getFloorZ(), 128)) << 7;
                            for (int x = 0; x < 128; x++) {
                                int avgY = 0;
                                for (int y = -1; y < 128; y++) {
                                    if (this.getLevel().getDimension() == Level.DIMENSION_NETHER) {
                                        if (y == -1) {
                                            continue;
                                        }
                                        graphics.setColor(colorizeMapColor(new SplittableRandom((((long) (worldZ + y) & 0x3ffffff) << 26) + ((long) (worldX + x) & 0x3ffffff)).nextBoolean() ? BlockColor.STONE_BLOCK_COLOR : BlockColor.DIRT_BLOCK_COLOR, 1));
                                    } else {
                                        if (y == -1) { // Hack: Make sure we have average world height for the first row
                                            avgY = this.getLevel().getHighestBlockAt(worldX + x, worldZ, false);
                                            continue;
                                        }

                                        int worldY = this.getLevel().getHighestBlockAt(worldX + x, worldZ + y, false);
                                        double avgYDifference = (worldY - avgY) * 4 / 5 + ((x + y & 1) - 0.5) * 0.4; // 4d / 5d would provide more detail but is that better?
                                        int colorDepth = 1;
                                        if (avgYDifference > 0.6) {
                                            colorDepth = 2;
                                        }
                                        if (avgYDifference < -0.6) {
                                            colorDepth = 0;
                                        }
                                        avgY = worldY;
                                        graphics.setColor(colorizeMapColor(this.getLevel().getMapColorAt(worldX + x, worldY, worldZ + y), colorDepth));
                                    }

                                    graphics.fillRect(x, y, x + 1, y + 1);
                                }
                            }

                            map.setImage(image);
                            map.sendImage(this);
                        } catch (Exception ex) {
                            this.getServer().getLogger().debug(username + ": there was an error while generating map image", ex);
                        }
                    }
                }

                return;
            case ProtocolInfo.INVENTORY_TRANSACTION_PACKET:
                if (this.isSpectator()) {
                    this.needSendInventory = true;
                    return;
                }

                InventoryTransactionPacket transactionPacket = (InventoryTransactionPacket) packet;

                Inventory inv;
                if ((transactionPacket.transactionType == InventoryTransactionPacket.TYPE_MISMATCH ||
                        (transactionPacket.transactionType == InventoryTransactionPacket.TYPE_NORMAL && this.isCreative() && Arrays.stream(transactionPacket.actions).anyMatch(action -> action.sourceType == NetworkInventoryAction.SOURCE_TODO)))
                        && (inv = getWindowById(SMITHING_WINDOW_ID)) instanceof SmithingInventory) {

                    SmithingInventory smithingInventory = (SmithingInventory) inv;
                    if (!smithingInventory.getResult().isNull()) {
                        InventoryTransactionPacket fixedPacket = new InventoryTransactionPacket();
                        fixedPacket.isRepairItemPart = true;
                        fixedPacket.actions = new NetworkInventoryAction[8];

                        Item fromIngredient = smithingInventory.getIngredient().clone();
                        Item toIngredient = fromIngredient.decrement(1);

                        Item fromEquipment = smithingInventory.getEquipment().clone();
                        Item toEquipment = fromEquipment.decrement(1);

                        Item fromTemplate = smithingInventory.getTemplate().clone();
                        Item toTemplate = fromTemplate.decrement(1);

                        Item fromResult = Item.get(Item.AIR);
                        Item toResult = smithingInventory.getResult().clone();

                        NetworkInventoryAction action = new NetworkInventoryAction();
                        action.windowId = ContainerIds.UI;
                        action.inventorySlot = SmithingInventory.SMITHING_INGREDIENT_UI_SLOT;
                        action.oldItem = fromIngredient.clone();
                        action.newItem = toIngredient.clone();
                        fixedPacket.actions[0] = action;

                        action = new NetworkInventoryAction();
                        action.windowId = ContainerIds.UI;
                        action.inventorySlot = SmithingInventory.SMITHING_EQUIPMENT_UI_SLOT;
                        action.oldItem = fromEquipment.clone();
                        action.newItem = toEquipment.clone();
                        fixedPacket.actions[1] = action;

                        action = new NetworkInventoryAction();
                        action.windowId = ContainerIds.UI;
                        action.inventorySlot = SmithingInventory.SMITHING_TEMPLATE_UI_SLOT;
                        action.oldItem = fromTemplate.clone();
                        action.newItem = toTemplate.clone();
                        fixedPacket.actions[2] = action;

                        if (this.getLoginChainData().getUIProfile() == 0) {
                            // We can't know whether shift click was used so we must make sure we won't overwrite item in cursor inventory
                            Item[] drops = this.inventory.addItem(this.playerUIInventory.getItemFast(0)); // Cloned in addItem
                            this.playerUIInventory.getCursorInventory().clear(0);

                            for (Item drop : drops) {
                                this.level.dropItem(this, drop);
                            }

                            action = new NetworkInventoryAction();
                            action.windowId = ContainerIds.UI;
                            action.inventorySlot = 0; // cursor
                            action.oldItem = Item.get(Item.AIR);
                            action.newItem = toResult.clone();
                            fixedPacket.actions[3] = action;
                        } else {
                            int emptyPlayerSlot = -1;
                            for (int slot = 0; slot < inventory.getSize(); slot++) {
                                if (inventory.getItemFast(slot).isNull()) {
                                    emptyPlayerSlot = slot;
                                    break;
                                }
                            }
                            if (emptyPlayerSlot == -1) {
                                this.needSendInventory = true;
                                return;
                            } else {
                                action = new NetworkInventoryAction();
                                action.windowId = ContainerIds.INVENTORY;
                                action.inventorySlot = emptyPlayerSlot;
                                action.oldItem = Item.get(Item.AIR);
                                action.newItem = toResult.clone();
                                fixedPacket.actions[3] = action;
                            }
                        }

                        action = new NetworkInventoryAction();
                        action.sourceType = NetworkInventoryAction.SOURCE_TODO;
                        action.windowId = NetworkInventoryAction.SOURCE_TYPE_ANVIL_RESULT;
                        action.inventorySlot = 2; // result
                        action.oldItem = toResult.clone();
                        action.newItem = fromResult.clone();
                        fixedPacket.actions[4] = action;

                        action = new NetworkInventoryAction();
                        action.sourceType = NetworkInventoryAction.SOURCE_TODO;
                        action.windowId = NetworkInventoryAction.SOURCE_TYPE_ANVIL_INPUT;
                        action.inventorySlot = 0; // equipment
                        action.oldItem = toEquipment.clone();
                        action.newItem = fromEquipment.clone();
                        fixedPacket.actions[5] = action;

                        action = new NetworkInventoryAction();
                        action.sourceType = NetworkInventoryAction.SOURCE_TODO;
                        action.windowId = NetworkInventoryAction.SOURCE_TYPE_ANVIL_MATERIAL;
                        action.inventorySlot = 1; // material
                        action.oldItem = toIngredient.clone();
                        action.newItem = fromIngredient.clone();
                        fixedPacket.actions[6] = action;

                        action = new NetworkInventoryAction();
                        action.sourceType = NetworkInventoryAction.SOURCE_TODO;
                        action.windowId = NetworkInventoryAction.SOURCE_TYPE_ANVIL_MATERIAL;
                        action.inventorySlot = 3; // template
                        action.oldItem = toTemplate.clone();
                        action.newItem = fromTemplate.clone();
                        fixedPacket.actions[7] = action;

                        transactionPacket = fixedPacket;
                    }
                }

                List<InventoryAction> actions = new ArrayList<>();
                for (NetworkInventoryAction networkInventoryAction : transactionPacket.actions) {
                    InventoryAction a = networkInventoryAction.createInventoryAction(this);

                    if (a == null) {
                        this.getServer().getLogger().debug("Unmatched inventory action from " + this.username + ": " + networkInventoryAction);
                        this.needSendInventory = true;
                        return;
                    }

                    actions.add(a);
                }

                if (transactionPacket.isCraftingPart) {
                    if (LoomTransaction.isIn(actions)) {
                        if (this.loomTransaction == null) {
                            this.loomTransaction = new LoomTransaction(this, actions);
                        } else {
                            for (InventoryAction action : actions) {
                                this.loomTransaction.addAction(action);
                            }
                        }
                        if (this.loomTransaction.canExecute()) {
                            if (this.loomTransaction.execute()) {
                                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BLOCK_LOOM_USE);
                            }
                            this.loomTransaction = null;
                        }
                        return;
                    }

                    if (this.craftingTransaction == null) {
                        this.craftingTransaction = new CraftingTransaction(this, actions);
                    } else {
                        for (InventoryAction action : actions) {
                            this.craftingTransaction.addAction(action);
                        }
                    }

                    if (this.craftingTransaction.getPrimaryOutput() != null && this.craftingTransaction.canExecute()) {
                        try {
                            this.craftingTransaction.execute();
                        } catch (Exception e) {
                            this.server.getLogger().debug(username + ": executing crafting transaction failed");
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
                    if (SmithingTransaction.isIn(actions)) {
                        if (this.smithingTransaction == null) {
                            this.smithingTransaction = new SmithingTransaction(this, actions);
                        } else {
                            for (InventoryAction action : actions) {
                                this.smithingTransaction.addAction(action);
                            }
                        }
                        if (this.smithingTransaction.canExecute()) {
                            if (this.smithingTransaction.execute()) {
                                Collection<Player> players = level.getChunkPlayers(getChunkX(), getChunkZ()).values();
                                players.remove(this);
                                if (!players.isEmpty()) {
                                    level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BLOCK_SMITHING_TABLE_USE);
                                }
                            }
                            this.smithingTransaction = null;
                        }
                    } else if (GrindstoneTransaction.isIn(actions)) {
                        if (this.grindstoneTransaction == null) {
                            this.grindstoneTransaction = new GrindstoneTransaction(this, actions);
                        } else {
                            for (InventoryAction action : actions) {
                                this.grindstoneTransaction.addAction(action);
                            }
                        }
                        if (this.grindstoneTransaction.canExecute()) {
                            if (this.grindstoneTransaction.execute()) {
                                Collection<Player> players = level.getChunkPlayers(getChunkX(), getChunkZ()).values();
                                players.remove(this);
                                if (!players.isEmpty()) {
                                    level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BLOCK_GRINDSTONE_USE);
                                }
                            }
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
                    if (!handleQuickCraft(transactionPacket, actions, this.craftingTransaction)) this.craftingTransaction = null;
                    return;
                } else if (this.enchantTransaction != null) {
                    if (!handleQuickCraft(transactionPacket, actions, this.enchantTransaction)) this.enchantTransaction = null;
                    return;
                } else if (this.repairItemTransaction != null) {
                    if (!handleQuickCraft(transactionPacket, actions, this.repairItemTransaction)) this.repairItemTransaction = null;
                    return;
                } else if (this.smithingTransaction != null) {
                    if (!handleQuickCraft(transactionPacket, actions, this.smithingTransaction)) this.smithingTransaction = null;
                    return;
                } else if (this.grindstoneTransaction != null) {
                    if (!handleQuickCraft(transactionPacket, actions, this.grindstoneTransaction)) this.grindstoneTransaction = null;
                    return;
                }

                switch (transactionPacket.transactionType) {
                    case InventoryTransactionPacket.TYPE_NORMAL:
                        InventoryTransaction transaction = new InventoryTransaction(this, actions);

                        if (!transaction.execute()) {
                            this.server.getLogger().debug("Failed to execute inventory transaction from " + this.username + " with actions: " + Arrays.toString(transactionPacket.actions));
                            return;
                        }

                        return;
                    case InventoryTransactionPacket.TYPE_MISMATCH:
                        if (transactionPacket.actions.length > 0) {
                            this.server.getLogger().debug("Expected 0 actions for mismatch, got " + transactionPacket.actions.length + ", " + Arrays.toString(transactionPacket.actions));
                        }
                        this.needSendInventory = true;
                        return;
                    case InventoryTransactionPacket.TYPE_USE_ITEM:
                        UseItemData useItemData = (UseItemData) transactionPacket.transactionData;
                        BlockVector3 blockVector = useItemData.blockPos;
                        BlockFace face = useItemData.face;

                        this.setShieldBlockingDelay(5);

                        boolean itemSent = false; // Fix inventory desync but only send the slot once

                        if (inventory.getHeldItemIndex() != useItemData.hotbarSlot) {
                            inventory.equipItem(useItemData.hotbarSlot);

                            itemSent = true; // Assume that the item is still correct even if the selected slot is not
                        }

                        switch (useItemData.actionType) {
                            case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_BLOCK:
                                // Hack: Fix client spamming right clicks
                                if ((lastRightClickPos != null && this.getInventory().getItemInHandFast().getBlockId() == BlockID.AIR && System.currentTimeMillis() - lastRightClickTime < 200.0 && blockVector.distanceSquared(lastRightClickPos) < 0.00001)) {
                                    return;
                                }

                                lastRightClickPos = blockVector;
                                lastRightClickTime = System.currentTimeMillis();

                                this.breakingBlock = null;

                                this.setUsingItem(false);

                                // We don't seem to verify useItemData.clickPos so don't use it for anything important
                                if (this.canInteract(blockVector.add(0.5, 0.5, 0.5), this.isCreative() ? 14 : 8)) {
                                    Item i = inventory.getItemInHand();
                                    if (this.isCreative()) {
                                        if (this.level.useItemOn(blockVector.asVector3(), i, face, useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, this) != null) {
                                            return;
                                        }
                                    } else {
                                        Item oldItem = i.clone(); // This must be cloned

                                        if ((i = this.level.useItemOn(blockVector.asVector3(), i, face, useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, this)) != null) {
                                            if (i.getCount() != oldItem.getCount() || i.getDamage() != oldItem.getDamage() || !i.equals(oldItem)) { // Quick checks first
                                                if (oldItem.getId() == i.getId() || i.getId() == 0) {
                                                    inventory.setItemInHand(i);

                                                    itemSent = true;
                                                } else if (Nukkit.DEBUG > 1) {
                                                    server.getLogger().debug("Tried to set item " + i.getId() + " but " + this.username + " had item " + oldItem.getId() + " in their hand slot");
                                                }
                                            }

                                            if (!itemSent && !oldItem.equals(useItemData.itemInHand)) {
                                                this.needSendHeldItem = true;
                                            }
                                            return;
                                        }
                                    }
                                }

                                this.needSendHeldItem = true;

                                if (blockVector.distanceSquared(this) > 10000) {
                                    return;
                                }

                                Block target = this.level.getBlock(blockVector.asVector3());
                                block = target.getSide(face);

                                this.level.sendBlocks(this, new Block[]{target, block}, UpdateBlockPacket.FLAG_ALL_PRIORITY);

                                if (target instanceof BlockDoor) {
                                    BlockDoor door = (BlockDoor) target;

                                    Block part;

                                    if ((door.getDamage() & 0x08) > 0) {
                                        part = target.down();

                                        if (part.getId() == target.getId()) {
                                            target = part;

                                            this.level.sendBlocks(this, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY);
                                        }
                                    }
                                }
                                return;
                            case InventoryTransactionPacket.USE_ITEM_ACTION_BREAK_BLOCK:
                                return;
                            case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_AIR:
                                if (!this.spawned || !this.isAlive()) {
                                    return;
                                }

                                if (inventory.getHeldItemIndex() != useItemData.hotbarSlot) {
                                    this.inventory.equipItem(useItemData.hotbarSlot);

                                    this.crossbowLoadTick = 0;
                                }

                                item = this.inventory.getItemInHand();

                                this.breakingBlock = null;

                                Vector3 directionVector = this.getDirectionVector();
                                PlayerInteractEvent interactEvent = new PlayerInteractEvent(this, item, directionVector, face, Action.RIGHT_CLICK_AIR);
                                this.server.getPluginManager().callEvent(interactEvent);

                                if (interactEvent.isCancelled()) {
                                    this.needSendHeldItem = true;
                                    return;
                                }

                                if (item instanceof ItemCrossbow) {
                                    ItemCrossbow crossbow = ((ItemCrossbow) item);
                                    if (crossbow.isLoaded()) {
                                        if (this.crossbowLoadTick + 5 < this.server.getTick()) {
                                            crossbow.launchArrow(this);
                                        }
                                    } else {
                                        if (this.isUsingItem()) {
                                            // Used item
                                            int ticksUsed = this.server.getTick() - this.startAction;
                                            this.crossbowLoadTick = this.server.getTick();
                                            this.setUsingItem(false);
                                            item.onUse(this, ticksUsed); // Load crossbow
                                        } else {
                                            this.setUsingItem(true);
                                            this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_CROSSBOW_LOADING_START);
                                        }
                                    }
                                    return;
                                }

                                int oldCount = item.getCount();
                                int oldDamage = item.getDamage();
                                if (item.onClickAir(this, directionVector)) {
                                    if (this.isSurvival() || this.isAdventure()) {
                                        // Don't set the item if not changed
                                        // Update this to use equals() if NBT is ever modified in onClickAir
                                        if (item.getId() == 0 || ((item.getCount() != oldCount || item.getDamage() != oldDamage) && this.inventory.getItemInHandFast().getId() == item.getId())) {
                                            if (item instanceof ItemFishingRod && item.getDamage() >= item.getMaxDurability()) {
                                                this.level.addSound(this, Sound.RANDOM_BREAK);
                                                this.level.addParticle(new ItemBreakParticle(this, item));
                                                item = Item.get(Item.AIR);
                                            }

                                            this.inventory.setItemInHand(item);
                                        }
                                    }

                                    if (this.isUsingItem()) {
                                        // Used item
                                        int ticksUsed = this.server.getTick() - this.startAction;
                                        this.setUsingItem(false);
                                        if (!item.onUse(this, ticksUsed)) {
                                            this.needSendHeldItem = true;
                                        }
                                    } else {
                                        this.setUsingItem(true);
                                    }
                                }

                                return;
                        }
                        return;
                    case InventoryTransactionPacket.TYPE_USE_ITEM_ON_ENTITY:
                        UseItemOnEntityData useItemOnEntityData = (UseItemOnEntityData) transactionPacket.transactionData;

                        Entity target = this.level.getEntity(useItemOnEntityData.entityRuntimeId);
                        if (target == null) {
                            return;
                        }

                        if (inventory.getHeldItemIndex() != useItemOnEntityData.hotbarSlot) {
                            inventory.equipItem(useItemOnEntityData.hotbarSlot);
                        }

                        item = this.inventory.getItemInHand();

                        switch (useItemOnEntityData.actionType) {
                            case InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_INTERACT:
                                if (this.distanceSquared(target) > 256) { // TODO: Note entity scale
                                    this.getServer().getLogger().debug(username + ": target entity is too far away");
                                    return;
                                }

                                this.breakingBlock = null;

                                this.setUsingItem(false);

                                PlayerInteractEntityEvent playerInteractEntityEvent = new PlayerInteractEntityEvent(this, target, item, useItemOnEntityData.clickPos);
                                if (this.isSpectator()) playerInteractEntityEvent.setCancelled();
                                getServer().getPluginManager().callEvent(playerInteractEntityEvent);

                                if (playerInteractEntityEvent.isCancelled()) {
                                    return;
                                }

                                if (target.onInteract(this, item, useItemOnEntityData.clickPos) && (this.isSurvival() || this.isAdventure())) {
                                    if (item.isTool()) {
                                        if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                                            level.addSound(this, Sound.RANDOM_BREAK);
                                            level.addParticle(new ItemBreakParticle(this, item));
                                            item = Item.get(Item.AIR);
                                        }
                                    } else {
                                        if (item.count > 1) {
                                            item.count--;
                                        } else {
                                            item = Item.get(Item.AIR);
                                        }
                                    }

                                    if (item.getId() == 0 || this.inventory.getItemInHandFast().getId() == item.getId()) {
                                        this.inventory.setItemInHand(item);
                                    } else if (Nukkit.DEBUG > 1) {
                                        server.getLogger().debug("Tried to set item " + item.getId() + " but " + this.username + " had item " + this.inventory.getItemInHandFast().getId() + " in their hand slot");
                                    }
                                }
                                return;
                            case InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_ATTACK:
                                if (target.getId() == this.getId()) {
                                    this.kick(PlayerKickEvent.Reason.INVALID_PVP, "Tried to attack invalid player");
                                    return;
                                }

                                if (!this.canInteractEntity(target, isCreative() ? 64 : 25)) { // 8 : 5
                                    return;
                                } else if (target instanceof Player) {
                                    if ((((Player) target).gamemode & 0x01) > 0) {
                                        return;
                                    } else if (!this.server.pvpEnabled) {
                                        return;
                                    }
                                }

                                this.breakingBlock = null;

                                this.setUsingItem(false);

                                if (this.sleeping != null) {
                                    this.getServer().getLogger().debug(username + ": USE_ITEM_ON_ENTITY_ACTION_ATTACK while sleeping");
                                    return;
                                }

                                if (this.inventoryOpen) {
                                    this.getServer().getLogger().debug(username + ": USE_ITEM_ON_ENTITY_ACTION_ATTACK while viewing inventory");
                                    return;
                                }

                                this.setShieldBlockingDelay(5);

                                if (server.attackStopSprint) {
                                    this.setSprinting(false);
                                }

                                Enchantment[] enchantments = item.getEnchantments();

                                float itemDamage = item.getAttackDamage();
                                for (Enchantment enchantment : enchantments) {
                                    itemDamage += enchantment.getDamageBonus(target);
                                }

                                Map<DamageModifier, Float> damage = new EnumMap<>(DamageModifier.class);
                                damage.put(DamageModifier.BASE, itemDamage);

                                float knockBack = 0.3f;
                                Enchantment knockBackEnchantment = item.getEnchantment(Enchantment.ID_KNOCKBACK);
                                if (knockBackEnchantment != null) {
                                    knockBack += knockBackEnchantment.getLevel() * 0.1f;
                                }

                                EntityDamageByEntityEvent entityDamageByEntityEvent = new EntityDamageByEntityEvent(this, target, DamageCause.ENTITY_ATTACK, damage, knockBack, enchantments);
                                entityDamageByEntityEvent.setWeapon(item);

                                if (this.isSpectator()) {
                                    entityDamageByEntityEvent.setCancelled();
                                }
                                if ((target instanceof Player) && !this.level.getGameRules().getBoolean(GameRule.PVP)) {
                                    entityDamageByEntityEvent.setCancelled();
                                }

                                if (!target.attack(entityDamageByEntityEvent)) {
                                    if (item.isTool() && !this.isCreative()) {
                                        this.needSendHeldItem = true;
                                    }
                                    return;
                                }

                                for (Enchantment enchantment : item.getEnchantments()) {
                                    enchantment.doPostAttack(this, target);
                                }

                                if (item.isTool() && !this.isCreative()) {
                                    if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                                        level.addSound(this, Sound.RANDOM_BREAK);
                                        level.addParticle(new ItemBreakParticle(this, item));
                                        this.inventory.clear(this.inventory.getHeldItemIndex(), true);
                                    } else {
                                        if (item.getId() == 0 || this.inventory.getItemInHandFast().getId() == item.getId()) {
                                            this.inventory.setItemInHand(item);
                                        } else if (Nukkit.DEBUG > 1) {
                                            server.getLogger().debug("Tried to set item " + item.getId() + " but " + this.username + " had item " + this.inventory.getItemInHandFast().getId() + " in their hand slot");
                                        }
                                    }
                                }
                                return;
                        }

                        return;
                    case InventoryTransactionPacket.TYPE_RELEASE_ITEM:
                        if (this.isSpectator()) {
                            this.needSendInventory = true;
                            return;
                        }
                        ReleaseItemData releaseItemData = (ReleaseItemData) transactionPacket.transactionData;

                        try {
                            switch (releaseItemData.actionType) {
                                case InventoryTransactionPacket.RELEASE_ITEM_ACTION_RELEASE:
                                    if (this.isUsingItem()) {
                                        int ticksUsed = this.server.getTick() - this.startAction;
                                        if (!this.inventory.getItemInHand().onRelease(this, ticksUsed)) {
                                            this.needSendHeldItem = true;
                                        }
                                        this.setUsingItem(false);
                                    } else {
                                        this.needSendHeldItem = true;
                                    }
                                    return;
                                case InventoryTransactionPacket.RELEASE_ITEM_ACTION_CONSUME:
                                    return;
                                default:
                                    this.getServer().getLogger().debug(username + ": unknown release item action type: " + releaseItemData.actionType);
                            }
                        } finally {
                            this.setUsingItem(false);
                        }
                        return;
                    default:
                        this.needSendHeldItem = true;
                }
                return;
            case ProtocolInfo.PLAYER_HOTBAR_PACKET:
                if (!this.spawned || !this.isAlive()) {
                    return;
                }

                PlayerHotbarPacket hotbarPacket = (PlayerHotbarPacket) packet;

                if (hotbarPacket.windowId != ContainerIds.INVENTORY) {
                    return;
                }

                if (this.inventory == null) {
                    return;
                }

                this.inventory.equipItem(hotbarPacket.selectedHotbarSlot);
                this.setUsingItem(false);
                return;
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
                return;
            case ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET:
                if (this.locallyInitialized) {
                    return;
                }
                this.doFirstSpawn();
                return;
            case ProtocolInfo.RESPAWN_PACKET:
                if (this.isAlive()) {
                    return;
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
                return;
            case ProtocolInfo.BOOK_EDIT_PACKET:
                if (!this.spawned) {
                    return;
                }

                if (this.inventory == null) {
                    return;
                }

                BookEditPacket bookEditPacket = (BookEditPacket) packet;
                Item oldBook = this.inventory.getItem(bookEditPacket.inventorySlot);
                if (oldBook.getId() != Item.BOOK_AND_QUILL) {
                    this.getServer().getLogger().debug(username + ": BookEditPacket for invalid item: expected Book & Quill (386), got " + oldBook.getId());
                    return;
                }

                if (bookEditPacket.text != null && bookEditPacket.text.length() > 256) {
                    this.getServer().getLogger().debug(username + ": BookEditPacket with too long text");
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
                        if (bookEditPacket.title == null || bookEditPacket.author == null || bookEditPacket.xuid == null || bookEditPacket.title.length() > 64 || bookEditPacket.author.length() > 64 || bookEditPacket.xuid.length() > 64) {
                            this.getServer().getLogger().debug(username + ": invalid BookEditPacket action SIGN_BOOK: title/author/xuid is too long");
                            return;
                        }
                        success = ((ItemBookWritten) newBook).signBook(bookEditPacket.title, bookEditPacket.author, bookEditPacket.xuid, ItemBookWritten.GENERATION_ORIGINAL);
                        break;
                    default:
                        this.getServer().getLogger().debug(username + ": BookEditPacket unknown action: " + bookEditPacket.action);
                        return;
                }

                if (success) {
                    PlayerEditBookEvent editBookEvent = new PlayerEditBookEvent(this, oldBook, newBook, bookEditPacket.action);
                    this.server.getPluginManager().callEvent(editBookEvent);
                    if (!editBookEvent.isCancelled()) {
                        this.inventory.setItem(bookEditPacket.inventorySlot, editBookEvent.getNewBook());
                    }
                }
                return;
            case ProtocolInfo.PACKET_VIOLATION_WARNING_PACKET:
                this.getServer().getLogger().warning("Packet violation warning 0x" + Integer.toHexString(((PacketViolationWarningPacket) packet).packetId) + " from " + this.username + ": " + packet);
                return;
            case ProtocolInfo.EMOTE_PACKET:
                if (!this.spawned || server.getTick() - this.lastEmote < 20 || this.isSpectator()) {
                    return;
                }
                this.lastEmote = server.getTick();
                EmotePacket emotePacket = (EmotePacket) packet;
                if (emotePacket.runtimeId != this.id) {
                    this.getServer().getLogger().debug(username + ": EmotePacket eid mismatch");
                    return;
                } else if (emotePacket.emoteID == null || emotePacket.emoteID.isEmpty() || emotePacket.emoteID.length() > 100) {
                    this.getServer().getLogger().debug(username + " EmotePacket invalid emote id: " + emotePacket.emoteID);
                    return;
                }
                EmotePacket cleanEmotePacket = new EmotePacket();
                cleanEmotePacket.runtimeId = emotePacket.runtimeId;
                cleanEmotePacket.emoteID = emotePacket.emoteID;
                Server.broadcastPacket(this.getViewers().values(), cleanEmotePacket);
                return;
            case ProtocolInfo.LECTERN_UPDATE_PACKET:
                if (!this.spawned) {
                    return;
                }

                LecternUpdatePacket lecternUpdatePacket = (LecternUpdatePacket) packet;
                if (lecternUpdatePacket.blockPosition.distanceSquared(this) > 4096) {
                    return;
                }
                if (!lecternUpdatePacket.dropBook) {
                    BlockEntity blockEntityLectern = this.level.getBlockEntityIfLoaded(this.chunk, lecternUpdatePacket.blockPosition.asVector3());
                    if (blockEntityLectern instanceof BlockEntityLectern) {
                        BlockEntityLectern lectern = (BlockEntityLectern) blockEntityLectern;
                        if (lectern.getRawPage() != lecternUpdatePacket.page) {
                            lectern.setRawPage(lecternUpdatePacket.page);
                        }
                    }
                }
                return;
            case ProtocolInfo.SET_DIFFICULTY_PACKET:
                if (!this.spawned) {
                    return;
                }

                if (!this.hasPermission("nukkit.command.difficulty")) {
                    if (!this.isOp()) {
                        this.kick(PlayerKickEvent.Reason.INVALID_PACKET, "Invalid SetDifficultyPacket", true);
                    }
                    return;
                }
                server.setDifficulty(((SetDifficultyPacket) packet).difficulty);
                Command.broadcastCommandMessage(this, new TranslationContainer("commands.difficulty.success", String.valueOf(server.getDifficulty())));

                SetDifficultyPacket difficultyPacket = new SetDifficultyPacket();
                difficultyPacket.difficulty = server.getDifficulty();
                Server.broadcastPacket(server.getOnlinePlayers().values(), difficultyPacket);
                return;
            case ProtocolInfo.REQUEST_PERMISSIONS_PACKET:
                if (!this.spawned) {
                    return;
                }

                if (!this.isOp()) {
                    this.kick(PlayerKickEvent.Reason.INVALID_PACKET, "Invalid RequestPermissionsPacket", true);
                    return;
                }
                this.sendMessage(TextFormat.RED + "Unimplemented feature: REQUEST_PERMISSIONS_PACKET"); // TODO
                return;
            case ProtocolInfo.SET_DEFAULT_GAME_TYPE_PACKET:
                if (!this.spawned) {
                    return;
                }

                if (!this.hasPermission("nukkit.command.defaultgamemode")) {
                    if (!this.isOp()) {
                        this.kick(PlayerKickEvent.Reason.INVALID_PACKET, "Invalid SetDefaultGameTypePacket", true);
                    }
                    return;
                }
                int gamemode = ((SetDefaultGameTypePacket) packet).gamemode & 0b11;
                server.gamemode = gamemode;
                server.setPropertyInt("gamemode", gamemode);
                Command.broadcastCommandMessage(this, new TranslationContainer("commands.defaultgamemode.success", new String[]{Server.getGamemodeString(server.getDefaultGamemode())}));

                SetDefaultGameTypePacket gameTypePacket = new SetDefaultGameTypePacket();
                gameTypePacket.gamemode = server.getDefaultGamemode();
                Server.broadcastPacket(server.getOnlinePlayers().values(), gameTypePacket);
                return;
            case ProtocolInfo.SETTINGS_COMMAND_PACKET:
                if (!this.spawned) {
                    return;
                }

                if (!this.hasPermission("nukkit.command.gamerule")) {
                    if (!this.isOp()) {
                        this.kick(PlayerKickEvent.Reason.INVALID_PACKET, "Invalid SettingsCommandPacket", true);
                    }
                    return;
                }
                String command = ((SettingsCommandPacket) packet).command;
                if (command.startsWith("/gamerule")) {
                    server.dispatchCommand(this, command.substring(1));
                } else {
                    this.getServer().getLogger().debug(username + ": SettingsCommandPacket unsupported command: " + command);
                }
                return;
        }
    }

    private boolean handleQuickCraft(InventoryTransactionPacket packet, List<InventoryAction> actions, InventoryTransaction transaction) {
        if (transaction.checkForItemPart(actions)) {
            for (InventoryAction action : actions) {
                transaction.addAction(action);
            }
            return true;
        } else {
            if (Nukkit.DEBUG > 1) {
                this.server.getLogger().debug(this.username + ": unexpected normal inventory action with incomplete transaction, refusing to execute " + packet);
            }
            this.removeAllWindows(false);
            this.needSendInventory = true;
            return false;
        }
    }

    private void setShieldBlockingDelay(int delay) {
        if (this.isBlocking()) {
            this.setBlocking(false);
            this.blockingDelay = delay;
        }
    }

    @Override
    protected void onBlock(Entity damager, EntityDamageBlockedEvent event, EntityDamageEvent source) {
        super.onBlock(damager, event, source);

        if (source.getWeapon() != null && source.getWeapon().isAxe()) {
            this.setShieldBlockingDelay(100);
            this.startItemCooldown(100, "shield");
        }
    }

    public void startItemCooldown(int cooldownDuration, String itemCategory) {
        PlayerStartItemCooldownPacket pk = new PlayerStartItemCooldownPacket();
        pk.itemCategory = itemCategory;
        pk.cooldownDuration = cooldownDuration;
        this.dataPacket(pk);
    }

    private void onBlockBreakAbort(BlockVector3 blockPos, BlockFace face) {
        if (this.isBreakingBlock()) {
            LevelEventPacket pk = new LevelEventPacket();
            pk.evid = LevelEventPacket.EVENT_BLOCK_STOP_BREAK;
            pk.x = (float) breakingBlock.x;
            pk.y = (float) breakingBlock.y;
            pk.z = (float) breakingBlock.z;
            pk.data = 0;
            this.getLevel().addChunkPacket((int) breakingBlock.x >> 4, (int) breakingBlock.z >> 4, pk);
        }
        this.breakingBlock = null;
    }

    private void onBlockBreakStart(BlockVector3 blockPos, BlockFace face) {
        if (this.isSpectator()) {
            return;
        }

        boolean posEquals = lastBreakPosition.equals(blockPos);
        this.lastBreakPosition = blockPos;
        long currentBreak = System.currentTimeMillis();
        // HACK: Client spams multiple left clicks so we need to skip them.
        if (posEquals && (currentBreak - this.lastBreak) < 10) {
            return;
        } else if (blockPos.distanceSquared(this) > 100) {
            this.breakingBlock = null;
            return;
        }

        // Reset current block break
        this.breakingBlock = null;

        this.setUsingItem(false);

        Item handItem = this.inventory.getItemInHand();
        Block target = this.level.getBlock(chunk, blockPos.x, blockPos.y, blockPos.z, false);
        PlayerInteractEvent playerInteractEvent = new PlayerInteractEvent(this, handItem, target, face, target.getId() == 0 ? Action.LEFT_CLICK_AIR : Action.LEFT_CLICK_BLOCK);
        this.getServer().getPluginManager().callEvent(playerInteractEvent);
        if (playerInteractEvent.isCancelled()) {
            this.needSendHeldItem = true;
            return;
        }

        switch (target.getId()) {
            case Block.AIR:
                return;
            case Block.NOTEBLOCK:
                ((BlockNoteblock) target).emitSound();
                break; // note blocks can be broken
            case Block.DRAGON_EGG:
                if (!this.isCreative()) {
                    ((BlockDragonEgg) target).teleport();
                    return;
                }
                break;
            case Block.ITEM_FRAME_BLOCK:
            case Block.GLOW_FRAME:
                BlockEntity itemFrame = this.level.getBlockEntityIfLoaded(this.chunk, this.temporalVector.setComponents(blockPos.x, blockPos.y, blockPos.z));
                if (itemFrame instanceof BlockEntityItemFrame && ((BlockEntityItemFrame) itemFrame).dropItem(this)) {
                    return;
                }
                break;
            case Block.LECTERN:
                BlockEntity lectern = this.level.getBlockEntityIfLoaded(this.chunk, this.temporalVector.setComponents(blockPos.x, blockPos.y, blockPos.z));
                if (lectern instanceof BlockEntityLectern && ((BlockEntityLectern) lectern).dropBook(this)) {
                    return;
                }
                break;
        }

        int bid = this.level.getBlockIdAt(this.chunk, blockPos.x + face.getXOffset(), blockPos.y + face.getYOffset(), blockPos.z + face.getZOffset());
        if (bid == Block.FIRE || bid == Block.SOUL_FIRE) {
            Vector3 block = this.temporalVector.setComponents(blockPos.x + face.getXOffset(), blockPos.y + face.getYOffset(), blockPos.z + face.getZOffset());
            this.level.setBlock(block, Block.get(BlockID.AIR), true);
            this.level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_EXTINGUISH_FIRE);
            return;
        }

        if (!this.isCreative()) {
            double breakTime = target.getBreakTime(handItem, this);
            int breakTimeTicks = (int) (breakTime * 20 + 0.5);
            if (breakTimeTicks > 0) {
                LevelEventPacket pk = new LevelEventPacket();
                pk.evid = LevelEventPacket.EVENT_BLOCK_START_BREAK;
                pk.x = (float) blockPos.x;
                pk.y = (float) blockPos.y;
                pk.z = (float) blockPos.z;
                pk.data = 65535 / breakTimeTicks;
                this.getLevel().addChunkPacket(blockPos.x >> 4, blockPos.z >> 4, pk);
            }
        }

        this.breakingBlock = target;
        this.breakingBlockFace = face;
        this.lastBreak = currentBreak;
    }

    private void onBlockBreakComplete(BlockVector3 blockPos, BlockFace face) { // From InventoryTransactionPacket.USE_ITEM_ACTION_BREAK_BLOCK
        if (!this.spawned || !this.isAlive()) {
            return;
        }
        this.resetCraftingGridType();
        Item i = this.getInventory().getItemInHand();
        Item oldItem = i.clone();
        if (this.canInteract(blockPos.add(0.5, 0.5, 0.5), this.isCreative() ? 14 : 8) && (i = this.level.useBreakOn(blockPos.asVector3(), face, i, this, true)) != null) {
            if (this.isSurvival() || this.isAdventure()) {
                this.foodData.updateFoodExpLevel(0.005);
                if (i.getCount() != oldItem.getCount() || i.getDamage() != oldItem.getDamage() || !i.equals(oldItem)) {
                    if (i.getId() == 0 || oldItem.getId() == i.getId()) {
                        inventory.setItemInHand(i);

                        // setItem can only send armor to others, I wonder why this isn't needed at other places though
                        inventory.sendHeldItem(this.getViewers().values());
                    } else if (Nukkit.DEBUG > 1) {
                        server.getLogger().debug("Tried to set item " + i.getId() + " but " + this.username + " had item " + oldItem.getId() + " in their hand slot");
                    }
                }
            }
            return;
        }
        this.needSendHeldItem = true;
        if (blockPos.distanceSquared(this) < 10000) {
            Vector3 pos = blockPos.asVector3();
            this.level.sendBlocks(this, new Block[]{this.level.getBlock(pos, false)}, UpdateBlockPacket.FLAG_ALL_PRIORITY);
            BlockEntity blockEntity = this.level.getBlockEntityIfLoaded(this.chunk, pos);
            if (blockEntity instanceof BlockEntitySpawnable) {
                ((BlockEntitySpawnable) blockEntity).spawnTo(this);
            }
        }
    }

    /**
     * Adjust map color to height map
     *
     * @param color block color
     * @param colorLevel color level
     * @return adjusted Color
     */
    private static Color colorizeMapColor(BlockColor color, int colorLevel) {
        int colorDepth;

        if (colorLevel == 2) {
            colorDepth = 255;
        } else if (colorLevel == 1) {
            colorDepth = 220;
        } else if (colorLevel == 0) {
            colorDepth = 180;
        } else {
            throw new IllegalArgumentException("Invalid colorLevel: " + colorLevel);
        }

        int r = color.getRed() * colorDepth / 255;
        int g = color.getGreen() * colorDepth / 255;
        int b = color.getBlue() * colorDepth / 255;

        return new Color(r, g, b);
    }

    /**
     * Sends a chat message as this player
     *
     * @param message message to send
     * @return successful
     */
    public boolean chat(String message) {
        this.resetCraftingGridType();

        if (this.removeFormat) {
            message = TextFormat.clean(message, true);
        }

        for (String msg : message.split("\n")) {
            if (!msg.trim().isEmpty() && msg.length() < 512) {
                PlayerChatEvent chatEvent = new PlayerChatEvent(this, msg);
                this.server.getPluginManager().callEvent(chatEvent);
                if (!chatEvent.isCancelled()) {
                    this.server.broadcastMessage(this.getServer().getLanguage().translateString(chatEvent.getFormat(), new String[]{chatEvent.getPlayer().displayName, chatEvent.getMessage()}), chatEvent.getRecipients());
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

    /**
     * Kick the player
     * @param reason reason
     * @param reasonString reason string
     * @param isAdmin display "kicked" or only reason string
     * @return PlayerKickEvent not cancelled
     */
    public boolean kick(PlayerKickEvent.Reason reason, String reasonString, boolean isAdmin) {
        PlayerKickEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerKickEvent(this, reason, reasonString, this.getLeaveMessage()));
        if (!ev.isCancelled()) {
            String message;
            if (isAdmin) {
                if (!this.isBanned()) {
                    message = "Kicked!" + (!reasonString.isEmpty() ? " Reason: " + reasonString : "");
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

    /**
     * Set view distance
     * @param distance view distance
     */
    public void setViewDistance(int distance) {
        this.viewDistance = distance;
        this.chunkRadius = distance;

        ChunkRadiusUpdatedPacket pk = new ChunkRadiusUpdatedPacket();
        pk.radius = distance;

        this.dataPacket(pk);
    }

    /**
     * Get view distance (client may have updated this within the limits)
     * @return view distance
     */
    public int getViewDistance() {
        return this.chunkRadius;
    }

    /**
     * Get maximum view distance. Use getViewDistance() to get the view distance possibly updated by client.
     * @return view distance
     */
    public int getMaximumViewDistance() {
        return this.viewDistance;
    }

    @Override
    public void sendMessage(String message) {
        this.sendMessage(message, false);
    }

    /**
     * Send a message
     * @param message message
     * @param isLocalized message has a translation
     */
    public void sendMessage(String message, boolean isLocalized) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_RAW;
        pk.message = this.server.getLanguage().translateString(message);
        pk.isLocalized = isLocalized;
        this.dataPacket(pk);
    }

    @Override
    public void sendMessage(TextContainer message) {
        if (message instanceof TranslationContainer) {
            this.sendTranslation(message.getText(), ((TranslationContainer) message).getParameters());
            return;
        }
        this.sendMessage(message.getText(), false);
    }

    public void sendTranslation(String message) {
        this.sendTranslation(message, new String[0]);
    }

    public void sendTranslation(String message, String[] parameters) {
        TextPacket pk = new TextPacket();
        if (this.server.isLanguageForced()) {
            pk.type = TextPacket.TYPE_RAW;
            pk.message = this.server.getLanguage().translateString(message, parameters);
        } else {
            pk.type = TextPacket.TYPE_TRANSLATION;
            pk.message = this.server.getLanguage().translateString(message, parameters, "nukkit.");
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = this.server.getLanguage().translateString(parameters[i], parameters, "nukkit.");
            }
            pk.parameters = parameters;
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
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_POPUP;
        pk.message = message;
        this.dataPacket(pk);
    }

    public void sendPopup(String message, String subtitle) {
        this.sendPopup(message);
    }

    public void sendTip(String message) {
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_TIP;
        pk.message = message;
        this.dataPacket(pk);
    }

    /**
     * Remove currently playing title
     */
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
        // Title won't send if an empty string is used
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

    /**
     * Send toast notification for 1.19+ client
     * @param title toast title
     * @param content toast text
     */
    public void sendToast(String title, String content) {
        ToastRequestPacket pk = new ToastRequestPacket();
        pk.title = title;
        pk.content = content;
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

    /**
     * Close and disconnect the player
     * @param message message
     * @param reason reason
     * @param notify send disconnection screen
     */
    public void close(TextContainer message, String reason, boolean notify) {
        if (this.connected && !this.closed) {
            if (notify && !reason.isEmpty()) {
                DisconnectPacket pk = new DisconnectPacket();
                // New disconnection screen doesn't support colors :(
                pk.message = reason = TextFormat.clean(reason);
                this.forceDataPacket(pk, null);
            }

            this.connected = false;

            // Do all inventory changes before the last save
            this.resetCraftingGridType();
            this.removeAllWindows(true);

            if (this.fishing != null) {
                this.stopFishing(false);
            }

            PlayerQuitEvent ev = null;
            if (this.username != null && !this.username.isEmpty()) {
                this.server.getPluginManager().callEvent(ev = new PlayerQuitEvent(this, message, true, reason));
                if (this.loggedIn && ev.getAutoSave()) {
                    this.save();
                }
            }

            for (Player player : this.server.getOnlinePlayers().values()) {
                if (!player.canSee(this)) {
                    player.showPlayer(this);
                }
            }

            this.hiddenPlayers.clear();

            this.unloadChunks(false);

            super.close();

            this.interfaz.close(this, notify ? reason : "");

            this.server.removeOnlinePlayer(this);

            if (this.loggedIn) {
                this.loggedIn = false;
            }

            if (ev != null && !Objects.equals(this.username, "") && this.spawned && !Objects.equals(ev.getQuitMessage().toString(), "")) {
                this.server.broadcastMessage(ev.getQuitMessage());
            }

            this.server.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_USERS, this);
            this.spawned = false;
            this.server.getLogger().info(this.getServer().getLanguage().translateString("nukkit.player.logOut",
                    TextFormat.AQUA + (this.username == null ? this.unverifiedUsername : this.username) + TextFormat.WHITE,
                    this.getAddress(),
                    String.valueOf(this.getPort()),
                    this.getServer().getLanguage().translateString(reason)));

            this.windows.clear();
            this.hasSpawned.clear();

            if (this.riding instanceof EntityRideable) {
                this.riding.passengers.remove(this);
            }

            this.riding = null;
        }

        if (this.perm != null) {
            this.perm.clearPermissions();
            this.perm = null;
        }

        this.inventory = null;
        this.chunk = null;

        this.server.removePlayer(this);

        if (this.loggedIn) {
            this.server.getLogger().warning("Player is still logged in: " + (this.username == null ? this.unverifiedUsername : this.username));
            this.interfaz.close(this, notify ? reason : "");
            this.server.removeOnlinePlayer(this);
            this.loggedIn = false;
        }

        this.clientMovements.clear();
    }

    /**
     * Save player data to disk
     */
    public void save() {
        this.save(false);
    }

    /**
     * Save player data to disk
     * @param async save asynchronously
     */
    public void save(boolean async) {
        if (this.closed) {
            throw new IllegalStateException("Tried to save closed player");
        }

        if (!this.server.shouldSavePlayerData) {
            return;
        }

        super.saveNBT();

        if (this.level != null) {
            this.namedTag.putString("Level", this.level.getFolderName());
            if (this.spawnPosition != null && this.spawnPosition.getLevel() != null) {
                this.namedTag.putString("SpawnLevel", this.spawnPosition.getLevel().getFolderName());
                this.namedTag.putInt("SpawnX", this.spawnPosition.getFloorX());
                this.namedTag.putInt("SpawnY", this.spawnPosition.getFloorY());
                this.namedTag.putInt("SpawnZ", this.spawnPosition.getFloorZ());
            }

            CompoundTag achievements = new CompoundTag();
            for (String achievement : this.achievements) {
                achievements.putByte(achievement, 1);
            }

            this.namedTag.putCompound("Achievements", achievements);

            this.namedTag.putInt("playerGameType", this.gamemode);
            this.namedTag.putLong("lastPlayed", System.currentTimeMillis() / 1000);

            this.namedTag.putString("lastIP", this.getAddress());

            this.namedTag.putInt("EXP", this.exp);
            this.namedTag.putInt("expLevel", this.expLevel);

            this.namedTag.putInt("foodLevel", this.foodData.getLevel());
            this.namedTag.putFloat("foodSaturationLevel", this.foodData.getFoodSaturationLevel());

            this.namedTag.putInt("TimeSinceRest", this.timeSinceRest);

            if (!this.username.isEmpty() && this.namedTag != null) {
                this.server.saveOfflinePlayerData(this.uuid, this.namedTag, async);
            }
        }
    }

    /**
     * Get player's username
     * @return username
     */
    public String getName() {
        return this.username;
    }

    @Override
    public void kill() {
        if (!this.spawned) {
            return;
        }

        boolean showMessages = this.level.getGameRules().getBoolean(GameRule.SHOW_DEATH_MESSAGES);
        String message = "";
        List<String> params = new ArrayList<>();
        EntityDamageEvent cause = this.getLastDamageCause();

        if (showMessages) {
            params.add(this.displayName);

            switch (cause == null ? DamageCause.CUSTOM : cause.getCause()) {
                case ENTITY_ATTACK:
                case THORNS:
                    if (cause instanceof EntityDamageByEntityEvent) {
                        Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                        killer = e;
                        if (e instanceof Player) {
                            message = "death.attack.player";
                            params.add(((Player) e).displayName);
                            break;
                        } else if (e instanceof EntityLiving) {
                            message = "death.attack.mob";
                            params.add(!Objects.equals(e.getNameTag(), "") ? e.getNameTag() : e.getName());
                            break;
                        } else {
                            message = "death.attack.generic";
                        }
                    } else {
                        message = "death.attack.generic";
                    }
                    break;
                case PROJECTILE:
                    if (cause instanceof EntityDamageByEntityEvent) {
                        Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                        killer = e;
                        if (e instanceof Player) {
                            message = "death.attack.arrow";
                            params.add(((Player) e).displayName);
                        } else if (e instanceof EntityLiving) {
                            message = "death.attack.arrow";
                            params.add(!Objects.equals(e.getNameTag(), "") ? e.getNameTag() : e.getName());
                            break;
                        } else {
                            message = "death.attack.generic";
                        }
                    } else {
                        message = "death.attack.generic";
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
                    message = "death.attack.lava";
                    break;

                case MAGMA:
                    message = "death.attack.magma";
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
                        int id = ((EntityDamageByBlockEvent) cause).getDamager().getId();
                        if (id == BlockID.CACTUS) {
                            message = "death.attack.cactus";
                        } else if (id == BlockID.ANVIL) {
                            message = "death.attack.anvil";
                        } else if (id == BlockID.SWEET_BERRY_BUSH) {
                            message = "death.attack.sweetBerry";
                        } else if (id == BlockID.POWDER_SNOW) {
                            message = "death.attack.freeze";
                        } else {
                            message = "death.attack.generic";
                        }
                    } else {
                        message = "death.attack.generic";
                    }
                    break;

                case BLOCK_EXPLOSION:
                case ENTITY_EXPLOSION:
                    if (cause instanceof EntityDamageByEntityEvent) {
                        Entity e = ((EntityDamageByEntityEvent) cause).getDamager();
                        killer = e;
                        if (e instanceof Player) {
                            message = "death.attack.explosion.player";
                            params.add(((Player) e).displayName);
                        } else if (e instanceof EntityLiving) {
                            message = "death.attack.explosion.player";
                            params.add(!Objects.equals(e.getNameTag(), "") ? e.getNameTag() : e.getName());
                            break;
                        } else if (e instanceof EntityFirework && cause.getEntity() instanceof Player) {
                            params.add(((Player) cause.getEntity()).displayName);
                            message = "death.attack.fireworks";
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

        this.resetCraftingGridType(); // This must be called before getDrops() for UI inventories to be handled properly

        PlayerDeathEvent ev = new PlayerDeathEvent(this, this.getDrops(), new TranslationContainer(message, params.toArray(new String[0])), this.expLevel);
        ev.setKeepInventory(this.level.gameRules.getBoolean(GameRule.KEEP_INVENTORY));
        ev.setKeepExperience(ev.getKeepInventory()); // Same as above
        this.server.getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            if (this.fishing != null) {
                this.stopFishing(false);
            }

            this.health = 0;
            this.scheduleUpdate();

            //this.resetCraftingGridType();

            if (!ev.getKeepInventory() && this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
                for (Item item : ev.getDrops()) {
                    if (!item.hasEnchantment(Enchantment.ID_VANISHING_CURSE)) {
                        this.level.dropItem(this, item, null, true, 40);
                    }
                }

                if (this.inventory != null) {
                    this.inventory.clearAll();
                }

                // Offhand inventory is already cleared in inventory.clearAll()
                // UI inventories are handled in resetCraftingGridType()
            }

            if (!ev.getKeepExperience() && this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
                if (this.isSurvival() || this.isAdventure()) {
                    int exp = ev.getExperience() * 7;
                    if (exp > 100) exp = 100;
                    this.getLevel().dropExpOrb(this, exp);
                }
                this.setExperience(0, 0);
            }

            if (level.getGameRules().getBoolean(GameRule.DO_IMMEDIATE_RESPAWN)) {
                this.respawn();
            } else {
                if (showMessages && !ev.getDeathMessage().toString().isEmpty()) {
                    this.server.broadcast(ev.getDeathMessage(), Server.BROADCAST_CHANNEL_USERS);

                    DeathInfoPacket pk = new DeathInfoPacket();
                    if (ev.getDeathMessage() instanceof TranslationContainer) {
                        pk.messageTranslationKey = this.server.getLanguage().translateString(ev.getDeathMessage().getText(), ((TranslationContainer) ev.getDeathMessage()).getParameters(), null);
                    } else {
                        pk.messageTranslationKey = ev.getDeathMessage().getText();
                    }
                    this.dataPacket(pk);
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
    }

    protected void respawn() {
        if (this.server.isHardcore()) {
            this.setBanned(true);
            return;
        }

        this.resetCraftingGridType();

        PlayerRespawnEvent playerRespawnEvent = new PlayerRespawnEvent(this, this.getSpawn());
        this.server.getPluginManager().callEvent(playerRespawnEvent);

        Position respawnPos = playerRespawnEvent.getRespawnPosition();

        this.teleport(respawnPos, null);

        this.sendBothExperience(this.exp, this.expLevel);

        this.setSprinting(false, false);
        this.setSneaking(false);
        this.setSwimming(false);
        this.setGliding(false);
        this.setCrawling(false);

        this.extinguish();
        this.setDataProperty(new ShortEntityData(Player.DATA_AIR, 400), false);
        this.airTicks = 400;
        this.deadTicks = 0;
        this.noDamageTicks = 60;
        this.timeSinceRest = 0;

        this.removeAllEffects(EntityPotionEffectEvent.Cause.DEATH);
        this.setHealth(this.getMaxHealth());
        this.foodData.setLevel(20, 20);

        this.sendData(this);

        this.setMovementSpeed(DEFAULT_SPEED);

        this.adventureSettings.update();
        this.inventory.sendContents(this);
        this.inventory.sendArmorContents(this);
        this.offhandInventory.sendContents(this);

        this.spawnToAll();
        this.scheduleUpdate();

        if (this.spawnPosition instanceof BlockRespawnAnchor && this.spawnPosition.level.getProvider() != null) {
            Block anchor = this.spawnPosition.level.getBlock(this.spawnPosition);
            if (anchor instanceof BlockRespawnAnchor) {
                int chargeLevel = anchor.getDamage();

                if (chargeLevel > 0) {
                    anchor.setDamage(chargeLevel - 1);
                    anchor.level.setBlock(anchor, anchor);

                    anchor.level.addLevelSoundEvent(anchor, LevelSoundEventPacket.SOUND_RESPAWN_ANCHOR_DEPLETE);
                }

                if (chargeLevel <= 1) {
                    this.setSpawn(server.getDefaultLevel().getSafeSpawn());
                }

            } else {
                this.setSpawn(server.getDefaultLevel().getSafeSpawn());
            }
        }
    }

    @Override
    public void setHealth(float health) {
        if (health < 1) {
            health = 0;
        }

        super.setHealth(health);

        // HACK: solve the client-side absorption bug
        if (this.spawned) {
            UpdateAttributesPacket pk = new UpdateAttributesPacket();
            int max = this.getMaxHealth();
            pk.entries = new Attribute[]{Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(max).setValue(this.health > 0 ? (this.health < max ? this.health : max) : 0)};
            pk.entityId = this.id;
            this.dataPacket(pk);
        }
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        super.setMaxHealth(maxHealth);

        if (this.spawned) {
            UpdateAttributesPacket pk = new UpdateAttributesPacket();
            int max = this.getMaxHealth();
            pk.entries = new Attribute[]{Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(max).setValue(this.health > 0 ? (this.health < max ? this.health : max) : 0)};
            pk.entityId = this.id;
            this.dataPacket(pk);
        }
    }

    /**
     * Get experience
     * @return experience (non-full levels)
     */
    public int getExperience() {
        return this.exp;
    }

    /**
     * Get experience level
     * @return experience level
     */
    public int getExperienceLevel() {
        return this.expLevel;
    }

    /**
     * Give the player more experience
     * @param add experience to add
     */
    public void addExperience(int add) {
        if (add == 0) return;
        int added = this.exp + add;
        int level = this.expLevel;
        int most = calculateRequireExperience(level);
        while (added >= most) {
            added -= most;
            level++;
            most = calculateRequireExperience(level);
        }
        this.setExperience(added, level);
    }

    /**
     * Calculate experience required for the level
     * @param level level
     * @return required experience
     */
    public static int calculateRequireExperience(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else if (level >= 15) {
            return 37 + (level - 15) * 5;
        } else {
            return 7 + (level << 1);
        }
    }

    /**
     * Set player's experience
     * @param exp experience (non-full levels)
     */
    public void setExperience(int exp) {
        setExperience(exp, this.expLevel);
    }

    /**
     * Set player's experience and experience level
     * @param exp experience (non-full levels)
     * @param level experience level
     */
    public void setExperience(int exp, int level) {
        PlayerExperienceChangeEvent ev = new PlayerExperienceChangeEvent(this, this.exp, this.expLevel, exp, level);
        this.server.getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return;
        }

        this.exp = ev.getNewExperience();
        this.expLevel = ev.getNewExperienceLevel();

        this.sendBothExperience(this.exp, this.expLevel);
    }

    /**
     * Send experience (non-full levels)
     */
    public void sendExperience() {
        sendExperience(this.exp);
    }

    /**
     * Send experience (non-full levels)
     * @param exp experience
     */
    public void sendExperience(int exp) {
        if (this.spawned) {
            this.setAttribute(Attribute.getAttribute(Attribute.EXPERIENCE).setValue(Math.max(0f, Math.min(1f, ((float) exp) / calculateRequireExperience(this.expLevel)))));
        }
    }

    /**
     * Send experience level
     */
    public void sendExperienceLevel() {
        sendExperienceLevel(this.expLevel);
    }

    /**
     * Send experience level
     * @param level experience level
     */
    public void sendExperienceLevel(int level) {
        if (this.spawned) {
            this.setAttribute(Attribute.getAttribute(Attribute.EXPERIENCE_LEVEL).setValue(level));
        }
    }

    /**
     * Send both player's experience and experience level in one packet
     * @param exp experience (non-full levels)
     * @param level experience level
     */
    private void sendBothExperience(int exp, int level) {
        if (this.spawned) {
            UpdateAttributesPacket pk = new UpdateAttributesPacket();
            pk.entries = new Attribute[]{Attribute.getAttribute(Attribute.EXPERIENCE_LEVEL).setValue(level), Attribute.getAttribute(Attribute.EXPERIENCE).setValue(Math.max(0f, Math.min(1f, ((float) exp) / calculateRequireExperience(this.expLevel))))};
            pk.entityId = this.id;
            this.dataPacket(pk);
        }
    }

    /**
     * Send updated attribute
     * @param attribute attribute
     */
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

    /**
     * Set player's movement speed
     * @param speed speed
     * @param send send updated speed to player
     */
    public void setMovementSpeed(float speed, boolean send) {
        if (speed < 0) { // Apparently effects can break this?
            server.getLogger().debug("Invalid setMovementSpeed: " + speed);
            return;
        }
        super.setMovementSpeed(speed);
        if (this.spawned && send) {
            this.setAttribute(Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue(speed).setDefaultValue(speed));
        }
    }

    /**
     * Send movement speed attribute
     * @param speed speed
     */
    public void sendMovementSpeed(float speed) {
        Attribute attribute = Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue(speed);
        this.setAttribute(attribute);
    }

    /**
     * Get the entity which killed the player
     * @return entity which killed the player or null
     */
    public Entity getKiller() {
        return killer;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (!spawned || closed || !this.isAlive()) {
            return false;
        }

        if (this.isSpectator() || (this.isCreative() && source.getCause() != DamageCause.SUICIDE)) {
            source.setCancelled();
            return false;
        } else if (source.getCause() == DamageCause.FALL && this.getAllowFlight()) {
            source.setCancelled();
            return false;
        }

        if (super.attack(source)) {
            if (this.getLastDamageCause() == source && this.spawned) {
                if (source instanceof EntityDamageByEntityEvent) {
                    Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
                    if (damager instanceof Player) {
                        ((Player) damager).foodData.updateFoodExpLevel(0.1);
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
            this.server.getLogger().debug(this.username + " attempted to drop a null item (" + item + ')');
            return true;
        }

        this.setUsingItem(false);

        Vector3 motion = this.getDirectionVector().multiply(0.4);
        EntityItem entityItem = this.level.dropAndGetItem(this.add(0, 1.3, 0), item, motion, 40);
        if (entityItem != null) {
            entityItem.droppedBy = this;
        }
        return true;
    }

    /**
     * Drops an item on the ground in front of the player. Returns the dropped item.
     *
     * @param item to drop
     * @return EntityItem if the item was dropped or null if the item was null
     */
    public EntityItem dropAndGetItem(Item item) {
        if (!this.spawned || !this.isAlive()) {
            return null;
        }

        if (item.isNull()) {
            this.server.getLogger().debug(this.getName() + " attempted to drop a null item (" + item + ')');
            return null;
        }

        this.setUsingItem(false);

        Vector3 motion = this.getDirectionVector().multiply(0.4);
        EntityItem entityItem = this.level.dropAndGetItem(this.add(0, 1.3, 0), item, motion, 40);
        if (entityItem != null) {
            entityItem.droppedBy = this;
        }
        return entityItem;
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

    /**
     * Send player's position and rotation
     * @param pos position
     * @param yaw yaw
     * @param pitch pitch
     * @param mode movement mode
     * @param targets receivers
     */
    public void sendPosition(Vector3 pos, double yaw, double pitch, int mode, Player[] targets) {
        MovePlayerPacket pk = new MovePlayerPacket();
        pk.eid = this.getId();
        pk.x = (float) pos.x;
        pk.y = (float) (pos.y + this.getBaseOffset());
        pk.z = (float) pos.z;
        pk.headYaw = (float) yaw;
        pk.pitch = (float) pitch;
        pk.yaw = (float) yaw;
        pk.mode = mode;
        pk.onGround = this.onGround;

        if (this.riding != null) {
            pk.ridingEid = this.riding.getId();
            pk.mode = MovePlayerPacket.MODE_PITCH;
        }

        this.ySize = 0;

        if (targets != null) {
            Server.broadcastPacket(targets, pk);
        } else {
            this.clientMovements.clear();

            this.dataPacket(pk);
        }
    }

    /**
     * Internal: Broadcast player movement to viewers
     * @param x x
     * @param y y
     * @param z z
     * @param yaw yaw
     * @param pitch pitch
     * @param headYaw headYaw
     */
    private void sendPositionToViewers(double x, double y, double z, double yaw, double pitch, double headYaw) {
        MovePlayerPacket pk = new MovePlayerPacket();
        pk.eid = this.getId();
        pk.x = (float) x;
        pk.y = (float) (y + this.getBaseOffset());
        pk.z = (float) z;
        pk.headYaw = (float) headYaw;
        pk.pitch = (float) pitch;
        pk.yaw = (float) yaw;
        pk.mode = MovePlayerPacket.MODE_NORMAL;
        pk.onGround = this.onGround;

        if (this.riding != null) {
            pk.ridingEid = this.riding.getId();
            pk.mode = MovePlayerPacket.MODE_PITCH;
        }

        this.ySize = 0;

        Server.broadcastPacket(this.getViewers().values(), pk);
    }

    @Override
    protected void checkChunks() {
        if (this.chunk == null || (this.chunk.getX() != this.getChunkX() || this.chunk.getZ() != this.getChunkZ())) {
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }
            this.chunk = this.level.getChunk(this.getChunkX(), this.getChunkZ(), true);

            if (!this.justCreated) {
                Map<Integer, Player> newChunk = this.level.getChunkPlayers(this.getChunkX(), this.getChunkZ());
                newChunk.remove(this.loaderId);

                for (Player player : new ArrayList<>(this.hasSpawned.values())) {
                    if (!newChunk.containsKey(player.loaderId)) {
                        this.despawnFrom(player);
                    } else {
                        newChunk.remove(player.loaderId);
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
        return checkTeleportPosition(false);
    }

    protected boolean checkTeleportPosition(boolean enderPearl) {
        if (this.teleportPosition != null) {
            int chunkX = this.teleportPosition.getChunkX();
            int chunkZ = this.teleportPosition.getChunkZ();

            for (int X = -1; X <= 1; ++X) {
                for (int Z = -1; Z <= 1; ++Z) {
                    long index = Level.chunkHash(chunkX + X, chunkZ + Z);
                    if (!this.usedChunks.containsKey(index) || !this.usedChunks.get(index)) {
                        return false;
                    }
                }
            }

            this.spawnToAll();
            if (!enderPearl) {
                this.forceMovement = this.teleportPosition;
            }
            this.teleportPosition = null;
            return true;
        }

        return false;
    }

    protected void sendPlayStatus(int status) {
        PlayStatusPacket pk = new PlayStatusPacket();
        pk.status = status;
        this.dataPacket(pk);
    }

    @Override
    public boolean teleport(Location location, TeleportCause cause) {
        if (!this.isOnline()) {
            return false;
        }

        Location to = location;

        if (cause != null) {
            Location from = this.getLocation();
            PlayerTeleportEvent event = new PlayerTeleportEvent(this, from, to, cause);
            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
            to = event.getTo();
        }

        // HACK: solve the client-side teleporting bug (inside into the block)
        if (super.teleport(to.getY() == to.getFloorY() ? to.add(0, 0.00001, 0) : to, null)) { // null to prevent fire of duplicate EntityTeleportEvent
            this.removeAllWindows();
            this.formOpen = false;

            this.teleportPosition = this;
            if (cause != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                this.forceMovement = this.teleportPosition;
            }

            if (this.dimensionChangeInProgress) {
                this.dimensionChangeInProgress = false;
            } else {
                this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_TELEPORT);
                this.checkTeleportPosition(cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
                this.dummyBossBars.values().forEach(DummyBossBar::reshow);
            }

            this.resetFallDistance();
            this.nextChunkOrderRun = 0;
            this.resetClientMovement();

            this.stopFishing(false);
            return true;
        }

        return false;
    }

    /**
     * deprecated: use teleport() with null cause instead
     */
    @Deprecated
    public void teleportImmediate(Location location) {
        this.teleportImmediate(location, TeleportCause.PLUGIN);
    }

    /**
     * deprecated: use teleport() with null cause instead
     */
    @Deprecated
    public void teleportImmediate(Location location, TeleportCause cause) {
        this.teleport(location, null);
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
        if (formOpen) return -1;
        ModalFormRequestPacket packet = new ModalFormRequestPacket();
        packet.formId = id;
        packet.data = window.getJSONData();
        this.formWindows.put(packet.formId, window);
        this.dataPacket(packet);
        this.formOpen = true;
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
        return this.createBossBar(new DummyBossBar.Builder(this).text(text).length(length).build());
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
     * @see DummyBossBar#setColor(BossBarColor) Set BossBar color
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

    /**
     * Get window id of an open Inventory
     * @param inventory inventory
     * @return id of the inventory window or -1 if player doesn't have the window open
     */
    public int getWindowId(Inventory inventory) {
        if (this.windows.containsKey(inventory)) {
            return this.windows.get(inventory);
        }

        return -1;
    }

    /**
     * Get on open inventory by window id
     * @param id window id
     * @return inventory (if open) or null
     */
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
            return cnt;
        } else if (!alwaysOpen) {
            this.removeWindow(inventory);

            return -1;
        } else {
            inventory.getViewers().add(this);
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

    protected void removeWindow(Inventory inventory, boolean isResponse) {
        inventory.close(this);
        if (/*isResponse &&*/ !this.permanentWindows.contains(this.getWindowId(inventory))) { // Possible dupe
            this.windows.remove(inventory);
        }
    }

    /**
     * Send contents of all open inventories to the player
     */
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
        this.addWindow(this.craftingGrid, ContainerIds.NONE);
    }

    /**
     * Get player's ui inventory
     * @return ui inventory
     */
    public PlayerUIInventory getUIInventory() {
        return playerUIInventory;
    }

    /**
     * Get player's cursor inventory
     * @return cursor inventory
     */
    public PlayerCursorInventory getCursorInventory() {
        return this.playerUIInventory.getCursorInventory();
    }

    /**
     * Get player's crafting grid
     * @return crafting grid
     */
    public CraftingGrid getCraftingGrid() {
        return this.craftingGrid;
    }

    /**
     * Set player's crafting grid
     * @param grid crafting grid
     */
    public void setCraftingGrid(CraftingGrid grid) {
        this.craftingGrid = grid;
        this.addWindow(grid, ContainerIds.NONE);
    }

    /**
     * Resets crafting grid type and moves all UI inventory contents back to player inventory or drops them.
     */
    public void resetCraftingGridType() {
        if (this.playerUIInventory != null) {
            Item[] drops;

            if (this.craftingGrid != null) {
                drops = this.inventory.addItem(this.craftingGrid.getContents().values().toArray(new Item[0]));
                this.craftingGrid.clearAll();

                for (Item drop : drops) {
                    this.level.dropItem(this, drop);
                }
            }

            drops = this.inventory.addItem(this.playerUIInventory.getCursorInventory().getItemFast(0)); // cloned in addItem
            this.playerUIInventory.getCursorInventory().clear(0);

            for (Item drop : drops) {
                this.level.dropItem(this, drop);
            }

            // Don't trust the client to handle this
            this.moveBlockUIContents(Player.ANVIL_WINDOW_ID); // LOOM_WINDOW_ID is the same as ANVIL_WINDOW_ID?
            this.moveBlockUIContents(Player.ENCHANT_WINDOW_ID);
            this.moveBlockUIContents(Player.BEACON_WINDOW_ID);
            this.moveBlockUIContents(Player.SMITHING_WINDOW_ID);
            this.playerUIInventory.clearAll();

            if (this.craftingGrid instanceof BigCraftingGrid && this.connected) {
                this.craftingGrid = this.playerUIInventory.getCraftingGrid();
                this.addWindow(this.craftingGrid, ContainerIds.NONE);
            }
        }

        this.craftingType = CRAFTING_SMALL;
    }

    /**
     * Move all block UI contents back to player inventory or drop them
     * @param window window id
     */
    private void moveBlockUIContents(int window) {
        Inventory inventory = this.getWindowById(window);
        if (inventory instanceof FakeBlockUIComponent) {
            Item[] drops = this.inventory.addItem(inventory.getContents().values().toArray(new Item[0]));
            inventory.clearAll();
            for (Item drop : drops) {
                this.level.dropItem(this, drop);
            }
        }
    }

    /**
     * Remove all windows
     */
    public void removeAllWindows() {
        removeAllWindows(false);
    }

    /**
     * Remove all windows
     * @param permanent remove permanent windows
     */
    public void removeAllWindows(boolean permanent) {
        for (Entry<Integer, Inventory> entry : new ArrayList<>(this.windowIndex.entrySet())) {
            if (!permanent && this.permanentWindows.contains(entry.getKey())) {
                continue;
            }

            this.removeWindow(entry.getValue());
        }
    }

    /**
     * Get id of the window client has requested to be closed
     * @return window id or Integer.MIN_VALUE if no window is being closed
     */
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

    /* Note: Update Level useChunkLoaderApi checks if more ChunkLoader API is ever used here */

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
        return this.connected;
    }

    /**
     * Get chunk cache from data
     * @param chunkX chunk x
     * @param chunkZ chunk z
     * @param subChunkCount sub chunk count
     * @param payload data
     * @return BatchPacket
     */
    public static BatchPacket getChunkCacheFromData(int chunkX, int chunkZ, int subChunkCount, byte[] payload, int dimension) {
        LevelChunkPacket pk = new LevelChunkPacket();
        pk.chunkX = chunkX;
        pk.chunkZ = chunkZ;
        pk.dimension = dimension;
        pk.subChunkCount = subChunkCount;
        pk.data = payload;
        pk.tryEncode();

        byte[] buf = pk.getBuffer();
        BinaryStream batched = new BinaryStream(new byte[5 + buf.length]).reset();
        batched.putUnsignedVarInt(buf.length);
        batched.put(buf);
        try {
            byte[] bytes = batched.getBuffer();
            BatchPacket compress = new BatchPacket();
            if (Server.getInstance().useSnappy) {
                compress.payload = SnappyCompression.compress(bytes);
            } else {
                compress.payload = Zlib.deflateRaw(bytes, Server.getInstance().networkCompressionLevel);
            }
            return compress;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Check whether food is enabled or not
     * @return food enabled
     */
    public boolean isFoodEnabled() {
        return !(this.isCreative() || this.isSpectator()) && this.foodEnabled;
    }

    /**
     * Enable or disable food
     * @param foodEnabled food enabled
     */
    public void setFoodEnabled(boolean foodEnabled) {
        this.foodEnabled = foodEnabled;
    }

    /**
     * Get player's food data
     * @return food data
     */
    public PlayerFood getFoodData() {
        return this.foodData;
    }

    /**
     * Send dimension change
     * @param dimension dimension id
     */
    public void setDimension(int dimension) {
        if (!this.loggedIn) {
            return; // Do not send ChangeDimensionPacket before StartGamePacket
        }

        this.dimensionChangeInProgress = true;
        this.awaitingDimensionAck = true;

        ChangeDimensionPacket changeDimensionPacket = new ChangeDimensionPacket();
        changeDimensionPacket.dimension = dimension;
        changeDimensionPacket.x = (float) this.x;
        changeDimensionPacket.y = (float) this.y;
        changeDimensionPacket.z = (float) this.z;
        changeDimensionPacket.respawn = !this.isAlive();
        this.dataPacket(changeDimensionPacket);

        NetworkChunkPublisherUpdatePacket chunkPublisherUpdatePacket = new NetworkChunkPublisherUpdatePacket();
        chunkPublisherUpdatePacket.position = this.asBlockVector3();
        chunkPublisherUpdatePacket.radius = this.chunkRadius << 4;
        this.dataPacket(chunkPublisherUpdatePacket);

        this.dimensionFix560 = true;
    }

    @Override
    protected void preSwitchLevel() {
        this.unloadChunks(true);
    }

    @Override
    protected void afterSwitchLevel() {
        // Send spawn to update compass position
        SetSpawnPositionPacket spawnPosition = new SetSpawnPositionPacket();
        spawnPosition.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
        Vector3 spawn = level.getProvider().getSpawn();
        spawnPosition.x = spawn.getFloorX();
        spawnPosition.y = spawn.getFloorY();
        spawnPosition.z = spawn.getFloorZ();
        spawnPosition.dimension = level.getDimension();
        this.dataPacket(spawnPosition);

        // Update time and weather
        level.sendTime(this);
        level.sendWeather(this);

        // Update game rules
        GameRulesChangedPacket packet = new GameRulesChangedPacket();
        packet.gameRulesMap = level.getGameRules().getGameRules();
        this.dataPacket(packet);
    }

    /**
     * Enable or disable movement check
     * @param checkMovement movement check enabled
     */
    public void setCheckMovement(boolean checkMovement) {
        this.checkMovement = checkMovement;
    }

    /**
     * @return player movement checks enabled
     */
    public boolean isCheckingMovement() {
        return this.checkMovement;
    }

    /**
     * Set locale
     * @param locale locale
     */
    public synchronized void setLocale(Locale locale) {
        this.locale.set(locale);
    }

    /**
     * Get locale
     * @return locale
     */
    public synchronized Locale getLocale() {
        return this.locale.get();
    }

    @Override
    public void setSprinting(boolean value) {
        this.setSprinting(value, true);
    }

    /**
     * Update movement speed to start/stop sprinting
     * @param value sprinting
     * @param send send updated speed to client
     */
    public void setSprinting(boolean value, boolean send) {
        if (isSprinting() != value) {
            super.setSprinting(value);
            this.setMovementSpeed(value ? getMovementSpeed() * 1.3f : getMovementSpeed() / 1.3f, send);
        }
    }

    /**
     * Transfer player to other server
     * @param address target server address
     */
    public void transfer(InetSocketAddress address) {
        transfer(address.getAddress().getHostAddress(), address.getPort());
    }

    /**
     * Transfer player to other server
     * @param hostName target server address
     * @param port target server port
     */
    public void transfer(String hostName, int port) {
        TransferPacket pk = new TransferPacket();
        pk.address = hostName;
        pk.port = port;
        this.dataPacket(pk);
    }

    /**
     * Get player's LoginChainData
     * @return login chain data
     */
    public LoginChainData getLoginChainData() {
        return this.loginChainData;
    }

    /**
     * Try to pick up an entity
     * @param entity target
     * @param near near
     * @return success
     */
    public boolean pickupEntity(Entity entity, boolean near) {
        if (!this.spawned || !this.isAlive() || !this.isOnline() || this.isSpectator() || entity.isClosed()) {
            return false;
        }

        if (near) {
            if (entity instanceof EntityArrow && ((EntityArrow) entity).hadCollision) {
                EntityArrow a = ((EntityArrow) entity);
                ItemArrow item = (ItemArrow) Item.get(Item.ARROW, a.getData());
                if (!this.isCreative() && !this.inventory.canAddItem(item)) {
                    return false;
                }

                InventoryPickupArrowEvent ev = new InventoryPickupArrowEvent(this.inventory, a);

                int pickupMode = a.getPickupMode();
                if (pickupMode == EntityArrow.PICKUP_NONE_REMOVE || pickupMode == EntityArrow.PICKUP_NONE || (pickupMode == EntityArrow.PICKUP_CREATIVE && !this.isCreative())) {
                    ev.setCancelled();
                }

                this.server.getPluginManager().callEvent(ev);

                if (pickupMode == EntityArrow.PICKUP_NONE_REMOVE) {
                    entity.close();
                }

                if (ev.isCancelled()) {
                    return false;
                }

                TakeItemEntityPacket pk = new TakeItemEntityPacket();
                pk.entityId = this.getId();
                pk.target = entity.getId();
                Server.broadcastPacket(entity.getViewers().values(), pk);
                this.dataPacket(pk);

                if (!this.isCreative()) {
                    this.inventory.addItem(item);
                }
                entity.close();
                return true;
            } else if (entity instanceof EntityThrownTrident) {
                if (!((EntityThrownTrident) entity).hadCollision) {
                    if (entity.noClip) {
                        if (!this.equals(((EntityProjectile) entity).shootingEntity)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }

                if (!((EntityThrownTrident) entity).shotByPlayer()) {
                    return false;
                }

                Item item = ((EntityThrownTrident) entity).getItem();

                if (((EntityProjectile) entity).shootingEntity != null && item.hasEnchantment(Enchantment.ID_TRIDENT_LOYALTY) && !this.equals(((EntityProjectile) entity).shootingEntity)) {
                    return false;
                }

                if (!this.isCreative() && !this.inventory.canAddItem(item)) {
                    return false;
                }

                InventoryPickupTridentEvent ev = new InventoryPickupTridentEvent(this.inventory, (EntityThrownTrident) entity);

                int pickupMode = ((EntityThrownTrident) entity).getPickupMode();
                if (pickupMode == EntityArrow.PICKUP_NONE_REMOVE || pickupMode == EntityThrownTrident.PICKUP_NONE || (pickupMode == EntityThrownTrident.PICKUP_CREATIVE && !this.isCreative())) {
                    ev.setCancelled();
                }

                this.server.getPluginManager().callEvent(ev);

                if (pickupMode == EntityArrow.PICKUP_NONE_REMOVE) {
                    entity.close();
                }

                if (ev.isCancelled()) {
                    return false;
                }

                TakeItemEntityPacket pk = new TakeItemEntityPacket();
                pk.entityId = this.getId();
                pk.target = entity.getId();
                Server.broadcastPacket(entity.getViewers().values(), pk);
                this.dataPacket(pk);

                if (!this.isCreative()) {
                    int favSlot = ((EntityThrownTrident) entity).getFavoredSlot();
                    if (favSlot != -1 && !this.isCreative() && inventory.getItemFast(favSlot).getId() == Item.AIR) {
                        this.inventory.setItem(favSlot, item.clone());
                    } else {
                        this.inventory.addItem(item); // cloned in addItem
                    }
                }

                entity.close();
                return true;
            } else if (entity instanceof EntityItem) {
                EntityItem entityItem = (EntityItem) entity;
                if (entityItem.getPickupDelay() <= 0) {
                    Item item = entityItem.getItem();

                    if (item != null) {
                        if (!this.isCreative() && !this.inventory.canAddItem(item)) {
                            return false;
                        }

                        InventoryPickupItemEvent ev;
                        this.server.getPluginManager().callEvent(ev = new InventoryPickupItemEvent(this.inventory, entityItem));
                        if (ev.isCancelled()) {
                            return false;
                        }

                        switch (item.getId()) {
                            case Item.WOOD:
                            case Item.WOOD2:
                                this.awardAchievement("mineWood");
                                break;
                            case Item.DIAMOND:
                                this.awardAchievement("diamonds");
                                if (entityItem.droppedBy != null && entityItem.droppedBy != this) {
                                    entityItem.droppedBy.awardAchievement("diamondsToYou");
                                }
                                break;
                            case Item.LEATHER:
                                this.awardAchievement("killCow");
                                break;
                            case Item.BLAZE_ROD:
                                this.awardAchievement("blazeRod");
                                break;
                        }

                        TakeItemEntityPacket pk = new TakeItemEntityPacket();
                        pk.entityId = this.getId();
                        pk.target = entity.getId();
                        Server.broadcastPacket(entity.getViewers().values(), pk);
                        this.dataPacket(pk);

                        this.inventory.addItem(item); // cloned in addItem
                        entity.close();
                        return true;
                    }
                }
            }
        }

        if (this.pickedXPOrb < this.server.getTick() && entity instanceof EntityXPOrb) {
            EntityXPOrb xpOrb = (EntityXPOrb) entity;
            if (xpOrb.getPickupDelay() <= 0 && this.boundingBox.isVectorInside(entity)) {
                int exp = xpOrb.getExp();
                entity.close();
                this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_EXPERIENCE_ORB);
                this.pickedXPOrb = this.server.getTick();

                IntArrayList itemsWithMending = new IntArrayList();
                for (int i = 0; i < 4; i++) {
                    Item item = inventory.getArmorItem(i);
                    if (item.getDamage() != 0 && item.hasEnchantment(Enchantment.ID_MENDING)) {
                        itemsWithMending.add(this.inventory.getSize() + i);
                    }
                }

                Item hand = inventory.getItemInHandFast();
                if (hand.getDamage() != 0 && hand.hasEnchantment(Enchantment.ID_MENDING)) {
                    itemsWithMending.add(this.inventory.getHeldItemIndex());
                }

                Item offhand = this.getOffhandInventory().getItem(0);
                if (offhand.getId() == Item.SHIELD && offhand.getDamage() != 0 && offhand.hasEnchantment(Enchantment.ID_MENDING)) {
                    itemsWithMending.add(-1);
                }

                if (!itemsWithMending.isEmpty()) {
                    int itemToRepair = itemsWithMending.getInt(ThreadLocalRandom.current().nextInt(itemsWithMending.size()));
                    boolean isOffhand = itemToRepair == -1;

                    Item repaired = isOffhand ? offhand : this.inventory.getItem(itemToRepair);
                    if (repaired instanceof ItemDurable) {
                        if (repaired.getDamage() > 0) {
                            int dmg = repaired.getDamage() - (exp << 1); // repair 2 points per xp
                            if (dmg < 0) {
                                dmg = 0;
                            }

                            repaired.setDamage(dmg);

                            if (isOffhand) {
                                this.getOffhandInventory().setItem(0, repaired);
                            } else {
                                this.inventory.setItem(itemToRepair, repaired);
                            }
                            return true;
                        }
                    }
                }

                this.addExperience(exp);
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
     * Check if the player is currently breaking a block
     *
     * @return is breaking a block
     */
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

    /**
     * Start fishing
     * @param fishingRod fishing rod item
     */
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
        double f = 1.1;
        EntityFishingHook fishingHook = (EntityFishingHook) Entity.createEntity(EntityFishingHook.NETWORK_ID, chunk, nbt, this);
        fishingHook.setMotion(new Vector3(-Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f, -Math.sin(Math.toRadians(pitch)) * f * f,
                Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f));
        ProjectileLaunchEvent ev = new ProjectileLaunchEvent(fishingHook);
        this.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            fishingHook.close();
        } else {
            this.fishing = fishingHook;
            fishingHook.rod = fishingRod;
            fishingHook.checkLure();
            fishingHook.spawnToAll();
            this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_THROW, -1, "minecraft:player", false, false);
        }
    }

    /**
     * Stop fishing
     * @param click clicked or forced
     */
    public void stopFishing(boolean click) {
        if (this.fishing != null && click) {
            fishing.reelLine();
        } else if (this.fishing != null) {
            this.fishing.close();
        }

        this.fishing = null;
    }

    @Override
    public boolean doesTriggerPressurePlate() {
        return this.gamemode != SPECTATOR;
    }

    /**
     * Get ticks since sleeping in the current world last time
     *
     * @return ticks since sleeping
     */
    public int getTimeSinceRest() {
        return timeSinceRest;
    }

    /**
     * Set ticks since sleeping in the current world last time
     *
     * @param ticks ticks since sleeping
     */
    public void setTimeSinceRest(int ticks) {
        this.timeSinceRest = ticks;
    }

    @Override
    public String toString() {
        return "Player(name='" + getName() + "', location=" + super.toString() + ')';
    }

    @Override
    public void setAirTicks(int ticks) {
        if (this.airTicks != ticks) {
            if (this.spawned || ticks > this.airTicks) { // Don't consume air before spawned
                this.airTicks = ticks;
                this.setDataPropertyAndSendOnlyToSelf(new ShortEntityData(DATA_AIR, ticks));
            }
        }
    }

    /**
     * Send current held item to client
     */
    private void syncHeldItem() {
        InventorySlotPacket pk = new InventorySlotPacket();
        pk.slot = this.inventory.getHeldItemIndex();
        pk.item = this.inventory.getItem(pk.slot);
        pk.inventoryId = ContainerIds.INVENTORY;
        this.dataPacket(pk);
    }

    /**
     * Run every tick to send updated data if needed
     */
    void resetPacketCounters() {
        if (this.needSendAdventureSettings) {
            this.needSendAdventureSettings = false;
            this.adventureSettings.update(false);
        }
        if (this.needSendData) {
            this.needSendData = false;
            this.sendData(this); // Send data only once even if multiple actions fail
        }
        if (this.needSendFoodLevel) {
            this.needSendFoodLevel = false;
            this.foodData.sendFoodLevel();
        }
        if (this.needSendInventory && this.spawned) {
            this.needSendInventory = false;
            this.getCursorInventory().sendContents(this);
            this.sendAllInventories();
        }
        if (this.needSendHeldItem && this.spawned) {
            this.needSendHeldItem = false;
            this.syncHeldItem();
        }
    }

    /**
     * Check whether player can eat (difficulty, gamemode, current food level)
     *
     * @param update send current food level to client
     * @return can eat
     */
    public boolean canEat(boolean update) {
        if (this.foodData.getLevel() < this.foodData.getMaxLevel() || this.isCreative() || this.server.getDifficulty() == 0) {
            return true;
        }
        if (update) {
            this.needSendFoodLevel = true;
        }
        return false;
    }

    /**
     * Get Player's NetworkPlayerSession
     *
     * @return network session
     */
    public NetworkPlayerSession getNetworkSession() {
        return this.networkSession;
    }

    @Override
    public final boolean canSaveToStorage() {
        return false;
    }

    /**
     * Whether interactions should be handled as if player is sneaking
     */
    public boolean sneakToBlockInteract() {
        return this.isSneaking() || this.flySneaking;
    }

    /**
     * Show or hide hud elements for the player
     * @param visible whether the listed elements will be visible
     * @param elements elements
     */
    public void setHudElementVisibility(boolean visible, HudElement... elements) {
        SetHudPacket pk = new SetHudPacket();
        pk.elements.addAll(Arrays.asList(elements));
        pk.visible = visible;
        this.dataPacket(pk);
    }

    @Override
    public void setGliding(boolean value) {
        this.fireworkBoostTicks = 0;

        super.setGliding(value);
    }

    /**
     * Close form windows sent with showFormWindow
     */
    public void closeFormWindows() {
        this.formWindows.clear();
        this.dataPacket(new ClientboundCloseFormPacket());
    }
}
