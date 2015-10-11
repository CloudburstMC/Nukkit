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
				$newPos = new Vector3($packet->x, $packet->y - $this->getEyeHeight(), $packet->z);
				$revert = false;
				if(!$this->isAlive() or $this->spawned !== true){
					$revert = true;
					$this->forceMovement = new Vector3($this->x, $this->y, $this->z);
				}
				if($this->teleportPosition !== null or ($this->forceMovement instanceof Vector3 and (($dist = $newPos->distanceSquared($this->forceMovement)) > 0.1 or $revert))){
					$this->sendPosition($this->forceMovement, $packet->yaw, $packet->pitch);
				}else{
					$packet->yaw %= 360;
					$packet->pitch %= 360;
					if($packet->yaw < 0){
						$packet->yaw += 360;
					}
					$this->setRotation($packet->yaw, $packet->pitch);
					$this->newPosition = $newPos;
					$this->forceMovement = null;
				}
				break;
			case Info::PLAYER_EQUIPMENT_PACKET:
				if($this->spawned === false or !$this->isAlive()){
					break;
				}
				if($packet->slot === 0x28 or $packet->slot === 0 or $packet->slot === 255){ //0 for 0.8.0 compatibility
					$packet->slot = -1; //Air
				}else{
					$packet->slot -= 9; //Get real block slot
				}
				if($this->isCreative()){ //Creative mode match
					$item = Item::get($packet->item, $packet->meta, 1);
					$slot = Item::getCreativeItemIndex($item);
				}else{
					$item = $this->inventory->getItem($packet->slot);
					$slot = $packet->slot;
				}
				if($packet->slot === -1){ //Air
					if($this->isCreative()){
						$found = false;
						for($i = 0; $i < $this->inventory->getHotbarSize(); ++$i){
							if($this->inventory->getHotbarSlotIndex($i) === -1){
								$this->inventory->setHeldItemIndex($i);
								$found = true;
								break;
							}
						}
						if(!$found){ //couldn't find a empty slot (error)
							$this->inventory->sendContents($this);
							break;
						}
					}else{
                        if($packet->selectedSlot >= 0 and $packet->selectedSlot < 9){
                            $this->inventory->setHeldItemIndex($packet->selectedSlot);
                            $this->inventory->setHeldItemSlot($packet->slot);
                        }else{
                            $this->inventory->sendContents($this);
                            break;
                        }
					}
				}elseif(!isset($item) or $slot === -1 or $item->getId() !== $packet->item or $item->getDamage() !== $packet->meta){ // packet error or not implemented
					$this->inventory->sendContents($this);
					break;
				}elseif($this->isCreative()){
					$this->inventory->setHeldItemIndex($packet->selectedSlot);
					$this->inventory->setItem($packet->selectedSlot, $item);
					$this->inventory->setHeldItemSlot($packet->selectedSlot);
				}else{
                    if($packet->selectedSlot >= 0 and $packet->selectedSlot < $this->inventory->getHotbarSize()){
                        $this->inventory->setHeldItemIndex($packet->selectedSlot);
                        $this->inventory->setHeldItemSlot($slot);
                    }else{
                        $this->inventory->sendContents($this);
                        break;
                    }
				}
				$this->inventory->sendHeldItem($this->hasSpawned);
				$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, false);
				break;
			case Info::USE_ITEM_PACKET:
				if($this->spawned === false or !$this->isAlive() or $this->blocked){
					break;
				}
				$blockVector = new Vector3($packet->x, $packet->y, $packet->z);
				$this->craftingType = 0;
				$packet->eid = $this->id;
				if($packet->face >= 0 and $packet->face <= 5){ //Use Block, place
					$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, false);
					if(!$this->canInteract($blockVector->add(0.5, 0.5, 0.5), 13) or $this->isSpectator()){
					}elseif($this->isCreative()){
						$item = $this->inventory->getItemInHand();
						if($this->level->useItemOn($blockVector, $item, $packet->face, $packet->fx, $packet->fy, $packet->fz, $this) === true){
							break;
						}
					}elseif($this->inventory->getItemInHand()->getId() !== $packet->item or (($damage = $this->inventory->getItemInHand()->getDamage()) !== $packet->meta and $damage !== null)){
						$this->inventory->sendHeldItem($this);
					}else{
						$item = $this->inventory->getItemInHand();
						$oldItem = clone $item;
						//TODO: Implement adventure mode checks
						if($this->level->useItemOn($blockVector, $item, $packet->face, $packet->fx, $packet->fy, $packet->fz, $this)){
							if(!$item->equals($oldItem, true) or $item->getCount() !== $oldItem->getCount()){
								$this->inventory->setItemInHand($item, $this);
								$this->inventory->sendHeldItem($this->hasSpawned);
							}
							break;
						}
					}
					$this->inventory->sendHeldItem($this);
					if($blockVector->distanceSquared($this) > 10000){
						break;
					}
					$target = $this->level->getBlock($blockVector);
					$block = $target->getSide($packet->face);
					$this->level->sendBlocks([$this], [$target, $block], UpdateBlockPacket::FLAG_ALL_PRIORITY);
					break;
				}elseif($packet->face === 0xff){
					$aimPos = (new Vector3($packet->x / 32768, $packet->y / 32768, $packet->z / 32768))->normalize();
					if($this->isCreative()){
						$item = $this->inventory->getItemInHand();
					}elseif($this->inventory->getItemInHand()->getId() !== $packet->item or (($damage = $this->inventory->getItemInHand()->getDamage()) !== $packet->meta and $damage !== null)){
						$this->inventory->sendHeldItem($this);
						break;
					}else{
						$item = $this->inventory->getItemInHand();
					}
					$ev = new PlayerInteractEvent($this, $item, $aimPos, $packet->face, PlayerInteractEvent::RIGHT_CLICK_AIR);
					$this->server->getPluginManager()->callEvent($ev);
					if($ev->isCancelled()){
						$this->inventory->sendHeldItem($this);
						break;
					}
					if($item->getId() === Item::SNOWBALL){
						$nbt = new Compound("", [
							"Pos" => new Enum("Pos", [
								new Double("", $this->x),
								new Double("", $this->y + $this->getEyeHeight()),
								new Double("", $this->z)
							]),
							"Motion" => new Enum("Motion", [
								new Double("", $aimPos->x),
								new Double("", $aimPos->y),
								new Double("", $aimPos->z)
							]),
							"Rotation" => new Enum("Rotation", [
								new Float("", $this->yaw),
								new Float("", $this->pitch)
							]),
						]);
						$f = 1.5;
						$snowball = Entity::createEntity("Snowball", $this->chunk, $nbt, $this);
						$snowball->setMotion($snowball->getMotion()->multiply($f));
						if($this->isSurvival()){
							$item->setCount($item->getCount() - 1);
							$this->inventory->setItemInHand($item->getCount() > 0 ? $item : Item::get(Item::AIR));
						}
						if($snowball instanceof Projectile){
							$this->server->getPluginManager()->callEvent($projectileEv = new ProjectileLaunchEvent($snowball));
							if($projectileEv->isCancelled()){
								$snowball->kill();
							}else{
								$snowball->spawnToAll();
								$this->level->addSound(new LaunchSound($this), $this->getViewers());
							}
						}else{
							$snowball->spawnToAll();
						}
					}
					$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, true);
					$this->startAction = $this->server->getTick();
				}
				break;
			case Info::PLAYER_ACTION_PACKET:
				if($this->spawned === false or $this->blocked === true or (!$this->isAlive() and $packet->action !== 7)){
					break;
				}
				$this->craftingType = 0;
				$packet->eid = $this->id;
				$pos = new Vector3($packet->x, $packet->y, $packet->z);
				switch($packet->action){
					case 0: //Start break
						if($pos->distanceSquared($this) > 10000){
							break;
						}
						$target = $this->level->getBlock($pos);
						$ev = new PlayerInteractEvent($this, $this->inventory->getItemInHand(), $target, $packet->face, $target->getId() === 0 ? PlayerInteractEvent::LEFT_CLICK_AIR : PlayerInteractEvent::LEFT_CLICK_BLOCK);
						$this->getServer()->getPluginManager()->callEvent($ev);
						if($ev->isCancelled()){
							$this->inventory->sendHeldItem($this);
							break;
						}
						$this->lastBreak = microtime(true);
						break;
					case 5: //Shot arrow
						if($this->startAction > -1 and $this->getDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION)){
							if($this->inventory->getItemInHand()->getId() === Item::BOW) {
								$bow = $this->inventory->getItemInHand();
								if ($this->isSurvival() and !$this->inventory->contains(Item::get(Item::ARROW, 0, 1))) {
									$this->inventory->sendContents($this);
									break;
								}
								$nbt = new Compound("", [
									"Pos" => new Enum("Pos", [
										new Double("", $this->x),
										new Double("", $this->y + $this->getEyeHeight()),
										new Double("", $this->z)
									]),
									"Motion" => new Enum("Motion", [
										new Double("", -sin($this->yaw / 180 * M_PI) * cos($this->pitch / 180 * M_PI)),
										new Double("", -sin($this->pitch / 180 * M_PI)),
										new Double("", cos($this->yaw / 180 * M_PI) * cos($this->pitch / 180 * M_PI))
									]),
									"Rotation" => new Enum("Rotation", [
										new Float("", $this->yaw),
										new Float("", $this->pitch)
									]),
									"Fire" => new Short("Fire", $this->isOnFire() ? 45 * 60 : 0)
								]);
								$diff = ($this->server->getTick() - $this->startAction);
								$p = $diff / 20;
								$f = min((($p ** 2) + $p * 2) / 3, 1) * 2;
								$ev = new EntityShootBowEvent($this, $bow, Entity::createEntity("Arrow", $this->chunk, $nbt, $this, $f == 2 ? true : false), $f);
								if ($f < 0.1 or $diff < 5) {
									$ev->setCancelled();
								}
								$this->server->getPluginManager()->callEvent($ev);
								if ($ev->isCancelled()) {
									$ev->getProjectile()->kill();
									$this->inventory->sendContents($this);
								} else {
									$ev->getProjectile()->setMotion($ev->getProjectile()->getMotion()->multiply($ev->getForce()));
									if($this->isSurvival()){
										$this->inventory->removeItem(Item::get(Item::ARROW, 0, 1));
										$bow->setDamage($bow->getDamage() + 1);
										if ($bow->getDamage() >= 385) {
											$this->inventory->setItemInHand(Item::get(Item::AIR, 0, 0));
										} else {
											$this->inventory->setItemInHand($bow);
										}
									}
									if ($ev->getProjectile() instanceof Projectile) {
										$this->server->getPluginManager()->callEvent($projectileEv = new ProjectileLaunchEvent($ev->getProjectile()));
										if ($projectileEv->isCancelled()) {
											$ev->getProjectile()->kill();
										} else {
											$ev->getProjectile()->spawnToAll();
											$this->level->addSound(new LaunchSound($this), $this->getViewers());
										}
									} else {
										$ev->getProjectile()->spawnToAll();
									}
								}
							}
						}elseif($this->inventory->getItemInHand()->getId() === Item::BUCKET and $this->inventory->getItemInHand()->getDamage() === 1){ //Milk!
							$this->server->getPluginManager()->callEvent($ev = new PlayerItemConsumeEvent($this, $this->inventory->getItemInHand()));
							if($ev->isCancelled()){
								$this->inventory->sendContents($this);
								break;
							}
							$pk = new EntityEventPacket();
							$pk->eid = $this->getId();
							$pk->event = EntityEventPacket::USE_ITEM;
							$pk->setChannel(Network::CHANNEL_WORLD_EVENTS);
							$this->dataPacket($pk);
							Server::broadcastPacket($this->getViewers(), $pk);
							if ($this->isSurvival()) {
								$slot = $this->inventory->getItemInHand();
								--$slot->count;
								$this->inventory->setItemInHand($slot);
								$this->inventory->addItem(Item::get(Item::BUCKET, 0, 1));
							}
							$this->removeAllEffects();
						}else{
							$this->inventory->sendContents($this);
						}
						break;
					case 6: //get out of the bed
						$this->stopSleep();
						break;
					case 7: //Respawn
						if($this->spawned === false or $this->isAlive() or !$this->isOnline()){
							break;
						}
						if($this->server->isHardcore()){
							$this->setBanned(true);
							break;
						}
						$this->craftingType = 0;
						$this->server->getPluginManager()->callEvent($ev = new PlayerRespawnEvent($this, $this->getSpawn()));
						$this->teleport($ev->getRespawnPosition());
						$this->extinguish();
						$this->setDataProperty(self::DATA_AIR, self::DATA_TYPE_SHORT, 300);
						$this->deadTicks = 0;
						$this->noDamageTicks = 60;
						$this->setHealth($this->getMaxHealth());
						$this->removeAllEffects();
						$this->sendData($this);
						$this->sendSettings();
						$this->inventory->sendContents($this);
						$this->inventory->sendArmorContents($this);
						$this->blocked = false;
						$this->spawnToAll();
						$this->scheduleUpdate();
						break;
				}
				$this->startAction = -1;
				$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, false);
				break;
			case Info::REMOVE_BLOCK_PACKET:
				if($this->spawned === false or $this->blocked === true or !$this->isAlive()){
					break;
				}
				$this->craftingType = 0;
				$vector = new Vector3($packet->x, $packet->y, $packet->z);
				if($this->isCreative()){
					$item = $this->inventory->getItemInHand();
				}else{
					$item = $this->inventory->getItemInHand();
				}
				$oldItem = clone $item;
				if($this->canInteract($vector->add(0.5, 0.5, 0.5), $this->isCreative() ? 13 : 6) and $this->level->useBreakOn($vector, $item, $this)){
					if($this->isSurvival()){
						if(!$item->equals($oldItem, true) or $item->getCount() !== $oldItem->getCount()){
							$this->inventory->setItemInHand($item, $this);
							$this->inventory->sendHeldItem($this->hasSpawned);
						}
					}
					break;
				}
				$this->inventory->sendContents($this);
				$target = $this->level->getBlock($vector);
				$tile = $this->level->getTile($vector);
				$this->level->sendBlocks([$this], [$target], UpdateBlockPacket::FLAG_ALL_PRIORITY);
                $this->inventory->sendHeldItem($this);
				if($tile instanceof Spawnable){
					$tile->spawnTo($this);
				}
				break;
			case Info::PLAYER_ARMOR_EQUIPMENT_PACKET:
				break;
			case Info::INTERACT_PACKET:
				if($this->spawned === false or !$this->isAlive() or $this->blocked){
					break;
				}
				$this->craftingType = 0;
				$target = $this->level->getEntity($packet->target);
				$cancelled = false;
				if(
					$target instanceof Player and
					$this->server->getConfigBoolean("pvp", true) === false
				){
					$cancelled = true;
				}
				if($target instanceof Entity and $this->getGamemode() !== Player::VIEW and $this->isAlive() and $target->isAlive()){
					if($target instanceof DroppedItem or $target instanceof Arrow){
						$this->kick("Attempting to attack an invalid entity");
						$this->server->getLogger()->warning($this->getServer()->getLanguage()->translateString("pocketmine.player.invalidEntity", [$this->getName()]));
						break;
					}
					$item = $this->inventory->getItemInHand();
					$damageTable = [
						Item::WOODEN_SWORD => 4,
						Item::GOLD_SWORD => 4,
						Item::STONE_SWORD => 5,
						Item::IRON_SWORD => 6,
						Item::DIAMOND_SWORD => 7,
						Item::WOODEN_AXE => 3,
						Item::GOLD_AXE => 3,
						Item::STONE_AXE => 3,
						Item::IRON_AXE => 5,
						Item::DIAMOND_AXE => 6,
						Item::WOODEN_PICKAXE => 2,
						Item::GOLD_PICKAXE => 2,
						Item::STONE_PICKAXE => 3,
						Item::IRON_PICKAXE => 4,
						Item::DIAMOND_PICKAXE => 5,
						Item::WOODEN_SHOVEL => 1,
						Item::GOLD_SHOVEL => 1,
						Item::STONE_SHOVEL => 2,
						Item::IRON_SHOVEL => 3,
						Item::DIAMOND_SHOVEL => 4,
					];
					$damage = [
						EntityDamageEvent::MODIFIER_BASE => isset($damageTable[$item->getId()]) ? $damageTable[$item->getId()] : 1,
					];
					if(!$this->canInteract($target, 8)){
						$cancelled = true;
					}elseif($target instanceof Player){
						if(($target->getGamemode() & 0x01) > 0){
							break;
						}elseif($this->server->getConfigBoolean("pvp") !== true or $this->server->getDifficulty() === 0){
							$cancelled = true;
						}
						$armorValues = [
							Item::LEATHER_CAP => 1,
							Item::LEATHER_TUNIC => 3,
							Item::LEATHER_PANTS => 2,
							Item::LEATHER_BOOTS => 1,
							Item::CHAIN_HELMET => 1,
							Item::CHAIN_CHESTPLATE => 5,
							Item::CHAIN_LEGGINGS => 4,
							Item::CHAIN_BOOTS => 1,
							Item::GOLD_HELMET => 1,
							Item::GOLD_CHESTPLATE => 5,
							Item::GOLD_LEGGINGS => 3,
							Item::GOLD_BOOTS => 1,
							Item::IRON_HELMET => 2,
							Item::IRON_CHESTPLATE => 6,
							Item::IRON_LEGGINGS => 5,
							Item::IRON_BOOTS => 2,
							Item::DIAMOND_HELMET => 3,
							Item::DIAMOND_CHESTPLATE => 8,
							Item::DIAMOND_LEGGINGS => 6,
							Item::DIAMOND_BOOTS => 3,
						];
						$points = 0;
						foreach($target->getInventory()->getArmorContents() as $index => $i){
							if(isset($armorValues[$i->getId()])){
								$points += $armorValues[$i->getId()];
							}
						}
						$damage[EntityDamageEvent::MODIFIER_ARMOR] = -floor($damage[EntityDamageEvent::MODIFIER_BASE] * $points * 0.04);
					}
					$ev = new EntityDamageByEntityEvent($this, $target, EntityDamageEvent::CAUSE_ENTITY_ATTACK, $damage);
					if($cancelled){
						$ev->setCancelled();
					}
					$target->attack($ev->getFinalDamage(), $ev);
					if($ev->isCancelled()){
						if($item->isTool() and $this->isSurvival()){
							$this->inventory->sendContents($this);
						}
						break;
					}
					if($item->isTool() and $this->isSurvival()){
						if($item->useOn($target) and $item->getDamage() >= $item->getMaxDurability()){
							$this->inventory->setItemInHand(Item::get(Item::AIR, 0, 1), $this);
						}else{
							$this->inventory->setItemInHand($item, $this);
						}
					}
				}
				break;
			case Info::ANIMATE_PACKET:
				if($this->spawned === false or !$this->isAlive()){
					break;
				}
				$this->server->getPluginManager()->callEvent($ev = new PlayerAnimationEvent($this, $packet->action));
				if($ev->isCancelled()){
					break;
				}
				$pk = new AnimatePacket();
				$pk->eid = $this->getId();
				$pk->action = $ev->getAnimationType();
				Server::broadcastPacket($this->getViewers(), $pk->setChannel(Network::CHANNEL_WORLD_EVENTS));
				break;
			case Info::SET_HEALTH_PACKET: //Not used
				break;
			case Info::ENTITY_EVENT_PACKET:
				if($this->spawned === false or $this->blocked === true or !$this->isAlive()){
					break;
				}
				$this->craftingType = 0;
				$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, false); //TODO: check if this should be true
				switch($packet->event){
					case 9: //Eating
						$items = [ //TODO: move this to item classes
							Item::APPLE => 4,
							Item::MUSHROOM_STEW => 10,
							Item::BEETROOT_SOUP => 10,
							Item::BREAD => 5,
							Item::RAW_PORKCHOP => 3,
							Item::COOKED_PORKCHOP => 8,
							Item::RAW_BEEF => 3,
							Item::STEAK => 8,
							Item::COOKED_CHICKEN => 6,
							Item::RAW_CHICKEN => 2,
							Item::MELON_SLICE => 2,
							Item::GOLDEN_APPLE => 10,
							Item::PUMPKIN_PIE => 8,
							Item::CARROT => 4,
							Item::POTATO => 1,
							Item::BAKED_POTATO => 6,
							Item::COOKIE => 2,
							Item::COOKED_FISH => [
								0 => 5,
								1 => 6
							],
							Item::RAW_FISH => [
								0 => 2,
								1 => 2,
								2 => 1,
								3 => 1
							],
						];
						$slot = $this->inventory->getItemInHand();
						if($this->getHealth() < $this->getMaxHealth() and isset($items[$slot->getId()])){
							$this->server->getPluginManager()->callEvent($ev = new PlayerItemConsumeEvent($this, $slot));
							if($ev->isCancelled()){
								$this->inventory->sendContents($this);
								break;
							}
							$pk = new EntityEventPacket();
							$pk->eid = $this->getId();
							$pk->event = EntityEventPacket::USE_ITEM;
							$pk->setChannel(Network::CHANNEL_WORLD_EVENTS);
							$this->dataPacket($pk);
							Server::broadcastPacket($this->getViewers(), $pk);
							$amount = $items[$slot->getId()];
							if(is_array($amount)){
								$amount = isset($amount[$slot->getDamage()]) ? $amount[$slot->getDamage()] : 0;
							}
                            $ev = new EntityRegainHealthEvent($this, $amount, EntityRegainHealthEvent::CAUSE_EATING);
							$this->heal($ev->getAmount(), $ev);
							--$slot->count;
							$this->inventory->setItemInHand($slot, $this);
							if($slot->getId() === Item::MUSHROOM_STEW or $slot->getId() === Item::BEETROOT_SOUP){
								$this->inventory->addItem(Item::get(Item::BOWL, 0, 1));
							}elseif($slot->getId() === Item::RAW_FISH and $slot->getDamage() === 3){ //Pufferfish
								//$this->addEffect(Effect::getEffect(Effect::HUNGER)->setAmplifier(2)->setDuration(15 * 20));
								$this->addEffect(Effect::getEffect(Effect::NAUSEA)->setAmplifier(1)->setDuration(15 * 20));
								$this->addEffect(Effect::getEffect(Effect::POISON)->setAmplifier(3)->setDuration(60 * 20));
							}
						}
						break;
				}
				break;
			case Info::DROP_ITEM_PACKET:
				if($this->spawned === false or $this->blocked === true or !$this->isAlive()){
					break;
				}
				$packet->eid = $this->id;
				$item = $this->inventory->getItemInHand();
				$ev = new PlayerDropItemEvent($this, $item);
				$this->server->getPluginManager()->callEvent($ev);
				if($ev->isCancelled()){
					$this->inventory->sendContents($this);
					break;
				}
				$this->inventory->setItemInHand(Item::get(Item::AIR, 0, 1), $this);
				$motion = $this->getDirectionVector()->multiply(0.4);
				$this->level->dropItem($this->add(0, 1.3, 0), $item, $motion, 40);
				$this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ACTION, false);
				break;
			case Info::TEXT_PACKET:
				if($this->spawned === false or !$this->isAlive()){
					break;
				}
				$this->craftingType = 0;
				if($packet->type === TextPacket::TYPE_CHAT){
					$packet->message = TextFormat::clean($packet->message, $this->removeFormat);
					foreach(explode("\n", $packet->message) as $message){
						if(trim($message) != "" and strlen($message) <= 255 and $this->messageCounter-- > 0){
							$ev = new PlayerCommandPreprocessEvent($this, $message);
							if(mb_strlen($ev->getMessage(), "UTF-8") > 320){
								$ev->setCancelled();
							}
							$this->server->getPluginManager()->callEvent($ev);
							if($ev->isCancelled()){
								break;
							}
							if(substr($ev->getMessage(), 0, 1) === "/"){ //Command
								Timings::$playerCommandTimer->startTiming();
								$this->server->dispatchCommand($ev->getPlayer(), substr($ev->getMessage(), 1));
								Timings::$playerCommandTimer->stopTiming();
							}else{
								$this->server->getPluginManager()->callEvent($ev = new PlayerChatEvent($this, $ev->getMessage()));
								if(!$ev->isCancelled()){
									$this->server->broadcastMessage($this->getServer()->getLanguage()->translateString($ev->getFormat(), [$ev->getPlayer()->getDisplayName(), $ev->getMessage()]), $ev->getRecipients());
								}
							}
						}
					}
				}
				break;
			case Info::CONTAINER_CLOSE_PACKET:
				if($this->spawned === false or $packet->windowid === 0){
					break;
				}
				$this->craftingType = 0;
				$this->currentTransaction = null;
				if(isset($this->windowIndex[$packet->windowid])){
					$this->server->getPluginManager()->callEvent(new InventoryCloseEvent($this->windowIndex[$packet->windowid], $this));
					$this->removeWindow($this->windowIndex[$packet->windowid]);
				}else{
					unset($this->windowIndex[$packet->windowid]);
				}
				break;
			case Info::CONTAINER_SET_CONTENT_PACKET:
				if($packet->windowid === ContainerSetContentPacket::SPECIAL_CRAFTING){
					if(count($packet->slots) < 9){
						$this->inventory->sendContents($this);
						break;
					}
					foreach($packet->slots as $i => $item){
						/** @var Item $item */
						if($item->getDamage() === -1 or $item->getDamage() === 0xffff){
							$item->setDamage(null);
						}
						if($i < 9 and $item->getId() > 0){
							$item->setCount(1);
						}
					}
					$result = $packet->slots[9];
					if($this->craftingType === 1 or $this->craftingType === 2){
						$recipe = new BigShapelessRecipe($result);
					}else{
						$recipe = new ShapelessRecipe($result);
					}
					/** @var Item[] $ingredients */
					$ingredients = [];
					for($x = 0; $x < 3; ++$x){
						for($y = 0; $y < 3; ++$y){
							$item = $packet->slots[$x * 3 + $y];
							if($item->getCount() > 0 and $item->getId() > 0){
								//TODO shaped
								$recipe->addIngredient($item);
								$ingredients[$x * 3 + $y] = $item;
							}
						}
					}
					if(!Server::getInstance()->getCraftingManager()->matchRecipe($recipe)){
						$this->server->getLogger()->debug("Unmatched recipe from player ". $this->getName() .": " . $recipe->getResult().", using: " . implode(", ", $recipe->getIngredientList()));
						$this->inventory->sendContents($this);
						break;
					}
					$canCraft = true;
					$used = array_fill(0, $this->inventory->getSize(), 0);
					foreach($ingredients as $ingredient){
						$slot = -1;
						$checkDamage = $ingredient->getDamage() === null ? false : true;
						foreach($this->inventory->getContents() as $index => $i){
							if($ingredient->equals($i, $checkDamage) and ($i->getCount() - $used[$index]) >= 1){
								$slot = $index;
								$used[$index]++;
								break;
							}
						}
						if($slot === -1){
							$canCraft = false;
							break;
						}
					}
					if(!$canCraft){
						$this->inventory->sendContents($this);
						break;
					}
					foreach($used as $slot => $count){
						if($count === 0){
							continue;
						}
						$item = $this->inventory->getItem($slot);
						if($item->getCount() > $count){
							$newItem = clone $item;
							$newItem->setCount($item->getCount() - $count);
						}else{
							$newItem = Item::get(Item::AIR, 0, 0);
						}
						$this->inventory->setItem($slot, $newItem);
					}
					$extraItem = $this->inventory->addItem($recipe->getResult());
					if(count($extraItem) > 0){
						foreach($extraItem as $item){
							$this->level->dropItem($this, $item);
						}
					}
					switch($recipe->getResult()->getId()){
						case Item::WORKBENCH:
							$this->awardAchievement("buildWorkBench");
							break;
						case Item::WOODEN_PICKAXE:
							$this->awardAchievement("buildPickaxe");
							break;
						case Item::FURNACE:
							$this->awardAchievement("buildFurnace");
							break;
						case Item::WOODEN_HOE:
							$this->awardAchievement("buildHoe");
							break;
						case Item::BREAD:
							$this->awardAchievement("makeBread");
							break;
						case Item::CAKE:
							//TODO: detect complex recipes like cake that leave remains
							$this->awardAchievement("bakeCake");
							$this->inventory->addItem(Item::get(Item::BUCKET, 0, 3));
							break;
						case Item::STONE_PICKAXE:
						case Item::GOLD_PICKAXE:
						case Item::IRON_PICKAXE:
						case Item::DIAMOND_PICKAXE:
							$this->awardAchievement("buildBetterPickaxe");
							break;
						case Item::WOODEN_SWORD:
							$this->awardAchievement("buildSword");
							break;
						case Item::DIAMOND:
							$this->awardAchievement("diamond");
							break;
					}
				}
				break;
			case Info::CONTAINER_SET_SLOT_PACKET:
				if($this->spawned === false or $this->blocked === true or !$this->isAlive()){
					break;
				}
				if($packet->slot < 0){
					break;
				}
				if($packet->windowid === 0){ //Our inventory
					if($packet->slot >= $this->inventory->getSize()){
						break;
					}
					if($this->isCreative()){
						if(Item::getCreativeItemIndex($packet->item) !== -1){
							$this->inventory->setItem($packet->slot, $packet->item);
							$this->inventory->setHotbarSlotIndex($packet->slot, $packet->slot); //links $hotbar[$packet->slot] to $slots[$packet->slot]
						}
					}
					$transaction = new BaseTransaction($this->inventory, $packet->slot, $this->inventory->getItem($packet->slot), $packet->item);
				}elseif($packet->windowid === ContainerSetContentPacket::SPECIAL_ARMOR){ //Our armor
					if($packet->slot >= 4){
						break;
					}
					$transaction = new BaseTransaction($this->inventory, $packet->slot + $this->inventory->getSize(), $this->inventory->getArmorItem($packet->slot), $packet->item);
				}elseif(isset($this->windowIndex[$packet->windowid])){
					$this->craftingType = 0;
					$inv = $this->windowIndex[$packet->windowid];
					$transaction = new BaseTransaction($inv, $packet->slot, $inv->getItem($packet->slot), $packet->item);
				}else{
					break;
				}
				if($transaction->getSourceItem()->equals($transaction->getTargetItem(), true) and $transaction->getTargetItem()->getCount() === $transaction->getSourceItem()->getCount()){ //No changes!
					//No changes, just a local inventory update sent by the server
					break;
				}
				if($this->currentTransaction === null or $this->currentTransaction->getCreationTime() < (microtime(true) - 8)){
					if($this->currentTransaction !== null){
						foreach($this->currentTransaction->getInventories() as $inventory){
							if($inventory instanceof PlayerInventory){
								$inventory->sendArmorContents($this);
							}
							$inventory->sendContents($this);
						}
					}
					$this->currentTransaction = new SimpleTransactionGroup($this);
				}
				$this->currentTransaction->addTransaction($transaction);
				if($this->currentTransaction->canExecute()){
					$achievements = [];
					foreach($this->currentTransaction->getTransactions() as $ts){
						$inv = $ts->getInventory();
						if($inv instanceof FurnaceInventory){
							if($ts->getSlot() === 2){
								switch($inv->getResult()->getId()){
									case Item::IRON_INGOT:
										$achievements[] = "acquireIron";
										break;
								}
							}
						}
					}
					if($this->currentTransaction->execute()){
						foreach($achievements as $a){
							$this->awardAchievement($a);
						}
					}
					$this->currentTransaction = null;
				}
				break;
			case Info.TILE_ENTITY_DATA_PACKET:
				if($this->spawned === false or $this->blocked === true or !$this->isAlive()){
					break;
				}
				$this->craftingType = 0;
				$pos = new Vector3($packet->x, $packet->y, $packet->z);
				if($pos->distanceSquared($this) > 10000){
					break;
				}
				$t = $this->level->getTile($pos);
				if($t instanceof Sign){
					$nbt = new NBT(NBT::LITTLE_ENDIAN);
					$nbt->read($packet->namedtag);
					$nbt = $nbt->getData();
					if($nbt["id"] !== Tile::SIGN){
						$t->spawnTo($this);
					}else{
						$ev = new SignChangeEvent($t->getBlock(), $this, [
							TextFormat::clean($nbt["Text1"], $this->removeFormat), TextFormat::clean($nbt["Text2"], $this->removeFormat), TextFormat::clean($nbt["Text3"], $this->removeFormat), TextFormat::clean($nbt["Text4"], $this->removeFormat)
						]);
						if(!isset($t->namedtag->Creator) or $t->namedtag["Creator"] !== $this->getUniqueId()){
							$ev->setCancelled();
						}else{
							foreach($ev->getLines() as $line){
								if(mb_strlen($line, "UTF-8") > 16){
									$ev->setCancelled();
								}
							}
						}
						$this->server->getPluginManager()->callEvent($ev);
						if(!$ev->isCancelled()){
							$t->setText($ev->getLine(0), $ev->getLine(1), $ev->getLine(2), $ev->getLine(3));
						}else{
							$t->spawnTo($this);
						}
					}
				}
				break;
			default:
				break;
		}
		$timings->stopTiming();
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
