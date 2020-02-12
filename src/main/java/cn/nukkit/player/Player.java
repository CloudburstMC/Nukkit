package cn.nukkit.player;

import cn.nukkit.Achievement;
import cn.nukkit.AdventureSettings;
import cn.nukkit.AdventureSettings.Type;
import cn.nukkit.PlayerFood;
import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.blockentity.BlockEntityLectern;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.entity.data.EntityData;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.entity.impl.EntityLiving;
import cn.nukkit.entity.impl.Human;
import cn.nukkit.entity.impl.projectile.EntityArrow;
import cn.nukkit.entity.impl.vehicle.EntityAbstractMinecart;
import cn.nukkit.entity.impl.vehicle.EntityBoat;
import cn.nukkit.entity.misc.DroppedItem;
import cn.nukkit.entity.misc.XpOrb;
import cn.nukkit.entity.projectile.Arrow;
import cn.nukkit.entity.projectile.FishingHook;
import cn.nukkit.entity.projectile.ThrownTrident;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.event.block.LecternPageChangeEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityDamageEvent.DamageModifier;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.inventory.InventoryPickupArrowEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.event.player.PlayerAsyncPreLoginEvent.LoginResult;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.inventory.*;
import cn.nukkit.inventory.transaction.CraftingTransaction;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.data.ReleaseItemData;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.inventory.transaction.data.UseItemOnEntityData;
import cn.nukkit.item.*;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.*;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.gamerule.GameRules;
import cn.nukkit.level.particle.PunchBlockParticle;
import cn.nukkit.math.*;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import cn.nukkit.pack.Pack;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.player.manager.PlayerChunkManager;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.*;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.entity.data.EntityData.INTERACTIVE_TAG;
import static cn.nukkit.entity.data.EntityData.PLAYER_BED_POSITION;
import static cn.nukkit.entity.data.EntityFlag.ACTION;
import static cn.nukkit.player.PlayerFlag.SLEEP;

/**
 * @author MagicDroidX &amp; Box
 * Nukkit Project
 */
@Log4j2
public class Player extends Human implements CommandSender, InventoryHolder, ChunkLoader, IPlayer {

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

    public static final float DEFAULT_SPEED = 0.1f;
    public static final float MAXIMUM_SPEED = 0.5f;

    public static final int PERMISSION_CUSTOM = 3;
    public static final int PERMISSION_OPERATOR = 2;
    public static final int PERMISSION_MEMBER = 1;
    public static final int PERMISSION_VISITOR = 0;

    public static final int ANVIL_WINDOW_ID = 2;
    public static final int ENCHANT_WINDOW_ID = 3;
    public static final int BEACON_WINDOW_ID = 4;

    protected final SourceInterface interfaz;

    public boolean playedBefore;
    public boolean spawned = false;
    public boolean loggedIn = false;
    public int gamemode;
    public long lastBreak;
    public Vector3f speed = null;

    protected int windowCnt = 4;

    protected final BiMap<Inventory, Integer> windows = HashBiMap.create();

    protected final BiMap<Integer, Inventory> windowIndex = windows.inverse();
    protected final Set<Integer> permanentWindows = new IntOpenHashSet();

    protected int messageCounter = 2;

    private String clientSecret;
    protected Vector3f forceMovement = null;

    public final HashSet<String> achievements = new HashSet<>();

    public int craftingType = CRAFTING_SMALL;

    protected PlayerUIInventory playerUIInventory;
    protected CraftingGrid craftingGrid;
    protected CraftingTransaction craftingTransaction;

    public long creationTime = 0;

    protected long randomClientId;
    protected Vector3f teleportPosition = null;
    protected BlockPosition sleeping = null;

    protected boolean connected = true;
    protected final InetSocketAddress socketAddress;
    protected boolean removeFormat = true;

    protected String username;
    protected String iusername;
    protected String displayName;

    protected int startAction = -1;
    protected Vector3f newPosition = null;
    protected Long clientID = null;

    private int loaderId;

    protected float stepHeight = 0.6f;

    protected final Map<UUID, Player> hiddenPlayers = new HashMap<>();
    protected Vector3i lastRightClickPos = null;

    protected int viewDistance;
    protected final int chunksPerTick;
    protected final int spawnThreshold;

    protected Position spawnPosition = null;

    protected int inAirTicks = 0;
    protected int startAirTicks = 5;

    protected AdventureSettings adventureSettings;

    protected boolean checkMovement = true;

    private final Queue<DataPacket> outboundQueue = new ConcurrentLinkedQueue<>();
    private final Queue<DataPacket> inboundQueue = new ConcurrentLinkedQueue<>();

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

    public FishingHook fishing = null;

    private final PlayerChunkManager chunkManager = new PlayerChunkManager(this);

    public int packetsRecieved;

    public long lastSkinChange;

    protected double lastRightClickTime = 0.0;
    private Vector3i lastBreakPosition = new Vector3i();

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

    public Player(SourceInterface interfaz, Long clientID, InetSocketAddress socketAddress) {
        super(EntityTypes.PLAYER, null, new CompoundTag());
        this.interfaz = interfaz;
        this.perm = new PermissibleBase(this);
        this.server = Server.getInstance();
        this.lastBreak = -1;
        this.socketAddress = socketAddress;
        this.clientID = clientID;
        this.chunksPerTick = this.server.getConfig("chunk-sending.per-tick", 4);
        this.spawnThreshold = this.server.getConfig("chunk-sending.spawn-threshold", 56);
        this.spawnPosition = null;
        this.gamemode = this.server.getGamemode();
        this.setLevel(this.server.getDefaultLevel());
        this.viewDistance = this.server.getViewDistance();
        //this.newPosition = new Vector3(0, 0, 0);
        this.boundingBox = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);
        this.lastSkinChange = -1;

        this.identity = null;

        this.creationTime = System.currentTimeMillis();
    }

    public boolean canSee(Player player) {
        return !this.hiddenPlayers.containsKey(player.getServerId());
    }

    public void hidePlayer(Player player) {
        if (this == player) {
            return;
        }
        this.hiddenPlayers.put(player.getServerId(), player);
        player.despawnFrom(this);
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

    public void showPlayer(Player player) {
        if (this == player) {
            return;
        }
        this.hiddenPlayers.remove(player.getServerId());
        if (player.isOnline()) {
            player.spawnTo(this);
        }
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
            this.server.updatePlayerListData(this.getServerId(), this.getUniqueId(), this.getDisplayName(), this.getSkin(), this.getLoginChainData().getXUID());
        }
    }

    @Override
    public void setSkin(Skin skin) {
        super.setSkin(skin);
        if (this.spawned) {
            this.server.updatePlayerListData(this.getServerId(), this.getUniqueId(), this.getDisplayName(), skin, this.getLoginChainData().getXUID());
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
        return this.getFlag(ACTION) && this.startAction > -1;
    }

    public void setUsingItem(boolean value) {
        this.startAction = value ? this.server.getTick() : -1;
        this.setFlag(ACTION, value);
    }

    public String getButtonText() {
        return this.buttonText;
    }

    public void setButtonText(String text) {
        this.buttonText = text;
        this.setStringData(INTERACTIVE_TAG, this.buttonText);
    }

    public Position getSpawn() {
        if (this.spawnPosition != null && this.spawnPosition.getLevel() != null) {
            return this.spawnPosition;
        } else {
            return this.server.getDefaultLevel().getSafeSpawn();
        }
    }

    private static boolean hasSubstantiallyMoved(Vector3f oldPos, Vector3f newPos) {
        return oldPos.getChunkX() != newPos.getChunkX() || oldPos.getChunkZ() != newPos.getChunkZ();
    }

    private static int distance(int centerX, int centerZ, int x, int z) {
        int dx = centerX - x;
        int dz = centerZ - z;
        return dx * dx + dz * dz;
    }

    @Deprecated
    public int dataPacket(DataPacket packet, boolean needACK) {
        return this.dataPacket(packet) ? 0 : -1;
    }

    protected void doFirstSpawn() {
        this.spawned = true;

        this.setEnableClientCommand(true);

        this.getAdventureSettings().update();

        this.sendPotionEffects(this);
        this.sendData(this);
        this.inventory.sendContents(this);
        this.inventory.sendArmorContents(this);

        SetTimePacket setTimePacket = new SetTimePacket();
        setTimePacket.time = this.level.getTime();
        this.dataPacket(setTimePacket);

        Position pos = this.level.getSafeSpawn(this);

        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(this, pos, true);

        this.server.getPluginManager().callEvent(respawnEvent);

        pos = respawnEvent.getRespawnPosition();

        if (this.getHealth() <= 0) {
            pos = this.getSpawn();

            RespawnPacket respawnPacket = new RespawnPacket();
            respawnPacket.x = (float) pos.x;
            respawnPacket.y = (float) pos.y;
            respawnPacket.z = (float) pos.z;
            this.dataPacket(respawnPacket);
        }

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

        if (this.gamemode == Player.SPECTATOR) {
            InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
            inventoryContentPacket.inventoryId = ContainerIds.CREATIVE;
            this.dataPacket(inventoryContentPacket);
        } else {
            inventory.sendCreativeContents();
        }

        this.getChunkManager().getLoadedChunks().forEach((LongConsumer) chunkKey -> {
            int chunkX = Chunk.fromKeyX(chunkKey);
            int chunkZ = Chunk.fromKeyZ(chunkKey);
            for (Entity entity : this.level.getLoadedChunkEntities(chunkX, chunkZ)) {
                if (this != entity && !entity.isClosed() && entity.isAlive()) {
                    entity.spawnTo(this);
                }
            }
        });

        int experience = this.getExperience();
        if (experience != 0) {
            this.sendExperience(experience);
        }

        int level = this.getExperienceLevel();
        if (level != 0) {
            this.sendExperienceLevel(this.getExperienceLevel());
        }

        this.teleport(pos, null); // Prevent PlayerTeleportEvent during player spawn

        if (!this.isSpectator()) {
            this.spawnToAll();
        }

        //todo Updater

        //Weather
        if (this.level.isRaining() || this.level.isThundering()) {
            this.getLevel().sendWeather(this);
        }
        this.getLevel().sendWeather(this);

        //FoodLevel
        PlayerFood food = this.getFoodData();
        if (food.getLevel() != food.getMaxLevel()) {
            food.sendFoodLevel();
        }
    }

    @Deprecated
    public int directDataPacket(DataPacket packet, boolean needACK) {
        return this.directDataPacket(packet) ? 0 : -1;
    }

    public int getPing() {
        return this.interfaz.getNetworkLatency(this);
    }

    public boolean sleepOn(BlockPosition pos) {
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

        this.setPosData(PLAYER_BED_POSITION, pos.asVector3i());
        this.setPlayerFlag(SLEEP, true);

        this.level.sleepTicks = 60;

        return true;
    }

    public void setSpawn(Vector3f pos) {
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
            this.setPosData(PLAYER_BED_POSITION, new Vector3i());
            this.setPlayerFlag(SLEEP, false);


            this.level.sleepTicks = 0;

            AnimatePacket pk = new AnimatePacket();
            pk.entityRuntimeId = this.getRuntimeId();
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

    /**
     * 0 is true
     * -1 is false
     * other is identifer
     *
     * @param packet packet to send
     * @return packet successfully sent
     */
    public boolean dataPacket(DataPacket packet) {
        if (!this.connected) {
            return false;
        }

        if (packet.pid() == ProtocolInfo.BATCH_PACKET) {
            return this.directDataPacket(packet);
        }

        DataPacketSendEvent event = new DataPacketSendEvent(this, packet);
        this.server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Outbound {}: {}", this.getName(), packet);
        }

        try (Timing ignored = Timings.getSendDataPacketTiming(packet).startTiming()) {
            this.outboundQueue.offer(packet);
        }
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
        if (!this.isCreative()) {
            return super.getDrops();
        }

        return new Item[0];
    }

    /**
     * 0 is true
     * -1 is false
     * other is identifer
     *
     * @param packet packet to send
     * @return packet successfully sent
     */
    public boolean directDataPacket(DataPacket packet) {
        if (!this.connected) {
            return false;
        }

        if (packet.pid() != ProtocolInfo.BATCH_PACKET) {
            DataPacketSendEvent event = new DataPacketSendEvent(this, packet);
            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }
        }

        try (Timing ignored = Timings.getSendDataPacketTiming(packet).startTiming()) {
            this.interfaz.putPacket(this, packet, false, true);
        }
        return true;
    }

    @Override
    protected void checkBlockCollision() {
        boolean portal = false;

        for (Block block : this.getCollisionBlocks()) {
            if (block.getId() == BlockIds.PORTAL) {
                portal = true;
                continue;
            }

            block.onEntityCollide(this);
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
            level.scheduleEntityUpdate(entity);

            if (!entity.isAlive() || !this.isAlive()) {
                continue;
            }

            this.pickupEntity(entity, true);
        }
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
            this.teleport(temporalVector.get().setComponents(this.x, this.y + 0.1, this.z));

            InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
            inventoryContentPacket.inventoryId = InventoryContentPacket.SPECIAL_CREATIVE;
            this.dataPacket(inventoryContentPacket);
        } else {
            if (this.isSurvival()) {
                this.getAdventureSettings().set(Type.FLYING, false);
            }
            InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
            inventoryContentPacket.inventoryId = InventoryContentPacket.SPECIAL_CREATIVE;
            inventoryContentPacket.slots = Item.getCreativeItems().toArray(new Item[0]);
            this.dataPacket(inventoryContentPacket);
        }

        this.resetFallDistance();

        this.inventory.sendContents(this);
        this.inventory.sendContents(this.getViewers());
        this.inventory.sendHeldItem(this.hasSpawned);

        this.inventory.sendCreativeContents();
        return true;
    }

    @Override
    public void addMovement(double x, double y, double z, double yaw, double pitch, double headYaw) {
        this.sendPosition(new Vector3f(x, y, z), yaw, pitch, MovePlayerPacket.MODE_NORMAL, this.getViewers().toArray(new Player[0]));
    }

    @Override
    public boolean setMotion(Vector3f motion) {
        if (super.setMotion(motion)) {
            if (this.chunk != null) {
                this.addMotion(this.motionX, this.motionY, this.motionZ);  //Send to others
                SetEntityMotionPacket pk = new SetEntityMotionPacket();
                pk.entityRuntimeId = this.getRuntimeId();
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
        pk.entityRuntimeId = this.getUniqueId();
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
                        Block block = this.level.getBlock(x, y, z);

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

    protected void processMovement(int tickDiff) {
        if (!this.isAlive() || !this.spawned || this.newPosition == null || this.teleportPosition != null || this.isSleeping()) {
            return;
        }

        Vector3f newPosition = this.newPosition;
        double distanceSquared = newPosition.distanceSquared(this);

        boolean revert = false;
        if ((distanceSquared / ((double) (tickDiff * tickDiff))) > 100 && (newPosition.y - this.y) > -5) {
            log.debug("{} moved too fast!", this.getName());
            revert = true;
        } else {
            if (this.chunk == null) {
                Chunk chunk = this.level.getLoadedChunk(newPosition.getChunkX(), newPosition.getChunkZ());
                if (chunk == null) {
                    revert = true;
                } else {
                    this.chunk = chunk;
                }
            }
        }

        double tdx = newPosition.x - this.x;
        double tdz = newPosition.z - this.z;
        double distance = Math.sqrt(tdx * tdx + tdz * tdz);

        if (!revert && distanceSquared != 0) {
            double dx = newPosition.x - this.x;
            double dy = newPosition.y - this.y;
            double dz = newPosition.z - this.z;

            this.fastMove(dx, dy, dz);
            if (this.newPosition == null) {
                return; //maybe solve that in better way
            }

            double diffX = this.x - newPosition.x;
            double diffY = this.y - newPosition.y;
            double diffZ = this.z - newPosition.z;

            double yS = 0.5 + this.ySize;
            if (diffY >= -yS || diffY <= yS) {
                diffY = 0;
            }

            if (diffX != 0 || diffY != 0 || diffZ != 0) {
                if (this.checkMovement && !server.getAllowFlight() && (this.isSurvival() || this.isAdventure())) {
                    // Some say: I cant move my head when riding because the server
                    // blocked my movement
                    if (!this.isSleeping() && this.vehicle == null && !this.hasEffect(Effect.LEVITATION)) {
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


                this.x = newPosition.x;
                this.y = newPosition.y;
                this.z = newPosition.z;
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

            if (this.speed == null) speed = new Vector3f(from.x - to.x, from.y - to.y, from.z - to.z);
            else this.speed.setComponents(from.x - to.x, from.y - to.y, from.z - to.z);
        } else {
            if (this.speed == null) speed = new Vector3f(0, 0, 0);
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

        if (revert) {

            this.lastX = from.x;
            this.lastY = from.y;
            this.lastZ = from.z;

            this.lastYaw = from.yaw;
            this.lastPitch = from.pitch;

            // We have to send slightly above otherwise the player will fall into the ground.
            Location location = from.add(0, 0.00001, 0);
            this.sendPosition(location, from.yaw, from.pitch, MovePlayerPacket.MODE_RESET);
            //this.sendSettings();
            this.forceMovement = location;
        } else {
            this.forceMovement = null;
        }

        this.newPosition = null;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        this.packetsRecieved = 0;

        if (!this.loggedIn) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0) {
            return true;
        }

        this.messageCounter = 2;

        this.lastUpdate = currentTick;

        try (Timing ignored = this.timing.startTiming()) {
            if (this.fishing != null) {
                if (this.distance(fishing.getPosition()) > 80) {
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
                this.getChunkManager().queueNewChunks();
                this.processMovement(tickDiff);

                if (!this.isSpectator()) {
                    this.checkNearEntities();
                }

                this.entityBaseTick(tickDiff);

                if (this.getServer().getDifficulty() == 0 && this.level.getGameRules().get(GameRules.NATURAL_REGENERATION)) {
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
                        if (this.getLevel().canBlockSeeSky(this.asVector3i())) {
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
                        if (this.checkMovement && !this.isGliding() && !server.getAllowFlight() && !this.getAdventureSettings().get(Type.ALLOW_FLIGHT) && this.inAirTicks > 20 && !this.isSleeping() && !this.isImmobile() && !this.isSwimming() && this.vehicle == null && !this.hasEffect(Effect.LEVITATION)) {
                            double expectedVelocity = (-this.getGravity()) / ((double) this.getDrag()) - ((-this.getGravity()) / ((double) this.getDrag())) * Math.exp(-((double) this.getDrag()) * ((double) (this.inAirTicks - this.startAirTicks)));
                            double diff = (this.speed.y - expectedVelocity) * (this.speed.y - expectedVelocity);

                            Identifier block = level.getBlock(this.asBlockPosition()).getId();
                            boolean ignore = block == BlockIds.LADDER || block == BlockIds.VINE || block == BlockIds.WEB;

                            if (!this.hasEffect(Effect.JUMP) && diff > 0.6 && expectedVelocity < this.speed.y && !ignore) {
                                if (this.inAirTicks < 100) {
                                    this.setMotion(new Vector3f(0, expectedVelocity, 0));
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
            }

            this.checkTeleportPosition();
            this.checkInteractNearby();

            if (this.spawned && this.dummyBossBars.size() > 0 && currentTick % 100 == 0) {
                this.dummyBossBars.values().forEach(DummyBossBar::updateBossEntityPosition);
            }

            this.updateData();
        }

        return true;
    }

    /**
     * Returns the Entity the player is looking at currently
     *
     * @param maxDistance the maximum distance to check for entities
     * @return Entity|null    either NULL if no entity is found or an instance of the entity
     */
    public EntityInteractable getEntityPlayerLookingAt(int maxDistance) {
        try (Timing timing = Timings.playerEntityLookingAtTimer.startTiming()) {
            EntityInteractable entity = null;

            Set<Entity> nearbyEntities = level.getNearbyEntities(boundingBox.grow(maxDistance, maxDistance, maxDistance), this);

            // get all blocks in looking direction until the max interact distance is reached (it's possible that startblock isn't found!)

            Position position = getPosition();
            position.y += getEyeHeight();
            for (Vector3i pos : BlockRayTrace.of(position, getDirectionVector(), maxDistance)) {
                Block block = this.level.getLoadedBlock(pos.x, pos.y, pos.z);
                if (block == null) {
                    break;
                }

                entity = getEntityAtPosition(nearbyEntities, block.getX(), block.getY(), block.getZ());

                if (entity != null) {
                    break;
                }
            }
            return entity;
        }
    }

    public boolean canInteract(Vector3f pos, double maxDistance) {
        return this.canInteract(pos, maxDistance, 6.0);
    }

    public boolean canInteract(Vector3f pos, double maxDistance, double maxDiff) {
        if (this.distanceSquared(pos) > maxDistance * maxDistance) {
            return false;
        }

        Vector2f dV = this.getDirectionPlane();
        double dot = dV.dot(new Vector2f(this.x, this.z));
        double dot1 = dV.dot(new Vector2f(pos.x, pos.z));
        return (dot1 - dot) >= -maxDiff;
    }

    private EntityInteractable getEntityAtPosition(Set<Entity> nearbyEntities, int x, int y, int z) {
        try (Timing ignored = Timings.playerEntityAtPositionTimer.startTiming()) {
            for (Entity nearestEntity : nearbyEntities) {
                Vector3f position = nearestEntity.getPosition();
                if (position.getFloorX() == x && position.getFloorY() == y && position.getFloorZ() == z
                        && nearestEntity instanceof EntityInteractable
                        && ((EntityInteractable) nearestEntity).canDoInteraction()) {
                    return (EntityInteractable) nearestEntity;
                }
            }
            return null;
        }
    }

    protected void completeLoginSequence() {
        PlayerLoginEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerLoginEvent(this, "Plugin reason"));
        if (ev.isCancelled()) {
            this.close(this.getLeaveMessage(), ev.getKickMessage());
            return;
        }

        StartGamePacket startGamePacket = new StartGamePacket();
        startGamePacket.entityUniqueId = this.getUniqueId();
        startGamePacket.entityRuntimeId = this.getRuntimeId();
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
        startGamePacket.spawnX = (int) this.x;
        startGamePacket.spawnY = (int) this.y;
        startGamePacket.spawnZ = (int) this.z;
        startGamePacket.hasAchievementsDisabled = true;
        startGamePacket.dayCycleStopTime = -1;
        startGamePacket.rainLevel = 0;
        startGamePacket.lightningLevel = 0;
        startGamePacket.commandsEnabled = this.isEnableClientCommand();
        startGamePacket.gameRules = getLevel().getGameRules();
        startGamePacket.levelId = "";
        startGamePacket.worldName = this.getServer().getNetwork().getName();
        startGamePacket.generator = 1; //0 old, 1 infinite, 2 flat
        this.dataPacket(startGamePacket);

        this.dataPacket(new BiomeDefinitionListPacket());
        this.dataPacket(new AvailableEntityIdentifiersPacket());
        this.dataPacket(new UpdateBlockPropertiesPacket());

        this.loggedIn = true;

        this.level.sendTime(this);

        this.setMovementSpeed(DEFAULT_SPEED);
        this.sendAttributes();
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setCanClimb(true);

        log.info(this.getServer().getLanguage().translateString("nukkit.player.logIn",
                TextFormat.AQUA + this.username + TextFormat.WHITE,
                this.getAddress(),
                String.valueOf(this.getPort()),
                String.valueOf(this.getUniqueId()),
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

    public void checkNetwork() {
        if (!this.isConnected()) {
            return;
        }

        try (Timing ignore = Timings.playerNetworkReceiveTimer.startTiming()) {
            DataPacket packet;
            while ((packet = this.inboundQueue.poll()) != null) {
                this.onDataPacket(packet);
            }
        }

        try (Timing ignore = Timings.playerNetworkSendTimer.startTiming()) {
            if (!this.outboundQueue.isEmpty()) {
                Player[] players = new Player[]{this};
                List<DataPacket> packets = new ArrayList<>(this.outboundQueue.size());

                DataPacket packet;
                while ((packet = this.outboundQueue.poll()) != null) {
                    packets.add(packet);
                }

                this.server.batchPackets(players, packets.toArray(new DataPacket[0]), false);
            }
        }

        if (!this.loggedIn) {
            return;
        }

        this.getChunkManager().sendQueued();

        if (this.getChunkManager().getChunksSent() >= this.spawnThreshold && !this.spawned && this.teleportPosition == null) {
            this.doFirstSpawn();
        }
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

        Player oldPlayer = null;
        for (Player p : new ArrayList<>(this.server.getOnlinePlayers().values())) {
            if (p != this && p.getName() != null && p.getName().equalsIgnoreCase(this.getName()) ||
                    this.getServerId().equals(p.getServerId())) {
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
            File dataFile = new File(server.getDataPath() + "players/" + this.identity.toString() + ".dat");
            if (legacyDataFile.exists() && !dataFile.exists()) {
                nbt = this.server.getOfflinePlayerData(this.username, false);

                if (!legacyDataFile.delete()) {
                    log.warn("Could not delete legacy player data for {}", this.username);
                }
            } else {
                nbt = this.server.getOfflinePlayerData(this.identity, true);
            }
        }

        if (nbt == null) {
            this.close(this.getLeaveMessage(), "Invalid data");
            return;
        }

        if (loginChainData.isXboxAuthed() && server.getPropertyBoolean("xbox-auth") || !server.getPropertyBoolean("xbox-auth")) {
            server.updateName(this.identity, this.username);
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

        this.adventureSettings = new AdventureSettings(this)
                .set(Type.WORLD_IMMUTABLE, isAdventure() || isSpectator())
                .set(Type.WORLD_BUILDER, !isAdventure() && !isSpectator())
                .set(Type.AUTO_JUMP, true)
                .set(Type.ALLOW_FLIGHT, isCreative())
                .set(Type.NO_CLIP, isSpectator());

        Level level;
        if ((level = this.server.getLevelByName(nbt.getString("Level"))) == null || !alive) {
            this.setLevel(this.server.getDefaultLevel());
            nbt.putString("Level", this.level.getName());
            nbt.getList("Pos", NumberTag.class)
                    .add(new FloatTag("0", (float) this.level.getSpawnLocation().x))
                    .add(new FloatTag("1", (float) this.level.getSpawnLocation().y))
                    .add(new FloatTag("2", (float) this.level.getSpawnLocation().z));
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

        UUID uuid = getServerId();
        nbt.putLong("UUIDLeast", uuid.getLeastSignificantBits());
        nbt.putLong("UUIDMost", uuid.getMostSignificantBits());

        if (this.server.getAutoSave()) {
            this.server.saveOfflinePlayerData(this.identity, nbt, true);
        }

        this.sendPlayStatus(PlayStatusPacket.LOGIN_SUCCESS);
        this.server.onPlayerLogin(this);

        ListTag<NumberTag> posList = nbt.getList("Pos", NumberTag.class);

        super.init(this.level.getChunk(posList.get(0).getData().intValue() >> 4, posList.get(2).getData().intValue() >> 4), nbt);

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

        this.dataPacket(this.server.getPackManager().getPacksInfos());
    }

    /**
     * Sends a chat message as this player. If the message begins with a / (forward-slash) it will be treated
     * as a command.
     *
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

    public void handleDataPacket(DataPacket packet) {
        if (packet.pid() == ProtocolInfo.BATCH_PACKET) {
            this.server.getNetwork().processBatch((BatchPacket) packet, this);
            return;
        }

        this.inboundQueue.offer(packet);
    }

    private void onDataPacket(DataPacket packet) {
        if (!connected) {
            return;
        }

        try (Timing ignored = Timings.getReceiveDataPacketTiming(packet).startTiming()) {
            if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
                log.trace("Inbound {}: {}", this.getName(), packet);
            }

            DataPacketReceiveEvent receiveEvent = new DataPacketReceiveEvent(this, packet);
            this.server.getPluginManager().callEvent(receiveEvent);
            if (receiveEvent.isCancelled()) {
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
                    if (!ProtocolInfo.SUPPORTED_PROTOCOLS.contains(loginPacket.getProtocol())) {
                        if (loginPacket.getProtocol() < ProtocolInfo.CURRENT_PROTOCOL) {
                            message = "disconnectionScreen.outdatedClient";

                            this.sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_CLIENT);
                        } else {
                            message = "disconnectionScreen.outdatedServer";

                            this.sendPlayStatus(PlayStatusPacket.LOGIN_FAILED_SERVER);
                        }
                        if (((LoginPacket) packet).protocol < 137) {
                            DisconnectPacket disconnectPacket = new DisconnectPacket();
                            disconnectPacket.message = message;
                            ByteBuf buffer = ByteBufAllocator.DEFAULT.directBuffer(64);
                            disconnectPacket.tryEncode(buffer);
                            BatchPacket batch = new BatchPacket();
                            batch.payload = buffer;
                            this.directDataPacket(batch);
                            // Still want to run close() to allow the player to be removed properly
                        }
                        this.close("", message, false);
                        break;
                    }

                    this.loginChainData = ClientChainData.read(loginPacket);


                    if (!loginChainData.isXboxAuthed() && server.getPropertyBoolean("xbox-auth")) {
                        this.close("", "disconnectionScreen.notAuthenticated");
                        break;
                    }

                    if (this.server.getOnlinePlayers().size() >= this.server.getMaxPlayers() && this.kick(PlayerKickEvent.Reason.SERVER_FULL, "disconnectionScreen.serverFull", false)) {
                        break;
                    }

                    this.randomClientId = this.loginChainData.getClientId();

                    this.identity = this.loginChainData.getClientUUID();

                    String username = this.loginChainData.getUsername();
                    boolean valid = true;
                    int len = username.length();
                    if (len > 16 || len < 3) {
                        valid = false;
                    }

                    for (int i = 0; i < len && valid; i++) {
                        char c = username.charAt(i);
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

                    this.username = TextFormat.clean(username);
                    this.displayName = this.username;
                    this.iusername = this.username.toLowerCase();
                    this.setNameTag(this.username);

                    if (!valid || Objects.equals(this.iusername, "rcon") || Objects.equals(this.iusername, "console")) {
                        this.close("", "disconnectionScreen.invalidName");

                        break;
                    }

                    if (!this.loginChainData.getSkin().isValid()) {
                        this.close("", "disconnectionScreen.invalidSkin");
                        break;
                    } else {
                        this.setSkin(this.loginChainData.getSkin());
                    }

                    PlayerPreLoginEvent playerPreLoginEvent;
                    this.server.getPluginManager().callEvent(playerPreLoginEvent = new PlayerPreLoginEvent(this, "Plugin reason"));
                    if (playerPreLoginEvent.isCancelled()) {
                        this.close("", playerPreLoginEvent.getKickMessage());

                        break;
                    }

                    Player playerInstance = this;
                    this.preLoginEventTask = new AsyncTask() {

                        private PlayerAsyncPreLoginEvent e;

                        @Override
                        public void onRun() {
                            e = new PlayerAsyncPreLoginEvent(username, identity, Player.this.getAddress(), Player.this.getPort());
                            server.getPluginManager().callEvent(e);
                        }

                        @Override
                        public void onCompletion(Server server) {
                            if (!playerInstance.closed) {
                                if (e.getLoginResult() == LoginResult.KICK) {
                                    playerInstance.close(e.getKickMessage(), e.getKickMessage());
                                } else if (playerInstance.shouldLogin) {
                                    playerInstance.completeLoginSequence();
                                }

                                for (Consumer<Server> action : e.getScheduledActions()) {
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
                    switch (responsePacket.status) {
                        case REFUSED:
                            this.close("", "disconnectionScreen.noReason");
                            break;
                        case SEND_PACKS:
                            for (String entry : responsePacket.packEntries) {
                                Pack pack = this.server.getPackManager().getPackByIdVersion(entry);
                                if (pack == null) {
                                    this.close("", "disconnectionScreen.resourcePack");
                                    break;
                                }

                                ResourcePackDataInfoPacket dataInfoPacket = new ResourcePackDataInfoPacket();
                                dataInfoPacket.packId = pack.getId() + "_" + pack.getVersion(); // Why not a separate field?
                                dataInfoPacket.maxChunkSize = 1048576; //megabyte
                                dataInfoPacket.chunkCount = (int) (pack.getSize() / dataInfoPacket.maxChunkSize);
                                dataInfoPacket.compressedPackSize = pack.getSize();
                                dataInfoPacket.sha256 = pack.getHash();
                                dataInfoPacket.type = pack.getType();
                                this.dataPacket(dataInfoPacket);
                            }
                            break;
                        case HAVE_ALL_PACKS:
                            this.dataPacket(this.server.getPackManager().getPackStack());
                            break;
                        case COMPLETED:
                            if (this.preLoginEventTask.isFinished()) {
                                this.completeLoginSequence();
                            } else {
                                this.shouldLogin = true;
                            }
                            break;
                    }
                    break;
                case ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET:
                    ResourcePackChunkRequestPacket requestPacket = (ResourcePackChunkRequestPacket) packet;
                    Pack resourcePack = this.server.getPackManager().getPackByIdVersion(requestPacket.packId);
                    if (resourcePack == null) {
                        this.close("", "disconnectionScreen.resourcePack");
                        break;
                    }

                    ResourcePackChunkDataPacket dataPacket = new ResourcePackChunkDataPacket();
                    dataPacket.packId = requestPacket.packId;
                    dataPacket.chunkIndex = requestPacket.chunkIndex;
                    dataPacket.data = resourcePack.getChunk(1048576 * requestPacket.chunkIndex, 1048576);
                    dataPacket.progress = 1048576 * requestPacket.chunkIndex;
                    this.dataPacket(dataPacket);
                    break;
                case ProtocolInfo.PLAYER_SKIN_PACKET:
                    PlayerSkinPacket skinPacket = (PlayerSkinPacket) packet;
                    Skin skin = skinPacket.skin;

                    if(!skin.isValid()) {
                        break;
                    }

                    PlayerChangeSkinEvent playerChangeSkinEvent = new PlayerChangeSkinEvent(this, skin);
                    playerChangeSkinEvent.setCancelled(TimeUnit.SECONDS.toMillis(this.server.getPlayerSkinChangeCooldown()) > System.currentTimeMillis() - this.lastSkinChange);
                    this.server.getPluginManager().callEvent(playerChangeSkinEvent);
                    if (!playerChangeSkinEvent.isCancelled()) {
                        this.lastSkinChange = System.currentTimeMillis();
                        this.setSkin(skin);
                    }

                    break;
                case ProtocolInfo.PLAYER_INPUT_PACKET:
                    if (!this.isAlive() || !this.spawned) {
                        break;
                    }
                    PlayerInputPacket ipk = (PlayerInputPacket) packet;
                    if (vehicle instanceof EntityAbstractMinecart) {
                        ((EntityAbstractMinecart) vehicle).setCurrentSpeed(ipk.motionY);
                    }
                    break;
                case ProtocolInfo.MOVE_PLAYER_PACKET:
                    if (this.teleportPosition != null) {
                        break;
                    }

                    MovePlayerPacket movePlayerPacket = (MovePlayerPacket) packet;
                    Vector3f newPos = new Vector3f(movePlayerPacket.x, movePlayerPacket.y - this.getEyeHeight(), movePlayerPacket.z);

                    movePlayerPacket.yaw %= 360;
                    movePlayerPacket.pitch %= 360;

                    if (movePlayerPacket.yaw < 0) {
                        movePlayerPacket.yaw += 360;
                    }

                    if (newPos.distanceSquared(this) < 0.01 && movePlayerPacket.yaw == this.yaw &&
                            movePlayerPacket.pitch == this.pitch) {
                        break;
                    }

                    if (this.distance(newPos) > 50) {
                        this.sendPosition(this, movePlayerPacket.yaw, movePlayerPacket.pitch, MovePlayerPacket.MODE_RESET);
                        break;
                    }

                    boolean revert = false;
                    if (!this.isAlive() || !this.spawned) {
                        revert = true;
                        this.forceMovement = new Vector3f(this.x, this.y, this.z);
                    }


                    if (this.forceMovement != null && (newPos.distanceSquared(this.forceMovement) > 0.1 || revert)) {
                        this.sendPosition(this.forceMovement, movePlayerPacket.yaw, movePlayerPacket.pitch, MovePlayerPacket.MODE_RESET);
                    } else {
                        this.setRotation(movePlayerPacket.yaw, movePlayerPacket.pitch);
                        this.newPosition = newPos;
                        this.forceMovement = null;
                    }


                    if (vehicle != null) {
                        if (vehicle instanceof EntityBoat) {
                            vehicle.setPositionAndRotation(temporalVector.get().setComponents(movePlayerPacket.x, movePlayerPacket.y - 1, movePlayerPacket.z), (movePlayerPacket.headYaw + 90) % 360, 0);
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

                    Item item = this.inventory.getItem(mobEquipmentPacket.hotbarSlot);

                    if (!item.equals(mobEquipmentPacket.item)) {
                        log.debug("Tried to equip " + mobEquipmentPacket.item + " but have " + item + " in target slot");
                        this.inventory.sendContents(this);
                        return;
                    }

                    this.inventory.equipItem(mobEquipmentPacket.hotbarSlot);

                    this.setFlag(ACTION, false);

                    break;
                case ProtocolInfo.PLAYER_ACTION_PACKET:
                    PlayerActionPacket playerActionPacket = (PlayerActionPacket) packet;
                    if (!this.spawned || (!this.isAlive() && playerActionPacket.action != PlayerActionPacket.ACTION_RESPAWN && playerActionPacket.action != PlayerActionPacket.ACTION_DIMENSION_CHANGE_REQUEST)) {
                        break;
                    }

                    playerActionPacket.entityRuntimeId = this.getRuntimeId();
                    BlockPosition pos = new BlockPosition(playerActionPacket.x, playerActionPacket.y, playerActionPacket.z);
                    BlockFace face = BlockFace.fromIndex(playerActionPacket.face);

                    actionswitch:
                    switch (playerActionPacket.action) {
                        case PlayerActionPacket.ACTION_START_BREAK:
                            long currentBreak = System.currentTimeMillis();
                            Vector3i currentBreakPosition = new Vector3i(playerActionPacket.x, playerActionPacket.y, playerActionPacket.z);
                            // HACK: Client spams multiple left clicks so we need to skip them.
                            if ((lastBreakPosition.equals(currentBreakPosition) && (currentBreak - this.lastBreak) < 10) || pos.distanceSquared(this) > 100) {
                                break;
                            }
                            Block target = this.level.getBlock(pos);
                            PlayerInteractEvent playerInteractEvent = new PlayerInteractEvent(this, this.inventory.getItemInHand(), target, face, target.getId() == AIR ? Action.LEFT_CLICK_AIR : Action.LEFT_CLICK_BLOCK);
                            this.getServer().getPluginManager().callEvent(playerInteractEvent);
                            if (playerInteractEvent.isCancelled()) {
                                this.inventory.sendHeldItem(this);
                                break;
                            }
                            if (target.getId() == BlockIds.NOTEBLOCK) {
                                ((BlockNoteblock) target).emitSound();
                                break actionswitch;
                            } else if (target.getId() == BlockIds.DRAGON_EGG) {
                                ((BlockDragonEgg) target).teleport();
                                break actionswitch;
                            }
                            Block block = target.getSide(face);
                            if (block.getId() == BlockIds.FIRE) {
                                this.level.setBlock(block, Block.get(AIR), true);
                                this.level.addLevelSoundEvent(block.asVector3f(), LevelSoundEventPacket.SOUND_EXTINGUISH_FIRE);
                                break;
                            }
                            if (!this.isCreative()) {
                                //improved this to take stuff like swimming, ladders, enchanted tools into account, fix wrong tool break time calculations for bad tools (pmmp/PocketMine-MP#211)
                                //Done by lmlstarqaq
                                double breakTime = Math.ceil(target.getBreakTime(this.inventory.getItemInHand(), this) * 20);
                                if (breakTime > 0) {
                                    LevelEventPacket pk = new LevelEventPacket();
                                    pk.evid = LevelEventPacket.EVENT_BLOCK_START_BREAK;
                                    pk.x = (float) pos.x;
                                    pk.y = (float) pos.y;
                                    pk.z = (float) pos.z;
                                    pk.data = (int) (65535 / breakTime);
                                    this.getLevel().addChunkPacket(pos.getChunkX(), pos.getChunkZ(), pk);
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
                            this.getLevel().addChunkPacket(pos.getChunkX(), pos.getChunkZ(), pk);
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

                            if (this.server.isHardcore()) {
                                this.setBanned(true);
                                break;
                            }

                            this.craftingType = CRAFTING_SMALL;
                            this.resetCraftingGridType();

                            PlayerRespawnEvent playerRespawnEvent = new PlayerRespawnEvent(this, this.getSpawn());
                            this.server.getPluginManager().callEvent(playerRespawnEvent);

                            Position respawnPos = playerRespawnEvent.getRespawnPosition();

                            this.teleport(respawnPos, null);

                            this.setSprinting(false);
                            this.setSneaking(false);

                            this.setShortData(EntityData.AIR, 400);
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
                            if (isSwimming()) {
                                PlayerToggleSwimEvent ptse = new PlayerToggleSwimEvent(this, false);
                                this.server.getPluginManager().callEvent(ptse);

                                if (ptse.isCancelled()) {
                                    this.sendData(this);
                                } else {
                                    this.setSwimming(false);
                                }
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
                                this.level.addParticle(new PunchBlockParticle(pos.asVector3f(), block, face));
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
                    }
                    this.updateData();

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

                    this.craftingType = CRAFTING_SMALL;
                    //this.resetCraftingGridType();

                    InteractPacket interactPacket = (InteractPacket) packet;

                    Entity targetEntity = this.level.getEntity(interactPacket.target);

                    if (targetEntity == null || !this.isAlive() || !targetEntity.isAlive()) {
                        break;
                    }

                    if (targetEntity instanceof DroppedItem || targetEntity instanceof EntityArrow || targetEntity instanceof XpOrb) {
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
                            if (this.vehicle == null) {
                                break;
                            }
                            this.dismount(this.vehicle);
                            break;
                    }
                    break;
                case ProtocolInfo.BLOCK_PICK_REQUEST_PACKET:
                    BlockPickRequestPacket pickRequestPacket = (BlockPickRequestPacket) packet;
                    Block block = this.level.getBlock(pickRequestPacket.x, pickRequestPacket.y, pickRequestPacket.z);
                    item = block.toItem();

                    if (pickRequestPacket.addUserData) {
                        BlockEntity blockEntity = this.getLevel().getLoadedBlockEntity(
                                new BlockPosition(pickRequestPacket.x, pickRequestPacket.y, pickRequestPacket.z));
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
                        log.debug("Got block-pick request from " + this.getName() + " when in spectator mode");
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
                            if (this.vehicle instanceof EntityBoat) {
                                ((EntityBoat) this.vehicle).onPaddle(animation, ((AnimatePacket) packet).rowingTime);
                            }
                            break;
                    }

                    AnimatePacket animatePacket = new AnimatePacket();
                    animatePacket.entityRuntimeId = this.getUniqueId();
                    animatePacket.action = animationEvent.getAnimationType();
                    Server.broadcastPacket(this.getViewers(), animatePacket);
                    break;
                case ProtocolInfo.SET_HEALTH_PACKET:
                    //use UpdateAttributePacket instead
                    break;

                case ProtocolInfo.ENTITY_EVENT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }
                    this.craftingType = CRAFTING_SMALL;
                    //this.resetCraftingGridType();

                    EntityEventPacket entityEventPacket = (EntityEventPacket) packet;

                    switch (entityEventPacket.event) {
                        case EntityEventPacket.EATING_ITEM:
                            if (entityEventPacket.data == 0 || entityEventPacket.entityRuntimeId != this.getRuntimeId()) {
                                break;
                            }

                            entityEventPacket.entityRuntimeId = this.getRuntimeId();

                            this.dataPacket(entityEventPacket);
                            Server.broadcastPacket(this.getViewers(), entityEventPacket);
                            break;
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

                    try (Timing ignored2 = Timings.playerCommandTimer.startTiming()) {
                        this.server.dispatchCommand(playerCommandPreprocessEvent.getPlayer(), playerCommandPreprocessEvent.getMessage().substring(1));
                    }
                    break;
                case ProtocolInfo.TEXT_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    TextPacket textPacket = (TextPacket) packet;

                    if (textPacket.type == TextPacket.TYPE_CHAT) {
                        this.chat(textPacket.message);
                    }
                    break;
                case ProtocolInfo.CONTAINER_CLOSE_PACKET:
                    ContainerClosePacket containerClosePacket = (ContainerClosePacket) packet;
                    if (!this.spawned) {
                        break;
                    }

                    if (this.windowIndex.containsKey(containerClosePacket.windowId)) {
                        this.server.getPluginManager().callEvent(new InventoryCloseEvent(this.windowIndex.get(containerClosePacket.windowId), this));
                        this.removeWindow(this.windowIndex.get(containerClosePacket.windowId));
                    } else {
                        this.windowIndex.remove(containerClosePacket.windowId);
                    }
                    if (containerClosePacket.windowId == -1) {
                        this.craftingType = CRAFTING_SMALL;
                        this.resetCraftingGridType();
                        this.addWindow(this.craftingGrid, ContainerIds.NONE);
                    }
                    break;
                case ProtocolInfo.CRAFTING_EVENT_PACKET:
                    break;
                case ProtocolInfo.BLOCK_ENTITY_DATA_PACKET:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    BlockEntityDataPacket blockEntityDataPacket = (BlockEntityDataPacket) packet;
                    this.craftingType = CRAFTING_SMALL;
                    this.resetCraftingGridType();

                    pos = new BlockPosition(blockEntityDataPacket.x, blockEntityDataPacket.y, blockEntityDataPacket.z);
                    if (pos.distanceSquared(this) > 10000) {
                        break;
                    }

                    BlockEntity blockEntity = this.level.getLoadedBlockEntity(pos);
                    if (blockEntity instanceof BlockEntitySpawnable) {
                        CompoundTag nbt;
                        try {
                            nbt = NBTIO.read(blockEntityDataPacket.namedTag, ByteOrder.LITTLE_ENDIAN, true);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        if (!((BlockEntitySpawnable) blockEntity).updateCompoundTag(nbt, this)) {
                            ((BlockEntitySpawnable) blockEntity).spawnTo(this);
                        }
                    }
                    break;
                case ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET:
                    this.getChunkManager().setChunkRadius(((RequestChunkRadiusPacket) packet).radius);
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
                    BlockPosition vector3 = new BlockPosition(itemFrameDropItemPacket.x, itemFrameDropItemPacket.y, itemFrameDropItemPacket.z);
                    blockEntity = this.level.getLoadedBlockEntity(vector3);
                    if (!(blockEntity instanceof BlockEntityItemFrame)) {
                        break;
                    }
                    BlockEntityItemFrame itemFrame = (BlockEntityItemFrame) blockEntity;
                    if (itemFrame != null) {
                        block = itemFrame.getBlock();
                        Item itemDrop = itemFrame.getItem();
                        ItemFrameDropItemEvent itemFrameDropItemEvent = new ItemFrameDropItemEvent(this, block, itemFrame, itemDrop);
                        this.server.getPluginManager().callEvent(itemFrameDropItemEvent);
                        if (!itemFrameDropItemEvent.isCancelled()) {
                            if (itemDrop.getId() != AIR) {
                                this.level.dropItem(block.asVector3f().add(0.5, 0, 0.5), itemDrop);
                                itemFrame.setItem(Item.get(AIR, 0, 0));
                                itemFrame.setItemRotation(0);
                                this.getLevel().addSound(this, Sound.BLOCK_ITEMFRAME_REMOVE_ITEM);
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
                case ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V1:
                case ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V2:
                case ProtocolInfo.LEVEL_SOUND_EVENT_PACKET:
                    if (!this.isSpectator() || (((LevelSoundEventPacket) packet).event != LevelSoundEventPacket.SOUND_HIT && ((LevelSoundEventPacket) packet).event != LevelSoundEventPacket.SOUND_ATTACK_NODAMAGE)) {
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
                        InventoryAction a = networkInventoryAction.createInventoryAction(this);

                        if (a == null) {
                            log.debug("Unmatched inventory action from " + this.getName() + ": " + networkInventoryAction);
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

                        if (this.craftingTransaction.getPrimaryOutput() != null) {
                            //we get the actions for this in several packets, so we can't execute it until we get the result

                            this.craftingTransaction.execute();
                            this.craftingTransaction = null;
                        }

                        return;
                    } else if (this.craftingTransaction != null) {
                        log.debug("Got unexpected normal inventory action with incomplete crafting transaction from " + this.getName() + ", refusing to execute crafting");
                        this.craftingTransaction = null;
                    }

                    switch (transactionPacket.transactionType) {
                        case InventoryTransactionPacket.TYPE_NORMAL:
                            InventoryTransaction transaction = new InventoryTransaction(this, actions);

                            if (!transaction.execute()) {
                                log.debug("Failed to execute inventory transaction from " + this.getName() + " with actions: " + Arrays.toString(transactionPacket.actions));
                                break packetswitch; //oops!
                            }

                            //TODO: fix achievement for getting iron from furnace

                            break packetswitch;
                        case InventoryTransactionPacket.TYPE_MISMATCH:
                            if (transactionPacket.actions.length > 0) {
                                log.debug("Expected 0 actions for mismatch, got " + transactionPacket.actions.length + ", " + Arrays.toString(transactionPacket.actions));
                            }
                            this.sendAllInventories();

                            break packetswitch;
                        case InventoryTransactionPacket.TYPE_USE_ITEM:
                            UseItemData useItemData = (UseItemData) transactionPacket.transactionData;

                            Vector3i blockVector = useItemData.blockPos;
                            face = useItemData.face;

                            int type = useItemData.actionType;
                            switch (type) {
                                case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_BLOCK:
                                    // Remove if client bug is ever fixed
                                    boolean spamBug = (lastRightClickPos != null && System.currentTimeMillis() - lastRightClickTime < 100.0 && blockVector.distanceSquared(lastRightClickPos) < 0.00001);
                                    lastRightClickPos = blockVector;
                                    lastRightClickTime = System.currentTimeMillis();
                                    if (spamBug) {
                                        return;
                                    }

                                    this.setFlag(ACTION, false);

                                    if (this.canInteract(blockVector.add(0.5, 0.5, 0.5), this.isCreative() ? 13 : 7)) {
                                        if (this.isCreative()) {
                                            Item i = inventory.getItemInHand();
                                            if (this.level.useItemOn(blockVector, i, face, useItemData.clickPos, this) != null) {
                                                break packetswitch;
                                            }
                                        } else if (inventory.getItemInHand().equals(useItemData.itemInHand)) {
                                            Item i = inventory.getItemInHand();
                                            Item oldItem = i.clone();
                                            //TODO: Implement adventure mode checks
                                            if ((i = this.level.useItemOn(blockVector, i, face, useItemData.clickPos, this)) != null) {
                                                if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                                    inventory.setItemInHand(i);
                                                    inventory.sendHeldItem(this.getViewers());
                                                }
                                                break packetswitch;
                                            }
                                        }
                                    }

                                    inventory.sendHeldItem(this);

                                    if (blockVector.distanceSquared(this) > 10000) {
                                        break packetswitch;
                                    }

                                    Block target = this.level.getBlock(blockVector);
                                    block = target.getSide(face);

                                    this.level.sendBlocks(new Player[]{this}, new Block[]{target, block}, UpdateBlockPacket.FLAG_ALL_PRIORITY);
                                    break packetswitch;
                                case InventoryTransactionPacket.USE_ITEM_ACTION_BREAK_BLOCK:
                                    if (!this.spawned || !this.isAlive()) {
                                        break packetswitch;
                                    }

                                    this.resetCraftingGridType();

                                    Item i = this.getInventory().getItemInHand();

                                    Item oldItem = i.clone();

                                    if (this.canInteract(blockVector.add(0.5, 0.5, 0.5), this.isCreative() ? 13 : 7) && (i = this.level.useBreakOn(blockVector, face, i, this, true)) != null) {
                                        if (this.isSurvival()) {
                                            this.getFoodData().updateFoodExpLevel(0.025);
                                            if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                                inventory.setItemInHand(i);
                                                inventory.sendHeldItem(this.getViewers());
                                            }
                                        }
                                        break packetswitch;
                                    }

                                    inventory.sendContents(this);
                                    target = this.level.getBlock(blockVector);
                                    blockEntity = this.level.getLoadedBlockEntity(blockVector);

                                    this.level.sendBlocks(new Player[]{this}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY);

                                    inventory.sendHeldItem(this);

                                    if (blockEntity instanceof BlockEntitySpawnable) {
                                        ((BlockEntitySpawnable) blockEntity).spawnTo(this);
                                    }

                                    break packetswitch;
                                case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_AIR:
                                    Vector3f directionVector = this.getDirectionVector();

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
                                        if (this.isSurvival()) {
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
                                    if (target.onInteract(this, item, useItemOnEntityData.clickPos) && this.isSurvival()) {
                                        if (item.isTool()) {
                                            if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                                                item = Item.get(AIR, 0, 0);
                                            }
                                        } else {
                                            if (item.getCount() > 1) {
                                                item.decrementCount();
                                            } else {
                                                item = Item.get(AIR, 0, 0);
                                            }
                                        }

                                        this.inventory.setItemInHand(item);
                                    }
                                    break;
                                case InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_ATTACK:
                                    float itemDamage = item.getAttackDamage();

                                    for (Enchantment enchantment : item.getEnchantments()) {
                                        itemDamage += enchantment.getDamageBonus(target);
                                    }

                                    Map<DamageModifier, Float> damage = new EnumMap<>(DamageModifier.class);
                                    damage.put(DamageModifier.BASE, itemDamage);

                                    if (!this.canInteract(target.getPosition(), isCreative() ? 8 : 5)) {
                                        break;
                                    } else if (target instanceof Player) {
                                        if ((((Player) target).getGamemode() & 0x01) > 0) {
                                            break;
                                        } else if (!this.server.getPropertyBoolean("pvp")) {
                                            break;
                                        }
                                    }

                                    EntityDamageByEntityEvent entityDamageByEntityEvent = new EntityDamageByEntityEvent(this, target, DamageCause.ENTITY_ATTACK, damage);
                                    if (this.isSpectator()) entityDamageByEntityEvent.setCancelled();
                                    if ((target instanceof Player) && !this.level.getGameRules().get(GameRules.PVP)) {
                                        entityDamageByEntityEvent.setCancelled();
                                    }

                                    if (!target.attack(entityDamageByEntityEvent)) {
                                        if (item.isTool() && this.isSurvival()) {
                                            this.inventory.sendContents(this);
                                        }
                                        break;
                                    }

                                    for (Enchantment enchantment : item.getEnchantments()) {
                                        enchantment.doPostAttack(this, target);
                                    }

                                    if (item.isTool() && this.isSurvival()) {
                                        if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                                            this.inventory.setItemInHand(Item.get(AIR, 0, 0));
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
                case ProtocolInfo.LECTERN_UPDATE_PACKET:
                    LecternUpdatePacket lecternUpdatePacket = (LecternUpdatePacket) packet;
                    Vector3i blockPosition = lecternUpdatePacket.blockPosition;
                    temporalVector.set(blockPosition.asVector3f());

                    if (lecternUpdatePacket.dropBook) {
                        Block blockLectern = this.getLevel().getBlock(temporalVector.get().asVector3i());
                        if (blockLectern instanceof BlockLectern) {
                            ((BlockLectern) blockLectern).dropBook(this);
                        }
                    } else {
                        BlockEntity blockEntityLectern = this.level.getBlockEntity(temporalVector.get().asVector3i());
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
                default:
                    break;
            }
        }
    }

    public int getChunkRadius() {
        return this.getChunkManager().getChunkRadius();
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

    public void setChunkRadius(int chunkRadius) {
        this.getChunkManager().setChunkRadius(chunkRadius);
    }

    public void save() {
        this.save(false);
    }

    public PlayerChunkManager getChunkManager() {
        return this.chunkManager;
    }

    public String getName() {
        return this.username;
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
                if (this.fishing != null) {
                    this.stopFishing(false);
                }
            }

            for (Player player : new ArrayList<>(this.server.getOnlinePlayers().values())) {
                if (!player.canSee(this)) {
                    player.showPlayer(this);
                }
            }

            this.hiddenPlayers.clear();

            this.removeAllWindows(true);

            this.getChunkManager().getLoadedChunks().forEach((LongConsumer) chunkKey -> {
                int chunkX = Chunk.fromKeyX(chunkKey);
                int chunkZ = Chunk.fromKeyZ(chunkKey);

                for (Entity entity : level.getLoadedChunkEntities(chunkX, chunkZ)) {
                    if (entity != this) {
                        entity.getViewers().remove(this);
                    }
                }
            });

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
            this.hasSpawned.clear();
            this.spawnPosition = null;

            if (!passengers.isEmpty()) {
                passengers.forEach(entity -> entity.dismount(this));
            }
            if (this.vehicle != null) {
                this.dismount(vehicle);
            }
        }

        if (this.perm != null) {
            this.perm.clearPermissions();
            this.perm = null;
        }

        if (this.inventory != null) {
            this.inventory = null;
        }

        this.getChunkManager().clear();

        this.chunk = null;

        this.server.removePlayer(this);
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
            pk.entityRuntimeId = this.getRuntimeId();
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
            pk.entityRuntimeId = this.getRuntimeId();
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
        pk.entityRuntimeId = this.getRuntimeId();
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
        }
        if (this.getLevel().getBlock(this.getPosition().asBlockPosition().add(0, -1, 0)).getId() == BlockIds.SLIME) {
            if (!this.isSneaking()) {
                //source.setCancelled();
                this.resetFallDistance();
                return false;
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
                pk.entityRuntimeId = this.getRuntimeId();
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
            log.debug(this.getName() + " attempted to drop a null item (" + item + ")");
            return true;
        }

        Vector3f motion = this.getDirectionVector().multiply(0.4);

        this.level.dropItem(this.add(0, 1.3, 0), item, motion, 40);

        this.setFlag(ACTION, false);
        return true;
    }

    public void sendPosition(Vector3f pos) {
        this.sendPosition(pos, this.yaw);
    }

    public void sendPosition(Vector3f pos, double yaw) {
        this.sendPosition(pos, yaw, this.pitch);
    }

    public void sendPosition(Vector3f pos, double yaw, double pitch) {
        this.sendPosition(pos, yaw, pitch, MovePlayerPacket.MODE_NORMAL);
    }

    public void sendPosition(Vector3f pos, double yaw, double pitch, int mode) {
        this.sendPosition(pos, yaw, pitch, mode, null);
    }

    public void sendPosition(Vector3f pos, double yaw, double pitch, int mode, Player[] targets) {
        MovePlayerPacket pk = new MovePlayerPacket();
        pk.entityRuntimeId = this.getUniqueId();
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
            pk.entityRuntimeId = this.getRuntimeId();
            this.dataPacket(pk);
        }
    }

    public void save(boolean async) {
        if (this.closed) {
            throw new IllegalStateException("Tried to save closed player");
        }

        super.saveNBT();

        if (this.level != null) {
            this.namedTag.putString("Level", this.level.getId());
            if (this.spawnPosition != null && this.spawnPosition.getLevel() != null) {
                this.namedTag.putString("SpawnLevel", this.spawnPosition.getLevel().getId());
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

            if (!this.username.isEmpty() && this.namedTag != null) {
                this.server.saveOfflinePlayerData(this.identity, this.namedTag, async);
            }
        }
    }

    @Override
    public void kill() {
        if (!this.spawned) {
            return;
        }

        boolean showMessages = this.level.getGameRules().get(GameRules.SHOW_DEATH_MESSAGES);
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
                    Block block = this.level.getBlock(asBlockPosition().add(0, -1, 0));
                    if (block.getId() == BlockIds.MAGMA) {
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
                        if (((EntityDamageByBlockEvent) cause).getDamager().getId() == BlockIds.CACTUS) {
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

                case HUNGER:
                    message = "death.attack.starve";
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

        if (this.fishing != null) {
            this.stopFishing(false);
        }

        this.health = 0;
        this.scheduleUpdate();

        PlayerDeathEvent ev = new PlayerDeathEvent(this, this.getDrops(), new TranslationContainer(message, params.toArray(new String[0])), this.getExperienceLevel());

        ev.setKeepExperience(this.level.getGameRules().get(GameRules.KEEP_INVENTORY));
        ev.setKeepInventory(ev.getKeepExperience());
        this.server.getPluginManager().callEvent(ev);

        if (!ev.getKeepInventory() && this.level.getGameRules().get(GameRules.DO_ENTITY_DROPS)) {
            for (Item item : ev.getDrops()) {
                this.level.dropItem(this, item, null, true, 40);
            }

            if (this.inventory != null) {
                this.inventory.clearAll();
            }
        }

        if (!ev.getKeepExperience() && this.level.getGameRules().get(GameRules.DO_ENTITY_DROPS)) {
            if (this.isSurvival() || this.isAdventure()) {
                int exp = ev.getExperience() * 7;
                if (exp > 100) exp = 100;
                this.getLevel().dropExpOrb(this, exp);
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
        pk.respawnState = RespawnPacket.STATE_SEARCHING_FOR_SPAWN;

        //this is a dirty hack to prevent dying in a different level than the respawn point from breaking everything
        if (this.level != pos.level)   {
            this.teleport(new Location(pos.x, -100, pos.z, pos.level), null);
            this.teleport(new Location(pos.x, pos.y, pos.z, pos.level), null);
        }

        this.extinguish();

        this.dataPacket(pk);
    }

    protected void sendPlayStatus(int status) {
        sendPlayStatus(status, false);
    }

    protected void sendPlayStatus(int status, boolean immediate) {
        PlayStatusPacket pk = new PlayStatusPacket();
        pk.status = status;

        if (immediate) {
            this.directDataPacket(pk);
        } else {
            this.dataPacket(pk);
        }
    }

    @Override
    protected void checkChunks() {
        if (this.chunk == null || (this.chunk.getX() != this.getChunkX() || this.chunk.getZ() != this.getChunkZ())) {
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }
            this.chunk = this.level.getChunk(this.getChunkX(), this.getChunkZ());

            if (!this.justCreated) {

                Set<Player> loaders = this.chunk.getPlayerLoaders();
                for (Player player : this.hasSpawned) {
                    if (!loaders.contains(player)) {
                        this.despawnFrom(player);
                    } else {
                        loaders.remove(player);
                    }
                }

                for (Player player : loaders) {
                    this.spawnTo(player);
                }
            }

            if (this.chunk == null) {
                return;
            }

            this.chunk.addEntity(this);
        }
    }

//    protected void forceSendEmptyChunks() {
//        int chunkPositionX = this.getChunkX();
//        int chunkPositionZ = this.getChunkZ();
//        for (int x = -chunkRadius; x < chunkRadius; x++) {
//            for (int z = -chunkRadius; z < chunkRadius; z++) {
//                LevelChunkPacket chunk = new LevelChunkPacket();
//                chunk.chunkX = chunkPositionX + x;
//                chunk.chunkZ = chunkPositionZ + z;
//                chunk.data = Unpooled.EMPTY_BUFFER;
//                this.dataPacket(chunk);
//            }
//        }
//    }

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
                dataPacket(pk);
            }

            this.forceMovement = new Vector3f(this.x, this.y, this.z);
            this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET);

            this.resetFallDistance();
            this.newPosition = null;

            //Weather
            this.getLevel().sendWeather(this);
            //Update time
            this.getLevel().sendTime(this);
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

        if (inventory.open(this)) {
            return cnt;
        } else {
            this.removeWindow(inventory);

            return -1;
        }
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
        inventory.close(this);
        this.windows.remove(inventory);
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
        this.addWindow(this.getInventory(), ContainerIds.INVENTORY, true);

        this.playerUIInventory = new PlayerUIInventory(this);
        this.addWindow(this.playerUIInventory, ContainerIds.UI, true);

        this.craftingGrid = this.playerUIInventory.getCraftingGrid();
        this.addWindow(this.craftingGrid, ContainerIds.NONE);

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
            Item[] drops = this.inventory.addItem(this.craftingGrid.getContents().values().toArray(new Item[0]));

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

    protected boolean checkTeleportPosition() {
        if (this.teleportPosition != null) {
            int chunkX = (int) this.teleportPosition.x >> 4;
            int chunkZ = (int) this.teleportPosition.z >> 4;

            for (int X = -1; X <= 1; ++X) {
                for (int Z = -1; Z <= 1; ++Z) {
                    long index = Chunk.key(chunkX + X, chunkZ + Z);
                    if (!this.getChunkManager().isChunkInView(index)) {
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

            this.teleportPosition = new Vector3f(this.x, this.y, this.z);
            this.getChunkManager().queueNewChunks(this.teleportPosition.getChunkX(), this.teleportPosition.getChunkZ());
            this.forceMovement = this.teleportPosition;
            this.sendPosition(this, this.yaw, this.pitch, MovePlayerPacket.MODE_TELEPORT);

            this.checkTeleportPosition();

            this.resetFallDistance();
            this.newPosition = null;

            //DummyBossBar
            this.getDummyBossBars().values().forEach(DummyBossBar::reshow);
            //Weather
            this.getLevel().sendWeather(this);
            //Update time
            this.getLevel().sendTime(this);
            return true;
        }

        return false;
    }

    public boolean isChunkInView(int x, int z) {
        return this.getChunkManager().isChunkInView(x, z);
    }

    @Override
    public void onChunkChanged(Chunk chunk) {
        this.getChunkManager().resendChunk(chunk.getX(), chunk.getZ());
    }

    @Override
    public int getLoaderId() {
        return this.loaderId;
    }

    @Override
    public boolean isLoaderActive() {
        return this.isConnected();
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
        this.directDataPacket(pk);
    }

    @Override
    public void onChunkLoaded(Chunk chunk) {

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
        String message = "Transferred to " + hostName + ":" + port;
        this.close(message, message, false);
    }

    public LoginChainData getLoginChainData() {
        return this.loginChainData;
    }

    @Override
    public void onChunkUnloaded(Chunk chunk) {
        //this.sentChunks.remove(Chunk.key(chunk.getX(), chunk.getZ()));
    }

    @Override
    public int hashCode() {
        if ((this.hash == 0) || (this.hash == 485)) {
            this.hash = (485 + (getServerId() != null ? getServerId().hashCode() : 0));
        }

        return this.hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Player)) {
            return false;
        }
        Player other = (Player) obj;
        return Objects.equals(this.getServerId(), other.getServerId()) && this.getUniqueId() == other.getUniqueId();
    }

    public boolean isBreakingBlock() {
        return this.breakingBlock != null;
    }

    /**
     * Show a window of a XBOX account's profile
     *
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
        FishingHook fishingHook = EntityRegistry.get().newEntity(EntityTypes.FISHING_HOOK, chunk, nbt);
        fishingHook.setOwner(this);
        fishingHook.setMotion(new Vector3f(-Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f, -Math.sin(Math.toRadians(pitch)) * f * f,
                Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f));
        ProjectileLaunchEvent ev = new ProjectileLaunchEvent(fishingHook);
        this.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            fishingHook.kill();
        } else {
            fishingHook.spawnToAll();
            this.fishing = fishingHook;
            fishingHook.setRod(fishingRod);
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
    public boolean switchLevel(Level level) {
        Level oldLevel = this.level;
        if (super.switchLevel(level)) {
            SetSpawnPositionPacket spawnPosition = new SetSpawnPositionPacket();
            spawnPosition.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
            Position spawn = level.getSpawnLocation();
            spawnPosition.x = spawn.getFloorX();
            spawnPosition.y = spawn.getFloorY();
            spawnPosition.z = spawn.getFloorZ();
            this.dataPacket(spawnPosition);

            this.getChunkManager().prepareRegion(this.getChunkX(), this.getChunkZ());

            SetTimePacket setTime = new SetTimePacket();
            setTime.time = level.getTime();
            this.dataPacket(setTime);

            GameRulesChangedPacket gameRulesChanged = new GameRulesChangedPacket();
            gameRulesChanged.gameRules = level.getGameRules();
            this.dataPacket(gameRulesChanged);
            return true;
        }

        return false;
    }

    public boolean pickupEntity(Entity entity, boolean near) {
        if (!this.spawned || !this.isAlive() || !this.isOnline() || this.getGamemode() == SPECTATOR || entity.isClosed()) {
            return false;
        }

        if (near) {
            if (entity instanceof Arrow && entity.getMotion().lengthSquared() == 0) {
                Item item = Item.get(ItemIds.ARROW);
                if (this.isSurvival() && !this.inventory.canAddItem(item)) {
                    return false;
                }

                InventoryPickupArrowEvent ev = new InventoryPickupArrowEvent(this.inventory, (EntityArrow) entity);

                int pickupMode = ((EntityArrow) entity).getPickupMode();
                if (pickupMode == EntityArrow.PICKUP_NONE || pickupMode == EntityArrow.PICKUP_CREATIVE && !this.isCreative()) {
                    ev.setCancelled();
                }

                this.server.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return false;
                }

                TakeItemEntityPacket pk = new TakeItemEntityPacket();
                pk.entityId = this.getUniqueId();
                pk.target = entity.getUniqueId();
                Server.broadcastPacket(entity.getViewers(), pk);
                this.dataPacket(pk);

                if (!this.isCreative()) {
                    this.inventory.addItem(item.clone());
                }
                entity.close();
                return true;
            } else if (entity instanceof ThrownTrident && entity.getMotion().lengthSquared() == 0) {
                Item item = ((ThrownTrident) entity).getTrident();
                if (this.isSurvival() && !this.inventory.canAddItem(item)) {
                    return false;
                }

                TakeItemEntityPacket pk = new TakeItemEntityPacket();
                pk.entityId = this.getUniqueId();
                pk.target = entity.getUniqueId();
                Server.broadcastPacket(entity.getViewers(), pk);
                this.dataPacket(pk);

                if (!this.isCreative()) {
                    this.inventory.addItem(item.clone());
                }
                entity.close();
                return true;
            } else if (entity instanceof DroppedItem) {
                if (((DroppedItem) entity).getPickupDelay() <= 0) {
                    Item item = ((DroppedItem) entity).getItem();

                    if (item != null) {
                        if (this.isSurvival() && !this.inventory.canAddItem(item)) {
                            return false;
                        }

                        InventoryPickupItemEvent ev;
                        this.server.getPluginManager().callEvent(ev = new InventoryPickupItemEvent(this.inventory, (DroppedItem) entity));
                        if (ev.isCancelled()) {
                            return false;
                        }

                        if (item.getId() == BlockIds.LOG) {
                            this.awardAchievement("mineWood");
                        } else if (item.getId() == ItemIds.DIAMOND) {
                            this.awardAchievement("diamond");
                        }

                        TakeItemEntityPacket pk = new TakeItemEntityPacket();
                        pk.entityId = this.getUniqueId();
                        pk.target = entity.getUniqueId();
                        Server.broadcastPacket(entity.getViewers(), pk);
                        this.dataPacket(pk);

                        entity.close();
                        this.inventory.addItem(item.clone());
                        return true;
                    }
                }
            }
        }

        int tick = this.getServer().getTick();
        if (pickedXPOrb < tick && entity instanceof XpOrb && this.boundingBox.isVectorInside(entity.getPosition())) {
            XpOrb xpOrb = (XpOrb) entity;
            if (xpOrb.getPickupDelay() <= 0) {
                int exp = xpOrb.getExperience();
                entity.kill();
                this.getLevel().addSound(this, Sound.RANDOM_ORB);
                pickedXPOrb = tick;

                //Mending
                ArrayList<Integer> itemsWithMending = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    if (inventory.getArmorItem(i).getEnchantment((short) Enchantment.ID_MENDING) != null) {
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

                this.addExperience(exp);
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "Player(name=" + getName() + ")";
    }
}
