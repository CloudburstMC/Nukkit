package cn.nukkit;

import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Arrow;
import cn.nukkit.entity.Effect;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.Human;
//import cn.nukkit.entity.Item;
import cn.nukkit.entity.Living;
import cn.nukkit.entity.Projectile;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.inventory.InventoryPickupArrowEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.player.PlayerAchievementAwardedEvent;
import cn.nukkit.event.player.PlayerAnimationEvent;
import cn.nukkit.event.player.PlayerBedEnterEvent;
import cn.nukkit.event.player.PlayerBedLeaveEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.event.player.PlayerGameModeChangeEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.event.TextContainer;
import cn.nukkit.event.Timings;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.inventory.BaseTransaction;
import cn.nukkit.inventory.BigShapelessRecipe;
import cn.nukkit.inventory.CraftingTransactionGroup;
import cn.nukkit.inventory.FurnaceInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.ShapelessRecipe;
import cn.nukkit.inventory.SimpleTransactionGroup;
import cn.nukkit.inventory.StonecutterShapelessRecipe;
import cn.nukkit.item.Item;
import cn.nukkit.level.ChunkLoader;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.sound.LaunchSound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.nbt.NBT;
import cn.nukkit.nbt.tag.Byte;
import cn.nukkit.nbt.tag.Compound;
import cn.nukkit.nbt.tag.Double;
import cn.nukkit.nbt.tag.Enum;
import cn.nukkit.nbt.tag.Float;
import cn.nukkit.nbt.tag.Int;
import cn.nukkit.nbt.tag.Long;
import cn.nukkit.nbt.tag.Short;
import cn.nukkit.nbt.tag.String;
import cn.nukkit.network.Network;
import cn.nukkit.network.protocol.AdventureSettingsPacket;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.ContainerSetContentPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.DisconnectPacket;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.FullChunkDataPacket;
import cn.nukkit.network.protocol.Info;
import cn.nukkit.network.protocol.PlayStatusPacket;
import cn.nukkit.network.protocol.RespawnPacket;
import cn.nukkit.network.protocol.TextPacket;
import cn.nukkit.network.protocol.MoveEntityPacket;
import cn.nukkit.network.protocol.MovePlayerPacket;
import cn.nukkit.network.protocol.SetDifficultyPacket;
import cn.nukkit.network.protocol.SetEntityMotionPacket;
import cn.nukkit.network.protocol.SetHealthPacket;
import cn.nukkit.network.protocol.SetSpawnPositionPacket;
import cn.nukkit.network.protocol.SetTimePacket;
import cn.nukkit.network.protocol.StartGamePacket;
import cn.nukkit.network.protocol.TakeItemEntityPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.tile.Sign;
import cn.nukkit.tile.Spawnable;
import cn.nukkit.tile.Tile;
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
    
    public Map<String, Integer> loginData = new HashMap<>();

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

    private Integer loaderId = null;

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

    private Map<Integer, Boolean> needACK = new HashMap<>();

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
        return !this.hiddenPlayers.containsKey(player.getUniqueId().toString());
    }

    public void hidePlayer(Player player) {
        if (this.equals(player)) {
            return;
        }
        this.hiddenPlayers.put(player.getUniqueId().toString(), player);
        player.despawnFrom(this);
    }

    public void showPlayer(Player player) {
        if (this.equals(player)) {
            return;
        }
        this.hiddenPlayers.remove(player.getUniqueId().toString());
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

        this.uuid = Utils.dataToUUID(ip, String.valueOf(port), String.valueOf(clientID));

        this.creationTime = System.currentTimeMillis();
    }

    public boolean isConnected() {
        return connected;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getAddress() {
        return this.ip;
    }

    //todo alot


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

    public void stopSleep() {
        //todo
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
        if(connected === false){
			return;
		}
		if(packet.pid() === Info.BATCH_PACKET){
			/** @var BatchPacket packet */
			this.server.getNetwork().processBatch(packet, this);
			return;
		}
		TimingsHandler timings = Timings.getReceiveDataPacketTimings(packet);
		timings.startTiming();
		this.server.getPluginManager().callEvent(ev = new DataPacketReceiveEvent(this, packet));
		if(ev.isCancelled()){
			timings.stopTiming();
			return;
		}
		switch(packet.pid){
			case Info.LOGIN_PACKET:
				if(this.loggedIn){
					break;
				}
				this.username = TextFormat.clean(packet.username);
				this.displayName = this.username;
				this.setNameTag(this.username);
				this.iusername = this.username.toLowerCase();
				
				if(this.server.getOnlinePlayers().lenght > this.server.getMaxPlayers() && this.kick("disconnectionScreen.serverFull", false)){
					break;
				}
				if(packet.protocol1 !== Info.CURRENT_PROTOCOL){
					if(packet.protocol1 < Info.CURRENT_PROTOCOL){
						String message = "disconnectionScreen.outdatedClient";
						PlayStatusPacket pk = new PlayStatusPacket();
						pk.status = PlayStatusPacket.LOGIN_FAILED_CLIENT;
						this.directDataPacket(pk.setChannel(Network.CHANNEL_PRIORITY));
					}else{
						String message = "disconnectionScreen.outdatedServer";
						PlayStatusPacket pk = new PlayStatusPacket();
						pk.status = PlayStatusPacket.LOGIN_FAILED_SERVER;
						this.directDataPacket(pk.setChannel(Network.CHANNEL_PRIORITY));
					}
					this.close("", message, false);
					break;
				}
				
				this.randomClientId = packet->clientId;
				this.loginData.put("clientId", packet.clientId);
				this.loginData.put("loginData", null);
				this.uuid = packet.clientUUID;
				this.rawUUID = this.uuid.toBinary();
				this.clientSecret = packet.clientSecret;
				
				Boolean valid = true;
				Integer len = packet.username.lenght();
				if(len > 16 || len < 3){
					valid = false;
				}
				for(Integer i = 0; i < len and valid; i++){
					Integer c = (int)packet.username{i}; //need to be edited
					if((c >= (int)"a" && c <= (int)"z") ||
						(c >= (int)"A" && c <= (int)"Z") ||
						(c >= (int)"0" && c <= (int)"9") || c === (int)"_"
					){
						continue;
					}
					Boolean valid = false;
					break;
				}
				if(!valid || this.iusername === "rcon" || this.iusername === "console"){
					this.close("", "disconnectionScreen.invalidName");
					break;
				}
				
				if(packet.skin.lenght() !== 64 * 32 * 4 && packet.skin.lenght() !== 64 * 64 * 4){
					this.close("", "disconnectionScreen.invalidSkin");
					break;
				}
				this.setSkin(packet.skin, packet.slim);
				this.server.getPluginManager().callEvent(PlayerPreLoginEvent ev = new PlayerPreLoginEvent(this, "Plugin reason"));
				if(ev.isCancelled()){
					this.close("", ev.getKickMessage());
					break;
				}
				
				this.onPlayerPreLogin();
				break;
			case Info.MOVE_PLAYER_PACKET:
				Vector3 newPos = new Vector3(packet.x, packet.y - this.getEyeHeight(), packet.z);
				revert = false;
				if(!this.isAlive() || this.spawned !== true){
					revert = true;
					this.forceMovement = new Vector3(this.x, this.y, this.z);
				}
				if(this.teleportPosition !== null || (this.forceMovement instanceof Vector3 && ((Integer dist = newPos.distanceSquared(this.forceMovement)) > 0.1 || revert))){
					this.sendPosition(this.forceMovement, packet.yaw, packet.pitch);
				}else{
					packet.yaw %= 360;
					packet.pitch %= 360;
					if(packet.yaw < 0){
						packet.yaw += 360;
					}
					this.setRotation(packet.yaw, packet.pitch);
					this.newPosition = newPos;
					this.forceMovement = null;
				}
				break;
			case Info.PLAYER_EQUIPMENT_PACKET:
				if(this.spawned === false || !this.isAlive()){
					break;
				}
				if(packet.slot === 0x28 || packet.slot === 0 || packet.slot === 255){ //0 for 0.8.0 compatibility
					packet.slot = -1; //Air
				}else{
					packet.slot -= 9; //Get real block slot
				}
				if(this.isCreative()){ //Creative mode match
					Item item = Item.get(packet.item, packet.meta, 1);
					Byte slot = Item.getCreativeItemIndex(item);
				}else{
					Item item = this.inventory.getItem(packet.slot);
					Byte slot = packet.slot;
				}
				if(packet.slot === -1){ //Air
					if(this.isCreative()){
						Boolean found = false;
						for(i = 0; i < this.inventory.getHotbarSize(); i++){
							if(this.inventory.getHotbarSlotIndex(i) === -1){
								this.inventory.setHeldItemIndex(i);
								found = true;
								break;
							}
						}
						if(!found){ //couldn't find a empty slot (error)
							this.inventory.sendContents(this);
							break;
						}
					}else{
                        			if(packet.selectedSlot >= 0 and packet.selectedSlot < 9){
                            				this.inventory.setHeldItemIndex(packet.selectedSlot);
                            				this.inventory.setHeldItemSlot(packet.slot);
                        			}else{
                            				this->inventory->sendContents(this);
                            				break;
                        			}
					}
				}else if(item == null || slot === -1 || item.getId() !== packet.item || item.getDamage() !== packet.meta){ // packet error or not implemented
					this.inventory.sendContents(this);
					break;
				}else if(this->isCreative()){
					this.inventory.setHeldItemIndex(packet.selectedSlot);
					this.inventory.setItem(packet.selectedSlot, item);
					this.inventory.setHeldItemSlot(packet.selectedSlot);
				}else{
                    			if(packet.selectedSlot >= 0 && packet.selectedSlot < this.inventory.getHotbarSize()){
                        			this.inventory.setHeldItemIndex(packet.selectedSlot);
                        			this.inventory.setHeldItemSlot(slot);
                    			}else{
                        			this.inventory.sendContents(this);
                        			break;
                    			}
				}
				this.inventory.sendHeldItem(this.hasSpawned);
				this.setDataFlag(this.DATA_FLAGS, this.DATA_FLAG_ACTION, false);
				break;
			case Info.USE_ITEM_PACKET:
				if(this.spawned === false || !this.isAlive() || this.blocked){
					break;
				}
				Vector3 blockVector = new Vector3(packet.x, packet.y, packet.z);
				this.craftingType = 0;
				packet.eid = this.id;
				if(packet.face >= 0 && packet.face <= 5){ //Use Block, place
					this.setDataFlag(this.DATA_FLAGS, this.DATA_FLAG_ACTION, false);
					if(!this.canInteract(blockVector.add(0.5, 0.5, 0.5), 13) || this.isSpectator()){
					}else if(this.isCreative()){
						Item item = this.inventory.getItemInHand();
						if(this.level.useItemOn(blockVector, item, packet.face, packet.fx, packet.fy, packet.fz, this) === true){
							break;
						}
					}else if(this.inventory.getItemInHand().getId() !== packet.item || ((damage = this.inventory.getItemInHand().getDamage()) !== packet.meta && damage !== null)){
						this.inventory.sendHeldItem(this);
					}else{
						Item item = this.inventory.getItemInHand();
						Item oldItem = item.clone();
						//TODO: Implement adventure mode checks
						if(this.level.useItemOn(blockVector, item, packet.face, packet.fx, packet.fy, packet.fz, this)){
							if(!item.equals(oldItem, true) || item.getCount() !== oldItem.getCount()){
								this.inventory.setItemInHand(item, this);
								this.inventory.sendHeldItem(this.hasSpawned);
							}
							break;
						}
					}
					this.inventory.sendHeldItem(this);
					if(blockVector.distanceSquared(this) > 10000){
						break;
					}
					Block target = this.level.getBlock(blockVector);
					Byte block = target.getSide(packet.face);
					this.level.sendBlocks(Player[] = this, Block[] = {target, block}, UpdateBlockPacket.FLAG_ALL_PRIORITY);
					break;
				}else if(packet.face === 0xff){
					Vector3 aimPos = (new Vector3(packet.x / 32768, packet.y / 32768, packet.z / 32768)).normalize();
					if(this.isCreative()){
						Item item = this.inventory.getItemInHand();
					}else if(this.inventory.getItemInHand().getId() !== packet.item || ((Byte damage = this.inventory.getItemInHand().getDamage()) !== packet.meta && damage !== null)){
						this.inventory.sendHeldItem(this);
						break;
					}else{
						Item item = this.inventory.getItemInHand();
					}
					PlayerInteractEvent ev = new PlayerInteractEvent(this, item, aimPos, packet.face, PlayerInteractEvent.RIGHT_CLICK_AIR);
					this.server.getPluginManager().callEvent(ev);
					if(ev->isCancelled()){
						this.inventory.sendHeldItem(this);
						break;
					}
					if(item.getId() === Item.SNOWBALL){
						Compound nbt = new Compound();
						nbt->putIntArray("Pos", new IntArray("Pos", int[] = {this.x, this.y + this.getYeyHeight(), this.z}));
						nbt->putIntArray("Motion", new IntArray("Motion", int[] = {aimPos.x, aimPos.y, aimPos.z}));
						nbt->putIntArray("Rotation", new IntArray("Rotation", int[] = {this.yaw, this.pitch}));
						
						int f = 1.5;
						snowball = Entity.createEntity("Snowball", this.chunk, nbt, this);
						snowball.setMotion(snowball.getMotion().multiply(f));
						if(this.isSurvival()){
							item.setCount(item.getCount() - 1);
							this.inventory.setItemInHand(item.getCount() > 0 ? item : Item.get(Item.AIR));
						}
						if(snowball instanceof Projectile){
							this.server.getPluginManager().callEvent(ProjectileLaunchEvent projectileEv = new ProjectileLaunchEvent(snowball));
							if(projectileEv.isCancelled()){
								snowball.kill();
							}else{
								snowball.spawnToAll();
								this.level.addSound(new LaunchSound(this), this.getViewers());
							}
						}else{
							snowball.spawnToAll();
						}
					}
					this.setDataFlag(this.DATA_FLAGS, this.DATA_FLAG_ACTION, true);
					this.startAction = this.server.getTick();
				}
				break;
			case Info::PLAYER_ACTION_PACKET:
				break;
			case Info::REMOVE_BLOCK_PACKET:
				break;
			case Info::PLAYER_ARMOR_EQUIPMENT_PACKET:
				break;
			case Info::INTERACT_PACKET:
				break;
			case Info::ANIMATE_PACKET:
				break;
			case Info::SET_HEALTH_PACKET: //Not used
				break;
			case Info::ENTITY_EVENT_PACKET:
				break;
			case Info::DROP_ITEM_PACKET:
				break;
			case Info::TEXT_PACKET:
				break;
			case Info::CONTAINER_CLOSE_PACKET:
				break;
			case Info::CONTAINER_SET_CONTENT_PACKET:
				break;
			case Info::CONTAINER_SET_SLOT_PACKET:
				break;
			case Info.TILE_ENTITY_DATA_PACKET:
				break;
			default:
				break;
		}
		timings.stopTiming();
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
    
    public void onPlayerPreLogin(){
	//TODO: implement auth
	this.tryAuthenticate();
    }
    
    public void authenticateCallback($valid){
	//TODO add more stuff after authentication is available
	if(!valid){
		this.close("", "disconnectionScreen.invalidSession");
		return;
	}
	this.processLogin();
    }
    
    protected function processLogin(){
    	if(!this.server.isWhitelisted(this.getName().toLowerCase())){
		this.close(this.getLeaveMessage(), "Server is white-listed");
		break;
	}else if(this.server.getNameBans().isBanned(this.getName().toLowerCase()) || this.server.getIPBans().isBanned(this.getAddress())){
		this.close(this.getLeaveMessage(), "You are banned");
		break;
	}
	if(this.hasPermission(Server.BROADCAST_CHANNEL_USERS)){
		this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_USERS, this);
	}
	if(this.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)){
		this.server.getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this);
	}
	for(Player p : this.server.getOnlinePlayers()){
		if(p !== this and p.getName().toLowerCase() === this.getName().toLowerCase()){
			if(p.kick("logged in from another location") === false){
				this.close(this.getLeaveMessage(), "Logged in from another location");
				timings.stopTiming();
				return;
			}
		}
	}
	Map nbt = this.server.getOfflinePlayerData(this.username);
	if(nbt.get("NameTag") == null){
		nbt.putString("NameTag", this.username);
	}else{
		nbt.putString("NameTag", this.username;);
	}
	this.gamemode = nbt.get("playerGameType") & 0x03;
	if(this.server.getForceGamemode()){
		this.gamemode = this.server.getGamemode();
		nbt.putByte("playerGameType", this.gamemode);
	}
	this.allowFlight = this.isCreative();
	if((Level level = this.server.getLevelByName(nbt.get("Level"))) === null){
		this.setLevel(this.server.getDefaultLevel());
		nbt.get("Level") = this.level.getName();
		nbt.get("Pos").get("x") = this.level.getSpawnLocation().x;
		nbt.get("Pos").get("y") = this.level.getSpawnLocation().y;
		nbt.get("Pos").get("z") = this.level.getSpawnLocation().z;
	}else{
		this.setLevel(level);
	}
	if(!(nbt instanceof Compound)){
		this.close(this.getLeaveMessage(), "Invalid data");
		break;
	}
	Achievement this.achievements[];
	/** @var Byte achievement */
	for(achievement : nbt.get("Achievements")){
		this.achievements[achievement.getName()] = achievement.getValue() > 0 ? true : false;
	}
	nbt.putLong("lastPlayed", Math.floor(System.currentTimeMillis() * 1000000));
	if(this.server.getAutoSave()){
		this.server.saveOfflinePlayerData(this.username, nbt, true);
	}
	super(this.level.getChunk(nbt.get("Pos").get("x") >> 4, nbt.get("Pos").get("z") >> 4, true), nbt);
	this.loggedIn = true;
	this.server.getPluginManager().callEvent(PlayerLoginEvent ev = new PlayerLoginEvent(this, "Plugin reason"));
	if(ev.isCancelled()){
		this.close(this.getLeaveMessage(), ev.getKickMessage());
		break;
	}
	if(this.isCreative()){
		this.inventory.setHeldItemSlot(0);
	}else{
		this.inventory.setHeldItemSlot(this.inventory.getHotbarSlotIndex(0));
	}
	PlayStatusPacket pk = new PlayStatusPacket();
	pk.status = PlayStatusPacket.LOGIN_SUCCESS;
	this.dataPacket(pk.setChannel(Network.CHANNEL_PRIORITY));
	if(this.spawnPosition === null && isset(this.namedtag.get("SpawnLevel")) && (Level level = this.server.getLevelByName(this.namedtag.get("SpawnLevel"))) instanceof Level){
		this.spawnPosition = new Position(this.namedtag.get("SpawnX"), this->namedtag.get("SpawnY"), this.namedtag.get("SpawnZ"), level);
	}
	Position spawnPosition = this->getSpawn();
	StartGamePacket pk = new StartGamePacket();
	pk.seed = -1;
	pk.x = this.x;
	pk.y = this.y;
	pk.z = this.z;
	pk.spawnX = spawnPosition.x;
	pk.spawnY = spawnPosition.y;
	pk.spawnZ = spawnPosition.z;
	pk.generator = 1; //0 old, 1 infinite, 2 flat
	pk.gamemode = this.gamemode & 0x01;
	pk.eid = 0; //Always use EntityID as zero for the actual player
	this.dataPacket(pk.setChannel(Network.CHANNEL_PRIORITY));
	SetTimePacket pk = new SetTimePacket();
	pk.time = this.level.getTime();
	pk.started = this.level.stopTime == false;
	this.dataPacket(pk.setChannel(Network.CHANNEL_PRIORITY));
	pk = new SetSpawnPositionPacket();
	pk.x = spawnPosition.x;
	pk.y = spawnPosition.y;
	pk.z = spawnPosition.z;
	this.dataPacket(pk.setChannel(Network.CHANNEL_PRIORITY));
	SetHealthPacket pk = new SetHealthPacket();
	pk.health = this.getHealth();
	this.dataPacket(pk.setChannel(Network.CHANNEL_PRIORITY));
	SetDifficultyPacket pk = new SetDifficultyPacket();
	pk.difficulty = this.server.getDifficulty();
	this.dataPacket(pk.setChannel(Network.CHANNEL_PRIORITY));
	this.server.getLogger().info(this.getServer().getLanguage().translateString("pocketmine.player.logIn", [
		TextFormat.AQUA + this.username + TextFormat.WHITE,
		this.ip,
		this.port,
		this.id,
		this.level.getName(),
		Math.round(this.x, 4),
		Math.round(this.y, 4),
		Math.round(this.z, 4)
	]));
	if(this.isOp()){
		this.setRemoveFormat(false);
	}
	if(this.gamemode === Player.SPECTATOR){
		ContainerSetContentPacket pk = new ContainerSetContentPacket();
		pk.windowid = ContainerSetContentPacket.SPECIAL_CREATIVE;
		this.dataPacket(pk.setChannel(Network.CHANNEL_PRIORITY));
	}else{
		ContainerSetContentPacket pk = new ContainerSetContentPacket();
		pk.windowid = ContainerSetContentPacket.SPECIAL_CREATIVE;
		pk.slots = Item.getCreativeItems();
		this.dataPacket(pk.setChannel(Network.CHANNEL_PRIORITY));
	}
	this.forceMovement = this.teleportPosition = this.getPosition();
	this.server.onPlayerLogin(this);
    }
}
