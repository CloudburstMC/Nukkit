package cn.nukkit;

import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Arrow;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.Human;
import cn.nukkit.event.TextContainer;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.event.inventory.InventoryPickupArrowEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.SimpleTransactionGroup;
import cn.nukkit.item.Item;
import cn.nukkit.level.ChunkLoader;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.*;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Zlib;

import java.util.*;

//import cn.nukkit.entity.Item;


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

    private String clientSecret;

    public Vector3 speed = null;

    public boolean blocked = false;

    //todo: achievements

    protected SimpleTransactionGroup currentTransaction = null;

    public int craftingType = 0; //0 = 2x2 crafting, 1 = 3x3 crafting, 2 = stonecutter

    protected boolean isCrafting = false;

    public long creationTime = 0;

    protected long randomClientId;

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

    private Integer loaderId = null;

    protected float stepHeight = 0.6f;

    public Map<String, Boolean> usedChunks = new HashMap<>();

    protected int chunkLoadCount = 0;
    protected Map<String, Integer> loadQueue = new HashMap<>();
    protected int nextChunkOrderRun = 5;

    protected Map<UUID, Player> hiddenPlayers = new HashMap<>();

    protected Vector3 newPosition;

    protected int viewDistance;
    protected int chunksPerTick;
    protected int spawnThreshold;

    private Position spawnPosition = null;

    protected int inAirTicks = 0;
    protected int startAirTicks = 5;

    protected boolean autoJump = true;

    protected boolean allowFlight = false;

    private Map<Integer, Boolean> needACK = new HashMap<>();

    private Map<Integer, List<DataPacket>> batchedPackets = new HashMap<>();

    private PermissibleBase perm = null;

    public TranslationContainer getLeaveMessage() {
        return new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.left", this.getDisplayName());
    }

    public String getClientSecret() {
        return clientSecret;
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

    public void setAllowFlight(boolean value) {
        this.allowFlight = value;
        this.sendSettings();
    }

    public boolean getAllowFlight() {
        return allowFlight;
    }

    public void setAutoJump(boolean value) {
        this.autoJump = value;
        this.sendSettings();
    }

    public boolean hasAutoJump() {
        return autoJump;
    }

    @Override
    public void spawnTo(Player player) {
        if (this.spawned && player.spawned && this.isAlive() && player.isAlive() && player.getLevel().equals(this.level) && player.canSee(this) && !this.isSpectator()) {
            super.spawnTo(player);
        }
    }

    @Override
    public Server getServer() {
        return null;
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
        return !this.hiddenPlayers.containsKey(player.getRawUniqueId());
    }

    public void hidePlayer(Player player) {
        if (this.equals(player)) {
            return;
        }
        this.hiddenPlayers.put(player.getRawUniqueId(), player);
        player.despawnFrom(this);
    }

    public void showPlayer(Player player) {
        if (this.equals(player)) {
            return;
        }
        this.hiddenPlayers.remove(player.getRawUniqueId());
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
        return this.perm.hasPermission(name);
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
        this.newPosition = new Vector3(0, 0, 0);
        this.boundingBox = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

        this.uuid = null;
        this.rawUUID = null;

        this.creationTime = System.currentTimeMillis();
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
            this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getDisplayName(), this.isSkinSlim(), this.getSkinData());
        }
    }

    @Override
    public void setSkin(byte[] skinData) {
        this.setSkin(skinData, false);
    }

    @Override
    public void setSkin(byte[] skinData, boolean isSlim) {
        super.setSkin(skinData, isSlim);
        if (this.spawned) {
            this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getDisplayName(), isSlim, skinData);
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

    @Override
    protected boolean switchLevel(Level targetLevel) {
        Level oldLevel = this.level;
        if (super.switchLevel(targetLevel)) {
            for (String index : this.usedChunks.keySet()) {
                Chunk.Entry chunkEntry = Level.getChunkXZ(index);
                int chunkX = chunkEntry.chunkX;
                int chunkZ = chunkEntry.chunkZ;
                this.unloadChunk(chunkX, chunkZ, oldLevel);
            }

            this.usedChunks = new HashMap<>();
            SetTimePacket pk = new SetTimePacket();
            pk.time = this.level.getTime();
            pk.started = !this.level.stopTime;
            this.dataPacket(pk);
            return true;
        }
        return false;
    }

    public void unloadChunk(int x, int z) {
        this.unloadChunk(x, z, null);
    }

    public void unloadChunk(int x, int z, Level level) {
        level = level == null ? this.level : level;
        String index = Level.chunkHash(x, z);
        if (this.usedChunks.containsKey(index)) {
            for (Entity entity : level.getChunkEntities(x, z).values()) {
                if (!entity.equals(this)) {
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
                if (!this.equals(entity) && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this);
                }
            }
        }
    }

    public void sendChunk(int x, int z, byte[] payload) {
        this.sendChunk(x, z, payload, FullChunkDataPacket.ORDER_COLUMNS);
    }

    public void sendChunk(int x, int z, byte[] payload, byte ordering) {
        if (!this.connected) {
            return;
        }

        this.usedChunks.put(Level.chunkHash(x, z), true);
        this.chunkLoadCount++;

        FullChunkDataPacket pk = new FullChunkDataPacket();
        pk.chunkX = x;
        pk.chunkZ = z;
        pk.order = ordering;
        pk.data = payload;
        this.batchDataPacket(pk);

        if (this.spawned) {
            for (Entity entity : this.level.getChunkEntities(x, z).values()) {
                if (!this.equals(entity) && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this);
                }
            }
        }
    }

    protected void sendNextChunk() {
        if (!this.connected) {
            return;
        }

        int count = 0;

        for (String index : this.loadQueue.keySet()) {
            if (count >= this.chunksPerTick) {
                break;
            }

            Chunk.Entry chunkEntry = Level.getChunkXZ(index);
            int chunkX = chunkEntry.chunkX;
            int chunkZ = chunkEntry.chunkZ;

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
            this.level.requestChunk(chunkX, chunkZ, this);
        }

        if (this.chunkLoadCount >= this.spawnThreshold && !this.spawned && this.teleportPosition == null) {
            this.doFirstSpawn();
        }
    }

    protected void doFirstSpawn() {
        this.spawned = true;

        this.sendSettings();
        this.sendPotionEffects(this);
        this.sendData(this);
        this.inventory.sendContents(this);
        this.inventory.sendArmorContents(this);

        SetTimePacket setTimePacket = new SetTimePacket();
        setTimePacket.time = this.level.getTime();
        setTimePacket.started = !this.level.stopTime;
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

        for (String index : this.usedChunks.keySet()) {
            Chunk.Entry chunkEntry = Level.getChunkXZ(index);
            int chunkX = chunkEntry.chunkX;
            int chunkZ = chunkEntry.chunkZ;
            for (Entity entity : this.level.getChunkEntities(chunkX, chunkZ).values()) {
                if (!this.equals(entity) && !entity.closed && entity.isAlive()) {
                    entity.spawnTo(this);
                }
            }
        }

        this.teleport(pos);

        this.spawnToAll();

        /*if (this.server.getUpdater().hasUpdate() and this.hasPermission(Server::BROADCAST_CHANNEL_ADMINISTRATIVE)){
            this.server.getUpdater().showPlayerUpdate(this);
        }*/
        //todo Updater

        if (this.getHealth() <= 0) {
            RespawnPacket respawnPacket1 = new RespawnPacket();
            pos = this.getSpawn();
            respawnPacket1.x = (float) pos.x;
            respawnPacket1.y = (float) pos.y;
            respawnPacket1.z = (float) pos.z;
            this.dataPacket(respawnPacket1);
        }
    }

    protected boolean orderChunks() {
        if (!this.connected) {
            return false;
        }

        this.nextChunkOrderRun = 200;

        //todo: low memory triggle?
        //viewDistance = this.server.getMemoryManager().getViewDistance(this.viewDistance);

        Map<String, Integer> newOrder = new HashMap<>();
        Map<String, Boolean> lastChunk = this.usedChunks;

        int centerX = (int) this.x >> 4;
        int centerZ = (int) this.z >> 4;

        int layer = 1;
        int leg = 0;
        int x = 0;
        int z = 0;

        for (int i = 0; i < viewDistance; ++i) {

            int chunkX = x + centerX;
            int chunkZ = z + centerZ;

            String index;
            if (!(this.usedChunks.containsKey(index = Level.chunkHash(chunkX, chunkZ))) || !this.usedChunks.get(index)) {
                newOrder.put(index, Math.abs(((int) this.x >> 4) - chunkX) + Math.abs(((int) this.z >> 4) - chunkZ));
            }
            lastChunk.remove(index);

            switch (leg) {
                case 0:
                    ++x;
                    if (x == layer) {
                        ++leg;
                    }
                    break;
                case 1:
                    ++z;
                    if (z == layer) {
                        ++leg;
                    }
                    break;
                case 2:
                    --x;
                    if (-x == layer) {
                        ++leg;
                    }
                    break;
                case 3:
                    --z;
                    if (-z == layer) {
                        leg = 0;
                        ++layer;
                    }
                    break;
            }
        }

        for (String index : lastChunk.keySet()) {
            Chunk.Entry entry = Level.getChunkXZ(index);
            this.unloadChunk(entry.chunkX, entry.chunkZ);
        }

        this.loadQueue = newOrder;

        return true;
    }

    public boolean batchDataPacket(DataPacket packet) {
        if (!this.connected) {
            return false;
        }

        DataPacketSendEvent event = new DataPacketSendEvent(this, packet);
        this.server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        if (!this.batchedPackets.containsKey(packet.getChannel())) {
            this.batchedPackets.put(packet.getChannel(), new ArrayList<>());
        }

        this.batchedPackets.get(packet.getChannel()).add(packet.clone());

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

        DataPacketSendEvent ev = new DataPacketSendEvent(this, packet);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return -1;
        }

        Integer identifier = this.interfaz.putPacket(this, packet, needACK, false);

        if (needACK && identifier != null) {
            this.needACK.put(identifier, false);
            return identifier;
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

        DataPacketSendEvent ev = new DataPacketSendEvent(this, packet);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return -1;
        }

        Integer identifier = this.interfaz.putPacket(this, packet, needACK, true);

        if (needACK && identifier != null) {
            this.needACK.put(identifier, false);
            return identifier;
        }

        return 0;
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
        this.teleport(new Position(pos.x + 0.5, pos.y - 0.5, pos.z + 0.5, this.level));

        this.setDataProperty(DATA_PLAYER_BED_POSITION, DATA_TYPE_POS, new Object[]{pos.x, pos.y, pos.z});
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
        pk.x = (int) this.spawnPosition.x;
        pk.y = (int) this.spawnPosition.y;
        pk.z = (int) this.spawnPosition.z;
        this.dataPacket(pk);
    }

    public void stopSleep() {
        if (this.sleeping != null) {
            PlayerBedLeaveEvent ev;
            this.server.getPluginManager().callEvent(ev = new PlayerBedLeaveEvent(this, this.level.getBlock(this.sleeping)));

            this.sleeping = null;
            this.setDataProperty(DATA_PLAYER_BED_POSITION, DATA_TYPE_POS, new Object[]{0, 0, 0});
            this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, false);


            this.level.sleepTicks = 0;

            AnimatePacket pk = new AnimatePacket();
            pk.eid = 0;
            pk.action = 3; //Wake up
            this.dataPacket(pk);
        }
    }

    public byte getGamemode() {
        return gamemode;
    }

    public boolean setGamemode(byte gamemode) {
        if (gamemode < 0 || gamemode > 3 || this.gamemode == gamemode) {
            return false;
        }

        PlayerGameModeChangeEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerGameModeChangeEvent(this, gamemode));

        if (ev.isCancelled()) {
            return false;
        }

        this.gamemode = gamemode;

        this.allowFlight = this.isCreative();

        if (this.isSpectator()) {
            this.despawnFromAll();
        } else {
            this.spawnToAll();
        }

        this.namedTag.putInt("playerGameType", this.gamemode);

        Position spawnPosition = this.getSpawn();

        StartGamePacket pk = new StartGamePacket();
        pk.seed = -1;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.spawnX = (int) spawnPosition.x;
        pk.spawnY = (int) spawnPosition.y;
        pk.spawnZ = (int) spawnPosition.z;
        pk.generator = 1; //0 old, 1 infinite, 2 flat
        pk.gamemode = this.gamemode & 0x01;
        pk.eid = 0;
        this.dataPacket(pk);
        this.sendSettings();

        if (this.gamemode == Player::SPECTATOR) {
            ContainerSetContentPacket pk = new ContainerSetContentPacket();
            pk.windowid = ContainerSetContentPacket.SPECIAL_CREATIVE;
            this.dataPacket(pk);
        } else {
            ContainerSetContentPacket pk = new ContainerSetContentPacket();
            pk.windowid = ContainerSetContentPacket.SPECIAL_CREATIVE;
            List<Item> slots = new ArrayList<>();
            for (Item item : Item.getCreativeItems()) {
                slots.add(item.clone());
            }
            pk.slots = slots.stream().toArray(Item[]::new);
            this.dataPacket(pk);
        }

        this.inventory.sendContents(this);
        this.inventory.sendContents(this.getViewers().values());
        this.inventory.sendHeldItem(this.hasSpawned.values());

        return true;
    }

    public void sendSettings() {
        /*
         bit mask | flag name
		0x00000001 world_inmutable
		0x00000002 no_pvp
		0x00000004 no_pvm
		0x00000008 no_mvp
		0x00000010 static_time
		0x00000020 nametags_visible
		0x00000040 auto_jump
		0x00000080 allow_fly
		0x00000100 noclip
		0x00000200 ?
		0x00000400 ?
		0x00000800 ?
		0x00001000 ?
		0x00002000 ?
		0x00004000 ?
		0x00008000 ?
		0x00010000 ?
		0x00020000 ?
		0x00040000 ?
		0x00080000 ?
		0x00100000 ?
		0x00200000 ?
		0x00400000 ?
		0x00800000 ?
		0x01000000 ?
		0x02000000 ?
		0x04000000 ?
		0x08000000 ?
		0x10000000 ?
		0x20000000 ?
		0x40000000 ?
		0x80000000 ?
		*/
        int flags = 0;
        if (this.isAdventure()) {
            flags |= 0x01; //Do not allow placing/breaking blocks, adventure mode
        }

		/*if(nametags !== false){
            flags |= 0x20; //Show Nametags
		}*/

        if (this.autoJump) {
            flags |= 0x40;
        }

        if (this.allowFlight) {
            flags |= 0x80;
        }

        if (this.isSpectator()) {
            flags |= 0x100;
        }

        AdventureSettingsPacket pk = new AdventureSettingsPacket();
        pk.flags = flags;
        this.dataPacket(pk);
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
    public boolean setDataProperty(int id, int type, Object value) {
        if (super.setDataProperty(id, type, value)) {
            this.sendData(this, new HashMap<Integer, Object[]>() {
                {
                    put(id, dataProperties.get(id));
                }
            });
            return true;
        }

        return false;
    }

    @Override
    protected void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        if (!this.onGround || movY != 0) {
            AxisAlignedBB bb = this.boundingBox.clone();
            bb.maxY = bb.minY + 0.5;
            bb.minY -= 1;

            if (this.level.getCollisionBlocks(bb, true).length > 0) {
                this.onGround = true;
            } else {
                this.onGround = false;
            }
        }
        this.isCollided = this.onGround;
    }

    @Override
    protected void checkBlockCollision() {
        for (Block block : this.getBlocksAround()) {
            block.onEntityCollide(this);
        }
    }

    protected void checkNearEntities() {
        for (Entity entity : this.level.getNearbyEntities(this.boundingBox.grow(1, 0.5, 1), this)) {
            entity.scheduleUpdate();

            if (!entity.isAlive()) {
                continue;
            }

            if (entity instanceof Arrow && ((Arrow) entity).hadCollision) {
                Item item = Item.get(Item.ARROW, 0, 1);
                if (this.isSurvival() && !this.inventory.canAddItem(item)) {
                    continue;
                }

                InventoryPickupArrowEvent ev;
                this.server.getPluginManager().callEvent(ev = new InventoryPickupArrowEvent(this.inventory, (Arrow) entity));
                if (ev.isCancelled()) {
                    continue;
                }

                TakeItemEntityPacket pk = new TakeItemEntityPacket();
                pk.entityId = this.getId();
                pk.target = entity.getId();
                Server.broadcastPacket(entity.getViewers().values(), pk);

                pk = new TakeItemEntityPacket();
                pk.entityId = 0;
                pk.target = entity.getId();
                this.dataPacket(pk);

                this.inventory.addItem(item.clone());
                entity.kill();
            } else if (entity instanceof cn.nukkit.entity.Item) {
                if (((cn.nukkit.entity.Item) entity).getPickupDelay() <= 0) {
                    Item item = ((cn.nukkit.entity.Item) entity).getItem();

                    if (item != null) {
                        if (this.isSurvival() && !this.inventory.canAddItem(item)) {
                            continue;
                        }

                        InventoryPickupItemEvent ev;
                        this.server.getPluginManager().callEvent(ev = new InventoryPickupItemEvent(this.inventory, (cn.nukkit.entity.Item) entity));
                        if (ev.isCancelled()) {
                            continue;
                        }

                        //todo: achievement
                        /*switch (item.getId()) {
                            case Item.WOOD:
                                this.awardAchievement("mineWood");
                                break;
                            case Item.DIAMOND:
                                this.awardAchievement("diamond");
                                break;
                        }*/

                        /*TakeItemEntityPacket pk = new TakeItemEntityPacket();
                        pk.entityId = this.getId();
                        pk.target = entity.getId();
                        Server.broadcastPacket(entity.getViewers().values(), pk);

                        pk = new TakeItemEntityPacket();
                        pk.entityId = 0;
                        pk.target = entity.getId();
                        this.dataPacket(pk);*/
                        //todo: check if this cause client crash

                        this.inventory.addItem(item.clone());
                        entity.kill();
                    }
                }
            }
        }
    }

    public void handleDataPacket(DataPacket packet) {
        if (!connected) {
            return;
        }
        if (packet.pid() == Info.BATCH_PACKET) {
            /** @var BatchPacket packet */
            this.server.getNetwork().processBatch((BatchPacket) packet, this);
            return;
        }
        DataPacketReceiveEvent ev = new DataPacketReceiveEvent(this, packet);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }

        switch (packet.pid()) {
            //todo alot
            default:
                break;
        }

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
        this.loadQueue.put(Level.chunkHash(chunk.getX(), chunk.getZ()), Math.abs(((int) this.x >> 4) - chunk.getX()) + Math.abs(((int) this.z >> 4) - chunk.getZ()));
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
        return getChunkCacheFromData(chunkX, chunkZ, payload, FullChunkDataPacket.ORDER_COLUMNS);
    }

    public static BatchPacket getChunkCacheFromData(int chunkX, int chunkZ, byte[] payload, byte ordering) {
        FullChunkDataPacket pk = new FullChunkDataPacket();
        pk.chunkX = chunkX;
        pk.chunkZ = chunkZ;
        pk.order = ordering;
        pk.data = payload;
        pk.encode();

        BatchPacket batch = new BatchPacket();
        try {
            batch.payload = Zlib.deflate(pk.getBuffer(), Server.getInstance().networkCompressionLevel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        batch.encode();
        batch.isEncoded = true;
        return batch;
    }
}
