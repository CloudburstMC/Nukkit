package cn.nukkit.player;

import cn.nukkit.Achievement;
import cn.nukkit.AdventureSettings;
import cn.nukkit.AdventureSettings.Type;
import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.ItemFrame;
import cn.nukkit.blockentity.Lectern;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.entity.impl.EntityLiving;
import cn.nukkit.entity.impl.Human;
import cn.nukkit.entity.impl.projectile.EntityArrow;
import cn.nukkit.entity.impl.vehicle.EntityAbstractMinecart;
import cn.nukkit.entity.impl.vehicle.EntityBoat;
import cn.nukkit.entity.misc.DroppedItem;
import cn.nukkit.entity.misc.ExperienceOrb;
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
import cn.nukkit.event.server.PlayerPacketSendEvent;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.inventory.*;
import cn.nukkit.inventory.transaction.CraftingTransaction;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.item.*;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.ChunkLoader;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.gamerule.GameRules;
import cn.nukkit.level.particle.PunchBlockParticle;
import cn.nukkit.locale.TextContainer;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.math.*;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.network.ProtocolInfo;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.network.protocol.types.InventoryTransactionUtils;
import cn.nukkit.pack.Pack;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.player.manager.PlayerChunkManager;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.registry.ItemRegistry;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.*;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.nukkitx.math.vector.Vector2f;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.data.*;
import com.nukkitx.protocol.bedrock.handler.BatchHandler;
import com.nukkitx.protocol.bedrock.packet.*;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

import static cn.nukkit.block.BlockIds.AIR;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.nukkitx.protocol.bedrock.data.EntityData.*;
import static com.nukkitx.protocol.bedrock.data.EntityFlag.USING_ITEM;

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

    protected final BedrockServerSession session;

    private boolean initialized;
    public boolean spawned = false;
    public boolean loggedIn = false;
    public long lastBreak;
    public Vector3f speed = null;

    protected final BiMap<Inventory, Byte> windows = HashBiMap.create();
    protected final BiMap<Byte, Inventory> windowIndex = windows.inverse();
    protected final ByteSet permanentWindows = new ByteOpenHashSet();
    protected byte windowCnt = 4;

    protected final PlayerData playerData = new PlayerData();

    protected int messageCounter = 2;

    private String clientSecret;
    protected Vector3f forceMovement = null;

    public int craftingType = CRAFTING_SMALL;

    protected PlayerUIInventory playerUIInventory;
    protected CraftingGrid craftingGrid;
    protected CraftingTransaction craftingTransaction;

    public long creationTime = 0;

    protected long randomClientId;
    protected Vector3f teleportPosition = null;
    protected final PlayerFood foodData = new PlayerFood(this, 20, 20);

    protected boolean connected = true;
    protected boolean removeFormat = true;

    protected String username;
    protected String iusername;
    protected String displayName;

    protected int startAction = -1;
    protected Vector3f newPosition = null;

    private int loaderId;

    protected float stepHeight = 0.6f;

    protected final Map<UUID, Player> hiddenPlayers = new HashMap<>();
    protected Vector3i lastRightClickPos = null;

    protected int viewDistance;
    protected final int chunksPerTick;
    protected final int spawnThreshold;
    private final Queue<BedrockPacket> inboundQueue = new ConcurrentLinkedQueue<>();

    protected int inAirTicks = 0;
    protected int startAirTicks = 5;

    protected AdventureSettings adventureSettings;

    protected boolean checkMovement = true;
    protected Vector3i sleeping = null;

    private PermissibleBase perm = null;

    private int exp = 0;
    private int expLevel = 0;
    protected Location spawnLocation = null;

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
    private Vector3i lastBreakPosition = Vector3i.ZERO;

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

    public Player(BedrockServerSession session) {
        super(EntityTypes.PLAYER, Location.from(Server.getInstance().getDefaultLevel()));
        this.session = session;
        session.setBatchedHandler(new Handler());
        this.perm = new PermissibleBase(this);
        this.server = Server.getInstance();
        this.lastBreak = -1;
        this.chunksPerTick = this.server.getConfig("chunk-sending.per-tick", 4);
        this.spawnThreshold = this.server.getConfig("chunk-sending.spawn-threshold", 56);
        this.spawnLocation = null;
        this.playerData.setGamemode(this.server.getGamemode());
        this.viewDistance = this.server.getViewDistance();
        //this.newPosition = new Vector3(0, 0, 0);
        this.boundingBox = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);
        this.lastSkinChange = -1;

        this.identity = null;

        this.creationTime = System.currentTimeMillis();
    }

    private static boolean hasSubstantiallyMoved(Vector3f oldPos, Vector3f newPos) {
        return oldPos.getFloorX() >> 4 != newPos.getFloorX() >> 4 || oldPos.getFloorZ() >> 4 != newPos.getFloorZ() >> 4;
    }

    @Override
    public Long getFirstPlayed() {
        return this.playerData.getFirstPlayed();
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
    public Long getLastPlayed() {
        return this.playerData.getLastPlayed();
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

    @Override
    public boolean hasPlayedBefore() {
        return this.playerData.getFirstPlayed() > 0;
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
    public void spawnTo(Player player) {
        if (this.spawned && player.spawned && this.isAlive() && player.isAlive() &&
                player.getLevel() == this.getLevel() && player.canSee(this) && !this.isSpectator()) {
            super.spawnTo(player);
        }
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

    @Override
    public void resetFallDistance() {
        super.resetFallDistance();
        if (this.inAirTicks != 0) {
            this.startAirTicks = 5;
        }
        this.inAirTicks = 0;
        this.highestPosition = this.getPosition().getY();
    }

    public void setEnableClientCommand(boolean enable) {
        this.enableClientCommand = enable;
        SetCommandsEnabledPacket packet = new SetCommandsEnabledPacket();
        packet.setCommandsEnabled(enable);
        this.sendPacket(packet);
        //if (enable) this.sendCommandData();
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

    public void sendCommandData() {
        if (!spawned) {
            return;
        }
        AvailableCommandsPacket packet = new AvailableCommandsPacket();
        List<CommandData> commandData = packet.getCommands();
        for (Command command : this.server.getCommandMap().getCommands().values()) {
            if (!command.testPermissionSilent(this)) {
                continue;
            }
            commandData.add(command.generateCustomCommandData(this));
        }
        this.sendPacket(packet);
    }

    public void removeAchievement(String achievementId) {
        this.playerData.getAchievements().remove(achievementId);
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

    public boolean hasAchievement(String achievementId) {
        return this.playerData.getAchievements().contains(achievementId);
    }

    @Override
    public void setSkin(SerializedSkin skin) {
        super.setSkin(skin);
        if (this.spawned) {
            this.server.updatePlayerListData(this.getServerId(), this.getUniqueId(), this.getDisplayName(), skin, this.getLoginChainData().getXUID());
        }
    }

    public String getAddress() {
        return this.getSocketAddress().getAddress().getHostAddress();
    }

    public InetSocketAddress getSocketAddress() {
        return this.session.getAddress();
    }

    public int getPort() {
        return this.getSocketAddress().getPort();
    }

    public boolean isSleeping() {
        return this.sleeping != null;
    }

    public int getInAirTicks() {
        return this.inAirTicks;
    }

    public Vector3f getNextPosition() {
        return this.newPosition != null ? this.newPosition : this.getPosition();
    }

    /**
     * Returns whether the player is currently using an item (right-click and hold).
     *
     * @return bool
     */
    public boolean isUsingItem() {
        return this.data.getFlag(USING_ITEM) && this.startAction > -1;
    }

    public String getButtonText() {
        return this.buttonText;
    }

    public void setUsingItem(boolean value) {
        this.startAction = value ? this.server.getTick() : -1;
        this.data.setFlag(USING_ITEM, value);
    }

    public void setButtonText(String text) {
        this.buttonText = text;
        this.data.setString(INTERACTIVE_TAG, this.buttonText);
    }

    public Location getSpawn() {
        if (this.spawnLocation != null && this.spawnLocation.getLevel() != null) {
            return this.spawnLocation;
        } else {
            return this.server.getDefaultLevel().getSafeSpawn();
        }
    }

    private static int distance(int centerX, int centerZ, int x, int z) {
        int dx = centerX - x;
        int dz = centerZ - z;
        return dx * dx + dz * dz;
    }

    public void setSpawn(Location location) {
        checkNotNull(location, "location");
        this.spawnLocation = location;
        SetSpawnPositionPacket packet = new SetSpawnPositionPacket();
        packet.setSpawnType(SetSpawnPositionPacket.Type.PLAYER_SPAWN);
        packet.setBlockPosition(this.spawnLocation.getPosition().toInt());
        this.sendPacket(packet);
    }

    protected void doFirstSpawn() {
        this.spawned = true;

        Location loc = this.getLevel().getSafeSpawn(this.getLocation());

        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(this, loc, true);

        this.server.getPluginManager().callEvent(respawnEvent);

        loc = respawnEvent.getRespawnLocation();

        if (this.getHealth() <= 0) {
            loc = this.getSpawn();

            RespawnPacket respawnPacket = new RespawnPacket();
            respawnPacket.setPosition(loc.getPosition());
            respawnPacket.setState(RespawnPacket.State.SERVER_SEARCHING);
            this.sendPacket(respawnPacket);
        }

        this.sendPlayStatus(PlayStatusPacket.Status.PLAYER_SPAWN);

        this.noDamageTicks = 60;

        this.getChunkManager().getLoadedChunks().forEach((LongConsumer) chunkKey -> {
            int chunkX = Chunk.fromKeyX(chunkKey);
            int chunkZ = Chunk.fromKeyZ(chunkKey);
            for (Entity entity : this.getLevel().getLoadedChunkEntities(chunkX, chunkZ)) {
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

        this.teleport(loc, null); // Prevent PlayerTeleportEvent during player spawn

        if (!this.isSpectator()) {
            this.spawnToAll();
        }

        //todo Updater

        //Weather
        if (this.getLevel().isRaining() || this.getLevel().isThundering()) {
            this.getLevel().sendWeather(this);
        }

        //FoodLevel
        PlayerFood food = this.getFoodData();
        if (food.getLevel() != food.getMaxLevel()) {
            food.sendFoodLevel();
        }
    }

    public long getPing() {
        return this.session.getLatency();
    }

    public boolean sleepOn(Vector3i pos) {
        if (!this.isOnline()) {
            return false;
        }

        for (Entity p : this.getLevel().getNearbyEntities(this.boundingBox.grow(2, 1, 2), this)) {
            if (p instanceof Player) {
                if (((Player) p).sleeping != null && pos.distance(((Player) p).sleeping) <= 0.1) {
                    return false;
                }
            }
        }

        PlayerBedEnterEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerBedEnterEvent(this, this.getLevel().getBlock(pos)));
        if (ev.isCancelled()) {
            return false;
        }

        this.sleeping = pos.clone();
        this.teleport(Location.from(pos.toFloat().add(0.5, 0.5, 0.5), this.getYaw(), this.getPitch(), this.getLevel()), null);

        this.data.setVector3i(BED_RESPAWN_POS, pos);
        this.data.setBoolean(CAN_START_SLEEP, true);

        this.setSpawn(Location.from(pos.toFloat(), this.getLevel()));

        this.getLevel().sleepTicks = 60;

        return true;
    }

    public void stopSleep() {
        if (this.sleeping != null) {
            this.server.getPluginManager().callEvent(new PlayerBedLeaveEvent(this, this.getLevel().getBlock(this.sleeping)));

            this.sleeping = null;
            this.data.setVector3i(BED_RESPAWN_POS, Vector3i.ZERO);
            this.data.setBoolean(CAN_START_SLEEP, false);


            this.getLevel().sleepTicks = 0;

            AnimatePacket pk = new AnimatePacket();
            pk.setRuntimeEntityId(this.getRuntimeId());
            pk.setAction(AnimatePacket.Action.WAKE_UP);
            this.sendPacket(pk);
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

        this.playerData.getAchievements().add(achievementId);
        achievement.broadcast(this);
        return true;
    }

    public int getGamemode() {
        return this.playerData.getGamemode();
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
    public boolean sendPacket(BedrockPacket packet) {
        if (!this.connected) {
            return false;
        }

        PlayerPacketSendEvent event = new PlayerPacketSendEvent(this, packet);
        this.server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
            log.trace("Outbound {}: {}", this.getName(), packet);
        }

        sendPacketInternal(packet);
        return true;
    }

    public void sendPacketInternal(BedrockPacket packet) {
        try (Timing ignored = Timings.getSendDataPacketTiming(packet).startTiming()) {
            this.session.sendPacket(packet);
        }
    }

    @Deprecated
    public void sendSettings() {
        this.getAdventureSettings().update();
    }

    public boolean isSurvival() {
        return this.getGamemode() == SURVIVAL;
    }

    public boolean isCreative() {
        return this.getGamemode() == CREATIVE;
    }

    public boolean isSpectator() {
        return this.getGamemode() == SPECTATOR;
    }

    public boolean isAdventure() {
        return this.getGamemode() == ADVENTURE;
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
    public boolean sendPacketImmediately(BedrockPacket packet) {
        if (!this.connected) {
            return false;
        }

        try (Timing ignored = Timings.getSendDataPacketTiming(packet).startTiming()) {
            this.session.sendPacketImmediately(packet);
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
        for (Entity entity : this.getLevel().getNearbyEntities(this.boundingBox.grow(1, 0.5f, 1), this)) {
            this.getLevel().scheduleEntityUpdate(entity);

            if (!entity.isAlive() || !this.isAlive()) {
                continue;
            }

            this.pickupEntity(entity, true);
        }
    }

    public boolean setGamemode(int gamemode, boolean clientSide, AdventureSettings newSettings) {
        if (gamemode < 0 || gamemode > 3 || this.getGamemode() == gamemode) {
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

        this.playerData.setGamemode(gamemode);

        if (this.isSpectator()) {
            this.keepMovement = true;
            this.despawnFromAll();
        } else {
            this.keepMovement = false;
            this.spawnToAll();
        }

        this.playerData.setGamemode(this.getGamemode());

        if (!clientSide) {
            SetPlayerGameTypePacket pk = new SetPlayerGameTypePacket();
            pk.setGamemode(getClientFriendlyGamemode(gamemode));
            this.sendPacket(pk);
        }

        this.setAdventureSettings(ev.getNewAdventureSettings());

        if (this.isSpectator()) {
            this.getAdventureSettings().set(Type.FLYING, true);
            this.teleport(this.getPosition().add(0, 0.1, 0));

            InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
            inventoryContentPacket.setContainerId(ContainerIds.SPECIAL_CREATIVE);
            this.sendPacket(inventoryContentPacket);
        } else {
            if (this.isSurvival()) {
                this.getAdventureSettings().set(Type.FLYING, false);
            }
            InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
            inventoryContentPacket.setContainerId(ContainerIds.SPECIAL_CREATIVE);
            inventoryContentPacket.setContents(Item.toNetwork(Item.getCreativeItems().toArray(new Item[0])));
            this.sendPacket(inventoryContentPacket);
        }

        this.resetFallDistance();

        this.getInventory().sendContents(this);
        this.getInventory().sendContents(this.getViewers());
        this.getInventory().sendHeldItem(this.hasSpawned);

        this.getInventory().sendCreativeContents();
        return true;
    }

    @Override
    public boolean setMotion(Vector3f motion) {
        if (super.setMotion(motion)) {
            if (this.chunk != null) {
                this.addMotion(this.getMotion());  //Send to others
                SetEntityMotionPacket packet = new SetEntityMotionPacket();
                packet.setRuntimeEntityId(this.getRuntimeId());
                packet.setMotion(motion);
                this.sendPacket(packet);  //Send to self
            }

            if (this.getMotion().getY() > 0) {
                //todo: check this
                this.startAirTicks = (int) ((-(Math.log(this.getGravity() / (this.getGravity() + this.getDrag() * this.getMotion().getY()))) / this.getDrag()) * 2 + 5);
            }

            return true;
        }

        return false;
    }

    public void sendAttributes() {
        UpdateAttributesPacket pk = new UpdateAttributesPacket();
        pk.setRuntimeEntityId(this.getRuntimeId());
        List<com.nukkitx.protocol.bedrock.data.Attribute> attributes = pk.getAttributes();
        attributes.add(Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(this.getMaxHealth()).setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0).toNetwork());
        attributes.add(Attribute.getAttribute(Attribute.MAX_HUNGER).setValue(this.getFoodData().getLevel()).toNetwork());
        attributes.add(Attribute.getAttribute(Attribute.MOVEMENT_SPEED).setValue(this.getMovementSpeed()).toNetwork());
        attributes.add(Attribute.getAttribute(Attribute.EXPERIENCE_LEVEL).setValue(this.getExperienceLevel()).toNetwork());
        attributes.add(Attribute.getAttribute(Attribute.EXPERIENCE).setValue(((float) this.getExperience()) / calculateRequireExperience(this.getExperienceLevel())).toNetwork());
        this.sendPacket(pk);
    }

    @Override
    protected void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        if (!this.onGround || movX != 0 || movY != 0 || movZ != 0) {
            boolean onGround = false;

            AxisAlignedBB bb = this.boundingBox.clone();
            bb.setMaxY(bb.getMinY() + 0.5f);
            bb.setMinY(bb.getMinY() - 1);

            AxisAlignedBB realBB = this.boundingBox.clone();
            realBB.setMaxY(realBB.getMinY() + 0.1f);
            realBB.setMinY(realBB.getMinY() - 0.2f);

            int minX = NukkitMath.floorDouble(bb.getMinX());
            int minY = NukkitMath.floorDouble(bb.getMinY());
            int minZ = NukkitMath.floorDouble(bb.getMinZ());
            int maxX = NukkitMath.ceilDouble(bb.getMaxX());
            int maxY = NukkitMath.ceilDouble(bb.getMaxY());
            int maxZ = NukkitMath.ceilDouble(bb.getMaxZ());

            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = this.getLevel().getBlock(x, y, z);

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
        if (canInteract(this.getPosition(), interactDistance)) {
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
        Vector3f currentPos = this.getPosition();
        float distanceSquared = newPosition.distanceSquared(currentPos);

        boolean revert = false;
        if ((distanceSquared / ((float) (tickDiff * tickDiff))) > 100 && (newPosition.getY() - currentPos.getY()) > -5) {
            log.debug("{} moved too fast!", this.getName());
            revert = true;
        } else {
            if (this.chunk == null) {
                Chunk chunk = this.getLevel().getLoadedChunk(newPosition);
                if (chunk == null) {
                    revert = true;
                } else {
                    this.chunk = chunk;
                }
            }
        }

        float tdx = newPosition.getX() - currentPos.getX();
        float tdz = newPosition.getZ() - currentPos.getZ();
        double distance = Math.sqrt(tdx * tdx + tdz * tdz);

        if (!revert && distanceSquared != 0) {
            float dx = newPosition.getX() - currentPos.getX();
            float dy = newPosition.getY() - currentPos.getY();
            float dz = newPosition.getZ() - currentPos.getZ();

            this.fastMove(dx, dy, dz);
            if (this.newPosition == null) {
                return; //maybe solve that in better way
            }

            double diffX = currentPos.getX() - newPosition.getX();
            double diffY = currentPos.getY() - newPosition.getY();
            double diffZ = currentPos.getZ() - newPosition.getZ();

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
                                    log.warn(this.getServer().getLanguage().translate("nukkit.player.invalidMove", this.getName()));
                                }
                            }
                        }
                    }
                }


                this.position = newPosition;
                float radius = this.getWidth() / 2;
                this.boundingBox.setBounds(this.position.getX() - radius, this.position.getY(), this.position.getZ() - radius,
                        this.position.getX() + radius, this.position.getY() + this.getHeight(), this.position.getZ() + radius);
            }
        }

        Location from = Location.from(this.lastPosition, this.lastYaw, this.lastPitch, this.getLevel());
        Location to = this.getLocation();

        double delta = Math.pow(this.lastPosition.getX() - to.getX(), 2) + Math.pow(this.lastPosition.getY() - to.getY(), 2) + Math.pow(this.position.getZ() - to.getZ(), 2);
        double deltaAngle = Math.abs(this.lastYaw - to.getYaw()) + Math.abs(this.lastPitch - to.getPitch());

        if (!revert && (delta > 0.0001d || deltaAngle > 1d)) {
            boolean isFirst = this.firstMove;

            this.firstMove = false;
            this.lastPosition = to.getPosition();

            this.lastYaw = to.getYaw();
            this.lastPitch = to.getPitch();

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
                        this.addMovement(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch(), this.getYaw());
                    }
                    //Biome biome = Biome.biomes[level.getBiomeId(this.getFloorX(), this.getFloorZ())];
                    //sendTip(biome.getName() + " (" + biome.doesOverhang() + " " + biome.getBaseHeight() + "-" + biome.getHeightVariation() + ")");
                } else {
                    this.blocksAround = blocksAround;
                    this.collisionBlocks = collidingBlocks;
                }
            }

            this.speed = from.getPosition().min(to.getPosition());
        } else {
            this.speed = Vector3f.ZERO;
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

            this.lastPosition = from.getPosition();

            this.lastYaw = from.getYaw();
            this.lastPitch = from.getPitch();

            // We have to send slightly above otherwise the player will fall into the ground.
            Location location = from.add(0, 0.00001, 0);
            log.debug("processMovement REVERTING");
            this.sendPosition(location.getPosition(), from.getYaw(), from.getPitch(), MovePlayerPacket.Mode.RESET);
            //this.sendSettings();
            this.forceMovement = location.getPosition();
        } else {
            this.forceMovement = null;
        }

        this.newPosition = null;
    }

    @Override
    public void addMovement(double x, double y, double z, double yaw, double pitch, double headYaw) {
        this.sendPosition(Vector3f.from(x, y, z), yaw, pitch, MovePlayerPacket.Mode.NORMAL, getViewers());
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
                if (this.getPosition().distance(fishing.getPosition()) > 80) {
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

                if (this.getServer().getDifficulty() == 0 && this.getLevel().getGameRules().get(GameRules.NATURAL_REGENERATION)) {
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
                        if (this.getLevel().canBlockSeeSky(this.getPosition())) {
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
                        this.highestPosition = this.getPosition().getY();
                    } else {
                        if (this.checkMovement && !this.isGliding() && !server.getAllowFlight() && !this.getAdventureSettings().get(Type.ALLOW_FLIGHT) && this.inAirTicks > 20 && !this.isSleeping() && !this.isImmobile() && !this.isSwimming() && this.vehicle == null && !this.hasEffect(Effect.LEVITATION)) {
                            double expectedVelocity = (-this.getGravity()) / ((double) this.getDrag()) - ((-this.getGravity()) / ((double) this.getDrag())) * Math.exp(-((double) this.getDrag()) * ((double) (this.inAirTicks - this.startAirTicks)));
                            double diff = (this.speed.getY() - expectedVelocity) * (this.speed.getY() - expectedVelocity);

                            Identifier block = this.getLevel().getBlock(this.getPosition()).getId();
                            boolean ignore = block == BlockIds.LADDER || block == BlockIds.VINE || block == BlockIds.WEB;

                            if (!this.hasEffect(Effect.JUMP) && diff > 0.6 && expectedVelocity < this.speed.getY() && !ignore) {
                                if (this.inAirTicks < 100) {
                                    this.setMotion(Vector3f.from(0, expectedVelocity, 0));
                                } else if (this.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server")) {
                                    return false;
                                }
                            }
                            if (ignore) {
                                this.resetFallDistance();
                            }
                        }

                        float curY = this.getPosition().getY();
                        if (curY > highestPosition) {
                            this.highestPosition = curY;
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

            this.data.update();
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

            Set<Entity> nearbyEntities = this.getLevel().getNearbyEntities(boundingBox.grow(maxDistance, maxDistance, maxDistance), this);

            // get all blocks in looking direction until the max interact distance is reached (it's possible that startblock isn't found!)

            Vector3f position = this.getPosition().add(0, getEyeHeight(), 0);
            for (Vector3i pos : BlockRayTrace.of(position, getDirectionVector(), maxDistance)) {
                Block block = this.getLevel().getLoadedBlock(pos);
                if (block == null) {
                    break;
                }

                entity = getEntityAtPosition(nearbyEntities, pos.getX(), pos.getY(), pos.getZ());

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
        if (this.getPosition().distanceSquared(pos) > maxDistance * maxDistance) {
            return false;
        }

        Vector2f dV = this.getDirectionPlane();
        double dot = dV.dot(this.getPosition().toVector2(true));
        double dot1 = dV.dot(pos.toVector2(true));
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

        Vector3f pos = this.getPosition();

        StartGamePacket startGamePacket = new StartGamePacket();
        startGamePacket.setUniqueEntityId(this.getUniqueId());
        startGamePacket.setRuntimeEntityId(this.getRuntimeId());
        startGamePacket.setPlayerGamemode(getClientFriendlyGamemode(this.getGamemode()));
        startGamePacket.setPlayerPosition(pos);
        startGamePacket.setRotation(Vector2f.from(this.getYaw(), this.getPitch()));
        startGamePacket.setSeed(-1);
        startGamePacket.setDimensionId(0);
        startGamePacket.setLevelGamemode(getClientFriendlyGamemode(this.getGamemode()));
        startGamePacket.setDifficulty(this.server.getDifficulty());
        startGamePacket.setDefaultSpawn(this.getSpawn().getPosition().toInt());
        startGamePacket.setAchievementsDisabled(true);
        startGamePacket.setTime(this.getLevel().getTime());
        startGamePacket.setRainLevel(0);
        startGamePacket.setLightningLevel(0);
        startGamePacket.setCommandsEnabled(this.isEnableClientCommand());
        startGamePacket.setMultiplayerGame(true);
        startGamePacket.setBroadcastingToLan(true);
        this.getLevel().getGameRules().toNetwork(startGamePacket.getGamerules());
        startGamePacket.setLevelId(""); // This is irrelevant since we have multiple levels
        startGamePacket.setWorldName(this.getServer().getNetwork().getName()); // We might as well use the MOTD instead of the default level name
        startGamePacket.setGeneratorId(1); // 0 old, 1 infinite, 2 flat - Has no effect to my knowledge
        startGamePacket.setItemEntries(ItemRegistry.get().getItemEntries());
        startGamePacket.setXblBroadcastMode(GamePublishSetting.PUBLIC);
        startGamePacket.setPlatformBroadcastMode(GamePublishSetting.PUBLIC);
        startGamePacket.setDefaultPlayerPermission(PlayerPermission.MEMBER);
        startGamePacket.setServerChunkTickRange(4);
        startGamePacket.setVanillaVersion("*");
        startGamePacket.setPremiumWorldTemplateId("");
        startGamePacket.setMultiplayerCorrelationId("");
        startGamePacket.setBlockPalette(BlockRegistry.get().getPaletteTag());
        startGamePacket.setItemEntries(ItemRegistry.get().getItemEntries());
        this.sendPacket(startGamePacket);

        UpdateBlockPropertiesPacket updateBlockPropertiesPacket = new UpdateBlockPropertiesPacket();
        updateBlockPropertiesPacket.setProperties(BlockRegistry.get().getPropertiesTag());
        this.sendPacket(updateBlockPropertiesPacket);

        this.setEnableClientCommand(true);

        this.getAdventureSettings().update();

        this.sendPotionEffects(this);
        this.sendData(this);
        this.getInventory().sendContents(this);
        this.getInventory().sendArmorContents(this);

        if (this.getGamemode() == Player.SPECTATOR) {
            InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
            inventoryContentPacket.setContainerId(ContainerIds.CREATIVE);
            this.sendPacket(inventoryContentPacket);
        } else {
            this.getInventory().sendCreativeContents();
        }

        SetTimePacket setTimePacket = new SetTimePacket();
        setTimePacket.setTime(this.getLevel().getTime());
        this.sendPacket(setTimePacket);

        this.getServer().sendRecipeList(this);

        this.loggedIn = true;

        this.getLevel().sendTime(this);

        this.setMovementSpeed(DEFAULT_SPEED);
        this.sendAttributes();
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setCanClimb(true);

        log.info(this.getServer().getLanguage().translate("nukkit.player.logIn",
                TextFormat.AQUA + this.username + TextFormat.WHITE,
                this.getAddress(),
                this.getPort(),
                this.getUniqueId(),
                this.getLevel().getName(),
                NukkitMath.round(pos.getX(), 4),
                NukkitMath.round(pos.getY(), 4),
                NukkitMath.round(pos.getZ(), 4)
        ));

        if (this.isOp() || this.hasPermission("nukkit.textcolor")) {
            this.setRemoveFormat(false);
        }

        this.server.addOnlinePlayer(this);
        this.server.onPlayerCompleteLoginSequence(this);

        BiomeDefinitionListPacket biomeDefinitionListPacket = new BiomeDefinitionListPacket();
        biomeDefinitionListPacket.setTag(EnumBiome.BIOME_DEFINITIONS);
        this.sendPacket(biomeDefinitionListPacket);

        AvailableEntityIdentifiersPacket availableEntityIdentifiersPacket = new AvailableEntityIdentifiersPacket();
        availableEntityIdentifiersPacket.setTag(EntityRegistry.get().getEntityIdentifiersPalette());
        this.sendPacket(availableEntityIdentifiersPacket);
    }

    public void checkNetwork() {
        if (!this.isConnected()) {
            return;
        }

        try (Timing ignore = Timings.playerNetworkReceiveTimer.startTiming()) {
            BedrockPacket packet;
            while ((packet = this.inboundQueue.poll()) != null) {
                this.onDataPacket(packet);
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
        } else if (this.server.getIPBans().isBanned(this.getSocketAddress().getAddress().getHostAddress())) {
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
            CompoundTagBuilder tag = CompoundTag.builder();
            oldPlayer.saveAdditionalData(tag);
            nbt = tag.buildRootTag();
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

        this.loadAdditionalData(nbt);

        if (this.server.getForceGamemode()) {
            this.playerData.setGamemode(this.server.getGamemode());
        }

        this.adventureSettings = new AdventureSettings(this)
                .set(Type.WORLD_IMMUTABLE, isAdventure() || isSpectator())
                .set(Type.WORLD_BUILDER, !isAdventure() && !isSpectator())
                .set(Type.AUTO_JUMP, true)
                .set(Type.ALLOW_FLIGHT, isCreative())
                .set(Type.NO_CLIP, isSpectator());

        Level level;
        if ((level = this.server.getLevelByName(this.playerData.getLevel())) == null || !isAlive()) {
            this.level = this.server.getDefaultLevel();
        } else {
            this.level = level;
        }

        if (this.server.getAutoSave()) {
            this.server.saveOfflinePlayerData(this.identity, nbt, true);
        }

        this.sendPlayStatus(PlayStatusPacket.Status.LOGIN_SUCCESS);
        this.server.onPlayerLogin(this);

        super.init(this.getLocation());

        if (this.isSpectator()) this.keepMovement = true;

        this.forceMovement = this.teleportPosition = this.getPosition();

        this.sendPacket(this.server.getPackManager().getPacksInfos());
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
                    this.server.broadcastMessage(this.getServer().getLanguage().translate(chatEvent.getFormat(), chatEvent.getPlayer().getDisplayName(), chatEvent.getMessage()), chatEvent.getRecipients());
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

    public void handleDataPacket(BedrockPacket packet) {
        this.inboundQueue.offer(packet);
    }

    private void onDataPacket(BedrockPacket packet) {
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
            switch (packet.getPacketType()) {
                case LOGIN:
                    if (this.loggedIn) {
                        break;
                    }

                    LoginPacket loginPacket = (LoginPacket) packet;

                    int protocolVersion = loginPacket.getProtocolVersion();
                    BedrockPacketCodec packetCodec = ProtocolInfo.getPacketCodec(protocolVersion);

                    if (packetCodec == null) {
                        String message;
                        if (protocolVersion < ProtocolInfo.getDefaultProtocolVersion()) {
                            message = "disconnectionScreen.outdatedClient";

                            this.sendPlayStatus(PlayStatusPacket.Status.FAILED_CLIENT);
                        } else {
                            message = "disconnectionScreen.outdatedServer";

                            this.sendPlayStatus(PlayStatusPacket.Status.FAILED_SERVER);
                        }
                        this.close("", message, false);
                        break;
                    }
                    this.session.setPacketCodec(packetCodec);

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
                            e = new PlayerAsyncPreLoginEvent(username, identity, Player.this.getSocketAddress());
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
                case RESOURCE_PACK_CLIENT_RESPONSE:
                    ResourcePackClientResponsePacket responsePacket = (ResourcePackClientResponsePacket) packet;
                    switch (responsePacket.getStatus()) {
                        case REFUSED:
                            this.close("", "disconnectionScreen.noReason");
                            break;
                        case SEND_PACKS:
                            for (String entry : responsePacket.getPackIds()) {
                                Pack pack = this.server.getPackManager().getPackByIdVersion(entry);
                                if (pack == null) {
                                    this.close("", "disconnectionScreen.resourcePack");
                                    break;
                                }

                                ResourcePackDataInfoPacket dataInfoPacket = new ResourcePackDataInfoPacket();
                                dataInfoPacket.setPackId(pack.getId());
                                dataInfoPacket.setPackVersion(pack.getVersion().toString());
                                dataInfoPacket.setMaxChunkSize(1048576); //megabyte
                                dataInfoPacket.setChunkCount(pack.getSize() / dataInfoPacket.getMaxChunkSize());
                                dataInfoPacket.setCompressedPackSize(pack.getSize());
                                dataInfoPacket.setHash(pack.getHash());
                                dataInfoPacket.setType(pack.getType());
                                this.sendPacket(dataInfoPacket);
                            }
                            break;
                        case HAVE_ALL_PACKS:
                            this.sendPacket(this.server.getPackManager().getPackStack());
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
                case RESOURCE_PACK_CHUNK_REQUEST:
                    ResourcePackChunkRequestPacket requestPacket = (ResourcePackChunkRequestPacket) packet;
                    Pack resourcePack = this.server.getPackManager().getPackByIdVersion(
                            requestPacket.getPackId() + "_" + requestPacket.getPackVersion());
                    if (resourcePack == null) {
                        this.close("", "disconnectionScreen.resourcePack");
                        break;
                    }

                    ResourcePackChunkDataPacket dataPacket = new ResourcePackChunkDataPacket();
                    dataPacket.setPackId(requestPacket.getPackId());
                    dataPacket.setPackVersion(requestPacket.getPackVersion());
                    dataPacket.setChunkIndex(requestPacket.getChunkIndex());
                    dataPacket.setData(resourcePack.getChunk(1048576 * requestPacket.getChunkIndex(), 1048576));
                    dataPacket.setProgress(1048576 * requestPacket.getChunkIndex());
                    this.sendPacket(dataPacket);
                    break;
                case PLAYER_SKIN:
                    PlayerSkinPacket skinPacket = (PlayerSkinPacket) packet;
                    SerializedSkin skin = skinPacket.getSkin();

                    if (!skin.isValid()) {
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
                case PLAYER_INPUT:
                    if (!this.isAlive() || !this.spawned) {
                        break;
                    }
                    PlayerInputPacket ipk = (PlayerInputPacket) packet;
                    if (vehicle instanceof EntityAbstractMinecart) {
                        ((EntityAbstractMinecart) vehicle).setCurrentSpeed(ipk.getInputMotion().getY());
                    }
                    break;
                case MOVE_PLAYER:
                    if (this.teleportPosition != null) {
                        break;
                    }

                    MovePlayerPacket movePlayerPacket = (MovePlayerPacket) packet;
                    Vector3f newPos = Vector3f.from(movePlayerPacket.getPosition().sub(0, this.getEyeHeight(), 0));
                    Vector3f currentPos = this.getPosition();

                    float yaw = movePlayerPacket.getRotation().getX() % 360;
                    float pitch = movePlayerPacket.getRotation().getY() % 360;

                    if (yaw < 0) {
                        yaw += 360;
                    }

                    if (newPos.distanceSquared(currentPos) < 0.01 && yaw == this.getYaw() && pitch == this.getPitch()) {
                        break;
                    }

                    if (currentPos.distance(newPos) > 50) {
                        log.debug("MovePlayerPacket too far REVERTING");
                        this.sendPosition(currentPos, yaw, pitch, MovePlayerPacket.Mode.RESET);
                        break;
                    }

                    boolean revert = false;
                    if (!this.isAlive() || !this.spawned) {
                        revert = true;
                        this.forceMovement = currentPos;
                    }


                    if (this.forceMovement != null && (newPos.distanceSquared(this.forceMovement) > 0.1 || revert)) {
                        log.debug("MovePlayerPacket forceMovement {} REVERTING {}", this.forceMovement, newPos);
                        this.sendPosition(this.forceMovement, yaw, pitch, MovePlayerPacket.Mode.RESET);
                    } else {
                        this.setRotation(yaw, pitch);
                        this.newPosition = newPos;
                        this.forceMovement = null;
                    }


                    if (vehicle != null) {
                        if (vehicle instanceof EntityBoat) {
                            vehicle.setPositionAndRotation(newPos.sub(0, 1, 0), (yaw + 90) % 360, 0);
                        }
                    }

                    break;
                case ADVENTURE_SETTINGS:
                    //TODO: player abilities, check for other changes
                    AdventureSettingsPacket adventureSettingsPacket = (AdventureSettingsPacket) packet;
                    Set<AdventureSettingsPacket.Flag> flags = adventureSettingsPacket.getFlags();
                    if (!server.getAllowFlight() && flags.contains(AdventureSettingsPacket.Flag.FLYING) && !this.getAdventureSettings().get(Type.ALLOW_FLIGHT)) {
                        this.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server");
                        break;
                    }
                    PlayerToggleFlightEvent playerToggleFlightEvent = new PlayerToggleFlightEvent(this, flags.contains(AdventureSettingsPacket.Flag.FLYING));
                    this.server.getPluginManager().callEvent(playerToggleFlightEvent);
                    if (playerToggleFlightEvent.isCancelled()) {
                        this.getAdventureSettings().update();
                    } else {
                        this.getAdventureSettings().set(Type.FLYING, playerToggleFlightEvent.isFlying());
                    }
                    break;
                case MOB_EQUIPMENT:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    MobEquipmentPacket mobEquipmentPacket = (MobEquipmentPacket) packet;

                    Item serverItem = this.getInventory().getItem(mobEquipmentPacket.getHotbarSlot());
                    Item clientItem = Item.fromNetwork(mobEquipmentPacket.getItem());

                    if (!serverItem.equals(clientItem)) {
                        log.debug("Tried to equip " + clientItem + " but have " + serverItem + " in target slot");
                        this.getInventory().sendContents(this);
                        return;
                    }

                    this.getInventory().equipItem(mobEquipmentPacket.getHotbarSlot());

                    this.setUsingItem(false);

                    break;
                case PLAYER_ACTION:
                    PlayerActionPacket playerActionPacket = (PlayerActionPacket) packet;
                    if (!this.spawned || (!this.isAlive() &&
                            playerActionPacket.getAction() != PlayerActionPacket.Action.RESPAWN &&
                            playerActionPacket.getAction() != PlayerActionPacket.Action.DIMENSION_CHANGE_REQUEST)) {
                        break;
                    }

                    playerActionPacket.setRuntimeEntityId(this.getRuntimeId());
                    currentPos = this.getPosition();
                    Vector3i blockPos = playerActionPacket.getBlockPosition();
                    BlockFace face = BlockFace.fromIndex(playerActionPacket.getFace());

                    actionswitch:
                    switch (playerActionPacket.getAction()) {
                        case START_BREAK:
                            long currentBreak = System.currentTimeMillis();
                            Vector3i currentBreakPosition = playerActionPacket.getBlockPosition();
                            // HACK: Client spams multiple left clicks so we need to skip them.
                            if ((lastBreakPosition.equals(currentBreakPosition) && (currentBreak - this.lastBreak) < 10) || currentPos.distanceSquared(blockPos.toFloat()) > 100) {
                                break;
                            }
                            Block target = this.getLevel().getBlock(blockPos);
                            PlayerInteractEvent playerInteractEvent = new PlayerInteractEvent(this, this.getInventory().getItemInHand(), target, face, target.getId() == AIR ? Action.LEFT_CLICK_AIR : Action.LEFT_CLICK_BLOCK);
                            this.getServer().getPluginManager().callEvent(playerInteractEvent);
                            if (playerInteractEvent.isCancelled()) {
                                this.getInventory().sendHeldItem(this);
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
                                this.getLevel().setBlock(block.getPosition(), Block.get(AIR), true);
                                this.getLevel().addLevelSoundEvent(block.getPosition(), SoundEvent.EXTINGUISH_FIRE);
                                break;
                            }
                            if (!this.isCreative()) {
                                //improved this to take stuff like swimming, ladders, enchanted tools into account, fix wrong tool break time calculations for bad tools (pmmp/PocketMine-MP#211)
                                //Done by lmlstarqaq
                                double breakTime = Math.ceil(target.getBreakTime(this.getInventory().getItemInHand(), this) * 20);
                                if (breakTime > 0) {
                                    LevelEventPacket levelEvent = new LevelEventPacket();
                                    levelEvent.setType(LevelEventType.BLOCK_START_BREAK);
                                    levelEvent.setPosition(blockPos.toFloat());
                                    levelEvent.setData((int) (65535 / breakTime));
                                    this.getLevel().addChunkPacket(blockPos, levelEvent);
                                }
                            }

                            this.breakingBlock = target;
                            this.lastBreak = currentBreak;
                            this.lastBreakPosition = currentBreakPosition;
                            break;

                        case ABORT_BREAK:
                        case STOP_BREAK:
                            LevelEventPacket levelEvent = new LevelEventPacket();
                            levelEvent.setType(LevelEventType.BLOCK_STOP_BREAK);
                            levelEvent.setPosition(blockPos.toFloat());
                            levelEvent.setData(0);
                            this.getLevel().addChunkPacket(blockPos, levelEvent);
                            this.breakingBlock = null;
                            break;
                        case GET_UPDATED_BLOCK:
                            break; //TODO
                        case DROP_ITEM:
                            break; //TODO
                        case STOP_SLEEP:
                            this.stopSleep();
                            break;
                        case RESPAWN:
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

                            Location respawnLoc = playerRespawnEvent.getRespawnLocation();

                            this.teleport(respawnLoc, null);

                            this.setSprinting(false);
                            this.setSneaking(false);

                            this.data.setShort(EntityData.AIR, 400);
                            this.deadTicks = 0;
                            this.noDamageTicks = 60;

                            this.removeAllEffects();
                            this.setHealth(this.getMaxHealth());
                            this.getFoodData().setLevel(20, 20);

                            this.sendData(this);

                            this.setMovementSpeed(DEFAULT_SPEED);

                            this.getAdventureSettings().update();
                            this.getInventory().sendContents(this);
                            this.getInventory().sendArmorContents(this);

                            this.spawnToAll();
                            this.scheduleUpdate();
                            break;
                        case JUMP:
                            break packetswitch;
                        case START_SPRINT:
                            PlayerToggleSprintEvent playerToggleSprintEvent = new PlayerToggleSprintEvent(this, true);
                            this.server.getPluginManager().callEvent(playerToggleSprintEvent);
                            if (playerToggleSprintEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSprinting(true);
                            }
                            break packetswitch;
                        case STOP_SPRINT:
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
                        case START_SNEAK:
                            PlayerToggleSneakEvent playerToggleSneakEvent = new PlayerToggleSneakEvent(this, true);
                            this.server.getPluginManager().callEvent(playerToggleSneakEvent);
                            if (playerToggleSneakEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSneaking(true);
                            }
                            break packetswitch;
                        case STOP_SNEAK:
                            playerToggleSneakEvent = new PlayerToggleSneakEvent(this, false);
                            this.server.getPluginManager().callEvent(playerToggleSneakEvent);
                            if (playerToggleSneakEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSneaking(false);
                            }
                            break packetswitch;
                        case DIMENSION_CHANGE_REQUEST:
                            this.sendPosition(this.getPosition(), this.getYaw(), this.getPitch(), MovePlayerPacket.Mode.NORMAL);
                            break; //TODO
                        case START_GLIDE:
                            PlayerToggleGlideEvent playerToggleGlideEvent = new PlayerToggleGlideEvent(this, true);
                            this.server.getPluginManager().callEvent(playerToggleGlideEvent);
                            if (playerToggleGlideEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setGliding(true);
                            }
                            break packetswitch;
                        case STOP_GLIDE:
                            playerToggleGlideEvent = new PlayerToggleGlideEvent(this, false);
                            this.server.getPluginManager().callEvent(playerToggleGlideEvent);
                            if (playerToggleGlideEvent.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setGliding(false);
                            }
                            break packetswitch;
                        case CONTINUE_BREAK:
                            if (this.isBreakingBlock()) {
                                block = this.getLevel().getBlock(blockPos);
                                this.getLevel().addParticle(new PunchBlockParticle(blockPos.toFloat(), block, face));
                            }
                            break;
                        case START_SWIMMING:
                            PlayerToggleSwimEvent ptse = new PlayerToggleSwimEvent(this, true);
                            this.server.getPluginManager().callEvent(ptse);

                            if (ptse.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSwimming(true);
                            }
                            break;
                        case STOP_SWIMMING:
                            ptse = new PlayerToggleSwimEvent(this, false);
                            this.server.getPluginManager().callEvent(ptse);

                            if (ptse.isCancelled()) {
                                this.sendData(this);
                            } else {
                                this.setSwimming(false);
                            }
                            break;
                    }
                    this.data.update();

                    this.setUsingItem(false);
                    break;
                case MOB_ARMOR_EQUIPMENT:
                    break;
                case MODAL_FORM_RESPONSE:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    ModalFormResponsePacket modalFormPacket = (ModalFormResponsePacket) packet;

                    FormWindow window;
                    if ((window = formWindows.remove(modalFormPacket.getFormId())) != null) {
                        window.setResponse(modalFormPacket.getFormData().trim());

                        PlayerFormRespondedEvent event = new PlayerFormRespondedEvent(this, modalFormPacket.getFormId(), window);
                        getServer().getPluginManager().callEvent(event);
                    } else if ((window = serverSettings.get(modalFormPacket.getFormId())) != null) {
                        window.setResponse(modalFormPacket.getFormData().trim());

                        PlayerSettingsRespondedEvent event = new PlayerSettingsRespondedEvent(this, modalFormPacket.getFormId(), window);
                        getServer().getPluginManager().callEvent(event);

                        //Set back new settings if not been cancelled
                        if (!event.isCancelled() && window instanceof FormWindowCustom)
                            ((FormWindowCustom) window).setElementsFromResponse();
                    }

                    break;

                case INTERACT:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    this.craftingType = CRAFTING_SMALL;
                    //this.resetCraftingGridType();

                    InteractPacket interactPacket = (InteractPacket) packet;

                    Entity targetEntity = this.getLevel().getEntity(interactPacket.getRuntimeEntityId());

                    if (targetEntity == null || !this.isAlive() || !targetEntity.isAlive()) {
                        break;
                    }

                    if (targetEntity instanceof DroppedItem || targetEntity instanceof EntityArrow || targetEntity instanceof ExperienceOrb) {
                        this.kick(PlayerKickEvent.Reason.INVALID_PVE, "Attempting to interact with an invalid entity");
                        log.warn(this.getServer().getLanguage().translate("nukkit.player.invalidEntity", this.getName()));
                        break;
                    }

                    serverItem = this.getInventory().getItemInHand();

                    switch (interactPacket.getAction()) {
                        case MOUSEOVER:
                            if (interactPacket.getRuntimeEntityId() == 0) {
                                break packetswitch;
                            }
                            this.getServer().getPluginManager().callEvent(new PlayerMouseOverEntityEvent(this, targetEntity));
                            break;
                        case LEAVE_VEHICLE:
                            if (this.vehicle == null) {
                                break;
                            }
                            this.dismount(this.vehicle);
                            break;
                    }
                    break;
                case BLOCK_PICK_REQUEST:
                    BlockPickRequestPacket pickRequestPacket = (BlockPickRequestPacket) packet;
                    com.nukkitx.math.vector.Vector3i pickPos = pickRequestPacket.getBlockPosition();
                    Block block = this.getLevel().getBlock(pickPos.getX(), pickPos.getY(), pickPos.getZ());
                    serverItem = block.toItem();

                    if (pickRequestPacket.isAddUserData()) {
                        BlockEntity blockEntity = this.getLevel().getLoadedBlockEntity(
                                Vector3i.from(pickPos.getX(), pickPos.getY(), pickPos.getZ()));
                        if (blockEntity != null) {
                            CompoundTag nbt = blockEntity.getItemTag();
                            if (nbt != null) {
                                serverItem.addTag(nbt);
                                serverItem.setLore("+(DATA)");
                            }
                        }
                    }

                    PlayerBlockPickEvent pickEvent = new PlayerBlockPickEvent(this, block, serverItem);
                    if (this.isSpectator()) {
                        log.debug("Got block-pick request from " + this.getName() + " when in spectator mode");
                        pickEvent.setCancelled();
                    }

                    this.server.getPluginManager().callEvent(pickEvent);

                    if (!pickEvent.isCancelled()) {
                        boolean itemExists = false;
                        int itemSlot = -1;
                        for (int slot = 0; slot < this.getInventory().getSize(); slot++) {
                            if (this.getInventory().getItem(slot).equals(pickEvent.getItem())) {
                                if (slot < this.getInventory().getHotbarSize()) {
                                    this.getInventory().setHeldItemSlot(slot);
                                } else {
                                    itemSlot = slot;
                                }
                                itemExists = true;
                                break;
                            }
                        }

                        for (int slot = 0; slot < this.getInventory().getHotbarSize(); slot++) {
                            if (this.getInventory().getItem(slot).isNull()) {
                                if (!itemExists && this.isCreative()) {
                                    this.getInventory().setHeldItemSlot(slot);
                                    this.getInventory().setItemInHand(pickEvent.getItem());
                                    break packetswitch;
                                } else if (itemSlot > -1) {
                                    this.getInventory().setHeldItemSlot(slot);
                                    this.getInventory().setItemInHand(this.getInventory().getItem(itemSlot));
                                    this.getInventory().clear(itemSlot, true);
                                    break packetswitch;
                                }
                            }
                        }

                        if (!itemExists && this.isCreative()) {
                            Item itemInHand = this.getInventory().getItemInHand();
                            this.getInventory().setItemInHand(pickEvent.getItem());
                            if (!this.getInventory().isFull()) {
                                for (int slot = 0; slot < this.getInventory().getSize(); slot++) {
                                    if (this.getInventory().getItem(slot).isNull()) {
                                        this.getInventory().setItem(slot, itemInHand);
                                        break;
                                    }
                                }
                            }
                        } else if (itemSlot > -1) {
                            Item itemInHand = this.getInventory().getItemInHand();
                            this.getInventory().setItemInHand(this.getInventory().getItem(itemSlot));
                            this.getInventory().setItem(itemSlot, itemInHand);
                        }
                    }
                    break;
                case ANIMATE:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    PlayerAnimationEvent animationEvent = new PlayerAnimationEvent(this, ((AnimatePacket) packet).getAction());
                    this.server.getPluginManager().callEvent(animationEvent);
                    if (animationEvent.isCancelled()) {
                        break;
                    }

                    AnimatePacket.Action animation = animationEvent.getAnimationType();

                    switch (animation) {
                        case ROW_RIGHT:
                        case ROW_LEFT:
                            if (this.vehicle instanceof EntityBoat) {
                                ((EntityBoat) this.vehicle).onPaddle(animation, ((AnimatePacket) packet).getRowingTime());
                            }
                            break;
                    }

                    AnimatePacket animatePacket = new AnimatePacket();
                    animatePacket.setRuntimeEntityId(this.getRuntimeId());
                    animatePacket.setAction(animationEvent.getAnimationType());
                    Server.broadcastPacket(this.getViewers(), animatePacket);
                    break;
                case SET_HEALTH:
                    // Cannot be trusted. Use UpdateAttributePacket instead
                    break;
                case ENTITY_EVENT:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }
                    this.craftingType = CRAFTING_SMALL;
                    //this.resetCraftingGridType();

                    EntityEventPacket entityEventPacket = (EntityEventPacket) packet;

                    switch (entityEventPacket.getType()) {
                        case EATING_ITEM:
                            if (entityEventPacket.getData() == 0 || entityEventPacket.getRuntimeEntityId() != this.getRuntimeId()) {
                                break;
                            }

                            entityEventPacket.setRuntimeEntityId(this.getRuntimeId());

                            this.sendPacket(entityEventPacket);
                            Server.broadcastPacket(this.getViewers(), entityEventPacket);
                            break;
                    }
                    break;
                case COMMAND_REQUEST:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }
                    this.craftingType = CRAFTING_SMALL;
                    CommandRequestPacket commandRequestPacket = (CommandRequestPacket) packet;
                    PlayerCommandPreprocessEvent playerCommandPreprocessEvent = new PlayerCommandPreprocessEvent(this, commandRequestPacket.getCommand());
                    this.server.getPluginManager().callEvent(playerCommandPreprocessEvent);
                    if (playerCommandPreprocessEvent.isCancelled()) {
                        break;
                    }

                    try (Timing ignored2 = Timings.playerCommandTimer.startTiming()) {
                        this.server.dispatchCommand(playerCommandPreprocessEvent.getPlayer(), playerCommandPreprocessEvent.getMessage().substring(1));
                    }
                    break;
                case TEXT:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    TextPacket textPacket = (TextPacket) packet;

                    if (textPacket.getType() == TextPacket.Type.CHAT) {
                        this.chat(textPacket.getMessage());
                    }
                    break;
                case CONTAINER_CLOSE:
                    ContainerClosePacket containerClosePacket = (ContainerClosePacket) packet;
                    if (!this.spawned) {
                        break;
                    }

                    if (this.windowIndex.containsKey(containerClosePacket.getWindowId())) {
                        this.server.getPluginManager().callEvent(new InventoryCloseEvent(this.windowIndex.get(containerClosePacket.getWindowId()), this));
                        this.removeWindow(this.windowIndex.get(containerClosePacket.getWindowId()));
                    } else {
                        this.windowIndex.remove(containerClosePacket.getWindowId());
                    }
                    if (containerClosePacket.getWindowId() == -1) {
                        this.craftingType = CRAFTING_SMALL;
                        this.resetCraftingGridType();
                        this.addWindow(this.craftingGrid, ContainerIds.NONE);
                    }
                    break;
                case CRAFTING_EVENT:
                    break;
                case BLOCK_ENTITY_DATA:
                    if (!this.spawned || !this.isAlive()) {
                        break;
                    }

                    BlockEntityDataPacket blockEntityDataPacket = (BlockEntityDataPacket) packet;
                    this.craftingType = CRAFTING_SMALL;
                    this.resetCraftingGridType();

                    blockPos = blockEntityDataPacket.getBlockPosition();
                    if (blockPos.distanceSquared(this.getPosition().toInt()) > 10000) {
                        break;
                    }

                    BlockEntity blockEntity = this.getLevel().getLoadedBlockEntity(blockPos);
                    if (blockEntity != null && blockEntity.isSpawnable()) {
                        if (!blockEntity.updateFromClient((CompoundTag) blockEntityDataPacket.getData(), this)) {
                            blockEntity.spawnTo(this);
                        }
                    }
                    break;
                case REQUEST_CHUNK_RADIUS:
                    this.getChunkManager().setChunkRadius(((RequestChunkRadiusPacket) packet).getRadius());
                    break;
                case SET_PLAYER_GAME_TYPE:
                    SetPlayerGameTypePacket setPlayerGameTypePacket = (SetPlayerGameTypePacket) packet;
                    if (setPlayerGameTypePacket.getGamemode() != this.getGamemode()) {
                        if (!this.hasPermission("nukkit.command.gamemode")) {
                            SetPlayerGameTypePacket setPlayerGameTypePacket1 = new SetPlayerGameTypePacket();
                            setPlayerGameTypePacket1.setGamemode(this.getGamemode() & 0x01);
                            this.sendPacket(setPlayerGameTypePacket1);
                            this.getAdventureSettings().update();
                            break;
                        }
                        this.setGamemode(setPlayerGameTypePacket.getGamemode(), true);
                        Command.broadcastCommandMessage(this, new TranslationContainer("commands.gamemode.success.self", Server.getGamemodeString(this.getGamemode())));
                    }
                    break;
                case ITEM_FRAME_DROP_ITEM:
                    ItemFrameDropItemPacket itemFrameDropItemPacket = (ItemFrameDropItemPacket) packet;
                    Vector3i vector3 = itemFrameDropItemPacket.getBlockPosition();
                    blockEntity = this.getLevel().getLoadedBlockEntity(vector3);
                    if (!(blockEntity instanceof ItemFrame)) {
                        break;
                    }
                    ItemFrame itemFrame = (ItemFrame) blockEntity;
                    block = itemFrame.getBlock();
                    Item itemDrop = itemFrame.getItem();
                    ItemFrameDropItemEvent itemFrameDropItemEvent = new ItemFrameDropItemEvent(this, block, itemFrame, itemDrop);
                    this.server.getPluginManager().callEvent(itemFrameDropItemEvent);
                    if (!itemFrameDropItemEvent.isCancelled()) {
                        if (itemDrop.getId() != AIR) {
                            this.getLevel().dropItem(block.getPosition(), itemDrop);
                            itemFrame.setItem(Item.get(AIR, 0, 0));
                            itemFrame.setItemRotation(0);
                            this.getLevel().addSound(this.getPosition(), Sound.BLOCK_ITEMFRAME_REMOVE_ITEM);
                        }
                    } else {
                        itemFrame.spawnTo(this);
                    }
                    break;
                case MAP_INFO_REQUEST:
                    MapInfoRequestPacket pk = (MapInfoRequestPacket) packet;
                    Item mapItem = null;

                    for (Item item1 : this.getInventory().getContents().values()) {
                        if (item1 instanceof ItemMap && ((ItemMap) item1).getMapId() == pk.getUniqueMapId()) {
                            mapItem = item1;
                        }
                    }

                    if (mapItem == null) {
                        for (BlockEntity be : this.getLevel().getBlockEntities()) {
                            if (be instanceof ItemFrame) {
                                ItemFrame itemFrame1 = (ItemFrame) be;

                                if (itemFrame1.getItem() instanceof ItemMap && ((ItemMap) itemFrame1.getItem()).getMapId() == pk.getUniqueMapId()) {
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
                case LEVEL_SOUND_EVENT_2:
                case LEVEL_SOUND_EVENT_3:
                    if (!this.isSpectator() || (((LevelSoundEvent2Packet) packet).getSound() != SoundEvent.HIT &&
                            ((LevelSoundEventPacket) packet).getSound() != SoundEvent.ATTACK_NODAMAGE)) {
                        this.getLevel().addChunkPacket(this.getPosition(), packet);
                    }
                    break;
                case INVENTORY_TRANSACTION:
                    if (this.isSpectator()) {
                        this.sendAllInventories();
                        break;
                    }

                    InventoryTransactionPacket transactionPacket = (InventoryTransactionPacket) packet;

                    List<InventoryAction> actions = new ArrayList<>();
                    for (InventoryActionData inventoryActionData : transactionPacket.getActions()) {
                        InventoryAction a = InventoryTransactionUtils.createInventoryAction(this, inventoryActionData);

                        if (a == null) {
                            log.debug("Unmatched inventory action from " + this.getName() + ": " + inventoryActionData);
                            this.sendAllInventories();
                            break packetswitch;
                        }

                        actions.add(a);
                    }

                    if (InventoryTransactionUtils.containsCraftingPart(transactionPacket)) {
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

                    switch (transactionPacket.getTransactionType()) {
                        case NORMAL:
                            InventoryTransaction transaction = new InventoryTransaction(this, actions);

                            if (!transaction.execute()) {
                                log.debug("Failed to execute inventory transaction from " + this.getName() + " with actions: " + transactionPacket.getActions());
                                break packetswitch; //oops!
                            }

                            //TODO: fix achievement for getting iron from furnace

                            break packetswitch;
                        case INVENTORY_MISMATCH:
                            if (transactionPacket.getActions().size() > 0) {
                                log.debug("Expected 0 actions for mismatch, got " + transactionPacket.getActions().size() + ", " + transactionPacket.getActions());
                            }
                            this.sendAllInventories();

                            break packetswitch;
                        case ITEM_USE:

                            Vector3i blockVector = transactionPacket.getBlockPosition();
                            face = BlockFace.fromIndex(transactionPacket.getFace());

                            switch (transactionPacket.getActionType()) {
                                case InventoryTransactionUtils.USE_ITEM_ACTION_CLICK_BLOCK:
                                    // Remove if client bug is ever fixed
                                    boolean spamBug = (lastRightClickPos != null && System.currentTimeMillis() - lastRightClickTime < 100.0 && blockVector.distanceSquared(lastRightClickPos) < 0.00001);
                                    lastRightClickPos = blockVector;
                                    lastRightClickTime = System.currentTimeMillis();
                                    if (spamBug) {
                                        return;
                                    }

                                    this.setUsingItem(false);

                                    if (this.canInteract(blockVector.toFloat().add(0.5, 0.5, 0.5), this.isCreative() ? 13 : 7)) {
                                        Item clientHand = Item.fromNetwork(transactionPacket.getItemInHand());
                                        if (this.isCreative()) {
                                            Item i = this.getInventory().getItemInHand();
                                            if (this.getLevel().useItemOn(blockVector, i, face,
                                                    transactionPacket.getClickPosition(), this) != null) {
                                                break packetswitch;
                                            }
                                        } else if (getInventory().getItemInHand().equals(clientHand)) {
                                            Item serverHand = getInventory().getItemInHand();
                                            Item oldItem = serverHand.clone();
                                            //TODO: Implement adventure mode checks
                                            if ((serverHand = this.getLevel().useItemOn(blockVector, serverHand, face,
                                                    transactionPacket.getClickPosition(), this)) != null) {
                                                if (!serverHand.equals(oldItem) ||
                                                        serverHand.getCount() != oldItem.getCount()) {
                                                    getInventory().setItemInHand(serverHand);
                                                    getInventory().sendHeldItem(this.getViewers());
                                                }
                                                break packetswitch;
                                            }
                                        }
                                    }

                                    getInventory().sendHeldItem(this);

                                    if (blockVector.distanceSquared(this.getPosition().toInt()) > 10000) {
                                        break packetswitch;
                                    }

                                    Block target = this.getLevel().getBlock(blockVector);
                                    block = target.getSide(face);

                                    this.getLevel().sendBlocks(new Player[]{this}, new Block[]{target, block}, UpdateBlockPacket.FLAG_ALL_PRIORITY);
                                    break packetswitch;
                                case InventoryTransactionUtils.USE_ITEM_ACTION_BREAK_BLOCK:
                                    if (!this.spawned || !this.isAlive()) {
                                        break packetswitch;
                                    }

                                    this.resetCraftingGridType();

                                    Item i = this.getInventory().getItemInHand();

                                    Item oldItem = i.clone();

                                    if (this.canInteract(blockVector.toFloat().add(0.5, 0.5, 0.5), this.isCreative() ? 13 : 7) &&
                                            (i = this.getLevel().useBreakOn(blockVector, face, i, this, true)) != null) {
                                        if (this.isSurvival()) {
                                            this.getFoodData().updateFoodExpLevel(0.025);
                                            if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                                getInventory().setItemInHand(i);
                                                getInventory().sendHeldItem(this.getViewers());
                                            }
                                        }
                                        break packetswitch;
                                    }

                                    getInventory().sendContents(this);
                                    target = this.getLevel().getBlock(blockVector);
                                    blockEntity = this.getLevel().getLoadedBlockEntity(blockVector);

                                    this.getLevel().sendBlocks(new Player[]{this}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY);

                                    getInventory().sendHeldItem(this);

                                    if (blockEntity.isSpawnable()) {
                                        blockEntity.spawnTo(this);
                                    }

                                    break packetswitch;
                                case InventoryTransactionUtils.USE_ITEM_ACTION_CLICK_AIR:
                                    Vector3f directionVector = this.getDirectionVector();

                                    Item clientHand = Item.fromNetwork(transactionPacket.getItemInHand());

                                    if (this.isCreative()) {
                                        serverItem = this.getInventory().getItemInHand();
                                    } else if (!this.getInventory().getItemInHand().equals(clientHand)) {
                                        this.getInventory().sendHeldItem(this);
                                        break packetswitch;
                                    } else {
                                        serverItem = this.getInventory().getItemInHand();
                                    }

                                    PlayerInteractEvent interactEvent = new PlayerInteractEvent(this, serverItem, directionVector, face, Action.RIGHT_CLICK_AIR);

                                    this.server.getPluginManager().callEvent(interactEvent);

                                    if (interactEvent.isCancelled()) {
                                        this.getInventory().sendHeldItem(this);
                                        break packetswitch;
                                    }

                                    if (serverItem.onClickAir(this, directionVector)) {
                                        if (this.isSurvival()) {
                                            this.getInventory().setItemInHand(serverItem);
                                        }

                                        if (!this.isUsingItem()) {
                                            this.setUsingItem(true);
                                            break packetswitch;
                                        }

                                        // Used item
                                        int ticksUsed = this.server.getTick() - this.startAction;
                                        this.setUsingItem(false);

                                        if (!serverItem.onUse(this, ticksUsed)) {
                                            this.getInventory().sendContents(this);
                                        }
                                    }

                                    break packetswitch;
                                default:
                                    //unknown
                                    break;
                            }
                            break;
                        case ITEM_USE_ON_ENTITY:

                            Entity target = this.getLevel().getEntity(transactionPacket.getRuntimeEntityId());
                            if (target == null) {
                                return;
                            }

                            Item clientHand = Item.fromNetwork(transactionPacket.getItemInHand());

                            if (!clientHand.equalsExact(this.getInventory().getItemInHand())) {
                                this.getInventory().sendHeldItem(this);
                            }

                            serverItem = this.getInventory().getItemInHand();

                            switch (transactionPacket.getActionType()) {
                                case InventoryTransactionUtils.USE_ITEM_ON_ENTITY_ACTION_INTERACT:
                                    PlayerInteractEntityEvent playerInteractEntityEvent = new PlayerInteractEntityEvent(this, target, serverItem, transactionPacket.getClickPosition());
                                    if (this.isSpectator()) playerInteractEntityEvent.setCancelled();
                                    getServer().getPluginManager().callEvent(playerInteractEntityEvent);

                                    if (playerInteractEntityEvent.isCancelled()) {
                                        break;
                                    }
                                    if (target.onInteract(this, serverItem, transactionPacket.getClickPosition()) && this.isSurvival()) {
                                        if (serverItem.isTool()) {
                                            if (serverItem.useOn(target) && serverItem.getMeta() >= serverItem.getMaxDurability()) {
                                                serverItem = Item.get(AIR, 0, 0);
                                            }
                                        } else {
                                            if (serverItem.getCount() > 1) {
                                                serverItem.decrementCount();
                                            } else {
                                                serverItem = Item.get(AIR, 0, 0);
                                            }
                                        }

                                        this.getInventory().setItemInHand(serverItem);
                                    }
                                    break;
                                case InventoryTransactionUtils.USE_ITEM_ON_ENTITY_ACTION_ATTACK:
                                    float itemDamage = serverItem.getAttackDamage();

                                    for (Enchantment enchantment : serverItem.getEnchantments()) {
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
                                    if ((target instanceof Player) && !this.getLevel().getGameRules().get(GameRules.PVP)) {
                                        entityDamageByEntityEvent.setCancelled();
                                    }

                                    if (!target.attack(entityDamageByEntityEvent)) {
                                        if (serverItem.isTool() && this.isSurvival()) {
                                            this.getInventory().sendContents(this);
                                        }
                                        break;
                                    }

                                    for (Enchantment enchantment : serverItem.getEnchantments()) {
                                        enchantment.doPostAttack(this, target);
                                    }

                                    if (serverItem.isTool() && this.isSurvival()) {
                                        if (serverItem.useOn(target) && serverItem.getMeta() >= serverItem.getMaxDurability()) {
                                            this.getInventory().setItemInHand(Item.get(AIR, 0, 0));
                                        } else {
                                            this.getInventory().setItemInHand(serverItem);
                                        }
                                    }
                                    return;
                                default:
                                    break; //unknown
                            }

                            break;
                        case ITEM_RELEASE:
                            if (this.isSpectator()) {
                                this.sendAllInventories();
                                break packetswitch;
                            }

                            try {
                                switch (transactionPacket.getActionType()) {
                                    case InventoryTransactionUtils.RELEASE_ITEM_ACTION_RELEASE:
                                        if (this.isUsingItem()) {
                                            serverItem = this.getInventory().getItemInHand();

                                            int ticksUsed = this.server.getTick() - this.startAction;
                                            if (!serverItem.onRelease(this, ticksUsed)) {
                                                this.getInventory().sendContents(this);
                                            }

                                            this.setUsingItem(false);
                                        } else {
                                            this.getInventory().sendContents(this);
                                        }
                                        return;
                                    case InventoryTransactionUtils.RELEASE_ITEM_ACTION_CONSUME:
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
                            this.getInventory().sendContents(this);
                            break;
                    }
                    break;
                case PLAYER_HOTBAR:
                    PlayerHotbarPacket hotbarPacket = (PlayerHotbarPacket) packet;

                    if (hotbarPacket.getContainerId() != ContainerIds.INVENTORY) {
                        return; // This should never happen
                    }

                    this.getInventory().equipItem(hotbarPacket.getSelectedHotbarSlot());
                    break;
                case SERVER_SETTINGS_REQUEST:
                    PlayerServerSettingsRequestEvent settingsRequestEvent = new PlayerServerSettingsRequestEvent(this, new HashMap<>(this.serverSettings));
                    this.getServer().getPluginManager().callEvent(settingsRequestEvent);

                    if (!settingsRequestEvent.isCancelled()) {
                        settingsRequestEvent.getSettings().forEach((id, formWindow) -> {
                            ServerSettingsResponsePacket re = new ServerSettingsResponsePacket();
                            re.setFormId(id);
                            re.setFormData(formWindow.getJSONData());
                            this.sendPacket(re);
                        });
                    }
                    break;
                case RESPAWN:
                    if (this.isAlive()) {
                        break;
                    }
                    RespawnPacket respawnPacket = (RespawnPacket) packet;
                    if (respawnPacket.getState() == RespawnPacket.State.CLIENT_READY) {
                        RespawnPacket respawn1 = new RespawnPacket();
                        respawn1.setPosition(this.getSpawn().getPosition());
                        respawn1.setState(RespawnPacket.State.SERVER_READY);
                        this.sendPacket(respawn1);
                    }
                    break;
                case LECTERN_UPDATE:
                    LecternUpdatePacket lecternUpdatePacket = (LecternUpdatePacket) packet;
                    Vector3i blockPosition = lecternUpdatePacket.getBlockPosition();

                    if (lecternUpdatePacket.isDroppingBook()) {
                        Block blockLectern = this.getLevel().getBlock(blockPosition);
                        if (blockLectern instanceof BlockLectern) {
                            ((BlockLectern) blockLectern).dropBook(this);
                        }
                    } else {
                        blockEntity = this.level.getBlockEntity(blockPosition);
                        if (blockEntity instanceof Lectern) {
                            Lectern lectern = (Lectern) blockEntity;
                            LecternPageChangeEvent lecternPageChangeEvent = new LecternPageChangeEvent(this, lectern, lecternUpdatePacket.getPage());
                            this.server.getPluginManager().callEvent(lecternPageChangeEvent);
                            if (!lecternPageChangeEvent.isCancelled()) {
                                lectern.setPage(lecternPageChangeEvent.getNewRawPage());
                                lectern.spawnToAll();
                                Block blockLectern = lectern.getBlock();
                                if (blockLectern instanceof BlockLectern) {
                                    ((BlockLectern) blockLectern).executeRedstonePulse();
                                }
                            }
                        }
                    }
                    break;
                case SET_LOCAL_PLAYER_AS_INITIALIZED:
                    if (this.initialized) {
                        break;
                    }
                    this.initialized = true;
                    PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(this,
                            new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.joined", this.getDisplayName())
                    );

                    this.server.getPluginManager().callEvent(playerJoinEvent);

                    if (playerJoinEvent.getJoinMessage().toString().trim().length() > 0) {
                        this.server.broadcastMessage(playerJoinEvent.getJoinMessage());
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

    public String getXuid() {
        return this.getLoginChainData().getXUID();
    }

    @Override
    public void sendMessage(String message) {
        TextPacket packet = new TextPacket();
        packet.setType(TextPacket.Type.RAW);
        packet.setXuid(this.getXuid());
        packet.setMessage(this.server.getLanguage().translateOnly("nukkit.", message));
        this.sendPacket(packet);
    }

    @Override
    public void sendMessage(TextContainer message) {
        if (message instanceof TranslationContainer) {
            this.sendTranslation(message.getText(), ((TranslationContainer) message).getParameters());
            return;
        }
        this.sendMessage(message.getText());
    }

    public void sendTranslation(String message, Object... parameters) {
        if (parameters == null) parameters = new Object[0];
        TextPacket packet = new TextPacket();
        if (!this.server.isLanguageForced()) {
            packet.setType(TextPacket.Type.TRANSLATION);
            packet.setMessage(this.server.getLanguage().translateOnly("nukkit.", message, parameters));
            String[] params = new String[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                params[i] = this.server.getLanguage().translateOnly("nukkit.", parameters[i].toString());
            }
            packet.setParameters(Arrays.asList(params));
        } else {
            packet.setType(TextPacket.Type.RAW);
            packet.setMessage(this.server.getLanguage().translate(message, parameters));
        }
        packet.setNeedsTranslation(true);
        packet.setXuid(this.getXuid());
        this.sendPacket(packet);
    }

    public void sendChat(String message) {
        this.sendChat("", message);
    }

    public void sendChat(String source, String message) {
        TextPacket packet = new TextPacket();
        packet.setType(TextPacket.Type.CHAT);
        packet.setSourceName(source);
        packet.setMessage(this.server.getLanguage().translateOnly("nukkit.", message));
        packet.setXuid(this.getXuid());
        this.sendPacket(packet);
    }

    public void sendPopup(String message) {
        this.sendPopup(message, "");
    }

    public void sendPopup(String message, String subtitle) {
        TextPacket packet = new TextPacket();
        packet.setType(TextPacket.Type.POPUP);
        packet.setMessage(message);
        packet.setXuid(this.getXuid());
        this.sendPacket(packet);
    }

    public void sendTip(String message) {
        TextPacket packet = new TextPacket();
        packet.setType(TextPacket.Type.TIP);
        packet.setMessage(message);
        packet.setXuid(this.getXuid());
        this.sendPacket(packet);
    }

    public void clearTitle() {
        SetTitlePacket packet = new SetTitlePacket();
        packet.setType(SetTitlePacket.Type.CLEAR_TITLE);
        this.sendPacket(packet);
    }

    /**
     * Resets both title animation times and subtitle for the next shown title
     */
    public void resetTitleSettings() {
        SetTitlePacket packet = new SetTitlePacket();
        packet.setType(SetTitlePacket.Type.RESET_TITLE);
        this.sendPacket(packet);
    }

    public void setSubtitle(String subtitle) {
        SetTitlePacket packet = new SetTitlePacket();
        packet.setType(SetTitlePacket.Type.SET_SUBTITLE);
        packet.setText(subtitle);
        this.sendPacket(packet);
    }

    public void setTitleAnimationTimes(int fadein, int duration, int fadeout) {
        SetTitlePacket packet = new SetTitlePacket();
        packet.setType(SetTitlePacket.Type.SET_ANIMATION_TIMES);
        packet.setFadeInTime(fadein);
        packet.setStayTime(duration);
        packet.setFadeOutTime(fadeout);
        this.sendPacket(packet);
    }


    private void setTitle(String text) {
        SetTitlePacket packet = new SetTitlePacket();
        packet.setType(SetTitlePacket.Type.SET_TITLE);
        packet.setText(text);
        this.sendPacket(packet);
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
        SetTitlePacket packet = new SetTitlePacket();
        packet.setType(SetTitlePacket.Type.SET_ACTIONBAR_MESSAGE);
        packet.setText(title);
        packet.setFadeInTime(fadein);
        packet.setStayTime(duration);
        packet.setFadeInTime(fadeout);
        this.sendPacket(packet);
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
                DisconnectPacket packet = new DisconnectPacket();
                packet.setKickMessage(reason);
                this.sendPacketImmediately(packet);
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

                for (Entity entity : this.getLevel().getLoadedChunkEntities(chunkX, chunkZ)) {
                    if (entity != this) {
                        entity.getViewers().remove(this);
                    }
                }
            });

            super.close();

            if (!this.session.isClosed()) {
                this.session.disconnect(notify ? reason : "");
            }

            if (this.loggedIn) {
                this.server.removeOnlinePlayer(this);
            }

            this.loggedIn = false;

            if (ev != null && !Objects.equals(this.username, "") && this.spawned && !Objects.equals(ev.getQuitMessage().toString(), "")) {
                this.server.broadcastMessage(ev.getQuitMessage());
            }

            this.server.getPluginManager().unsubscribeFromPermission(Server.BROADCAST_CHANNEL_USERS, this);
            this.spawned = false;
            log.info(this.getServer().getLanguage().translate("nukkit.player.logOut",
                    TextFormat.AQUA + (this.getName() == null ? "" : this.getName()) + TextFormat.WHITE,
                    this.getAddress(),
                    this.getPort(),
                    reason));
            this.windows.clear();
            this.hasSpawned.clear();
            this.spawnLocation = null;

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
            UpdateAttributesPacket packet = new UpdateAttributesPacket();
            packet.getAttributes().add(attr.toNetwork());
            packet.setRuntimeEntityId(this.getRuntimeId());
            this.sendPacket(packet);
        }
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        super.setMaxHealth(maxHealth);

        Attribute attr = Attribute.getAttribute(Attribute.MAX_HEALTH).setMaxValue(this.getAbsorption() % 2 != 0 ? this.getMaxHealth() + 1 : this.getMaxHealth()).setValue(health > 0 ? (health < getMaxHealth() ? health : getMaxHealth()) : 0);
        if (this.spawned) {
            UpdateAttributesPacket packet = new UpdateAttributesPacket();
            packet.getAttributes().add(attr.toNetwork());
            packet.setRuntimeEntityId(this.getRuntimeId());
            this.sendPacket(packet);
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
        UpdateAttributesPacket packet = new UpdateAttributesPacket();
        packet.getAttributes().add(attribute.toNetwork());
        packet.setRuntimeEntityId(this.getRuntimeId());
        this.sendPacket(packet);
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
        if (this.getLevel().getBlock(this.getPosition().add(0, -1, 0)).getId() == BlockIds.SLIME) {
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
                EntityEventPacket packet = new EntityEventPacket();
                packet.setRuntimeEntityId(this.getRuntimeId());
                packet.setType(EntityEventType.HURT_ANIMATION);
                this.sendPacket(packet);
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

        Vector3f motion = this.getDirectionVector().mul(0.4);

        this.getLevel().dropItem(this.getPosition().add(0, 1.3, 0), item, motion, 40);

        this.setUsingItem(false);
        return true;
    }

    public void sendPosition(Vector3f pos) {
        this.sendPosition(pos, this.getYaw());
    }

    public void sendPosition(Vector3f pos, double yaw) {
        this.sendPosition(pos, yaw, this.getPitch());
    }

    public void sendPosition(Vector3f pos, double yaw, double pitch) {
        this.sendPosition(pos, yaw, pitch, MovePlayerPacket.Mode.NORMAL);
    }

    public void sendPosition(Vector3f pos, double yaw, double pitch, MovePlayerPacket.Mode mode) {
        this.sendPosition(pos, yaw, pitch, mode, null);
    }

    public void sendPosition(Vector3f pos, double yaw, double pitch, MovePlayerPacket.Mode mode, Collection<Player> targets) {
        MovePlayerPacket packet = new MovePlayerPacket();
        packet.setRuntimeEntityId(this.getRuntimeId());
        packet.setPosition(pos.add(0, getEyeHeight(), 0));
        packet.setRotation(com.nukkitx.math.vector.Vector3f.from(yaw, pitch, yaw));
        packet.setMode(mode);
        if (mode == MovePlayerPacket.Mode.TELEPORT) {
            packet.setTeleportationCause(MovePlayerPacket.TeleportationCause.BEHAVIOR);
        }

        if (targets != null) {
            Server.broadcastPacket(targets, packet);
        } else {
            this.sendPacket(packet);
        }
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        this.playerData.loadData(tag);

        String level = this.playerData.getLevel();
        this.level = this.server.getLevel(level);
        if (this.level == null) {
            this.level = this.server.getDefaultLevel();
        }

        super.loadAdditionalData(tag);

        int exp = tag.getInt("EXP");
        int expLevel = tag.getInt("expLevel");
        this.setExperience(exp, expLevel);

        tag.listenForInt("foodLevel", this.foodData::setLevel);
        tag.listenForFloat("FoodSaturationLevel", this.foodData::setFoodSaturationLevel);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        this.playerData.setLevel(this.level.getId());
        if (this.spawnLocation != null && this.spawnLocation.getLevel() != null) {
            this.playerData.setSpawnLevel(this.spawnLocation.getLevel().getId());
            this.playerData.setSpawnLocation(this.spawnLocation.getPosition().toInt());
        }

        this.playerData.saveData(tag);

        tag.intTag("EXP", this.getExperience());
        tag.intTag("expLevel", this.getExperienceLevel());

        tag.intTag("foodLevel", this.getFoodData().getLevel());
        tag.floatTag("foodSaturationLevel", this.getFoodData().getFoodSaturationLevel());
    }

    public void save(boolean async) {
        if (this.closed) {
            throw new IllegalStateException("Tried to save closed player");
        }

        if (!this.loggedIn || this.username.isEmpty()) {
            return; // No point in saving player data from here.
        }

        CompoundTagBuilder tag = CompoundTag.builder();
        this.saveAdditionalData(tag);

        this.server.saveOfflinePlayerData(this.identity, tag.buildRootTag(), async);
    }

    @Override
    public void kill() {
        if (!this.spawned) {
            return;
        }

        boolean showMessages = this.getLevel().getGameRules().get(GameRules.SHOW_DEATH_MESSAGES);
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
                            params.add(!Objects.equals(e.getCustomName(), "") ? e.getCustomName() : e.getName());
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
                            params.add(!Objects.equals(e.getCustomName(), "") ? e.getCustomName() : e.getName());
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
                    Block block = this.getLevel().getBlock(this.getPosition().add(0, -1, 0));
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
                            params.add(!Objects.equals(e.getCustomName(), "") ? e.getCustomName() : e.getName());
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

        ev.setKeepExperience(this.getLevel().getGameRules().get(GameRules.KEEP_INVENTORY));
        ev.setKeepInventory(ev.getKeepExperience());
        this.server.getPluginManager().callEvent(ev);

        if (!ev.getKeepInventory() && this.getLevel().getGameRules().get(GameRules.DO_ENTITY_DROPS)) {
            for (Item item : ev.getDrops()) {
                this.getLevel().dropItem(this.getPosition(), item, null, true, 40);
            }

            this.getInventory().clearAll();
        }

        if (!ev.getKeepExperience() && this.getLevel().getGameRules().get(GameRules.DO_ENTITY_DROPS)) {
            if (this.isSurvival() || this.isAdventure()) {
                int exp = ev.getExperience() * 7;
                if (exp > 100) exp = 100;
                this.getLevel().dropExpOrb(this.getPosition(), exp);
            }
            this.setExperience(0, 0);
        }

        if (showMessages && !ev.getDeathMessage().toString().isEmpty()) {
            this.server.broadcast(ev.getDeathMessage(), Server.BROADCAST_CHANNEL_USERS);
        }


        RespawnPacket packet = new RespawnPacket();
        Location location = this.getSpawn();
        packet.setPosition(location.getPosition());
        packet.setState(RespawnPacket.State.SERVER_SEARCHING);

        //this is a dirty hack to prevent dying in a different level than the respawn point from breaking everything
        if (this.getLevel() != location.getLevel()) {
            this.teleport(location, null);
        }

        this.extinguish();

        this.sendPacket(packet);
    }

    protected void sendPlayStatus(PlayStatusPacket.Status status) {
        sendPlayStatus(status, false);
    }

    protected void sendPlayStatus(PlayStatusPacket.Status status, boolean immediate) {
        PlayStatusPacket packet = new PlayStatusPacket();
        packet.setStatus(status);

        if (immediate) {
            this.sendPacketImmediately(packet);
        } else {
            this.sendPacket(packet);
        }
    }

    @Override
    protected void checkChunks() {
        Vector3f pos = this.getPosition();
        if (this.chunk == null || (this.chunk.getX() != pos.getFloorX() >> 4 || this.chunk.getZ() != pos.getFloorZ() >> 4)) {
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }
            this.chunk = this.getLevel().getChunk(pos);

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
                if (window == this.getInventory()) {
                    continue;
                }
                this.removeWindow(window);
            }

            if (from.getLevel() != location.getLevel()) { //Different level, update compass position
                SetSpawnPositionPacket packet = new SetSpawnPositionPacket();
                packet.setSpawnType(SetSpawnPositionPacket.Type.WORLD_SPAWN);
                Location spawn = location.getLevel().getSpawnLocation();
                packet.setBlockPosition(spawn.getPosition().toInt());
                sendPacket(packet);
            }

            this.forceMovement = this.getPosition();
            log.debug("teleportImmediate REVERTING");
            this.sendPosition(this.getPosition(), this.getY(), this.getPitch(), MovePlayerPacket.Mode.RESET);

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
        packet.setFormId(id);
        packet.setFormData(window.getJSONData());
        this.formWindows.put(id, window);

        this.sendPacket(packet);
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

    public byte getWindowId(Inventory inventory) {
        if (this.windows.containsKey(inventory)) {
            return this.windows.get(inventory);
        }

        return -1;
    }

    public Inventory getWindowById(int id) {
        return this.windowIndex.get((byte) id);
    }

    public int addWindow(Inventory inventory) {
        return this.addWindow(inventory, null);
    }

    public int addWindow(Inventory inventory, Byte forceId) {
        return addWindow(inventory, forceId, false);
    }

    public int addWindow(Inventory inventory, Byte forceId, boolean isPermanent) {
        if (this.windows.containsKey(inventory)) {
            return this.windows.get(inventory);
        }
        byte cnt;
        if (forceId == null) {
            this.windowCnt = cnt = (byte) Math.max(4, ++this.windowCnt % 99);
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
        for (Entry<Inventory, Byte> entry : this.windows.entrySet()) {
            if (!this.permanentWindows.contains((byte) entry.getValue())) {
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
            Item[] drops = this.getInventory().addItem(this.craftingGrid.getContents().values().toArray(new Item[0]));

            if (drops.length > 0) {
                for (Item drop : drops) {
                    this.dropItem(drop);
                }
            }

            drops = this.getInventory().addItem(this.getCursorInventory().getItem(0));
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
        for (Entry<Byte, Inventory> entry : new ArrayList<>(this.windowIndex.entrySet())) {
            if (!permanent && this.permanentWindows.contains((byte) entry.getKey())) {
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
            int chunkX = this.teleportPosition.getFloorX() >> 4;
            int chunkZ = this.teleportPosition.getFloorZ() >> 4;

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

            this.teleportPosition = this.getPosition();
            this.getChunkManager().queueNewChunks(this.teleportPosition);
            this.forceMovement = this.teleportPosition;
            this.sendPosition(this.getPosition(), this.getYaw(), this.getPitch(), MovePlayerPacket.Mode.TELEPORT);

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
        ChangeDimensionPacket packet = new ChangeDimensionPacket();
        packet.setDimension(dimension);
        packet.setPosition(this.getPosition());
        this.sendPacketImmediately(packet);
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
        pk.setAddress(hostName);
        pk.setPort(port);
        this.sendPacket(pk);
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
        ShowProfilePacket packet = new ShowProfilePacket();
        packet.setXuid(xuid);
        this.sendPacket(packet);
    }

    public void startFishing(Item fishingRod) {
        Location location = Location.from(this.getPosition().add(0, this.getEyeHeight(), 0), this.getYaw(),
                this.getPitch(), this.getLevel());
        double f = 1;
        FishingHook fishingHook = EntityRegistry.get().newEntity(EntityTypes.FISHING_HOOK, location);
        fishingHook.setOwner(this);
        fishingHook.setMotion(Vector3f.from(-Math.sin(Math.toRadians(this.getYaw())) * Math.cos(Math.toRadians(this.getPitch())) * f * f,
                -Math.sin(Math.toRadians(this.getPitch())) * f * f, Math.cos(Math.toRadians(this.getYaw())) * Math.cos(Math.toRadians(this.getPitch())) * f * f));
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
        Level oldLevel = this.getLevel();
        if (super.switchLevel(level)) {
            SetSpawnPositionPacket spawnPosition = new SetSpawnPositionPacket();
            spawnPosition.setSpawnType(SetSpawnPositionPacket.Type.WORLD_SPAWN);
            Location spawn = level.getSpawnLocation();
            spawnPosition.setBlockPosition(spawn.getPosition().toInt());
            this.sendPacket(spawnPosition);

            this.getChunkManager().prepareRegion(this.getPosition());

            SetTimePacket setTime = new SetTimePacket();
            setTime.setTime(level.getTime());
            this.sendPacket(setTime);

            GameRulesChangedPacket gameRulesChanged = new GameRulesChangedPacket();
            level.getGameRules().toNetwork(gameRulesChanged.getGameRules());
            this.sendPacket(gameRulesChanged);
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
                if (this.isSurvival() && !this.getInventory().canAddItem(item)) {
                    return false;
                }

                InventoryPickupArrowEvent ev = new InventoryPickupArrowEvent(this.getInventory(), (EntityArrow) entity);

                int pickupMode = ((EntityArrow) entity).getPickupMode();
                if (pickupMode == EntityArrow.PICKUP_NONE || pickupMode == EntityArrow.PICKUP_CREATIVE && !this.isCreative()) {
                    ev.setCancelled();
                }

                this.server.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return false;
                }

                TakeItemEntityPacket packet = new TakeItemEntityPacket();
                packet.setRuntimeEntityId(this.getRuntimeId());
                packet.setItemRuntimeEntityId(entity.getRuntimeId());
                Server.broadcastPacket(entity.getViewers(), packet);
                this.sendPacket(packet);

                if (!this.isCreative()) {
                    this.getInventory().addItem(item.clone());
                }
                entity.close();
                return true;
            } else if (entity instanceof ThrownTrident && entity.getMotion().lengthSquared() == 0) {
                Item item = ((ThrownTrident) entity).getTrident();
                if (this.isSurvival() && !this.getInventory().canAddItem(item)) {
                    return false;
                }

                TakeItemEntityPacket packet = new TakeItemEntityPacket();
                packet.setRuntimeEntityId(this.getRuntimeId());
                packet.setItemRuntimeEntityId(entity.getRuntimeId());
                Server.broadcastPacket(entity.getViewers(), packet);
                this.sendPacket(packet);

                if (!this.isCreative()) {
                    this.getInventory().addItem(item.clone());
                }
                entity.close();
                return true;
            } else if (entity instanceof DroppedItem) {
                if (((DroppedItem) entity).getPickupDelay() <= 0) {
                    Item item = ((DroppedItem) entity).getItem();

                    if (item != null) {
                        if (this.isSurvival() && !this.getInventory().canAddItem(item)) {
                            return false;
                        }

                        InventoryPickupItemEvent ev;
                        this.server.getPluginManager().callEvent(ev = new InventoryPickupItemEvent(this.getInventory(), (DroppedItem) entity));
                        if (ev.isCancelled()) {
                            return false;
                        }

                        if (item.getId() == BlockIds.LOG) {
                            this.awardAchievement("mineWood");
                        } else if (item.getId() == ItemIds.DIAMOND) {
                            this.awardAchievement("diamond");
                        }

                        TakeItemEntityPacket packet = new TakeItemEntityPacket();
                        packet.setRuntimeEntityId(this.getRuntimeId());
                        packet.setItemRuntimeEntityId(entity.getRuntimeId());
                        Server.broadcastPacket(entity.getViewers(), packet);
                        this.sendPacket(packet);

                        entity.close();
                        this.getInventory().addItem(item.clone());
                        return true;
                    }
                }
            }
        }

        int tick = this.getServer().getTick();
        if (pickedXPOrb < tick && entity instanceof ExperienceOrb && this.boundingBox.isVectorInside(entity.getPosition())) {
            ExperienceOrb experienceOrb = (ExperienceOrb) entity;
            if (experienceOrb.getPickupDelay() <= 0) {
                int exp = experienceOrb.getExperience();
                entity.kill();
                this.getLevel().addSound(this.getPosition(), Sound.RANDOM_ORB);
                pickedXPOrb = tick;

                //Mending
                ArrayList<Integer> itemsWithMending = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    if (getInventory().getArmorItem(i).getEnchantment((short) Enchantment.ID_MENDING) != null) {
                        itemsWithMending.add(getInventory().getSize() + i);
                    }
                }
                if (getInventory().getItemInHand().getEnchantment((short) Enchantment.ID_MENDING) != null) {
                    itemsWithMending.add(getInventory().getHeldItemIndex());
                }
                if (itemsWithMending.size() > 0) {
                    Random rand = new Random();
                    Integer itemToRepair = itemsWithMending.get(rand.nextInt(itemsWithMending.size()));
                    Item toRepair = getInventory().getItem(itemToRepair);
                    if (toRepair instanceof ItemTool || toRepair instanceof ItemArmor) {
                        if (toRepair.getMeta() > 0) {
                            int dmg = toRepair.getMeta() - 2;
                            if (dmg < 0)
                                dmg = 0;
                            toRepair.setMeta(dmg);
                            getInventory().setItem(itemToRepair, toRepair);
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

    private class Handler implements BatchHandler {

        @Override
        public void handle(BedrockSession session, ByteBuf buffer, Collection<BedrockPacket> packets) {
            for (BedrockPacket packet : packets) {
                Player.this.handleDataPacket(packet);
            }
        }
    }
}
