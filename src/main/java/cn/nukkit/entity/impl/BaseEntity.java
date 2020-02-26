package cn.nukkit.entity.impl;

import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.entity.*;
import cn.nukkit.entity.data.*;
import cn.nukkit.entity.misc.LightningBolt;
import cn.nukkit.event.Event;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityPortalEnterEvent.PortalType;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.EnumLevel;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.*;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.NumberTag;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.EntityLink;
import cn.nukkit.player.Player;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.utils.ChunkException;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import co.aikar.timings.TimingsHistory;
import com.google.common.collect.Iterables;
import com.spotify.futures.CompletableFutures;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static cn.nukkit.block.BlockIds.*;
import static cn.nukkit.entity.data.EntityData.AIR;
import static cn.nukkit.entity.data.EntityData.ALWAYS_SHOW_NAMETAG;
import static cn.nukkit.entity.data.EntityData.*;
import static cn.nukkit.entity.data.EntityFlag.*;
import static cn.nukkit.network.protocol.SetEntityLinkPacket.TYPE_PASSENGER;
import static cn.nukkit.network.protocol.SetEntityLinkPacket.TYPE_REMOVE;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author MagicDroidX
 */
@Log4j2
public abstract class BaseEntity extends Location implements Entity, Metadatable {

    protected static final ThreadLocal<Vector3f> temporalVector = ThreadLocal.withInitial(Vector3f::new);

    protected final Set<Player> hasSpawned = ConcurrentHashMap.newKeySet();

    protected final Map<Integer, Effect> effects = new ConcurrentHashMap<>();
    protected final List<Entity> passengers = new ArrayList<>();
    private final long runtimeId = EntityRegistry.get().newEntityId();
    private final EntityFlags flags = new EntityFlags();
    private final EntityDataMap data = new EntityDataMap()
            .putFlags(flags)
            .putShort(AIR, 400)
            .putShort(MAX_AIR, 400)
            .putString(NAMETAG, "")
            .putLong(LEAD_HOLDER_EID, -1)
            .putFloat(SCALE, 1f);
    private final EntityDataMap dataChangeSet = new EntityDataMap();
    private final EntityType<?> type;
    public Chunk chunk;
    public List<Block> blocksAround = new ArrayList<>();
    public List<Block> collisionBlocks = new ArrayList<>();
    public double lastX;
    public double lastY;
    public double lastZ;
    public boolean firstMove = true;
    public double motionX;
    public double motionY;
    public double motionZ;
    public double lastMotionX;
    public double lastMotionY;
    public double lastMotionZ;
    public double lastYaw;
    public double lastPitch;
    public double pitchDelta;
    public double yawDelta;
    public double entityCollisionReduction = 0; // Higher than 0.9 will result a fast collisions
    public AxisAlignedBB boundingBox;
    public boolean onGround;
    public boolean inBlock = false;
    public boolean positionChanged;
    public boolean motionChanged;
    public int deadTicks = 0;
    public boolean keepMovement = false;
    public float fallDistance = 0;
    public int ticksLived = 0;
    public int lastUpdate;
    public int maxFireTicks;
    public int fireTicks = 0;
    public int inPortalTicks = 0;
    public float scale = 1;
    public CompoundTag namedTag;
    public boolean isCollided = false;
    public boolean isCollidedHorizontally = false;
    public boolean isCollidedVertically = false;
    public int noDamageTicks;
    public boolean justCreated;
    public boolean fireProof;
    public boolean invulnerable;
    public double highestPosition;
    public boolean closed = false;
    protected Entity vehicle;
    protected EntityDamageEvent lastDamageCause = null;
    protected int age = 0;
    protected float health = 20;
    protected float absorption = 0;
    protected float ySize = 0;
    protected boolean isStatic = false;
    protected Server server;
    protected Timing timing;
    protected boolean isPlayer = false;
    private int maxHealth = 20;
    private volatile boolean initialized;

    public BaseEntity(EntityType<?> type, Chunk chunk, CompoundTag tag) {
        this.type = type;
        if (this instanceof Player) {
            return;
        }

        this.init(chunk, tag);
    }

    public float getHeight() {
        return 0;
    }

    public float getEyeHeight() {
        return this.getHeight() / 2 + 0.1f;
    }

    public float getWidth() {
        return 0;
    }

    public float getLength() {
        return 0;
    }

    protected double getStepHeight() {
        return 0;
    }

    public boolean canCollide() {
        return true;
    }

    public float getGravity() {
        return 0;
    }

    public float getDrag() {
        return 0;
    }

    protected float getBaseOffset() {
        return 0;
    }

    protected void initEntity() {
        if (this.namedTag.contains("ActiveEffects")) {
            ListTag<CompoundTag> effects = this.namedTag.getList("ActiveEffects", CompoundTag.class);
            for (CompoundTag e : effects.getAll()) {
                Effect effect = Effect.getEffect(e.getByte("Id"));
                if (effect == null) {
                    continue;
                }

                effect.setAmplifier(e.getByte("Amplifier")).setDuration(e.getInt("Duration")).setVisible(e.getBoolean("showParticles"));

                this.addEffect(effect);
            }
        }

        if (this.namedTag.contains("CustomName")) {
            this.setNameTag(this.namedTag.getString("CustomName"));
            if (this.namedTag.contains("CustomNameVisible")) {
                this.setNameTagVisible(this.namedTag.getBoolean("CustomNameVisible"));
            }
            if (this.namedTag.contains("CustomNameAlwaysVisible")) {
                this.setNameTagAlwaysVisible(this.namedTag.getBoolean("CustomNameAlwaysVisible"));
            }
        }

        this.setFlag(HAS_COLLISION, true);
        this.setFloatData(BOUNDING_BOX_HEIGHT, this.getHeight());
        this.setFloatData(BOUNDING_BOX_WIDTH, this.getWidth());
        this.setIntData(HEALTH, (int) this.getHealth());

        this.scheduleUpdate();
    }

    public EntityType<?> getType() {
        return type;
    }

    public EntityDataMap getData() {
        EntityDataMap map = new EntityDataMap();
        map.putAll(this.data);
        return map;
    }

    public boolean getFlag(EntityFlag flag) {
        return flags.getFlag(flag);
    }

    public void setFlag(EntityFlag flag, boolean value) {
        boolean oldValue = this.flags.getFlag(flag);
        if (value != oldValue) {
            this.flags.setFlag(flag, value);
            this.dataChangeSet.putFlags(this.flags);
        }
    }

    public boolean hasCustomName() {
        return !this.getNameTag().isEmpty();
    }

    public String getNameTag() {
        return this.getStringData(NAMETAG);
    }

    public void setNameTag(String name) {
        this.setStringData(NAMETAG, name);
    }

    public boolean isNameTagVisible() {
        return this.getFlag(CAN_SHOW_NAMETAG);
    }

    public void setNameTagVisible(boolean value) {
        this.setFlag(CAN_SHOW_NAMETAG, value);
    }

    public void setNameTagVisible() {
        this.setNameTagVisible(true);
    }

    public boolean isNameTagAlwaysVisible() {
        return this.getByteData(ALWAYS_SHOW_NAMETAG) == 1;
    }

    public void setNameTagAlwaysVisible(boolean value) {
        this.setByteData(ALWAYS_SHOW_NAMETAG, value ? 1 : 0);
    }

    public void setNameTagAlwaysVisible() {
        this.setNameTagAlwaysVisible(true);
    }

    public String getScoreTag() {
        return this.getStringData(SCORE_TAG);
    }

    public void setScoreTag(String score) {
        this.setStringData(SCORE_TAG, score);
    }

    public boolean isImmobile() {
        return this.getFlag(IMMOBILE);
    }

    public void setImmobile(boolean value) {
        this.setFlag(IMMOBILE, value);
    }

    public void setImmobile() {
        this.setImmobile(true);
    }

    public boolean canClimb() {
        return this.getFlag(CAN_CLIMB);
    }

    public void setCanClimb() {
        this.setCanClimb(true);
    }

    public void setCanClimb(boolean value) {
        this.setFlag(CAN_CLIMB, value);
    }

    public boolean canClimbWalls() {
        return this.getFlag(WALLCLIMBING);
    }

    public void setCanClimbWalls() {
        this.setCanClimbWalls(true);
    }

    public void setCanClimbWalls(boolean value) {
        this.setFlag(WALLCLIMBING, value);
    }

    public float getScale() {
        return this.scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
        this.setFloatData(SCALE, this.scale);
        this.recalculateBoundingBox();
    }

    public List<Entity> getPassengers() {
        return passengers;
    }

    public Entity getPassenger() {
        return Iterables.getFirst(this.passengers, null);
    }

    public boolean isPassenger(Entity entity) {
        return this.passengers.contains(entity);
    }

    public boolean isControlling(Entity entity) {
        return this.passengers.indexOf(entity) == 0;
    }

    public boolean hasControllingPassenger() {
        return !this.passengers.isEmpty() && isControlling(this.passengers.get(0));
    }

    public Entity getVehicle() {
        return vehicle;
    }

    public Map<Integer, Effect> getEffects() {
        return effects;
    }

    public void removeAllEffects() {
        for (Effect effect : this.effects.values()) {
            this.removeEffect(effect.getId());
        }
    }

    public void removeEffect(int effectId) {
        if (this.effects.containsKey(effectId)) {
            Effect effect = this.effects.get(effectId);
            this.effects.remove(effectId);
            effect.remove(this);

            this.recalculateEffectColor();
        }
    }

    public Effect getEffect(int effectId) {
        return this.effects.getOrDefault(effectId, null);
    }

    public boolean hasEffect(int effectId) {
        return this.effects.containsKey(effectId);
    }

    public void addEffect(Effect effect) {
        if (effect == null) {
            return; //here add null means add nothing
        }

        effect.add(this);

        this.effects.put(effect.getId(), effect);

        this.recalculateEffectColor();

        if (effect.getId() == Effect.HEALTH_BOOST) {
            this.setHealth(this.getHealth() + 4 * (effect.getAmplifier() + 1));
        }

    }

    public void recalculateBoundingBox() {
        float height = this.getHeight() * this.scale;
        double radius = (this.getWidth() * this.scale) / 2d;
        this.boundingBox.setBounds(x - radius, y, z - radius, x + radius, y + height, z + radius);

        setFloatData(BOUNDING_BOX_HEIGHT, this.getHeight());
        setFloatData(BOUNDING_BOX_WIDTH, this.getWidth());
    }

    protected void recalculateEffectColor() {
        int[] color = new int[3];
        int count = 0;
        boolean ambient = true;
        for (Effect effect : this.effects.values()) {
            if (effect.isVisible()) {
                int[] c = effect.getColor();
                color[0] += c[0] * (effect.getAmplifier() + 1);
                color[1] += c[1] * (effect.getAmplifier() + 1);
                color[2] += c[2] * (effect.getAmplifier() + 1);
                count += effect.getAmplifier() + 1;
                if (!effect.isAmbient()) {
                    ambient = false;
                }
            }
        }

        if (count > 0) {
            int r = (color[0] / count) & 0xff;
            int g = (color[1] / count) & 0xff;
            int b = (color[2] / count) & 0xff;

            this.setIntData(POTION_COLOR, (r << 16) + (g << 8) + b);
            this.setByteData(POTION_AMBIENT, ambient ? 1 : 0);
        } else {
            this.setIntData(POTION_COLOR, 0);
            this.setByteData(POTION_AMBIENT, 0);
        }
    }

    protected final void init(Chunk chunk, CompoundTag nbt) {
        if (chunk == null) {
            throw new ChunkException("Invalid garbage Chunk given to Entity");
        }

        if (this.initialized) {
            // We've already initialized this entity
            return;
        }
        this.initialized = true;

        this.timing = Timings.getEntityTiming(this.getType());

        this.isPlayer = this instanceof Player;

        this.justCreated = true;
        this.namedTag = nbt;

        this.chunk = chunk;
        this.setLevel(chunk.getLevel());
        this.server = chunk.getLevel().getServer();

        this.boundingBox = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);

        ListTag<NumberTag> posList = this.namedTag.getList("Pos", NumberTag.class);
        ListTag<FloatTag> rotationList = this.namedTag.getList("Rotation", FloatTag.class);
        ListTag<NumberTag> motionList = this.namedTag.getList("Motion", NumberTag.class);
        this.setPositionAndRotation(
                temporalVector.get().setComponents(
                        posList.get(0).getData().doubleValue(),
                        posList.get(1).getData().doubleValue(),
                        posList.get(2).getData().doubleValue()
                ),
                rotationList.get(0).data,
                rotationList.get(1).data
        );

        this.setMotion(temporalVector.get().setComponents(
                motionList.get(0).getData().doubleValue(),
                motionList.get(1).getData().doubleValue(),
                motionList.get(2).getData().doubleValue()
        ));

        if (!this.namedTag.contains("FallDistance")) {
            this.namedTag.putFloat("FallDistance", 0);
        }
        this.fallDistance = this.namedTag.getFloat("FallDistance");
        this.highestPosition = this.y + this.namedTag.getFloat("FallDistance");

        if (!this.namedTag.contains("Fire") || this.namedTag.getShort("Fire") > 32767) {
            this.namedTag.putShort("Fire", 0);
        }
        this.fireTicks = this.namedTag.getShort("Fire");

        if (!this.namedTag.contains("Air")) {
            this.namedTag.putShort("Air", 300);
        }
        this.setShortData(AIR, this.namedTag.getShort("Air"));

        if (!this.namedTag.contains("OnGround")) {
            this.namedTag.putBoolean("OnGround", false);
        }
        this.onGround = this.namedTag.getBoolean("OnGround");

        if (!this.namedTag.contains("Invulnerable")) {
            this.namedTag.putBoolean("Invulnerable", false);
        }
        this.invulnerable = this.namedTag.getBoolean("Invulnerable");

        if (!this.namedTag.contains("Scale")) {
            this.namedTag.putFloat("Scale", 1);
        }
        this.scale = this.namedTag.getFloat("Scale");
        this.setFloatData(SCALE, scale);

        chunk.addEntity(this);
        this.level.addEntity(this);

        this.initEntity();

        this.lastUpdate = this.server.getTick();
        this.server.getPluginManager().callEvent(new EntitySpawnEvent(this));

        this.scheduleUpdate();
    }

    @Override
    public CompoundTag getTag() {
        return namedTag;
    }

    public void saveNBT() {
        if (!(this instanceof Player)) {
            this.namedTag.putString("identifier", this.type.getIdentifier().toString());
            if (!this.getNameTag().equals("")) {
                this.namedTag.putString("CustomName", this.getNameTag());
                this.namedTag.putBoolean("CustomNameVisible", this.isNameTagVisible());
                this.namedTag.putBoolean("CustomNameAlwaysVisible", this.isNameTagAlwaysVisible());
            } else {
                this.namedTag.remove("CustomName");
                this.namedTag.remove("CustomNameVisible");
                this.namedTag.remove("CustomNameAlwaysVisible");
            }
        }

        this.namedTag.putList(new ListTag<FloatTag>("Pos")
                .add(new FloatTag("0", (float) this.x))
                .add(new FloatTag("1", (float) this.y))
                .add(new FloatTag("2", (float) this.z))
        );

        this.namedTag.putList(new ListTag<FloatTag>("Motion")
                .add(new FloatTag("0", (float) this.motionX))
                .add(new FloatTag("1", (float) this.motionY))
                .add(new FloatTag("2", (float) this.motionZ))
        );

        this.namedTag.putList(new ListTag<FloatTag>("Rotation")
                .add(new FloatTag("0", (float) this.yaw))
                .add(new FloatTag("1", (float) this.pitch))
        );

        this.namedTag.putFloat("FallDistance", this.fallDistance);
        this.namedTag.putShort("Fire", this.fireTicks);
        this.namedTag.putShort("Air", this.getShortData(AIR));
        this.namedTag.putBoolean("OnGround", this.onGround);
        this.namedTag.putBoolean("Invulnerable", this.invulnerable);
        this.namedTag.putFloat("Scale", this.scale);

        if (!this.effects.isEmpty()) {
            ListTag<CompoundTag> list = new ListTag<>("ActiveEffects");
            for (Effect effect : this.effects.values()) {
                list.add(new CompoundTag(String.valueOf(effect.getId()))
                        .putByte("Id", effect.getId())
                        .putByte("Amplifier", effect.getAmplifier())
                        .putInt("Duration", effect.getDuration())
                        .putBoolean("Ambient", false)
                        .putBoolean("ShowParticles", effect.isVisible())
                );
            }

            this.namedTag.putList(list);
        } else {
            this.namedTag.remove("ActiveEffects");
        }
    }

    public String getName() {
        if (this.hasCustomName()) {
            return this.getNameTag();
        } else {
            // FIXME: 04/01/2020 Use language files
            return EntityRegistry.get().getLegacyName(this.type.getIdentifier());
        }
    }

    public void spawnTo(Player player) {
        if (!player.isChunkInView(this.chunk.getX(), this.chunk.getZ()) || !this.hasSpawned.add(player)) {
            // out of range or already spawned
            return;
        }

        player.dataPacket(createAddEntityPacket());

        if (this.vehicle != null) {
            this.vehicle.spawnTo(player);

            SetEntityLinkPacket pkk = new SetEntityLinkPacket();
            pkk.vehicleUniqueId = this.vehicle.getUniqueId();
            pkk.riderUniqueId = this.getUniqueId();
            pkk.type = 1;
            pkk.immediate = 1;

            player.dataPacket(pkk);
        }
    }

    protected DataPacket createAddEntityPacket() {
        AddEntityPacket addEntity = new AddEntityPacket();
        addEntity.type = this.getType().getIdentifier();
        addEntity.entityUniqueId = this.getUniqueId();
        addEntity.entityRuntimeId = this.getUniqueId();
        addEntity.yaw = (float) this.yaw;
        addEntity.headYaw = (float) this.yaw;
        addEntity.pitch = (float) this.pitch;
        addEntity.x = (float) this.x;
        addEntity.y = (float) this.y;
        addEntity.z = (float) this.z;
        addEntity.speedX = (float) this.motionX;
        addEntity.speedY = (float) this.motionY;
        addEntity.speedZ = (float) this.motionZ;
        addEntity.dataMap = this.data;

        addEntity.links = new EntityLink[this.passengers.size()];
        for (int i = 0; i < addEntity.links.length; i++) {
            addEntity.links[i] = new EntityLink(this.getUniqueId(), this.passengers.get(i).getUniqueId(), i == 0 ? EntityLink.TYPE_RIDER : TYPE_PASSENGER, false);
        }

        return addEntity;
    }

    public Set<Player> getViewers() {
        return hasSpawned;
    }

    public void sendPotionEffects(Player player) {
        for (Effect effect : this.effects.values()) {
            MobEffectPacket pk = new MobEffectPacket();
            pk.eid = this.getUniqueId();
            pk.effectId = effect.getId();
            pk.amplifier = effect.getAmplifier();
            pk.particles = effect.isVisible();
            pk.duration = effect.getDuration();
            pk.eventId = MobEffectPacket.EVENT_ADD;

            player.dataPacket(pk);
        }
    }

    public void updateData() {
        if (this.dataChangeSet.isEmpty()) {
            return;
        }

        this.sendDataToViewers(this.dataChangeSet);

        if (this.isPlayer) {
            sendData((Player) this, this.dataChangeSet);
        }

        this.dataChangeSet.clear();
    }

    protected void sendData(Player player) {
        sendData(player, this.data);
    }

    private void sendData(Player player, EntityDataMap map) {
        SetEntityDataPacket packet = new SetEntityDataPacket();
        packet.entityRuntimeId = this.getUniqueId();
        packet.dataMap.putAll(map);

        player.dataPacket(packet);
    }

    private void sendDataToViewers(EntityDataMap map) {
        SetEntityDataPacket packet = new SetEntityDataPacket();
        packet.entityRuntimeId = this.getRuntimeId();
        packet.dataMap.putAll(map);

        Server.broadcastPacket(this.getViewers(), packet);
    }

    public void despawnFrom(Player player) {
        if (this.hasSpawned.remove(player)) {
            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.entityUniqueId = this.getUniqueId();
            player.dataPacket(pk);
        }
    }

    public boolean attack(EntityDamageEvent source) {
        if (hasEffect(Effect.FIRE_RESISTANCE)
                && (source.getCause() == DamageCause.FIRE
                || source.getCause() == DamageCause.FIRE_TICK
                || source.getCause() == DamageCause.LAVA)) {
            return false;
        }

        getServer().getPluginManager().callEvent(source);
        if (source.isCancelled()) {
            return false;
        }
        if (this.absorption > 0) {  // Damage Absorption
            this.setAbsorption(Math.max(0, this.getAbsorption() + source.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION)));
        }
        setLastDamageCause(source);
        setHealth(getHealth() - source.getFinalDamage());
        return true;
    }

    public boolean attack(float damage) {
        return this.attack(new EntityDamageEvent(this, DamageCause.CUSTOM, damage));
    }

    public void heal(EntityRegainHealthEvent source) {
        this.server.getPluginManager().callEvent(source);
        if (source.isCancelled()) {
            return;
        }
        this.setHealth(this.getHealth() + source.getAmount());
    }

    public void heal(float amount) {
        this.heal(new EntityRegainHealthEvent(this, amount, EntityRegainHealthEvent.CAUSE_REGEN));
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        if (this.health == health) {
            return;
        }

        if (health < 1) {
            if (this.isAlive()) {
                this.kill();
            }
        } else if (health <= this.getMaxHealth() || health < this.health) {
            this.health = health;
        } else {
            this.health = this.getMaxHealth();
        }

        this.setIntData(HEALTH, (int) this.health);
    }

    public boolean isAlive() {
        return this.health > 0;
    }

    public boolean isClosed() {
        return closed;
    }

    public EntityDamageEvent getLastDamageCause() {
        return lastDamageCause;
    }

    public void setLastDamageCause(EntityDamageEvent type) {
        this.lastDamageCause = type;
    }

    public int getMaxHealth() {
        return maxHealth + (this.hasEffect(Effect.HEALTH_BOOST) ? 4 * (this.getEffect(Effect.HEALTH_BOOST).getAmplifier() + 1) : 0);
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public boolean canCollideWith(Entity entity) {
        return !this.justCreated && this != entity;
    }

    protected boolean checkObstruction(double x, double y, double z) {
        if (this.level.getCollisionCubes(this, this.getBoundingBox(), false).length == 0) {
            return false;
        }

        int i = NukkitMath.floorDouble(x);
        int j = NukkitMath.floorDouble(y);
        int k = NukkitMath.floorDouble(z);

        double diffX = x - i;
        double diffY = y - j;
        double diffZ = z - k;

        BlockRegistry registry = BlockRegistry.get();

        if (!registry.getBlock(this.level.getBlockIdAt(i, j, k), 0).isTransparent()) {
            boolean flag = registry.getBlock(this.level.getBlockIdAt(i - 1, j, k), 0).isTransparent();
            boolean flag1 = registry.getBlock(this.level.getBlockIdAt(i + 1, j, k), 0).isTransparent();
            boolean flag2 = registry.getBlock(this.level.getBlockIdAt(i, j - 1, k), 0).isTransparent();
            boolean flag3 = registry.getBlock(this.level.getBlockIdAt(i, j + 1, k), 0).isTransparent();
            boolean flag4 = registry.getBlock(this.level.getBlockIdAt(i, j, k - 1), 0).isTransparent();
            boolean flag5 = registry.getBlock(this.level.getBlockIdAt(i, j, k + 1), 0).isTransparent();

            int direction = -1;
            double limit = 9999;

            if (flag) {
                limit = diffX;
                direction = 0;
            }

            if (flag1 && 1 - diffX < limit) {
                limit = 1 - diffX;
                direction = 1;
            }

            if (flag2 && diffY < limit) {
                limit = diffY;
                direction = 2;
            }

            if (flag3 && 1 - diffY < limit) {
                limit = 1 - diffY;
                direction = 3;
            }

            if (flag4 && diffZ < limit) {
                limit = diffZ;
                direction = 4;
            }

            if (flag5 && 1 - diffZ < limit) {
                direction = 5;
            }

            double force = new Random().nextDouble() * 0.2 + 0.1;

            if (direction == 0) {
                this.motionX = -force;

                return true;
            }

            if (direction == 1) {
                this.motionX = force;

                return true;
            }

            if (direction == 2) {
                this.motionY = -force;

                return true;
            }

            if (direction == 3) {
                this.motionY = force;

                return true;
            }

            if (direction == 4) {
                this.motionZ = -force;

                return true;
            }

            if (direction == 5) {
                this.motionZ = force;

                return true;
            }
        }

        return false;
    }

    public boolean entityBaseTick() {
        return this.entityBaseTick(1);
    }

    public boolean entityBaseTick(int tickDiff) {
        try (Timing ignored = Timings.entityBaseTickTimer.startTiming()) {

            if (!this.isPlayer) {
                this.blocksAround = null;
                this.collisionBlocks = null;
            }
            this.justCreated = false;

            if (!this.isAlive()) {
                this.removeAllEffects();
                this.despawnFromAll();
                if (!this.isPlayer) {
                    this.close();
                }
                return false;
            }
            if (vehicle != null && !vehicle.isAlive() && vehicle instanceof Rideable) {
                this.mount(vehicle);
            }

            updatePassengers();

            if (!this.effects.isEmpty()) {
                for (Effect effect : this.effects.values()) {
                    if (effect.canTick()) {
                        effect.applyEffect(this);
                    }
                    effect.setDuration(effect.getDuration() - tickDiff);

                    if (effect.getDuration() <= 0) {
                        this.removeEffect(effect.getId());
                    }
                }
            }

            boolean hasUpdate = false;

            this.checkBlockCollision();

            if (this.y <= -16 && this.isAlive()) {
                if (this instanceof Player) {
                    Player player = (Player) this;
                    if (player.getGamemode() != 1) this.attack(new EntityDamageEvent(this, DamageCause.VOID, 10));
                } else {
                    this.attack(new EntityDamageEvent(this, DamageCause.VOID, 10));
                    hasUpdate = true;
                }
            }

            if (this.fireTicks > 0) {
                if (this.fireProof) {
                    this.fireTicks -= 4 * tickDiff;
                    if (this.fireTicks < 0) {
                        this.fireTicks = 0;
                    }
                } else {
                    if (!this.hasEffect(Effect.FIRE_RESISTANCE) && ((this.fireTicks % 20) == 0 || tickDiff > 20)) {
                        this.attack(new EntityDamageEvent(this, DamageCause.FIRE_TICK, 1));
                    }
                    this.fireTicks -= tickDiff;
                }
                if (this.fireTicks <= 0) {
                    this.extinguish();
                } else if (!this.fireProof && (!(this instanceof Player) || !((Player) this).isSpectator())) {
                    this.setFlag(ON_FIRE, true);
                    hasUpdate = true;
                }
            }

            if (this.noDamageTicks > 0) {
                this.noDamageTicks -= tickDiff;
                if (this.noDamageTicks < 0) {
                    this.noDamageTicks = 0;
                }
            }

            if (this.inPortalTicks == 80) {
                EntityPortalEnterEvent ev = new EntityPortalEnterEvent(this, PortalType.NETHER);
                getServer().getPluginManager().callEvent(ev);

                if (!ev.isCancelled()) {
                    Position newPos = EnumLevel.moveToNether(this);
                    if (newPos != null) {
                        List<CompletableFuture<Chunk>> chunksToLoad = new ArrayList<>();
                        for (int x = -1; x < 2; x++) {
                            for (int z = -1; z < 2; z++) {
                                int chunkX = (newPos.getChunkX()) + x, chunkZ = (newPos.getChunkZ()) + z;
                                chunksToLoad.add(newPos.level.getChunkFuture(chunkX, chunkZ));
                            }
                        }
                        CompletableFutures.allAsList(chunksToLoad).whenComplete((chunks, throwable) -> {
                            if (chunks == null || throwable != null) {
                                return;
                            }

                            this.teleport(newPos.add(1.5, 1, 0.5));
                            BlockNetherPortal.spawnPortal(newPos);
                        });
                    }
                }
            }

            this.age += tickDiff;
            this.ticksLived += tickDiff;
            TimingsHistory.activatedEntityTicks++;

            return hasUpdate;
        }
    }

    public void updateMovement() {
        double diffPosition = (this.x - this.lastX) * (this.x - this.lastX) + (this.y - this.lastY) * (this.y - this.lastY) + (this.z - this.lastZ) * (this.z - this.lastZ);
        double diffRotation = (this.yaw - this.lastYaw) * (this.yaw - this.lastYaw) + (this.pitch - this.lastPitch) * (this.pitch - this.lastPitch);

        double diffMotion = (this.motionX - this.lastMotionX) * (this.motionX - this.lastMotionX) + (this.motionY - this.lastMotionY) * (this.motionY - this.lastMotionY) + (this.motionZ - this.lastMotionZ) * (this.motionZ - this.lastMotionZ);

        if (diffPosition > 0.0001 || diffRotation > 1.0) { //0.2 ** 2, 1.5 ** 2
            this.lastX = this.x;
            this.lastY = this.y;
            this.lastZ = this.z;

            this.lastYaw = this.yaw;
            this.lastPitch = this.pitch;

            this.addMovement(this.x, this.y + this.getBaseOffset(), this.z, this.yaw, this.pitch, this.yaw);
        }

        if (diffMotion > 0.0025 || (diffMotion > 0.0001 && this.getMotion().lengthSquared() <= 0.0001)) { //0.05 ** 2
            this.lastMotionX = this.motionX;
            this.lastMotionY = this.motionY;
            this.lastMotionZ = this.motionZ;

            this.addMotion(this.motionX, this.motionY, this.motionZ);
        }
    }

    public void addMovement(double x, double y, double z, double yaw, double pitch, double headYaw) {
        this.level.addEntityMovement(this, x, y, z, yaw, pitch, headYaw);
    }

    public void addMotion(double motionX, double motionY, double motionZ) {
        SetEntityMotionPacket pk = new SetEntityMotionPacket();
        pk.entityRuntimeId = this.getRuntimeId();
        pk.motionX = (float) motionX;
        pk.motionY = (float) motionY;
        pk.motionZ = (float) motionZ;

        Server.broadcastPacket(this.hasSpawned, pk);
    }

    public Vector3f getDirectionVector() {
        Vector3f vector = super.getDirectionVector();
        return temporalVector.get().setComponents(vector.x, vector.y, vector.z);
    }

    public Vector2f getDirectionPlane() {
        return (new Vector2f((float) (-Math.cos(Math.toRadians(this.yaw) - Math.PI / 2)), (float) (-Math.sin(Math.toRadians(this.yaw) - Math.PI / 2)))).normalize();
    }

    public BlockFace getHorizontalFacing() {
        return BlockFace.fromHorizontalIndex(NukkitMath.floorDouble((this.yaw * 4.0F / 360.0F) + 0.5D) & 3);
    }

    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (!this.isAlive()) {
            ++this.deadTicks;
            if (this.deadTicks >= 10) {
                this.despawnFromAll();
                if (!this.isPlayer) {
                    this.close();
                }
            }
            return this.deadTicks < 10;
        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0) {
            return false;
        }

        this.lastUpdate = currentTick;

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        this.updateMovement();

        this.updateData();

        return hasUpdate;
    }

    /**
     * Mount or dismounts an Entity from a/into vehicle
     *
     * @param vehicle The target Entity
     * @return {@code true} if the mounting successful
     */
    @Override
    public boolean mount(Entity vehicle, MountMode mode) {
        checkNotNull(vehicle, "The target of the mounting entity can't be null");

        if (this.vehicle != null && !this.vehicle.dismount(this)) {
            return false;
        }

        // Entity entering a vehicle
        EntityVehicleEnterEvent ev = new EntityVehicleEnterEvent(vehicle, this);
        server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }

        // Add variables to entity
        this.vehicle = vehicle;

        vehicle.onMount(this); // Flags have to be set before
//        this.setFlag(RIDING, true);
        updateData(); // force any data that needs to be sent
        broadcastLinkPacket(vehicle, mode.getId());

        return true;
    }

    public boolean dismount(Entity vehicle) {
        if (this.vehicle == null) {
            // Not in a vehicle
            return false;
        }

        // Run the events
        EntityVehicleExitEvent event = new EntityVehicleExitEvent(this, vehicle);
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        broadcastLinkPacket(vehicle, TYPE_REMOVE);

        // Refurbish the entity
        this.vehicle = null;
//        vehicle.setFlag(RIDING, false);
        vehicle.onDismount(this);

        this.setSeatPosition(new Vector3f());
        updatePassengerPosition(vehicle);
        this.setFlag(MOVING, true);

        return true;
    }

    @Override
    public void onMount(Entity passenger) {
        checkArgument(passenger.getVehicle() == this, "passenger is not in this vehicle");
        checkArgument(this.passengers.add(this), "passenger is already mounted to this vehicle");
        passenger.setSeatPosition(this.getMountedOffset(passenger));
        this.updatePassengerPosition(passenger);
    }

    @Override
    public void onDismount(Entity passenger) {
        checkArgument(passenger.getVehicle() != this, "passenger is still mounted");
        checkArgument(this.passengers.remove(this), "passenger is not in this vehicle");
        passenger.setSeatPosition(new Vector3f());
    }

    protected void broadcastLinkPacket(Entity vehicle, byte type) {
        SetEntityLinkPacket packet = new SetEntityLinkPacket();
        packet.riderUniqueId = getUniqueId(); // From who?
        packet.vehicleUniqueId = vehicle.getUniqueId();         // To the?
        packet.type = type;

        Server.broadcastPacket(vehicle.getViewers(), packet);
    }

    public void updatePassengers() {
        if (this.passengers.isEmpty()) {
            return;
        }

        for (Entity passenger : new ArrayList<>(this.passengers)) {
            if (!passenger.isAlive()) {
                dismount(passenger);
                continue;
            }

            updatePassengerPosition(passenger);
        }
    }

    protected void updatePassengerPosition(Entity passenger) {
        passenger.setPosition(this.add(passenger.getSeatPosition()));
    }

    public Vector3f getSeatPosition() {
        return this.getVector3fData(RIDER_SEAT_POSITION);
    }

    public void setSeatPosition(Vector3f pos) {
        this.setVector3fData(RIDER_SEAT_POSITION, pos);
    }

    public Vector3f getMountedOffset(Entity entity) {
        return new Vector3f(0, getHeight() * 0.75f);
    }

    public final void scheduleUpdate() {
        this.level.scheduleEntityUpdate(this);
    }

    @Override
    public int getNoDamageTicks() {
        return noDamageTicks;
    }

    public void setNoDamageTicks(int noDamageTicks) {
        this.noDamageTicks = noDamageTicks;
    }

    @Override
    public int getFireTicks() {
        return fireTicks;
    }

    @Override
    public void setOnFire(int seconds) {
        int ticks = seconds * 20;
        if (ticks > this.fireTicks) {
            this.fireTicks = ticks;
        }
    }

    public float getAbsorption() {
        return absorption;
    }

    public void setAbsorption(float absorption) {
        if (absorption != this.absorption) {
            this.absorption = absorption;
            if (this instanceof Player)
                ((Player) this).setAttribute(Attribute.getAttribute(Attribute.ABSORPTION).setValue(absorption));
        }
    }

    public BlockFace getDirection() {
        double rotation = this.yaw % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if ((0 <= rotation && rotation < 45) || (315 <= rotation && rotation < 360)) {
            return BlockFace.SOUTH;
        } else if (45 <= rotation && rotation < 135) {
            return BlockFace.WEST;
        } else if (135 <= rotation && rotation < 225) {
            return BlockFace.NORTH;
        } else if (225 <= rotation && rotation < 315) {
            return BlockFace.EAST;
        } else {
            return null;
        }
    }

    public void extinguish() {
        this.fireTicks = 0;
        this.setFlag(ON_FIRE, false);
    }

    public boolean canTriggerWalking() {
        return true;
    }

    @Override
    public double getHighestPosition() {
        return highestPosition;
    }

    @Override
    public void setHighestPosition(double highestPosition) {
        this.highestPosition = highestPosition;
    }

    public void resetFallDistance() {
        this.highestPosition = 0;
    }

    protected void updateFallState(boolean onGround) {
        if (onGround) {
            fallDistance = (float) (this.highestPosition - this.y);

            if (fallDistance > 0) {
                // check if we fell into at least 1 block of water
                if (this instanceof EntityLiving && !(this.getLevelBlock() instanceof BlockWater)) {
                    this.fall(fallDistance);
                }
                this.resetFallDistance();
            }
        }
    }

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public void fall(float fallDistance) {
        float damage = (float) Math.floor(fallDistance - 3 - (this.hasEffect(Effect.JUMP) ? this.getEffect(Effect.JUMP).getAmplifier() + 1 : 0));
        if (damage > 0) {
            this.attack(new EntityDamageEvent(this, DamageCause.FALL, damage));
        }

        if (fallDistance > 0.75) {
            Block down = this.level.getBlock(this.asBlockPosition().down());

            if (down.getId() == FARMLAND) {
                Event ev;

                if (this instanceof Player) {
                    ev = new PlayerInteractEvent((Player) this, null, down, null, Action.PHYSICAL);
                } else {
                    ev = new EntityInteractEvent(this, down);
                }

                this.server.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return;
                }
                this.level.setBlock(down, Block.get(BlockIds.DIRT), false, true);
            }
        }
    }

    public void handleLavaMovement() {
        //todo
    }

    public void moveFlying(float strafe, float forward, float friction) {
        // This is special for Nukkit! :)
        float speed = strafe * strafe + forward * forward;
        if (speed >= 1.0E-4F) {
            speed = MathHelper.sqrt(speed);
            if (speed < 1.0F) {
                speed = 1.0F;
            }
            speed = friction / speed;
            strafe *= speed;
            forward *= speed;
            float nest = MathHelper.sin((float) (this.yaw * 3.1415927F / 180.0F));
            float place = MathHelper.cos((float) (this.yaw * 3.1415927F / 180.0F));
            this.motionX += strafe * place - forward * nest;
            this.motionZ += forward * place + strafe * nest;
        }
    }

    public void onCollideWithPlayer(Human entityPlayer) {

    }

    @Override
    public void onEntityCollision(Entity entity) {
        if (entity.getVehicle() != this && !entity.getPassengers().contains(this)) {
            double dx = entity.getX() - this.x;
            double dy = entity.getZ() - this.z;
            double dz = NukkitMath.getDirection(dx, dy);

            if (dz >= 0.009999999776482582D) {
                dz = MathHelper.sqrt((float) dz);
                dx /= dz;
                dy /= dz;
                double d3 = 1.0D / dz;

                if (d3 > 1.0D) {
                    d3 = 1.0D;
                }

                dx *= d3;
                dy *= d3;
                dx *= 0.05000000074505806;
                dy *= 0.05000000074505806;
                dx *= 1F + entityCollisionReduction;

                if (this.vehicle == null) {
                    motionX -= dx;
                    motionZ -= dy;
                }
            }
        }
    }

    public void onStruckByLightning(LightningBolt lightningBolt) {
        if (this.attack(new EntityDamageByEntityEvent(lightningBolt, this, DamageCause.LIGHTNING, 5))) {
            if (this.fireTicks < 8 * 20) {
                this.setOnFire(8);
            }
        }
    }

    public boolean onInteract(Player player, Item item, Vector3f clickedPos) {
        return onInteract(player, item);
    }

    public boolean onInteract(Player player, Item item) {
        return false;
    }

    protected boolean switchLevel(Level targetLevel) {
        if (this.closed) {
            return false;
        }

        if (this.isValid()) {
            EntityLevelChangeEvent ev = new EntityLevelChangeEvent(this, this.level, targetLevel);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }

            this.level.removeEntity(this);
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }
            this.despawnFromAll();
        }

        this.setLevel(targetLevel);
        this.level.addEntity(this);
        this.chunk = null;

        return true;
    }

    public Position getPosition() {
        return new Position(this.x, this.y, this.z, this.level);
    }

    public Location getLocation() {
        return new Location(this.x, this.y, this.z, this.yaw, this.pitch, this.level);
    }

    public boolean isInsideOfWater() {
        double y = this.y + this.getEyeHeight();
        Block block = this.level.getLoadedBlock(NukkitMath.floorDouble(this.x), NukkitMath.floorDouble(y),
                NukkitMath.floorDouble(this.z));

        if (block instanceof BlockWater || (block = block != null? block.getBlockAtLayer(1): null) instanceof BlockWater) {
            double f = (block.y + 1) - (((BlockWater) block).getFluidHeightPercent() - 0.1111111);
            return y < f;
        }

        return false;
    }

    public boolean isInsideOfSolid() {
        double y = this.y + this.getEyeHeight();
        Block block = this.level.getLoadedBlock(
                temporalVector.get().setComponents(
                        NukkitMath.floorDouble(this.x),
                        NukkitMath.floorDouble(y),
                        NukkitMath.floorDouble(this.z))
        );

        if (block == null) {
            return true;
        }

        AxisAlignedBB bb = block.getBoundingBox();

        return bb != null && block.isSolid() && !block.isTransparent() && bb.intersectsWith(this.getBoundingBox());

    }

    public boolean isInsideOfFire() {
        for (Block block : this.getCollisionBlocks()) {
            if (block instanceof BlockFire) {
                return true;
            }
        }

        return false;
    }

    public boolean isOnLadder() {
        Block block = this.level.getLoadedBlock(
                temporalVector.get().setComponents(
                        NukkitMath.floorDouble(this.x),
                        NukkitMath.floorDouble(this.y),
                        NukkitMath.floorDouble(this.z))
        );

        return block != null && block.getId() == LADDER;
    }

    public boolean fastMove(double dx, double dy, double dz) {
        if (dx == 0 && dy == 0 && dz == 0) {
            return true;
        }

        try (Timing ignored = Timings.entityMoveTimer.startTiming()) {
            AxisAlignedBB newBB = this.boundingBox.getOffsetBoundingBox(dx, dy, dz);

            if (server.getAllowFlight() || !this.level.hasCollision(this, newBB, false)) {
                this.boundingBox = newBB;
            }

            this.x = (this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2;
            this.y = this.boundingBox.getMinY() - this.ySize;
            this.z = (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2;

            this.checkChunks();

            if (!this.onGround || dy != 0) {
                AxisAlignedBB bb = this.boundingBox.clone();
                bb.setMinY(bb.getMinY() - 0.75);

                this.onGround = this.level.getCollisionBlocks(bb).length > 0;
            }
            this.isCollided = this.onGround;
            this.updateFallState(this.onGround);
            return true;
        }
    }

    public boolean move(double dx, double dy, double dz) {
        if (dx == 0 && dz == 0 && dy == 0) {
            return true;
        }

        if (this.keepMovement) {
            this.boundingBox.offset(dx, dy, dz);
            this.setPosition(temporalVector.get().setComponents((this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2, this.boundingBox.getMinY(), (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2));
            this.onGround = this.isPlayer;
        } else {

            try (Timing ignored = Timings.entityMoveTimer.startTiming()) {
                this.ySize *= 0.4;

                double movX = dx;
                double movY = dy;
                double movZ = dz;

                AxisAlignedBB axisalignedbb = this.boundingBox.clone();

                AxisAlignedBB[] list = this.level.getCollisionCubes(this, this.level.getTickRate() > 1 ? this.boundingBox.getOffsetBoundingBox(dx, dy, dz) : this.boundingBox.addCoord(dx, dy, dz), false, true);

                for (AxisAlignedBB bb : list) {
                    dy = bb.calculateYOffset(this.boundingBox, dy);
                }

                this.boundingBox.offset(0, dy, 0);

                boolean fallingFlag = (this.onGround || (dy != movY && movY < 0));

                for (AxisAlignedBB bb : list) {
                    dx = bb.calculateXOffset(this.boundingBox, dx);
                }

                this.boundingBox.offset(dx, 0, 0);

                for (AxisAlignedBB bb : list) {
                    dz = bb.calculateZOffset(this.boundingBox, dz);
                }

                this.boundingBox.offset(0, 0, dz);

                if (this.getStepHeight() > 0 && fallingFlag && this.ySize < 0.05 && (movX != dx || movZ != dz)) {
                    double cx = dx;
                    double cy = dy;
                    double cz = dz;
                    dx = movX;
                    dy = this.getStepHeight();
                    dz = movZ;

                    AxisAlignedBB axisalignedbb1 = this.boundingBox.clone();

                    this.boundingBox.setBB(axisalignedbb);

                    list = this.level.getCollisionCubes(this, this.boundingBox.addCoord(dx, dy, dz), false);

                    for (AxisAlignedBB bb : list) {
                        dy = bb.calculateYOffset(this.boundingBox, dy);
                    }

                    this.boundingBox.offset(0, dy, 0);

                    for (AxisAlignedBB bb : list) {
                        dx = bb.calculateXOffset(this.boundingBox, dx);
                    }

                    this.boundingBox.offset(dx, 0, 0);

                    for (AxisAlignedBB bb : list) {
                        dz = bb.calculateZOffset(this.boundingBox, dz);
                    }

                    this.boundingBox.offset(0, 0, dz);

                    this.boundingBox.offset(0, 0, dz);

                    if ((cx * cx + cz * cz) >= (dx * dx + dz * dz)) {
                        dx = cx;
                        dy = cy;
                        dz = cz;
                        this.boundingBox.setBB(axisalignedbb1);
                    } else {
                        this.ySize += 0.5;
                    }

                }

                this.x = (this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2;
                this.y = this.boundingBox.getMinY() - this.ySize;
                this.z = (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2;

                this.checkChunks();

                this.checkGroundState(movX, movY, movZ, dx, dy, dz);
                this.updateFallState(this.onGround);

                if (movX != dx) {
                    this.motionX = 0;
                }

                if (movY != dy) {
                    this.motionY = 0;
                }

                if (movZ != dz) {
                    this.motionZ = 0;
                }

                //TODO: vehicle collision events (first we need to spawn them!)
            }
        }
        return true;
    }

    protected void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        this.isCollidedVertically = movY != dy;
        this.isCollidedHorizontally = (movX != dx || movZ != dz);
        this.isCollided = (this.isCollidedHorizontally || this.isCollidedVertically);
        this.onGround = (movY != dy && movY < 0);
    }

    public List<Block> getBlocksAround() {
        if (this.blocksAround == null) {
            int minX = NukkitMath.floorDouble(this.boundingBox.getMinX());
            int minY = NukkitMath.floorDouble(this.boundingBox.getMinY());
            int minZ = NukkitMath.floorDouble(this.boundingBox.getMinZ());
            int maxX = NukkitMath.ceilDouble(this.boundingBox.getMaxX());
            int maxY = NukkitMath.ceilDouble(this.boundingBox.getMaxY());
            int maxZ = NukkitMath.ceilDouble(this.boundingBox.getMaxZ());

            this.blocksAround = new ArrayList<>();

            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        this.blocksAround.add(this.level.getBlock(x, y, z));
                    }
                }
            }
        }

        return this.blocksAround;
    }

    public List<Block> getCollisionBlocks() {
        if (this.collisionBlocks == null) {
            this.collisionBlocks = new ArrayList<>();

            for (Block b : getBlocksAround()) {
                if (b.collidesWithBB(this.getBoundingBox(), true)) {
                    this.collisionBlocks.add(b);
                }
                Block layer1 = b.getBlockAtLayer(1);
                if (layer1.collidesWithBB(this.getBoundingBox(), true)) {
                    this.collisionBlocks.add(layer1);
                }
            }
        }

        return this.collisionBlocks;
    }

    /**
     * Returns whether this entity can be moved by currents in liquids.
     *
     * @return boolean
     */
    public boolean canBeMovedByCurrents() {
        return true;
    }

    protected void checkBlockCollision() {
        Vector3f vector = new Vector3f(0, 0, 0);
        boolean portal = false;

        for (Block block : this.getCollisionBlocks()) {
            if (block.getId() == PORTAL) {
                portal = true;
                continue;
            }

            block.onEntityCollide(this);
            block.addVelocityToEntity(this, vector);
        }

        if (portal) {
            if (this.inPortalTicks < 80) {
                this.inPortalTicks = 80;
            } else {
                this.inPortalTicks++;
            }
        } else {
            this.inPortalTicks = 0;
        }

        if (vector.lengthSquared() > 0) {
            vector = vector.normalize();
            double d = 0.014d;
            this.motionX += vector.x * d;
            this.motionY += vector.y * d;
            this.motionZ += vector.z * d;
        }
    }

    public boolean setPositionAndRotation(Vector3f pos, double yaw, double pitch) {
        if (this.setPosition(pos)) {
            this.setRotation(yaw, pitch);
            return true;
        }

        return false;
    }

    public void setRotation(double yaw, double pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.scheduleUpdate();
    }

    public boolean canTriggerPressurePlate() {
        return true;
    }

    public boolean canPassThrough() {
        return true;
    }

    protected void checkChunks() {
        if (this.chunk == null || (this.chunk.getX() != this.getChunkX() || this.chunk.getZ() != this.getChunkZ())) {
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }
            this.chunk = this.level.getLoadedChunk(this.getChunkX(), this.getChunkZ());
            if (chunk == null) {
                return; // Converter will throw NPE otherwise.
            }

            if (!this.justCreated) {
                Set<Player> loaders = chunk.getPlayerLoaders();
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

    public boolean setPosition(Vector3f pos) {
        if (this.closed) {
            return false;
        }

        if (pos instanceof Position && ((Position) pos).level != null && ((Position) pos).level != this.level) {
            if (!this.switchLevel(((Position) pos).getLevel())) {
                return false;
            }
        }

        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;

        this.recalculateBoundingBox();

        this.checkChunks();

        return true;
    }

    public Vector3f getMotion() {
        return new Vector3f(this.motionX, this.motionY, this.motionZ);
    }

    public boolean setMotion(Vector3f motion) {
        if (!this.justCreated) {
            EntityMotionEvent ev = new EntityMotionEvent(this, motion);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }
        }

        this.motionX = motion.x;
        this.motionY = motion.y;
        this.motionZ = motion.z;

        if (!this.justCreated) {
            this.updateMovement();
        }

        return true;
    }

    public boolean isOnGround() {
        return onGround;
    }

    @Override
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void kill() {
        this.health = 0;
        this.scheduleUpdate();

        for (Entity passenger : new ArrayList<>(this.passengers)) {
            passenger.dismount(this);
        }
    }

    public boolean teleport(Vector3f pos) {
        return this.teleport(pos, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public boolean teleport(Vector3f pos, PlayerTeleportEvent.TeleportCause cause) {
        return this.teleport(Location.fromObject(pos, this.level, this.yaw, this.pitch), cause);
    }

    public boolean teleport(Position pos) {
        return this.teleport(pos, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public boolean teleport(Position pos, PlayerTeleportEvent.TeleportCause cause) {
        return this.teleport(Location.fromObject(pos, pos.level, this.yaw, this.pitch), cause);
    }

    public boolean teleport(Location location) {
        return this.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        double yaw = location.yaw;
        double pitch = location.pitch;

        Location from = this.getLocation();
        Location to = location;
        if (cause != null) {
            EntityTeleportEvent ev = new EntityTeleportEvent(this, from, to);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }
            to = ev.getTo();
        }

        this.ySize = 0;

        this.setMotion(temporalVector.get().setComponents(0, 0, 0));

        if (this.setPositionAndRotation(to, yaw, pitch)) {
            this.resetFallDistance();
            this.onGround = true;

            this.updateMovement();

            return true;
        }

        return false;
    }

    public long getUniqueId() {
        return this.runtimeId;
    }

    public long getRuntimeId() {
        return this.runtimeId;
    }

    public void respawnToAll() {
        for (Player player : this.hasSpawned) {
            this.spawnTo(player);
        }
        this.hasSpawned.clear();
    }

    public void spawnToAll() {
        if (this.chunk == null || this.closed) {
            return;
        }

        for (Player player : this.level.getChunkPlayers(this.chunk.getX(), this.chunk.getZ())) {
            if (player.isOnline()) {
                this.spawnTo(player);
            }
        }
    }

    public void despawnFromAll() {
        for (Player player : this.hasSpawned) {
            this.despawnFrom(player);
        }
    }

    public void close() {
        if (!this.closed) {
            this.closed = true;
            this.server.getPluginManager().callEvent(new EntityDespawnEvent(this));
            this.despawnFromAll();
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }

            if (this.level != null) {
                this.level.removeEntity(this);
            }
        }
    }

    @Nullable
    @Override
    public Entity getOwner() {
        long id = this.data.getLong(OWNER_EID, -1);
        if (id == -1) {
            return null;
        }
        return this.level.getEntity(id);
    }

    @Override
    public void setOwner(@Nullable Entity entity) {
        setLongData(OWNER_EID, entity == null ? -1 : entity.getUniqueId());
    }

    public boolean hasData(EntityData data) {
        return this.data.contains(data);
    }

    public EntityDataType getDataType(EntityData data) {
        return this.data.getType(data);
    }

    public int getIntData(EntityData data) {
        return this.data.getInt(data);
    }

    public void setIntData(EntityData data, int value) {
        int oldValue = getIntData(data);
        if (oldValue != value) {
            this.data.putInt(data, value);
            this.dataChangeSet.putInt(data, value);
        }
    }

    public short getShortData(EntityData data) {
        return this.data.getShort(data);
    }

    public void setShortData(EntityData data, int value) {
        value = (short) value;
        int oldValue = getShortData(data);
        if (oldValue != value) {
            this.data.putShort(data, value);
            this.dataChangeSet.putShort(data, value);
        }
    }

    public byte getByteData(EntityData data) {
        return this.data.getByte(data);
    }

    public void setByteData(EntityData data, int value) {
        value = (byte) value;
        int oldValue = getByteData(data);
        if (oldValue != value) {
            this.data.putByte(data, value);
            this.dataChangeSet.putByte(data, value);
        }
    }

    public boolean getBooleanData(EntityData data) {
        return getByteData(data) != 0;
    }

    public void setBooleanData(EntityData data, boolean value) {
        boolean oldValue = getBooleanData(data);
        if (oldValue != value) {
            this.data.putByte(data, value ? 1 : 0);
            this.dataChangeSet.putByte(data, value ? 1 : 0);
        }
    }

    public long getLongData(EntityData data) {
        return this.data.getLong(data);
    }

    public void setLongData(EntityData data, long value) {
        long oldValue = getLongData(data);
        if (oldValue != value) {
            this.data.putLong(data, value);
            this.dataChangeSet.putLong(data, value);
        }
    }

    public String getStringData(EntityData data) {
        return this.data.getString(data);
    }

    public void setStringData(EntityData data, String value) {
        String oldValue = getStringData(data);
        if (!Objects.equals(oldValue, value)) {
            this.data.putString(data, value);
            this.dataChangeSet.putString(data, value);
        }
    }

    public float getFloatData(EntityData data) {
        return this.data.getFloat(data);
    }

    public void setFloatData(EntityData data, float value) {
        float oldValue = getFloatData(data);
        if (oldValue != value) {
            this.data.putFloat(data, value);
            this.dataChangeSet.putFloat(data, value);
        }
    }

    public CompoundTag getTagData(EntityData data) {
        return this.data.getTag(data);
    }

    public void setTagData(EntityData data, CompoundTag value) {
        CompoundTag oldValue = getTagData(data);
        if (!Objects.equals(oldValue, value)) {
            this.data.putTag(data, value);
            this.dataChangeSet.putTag(data, value);
        }
    }

    public Vector3i getPosData(EntityData data) {
        return this.data.getPos(data);
    }

    public void setPosData(EntityData data, Vector3i value) {
        Vector3i oldValue = getPosData(data);
        if (!Objects.equals(oldValue, value)) {
            this.data.putPos(data, value);
            this.dataChangeSet.putPos(data, value);
        }
    }

    public Vector3f getVector3fData(EntityData data) {
        return this.data.getVector3f(data);
    }

    public void setVector3fData(EntityData data, Vector3f value) {
        Vector3f oldValue = getVector3fData(data);
        if (!Objects.equals(oldValue, value)) {
            this.data.putVector3f(data, value);
            this.dataChangeSet.putVector3f(data, value);
        }
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        this.server.getEntityMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return this.server.getEntityMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return this.server.getEntityMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        this.server.getEntityMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    public Server getServer() {
        return server;
    }

    @Override
    public String toString() {
        return "Entity(type=" + type.getIdentifier() + ", id=" + getUniqueId() + ")";
    }
}
