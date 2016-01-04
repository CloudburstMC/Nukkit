package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.Water;
import cn.nukkit.entity.data.*;
import cn.nukkit.event.entity.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.MobEffectPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.SetEntityDataPacket;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.ChunkException;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Entity extends Location implements Metadatable {

    public static final int NETWORK_ID = -1;

    public abstract int getNetworkId();

    public static final int DATA_TYPE_BYTE = 0;
    public static final int DATA_TYPE_SHORT = 1;
    public static final int DATA_TYPE_INT = 2;
    public static final int DATA_TYPE_FLOAT = 3;
    public static final int DATA_TYPE_STRING = 4;
    public static final int DATA_TYPE_SLOT = 5;
    public static final int DATA_TYPE_POS = 6;
    public static final int DATA_TYPE_ROTATION = 7;
    public static final int DATA_TYPE_LONG = 8;

    public static final int DATA_FLAGS = 0;
    public static final int DATA_AIR = 1;
    public static final int DATA_NAMETAG = 2;
    public static final int DATA_SHOW_NAMETAG = 3;
    public static final int DATA_SILENT = 4;
    public static final int DATA_POTION_COLOR = 7;
    public static final int DATA_POTION_AMBIENT = 8;
    public static final int DATA_NO_AI = 15;
    public static final int DATA_POTION_TYPE = 16;

    public static final int DATA_FLAG_ONFIRE = 0;
    public static final int DATA_FLAG_SNEAKING = 1;
    public static final int DATA_FLAG_RIDING = 2;
    public static final int DATA_FLAG_SPRINTING = 3;
    public static final int DATA_FLAG_ACTION = 4;
    public static final int DATA_FLAG_INVISIBLE = 5;

    public static long entityCount = 1;

    private static Map<Integer, Class<? extends Entity>> knownEntities = new HashMap<>();
    private static Map<String, Class<? extends Entity>> shortNames = new HashMap<>();

    protected Map<Integer, Player> hasSpawned = new HashMap<>();

    protected Map<Integer, Effect> effects = new HashMap<>();

    protected long id;

    protected int dataFlags = 0;

    protected Map<Integer, EntityData> dataProperties = new HashMap<Integer, EntityData>() {{
        put(DATA_FLAGS, new ByteEntityData((byte) 0));
        put(DATA_AIR, new ShortEntityData(300));
        put(DATA_NAMETAG, new StringEntityData(""));
        put(DATA_SHOW_NAMETAG, new ByteEntityData((byte) 1));
        put(DATA_SILENT, new ByteEntityData((byte) 0));
        put(DATA_NO_AI, new ByteEntityData((byte) 0));
    }};

    public Entity passenger = null;

    public Entity vehicle = null;

    public FullChunk chunk;

    protected EntityDamageEvent lastDamageCause = null;

    private List<Block> blocksAround = new ArrayList<>();

    public double lastX;
    public double lastY;
    public double lastZ;

    public boolean firstMove = true;

    public double motionX;
    public double motionY;
    public double motionZ;

    public Vector3 temporalVector;
    public double lastMotionX;
    public double lastMotionY;
    public double lastMotionZ;

    public double lastYaw;
    public double lastPitch;

    public AxisAlignedBB boundingBox;
    public boolean onGround;
    public boolean inBlock = false;
    public boolean positionChanged;
    public boolean motionChanged;
    public int deadTicks = 0;
    protected int age = 0;

    protected int health = 20;
    private int maxHealth = 20;

    protected float ySize = 0;
    public boolean keepMovement = false;

    public float fallDistance = 0;
    public int ticksLived = 0;
    public int lastUpdate;
    public int maxFireTicks;
    public int fireTicks = 0;

    public CompoundTag namedTag;

    protected boolean isStatic = false;

    public boolean isCollided = false;
    public boolean isCollidedHorizontally = false;
    public boolean isCollidedVertically = false;

    public int noDamageTicks;
    public boolean justCreated;
    public boolean fireProof;
    public boolean invulnerable;

    protected Server server;

    public boolean closed = false;

    protected boolean isPlayer = false;

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

    protected float getGravity() {
        return 0;
    }

    protected float getDrag() {
        return 0;
    }

    public Entity(FullChunk chunk, CompoundTag nbt) {
        if (this instanceof Player) {
            return;
        }

        if ((chunk == null || chunk.getProvider() == null)) {
            throw new ChunkException("Invalid garbage Chunk given to Entity");
        }

        this.isPlayer = this instanceof Player;
        this.temporalVector = new Vector3();

        this.id = Entity.entityCount++;
        this.justCreated = true;
        this.namedTag = nbt;

        this.chunk = chunk;
        this.setLevel(chunk.getProvider().getLevel());
        this.server = chunk.getProvider().getLevel().getServer();

        this.boundingBox = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

        ListTag<DoubleTag> posList = this.namedTag.getList("Pos", new ListTag<>());
        ListTag<FloatTag> rotationList = this.namedTag.getList("Rotation", new ListTag<>());
        ListTag<DoubleTag> motionList = this.namedTag.getList("Motion", new ListTag<>());
        this.setPositionAndRotation(
                this.temporalVector.setComponents(
                        posList.get(0).data,
                        posList.get(1).data,
                        posList.get(2).data
                ),
                rotationList.get(0).data,
                rotationList.get(1).data
        );

        this.setMotion(this.temporalVector.setComponents(
                motionList.get(0).data,
                motionList.get(1).data,
                motionList.get(2).data
        ));

        if (!this.namedTag.contains("FallDistance")) {
            this.namedTag.putFloat("FallDistance", 0);
        }
        this.fallDistance = this.namedTag.getFloat("FallDistance");

        if (!this.namedTag.contains("Fire")) {
            this.namedTag.putShort("Fire", 0);
        }
        this.fireTicks = this.namedTag.getShort("Fire");

        if (!this.namedTag.contains("Air")) {
            this.namedTag.putShort("Air", 300);
        }
        this.setDataProperty(DATA_AIR, new ShortEntityData(this.namedTag.getShort("Air")));

        if (!this.namedTag.contains("OnGround")) {
            this.namedTag.putBoolean("OnGround", false);
        }
        this.onGround = this.namedTag.getBoolean("OnGround");

        if (!this.namedTag.contains("Invulnerable")) {
            this.namedTag.putBoolean("Invulnerable", false);
        }
        this.invulnerable = this.namedTag.getBoolean("Invulnerable");

        this.chunk.addEntity(this);
        this.level.addEntity(this);
        this.initEntity();
        this.lastUpdate = this.server.getTick();
        this.server.getPluginManager().callEvent(new EntitySpawnEvent(this));

        this.scheduleUpdate();

    }

    public String getNameTag() {
        return this.getDataPropertyString(DATA_NAMETAG).getData();
    }

    public boolean isNameTagVisible() {
        return this.getDataPropertyByte(DATA_SHOW_NAMETAG).data > 0;
    }

    public void setNameTag(String name) {
        this.setDataProperty(DATA_NAMETAG, new StringEntityData(name));
    }

    public void setNameTagVisible() {
        this.setNameTagVisible(true);
    }

    public void setNameTagVisible(boolean visible) {
        this.setDataProperty(DATA_SHOW_NAMETAG, new ByteEntityData(visible ? (byte) 1 : (byte) 0));
    }

    public boolean isSneaking() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_SNEAKING);
    }

    public void setSneaking() {
        this.setSneaking(true);
    }

    public void setSneaking(boolean value) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SNEAKING, value);
    }

    public boolean isSprinting() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_SPRINTING);
    }

    public void setSprinting() {
        this.setSprinting(true);
    }

    public void setSprinting(boolean value) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SPRINTING, value);
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
        return this.effects.containsKey(effectId) ? this.effects.get(effectId) : null;
    }

    public boolean hasEffect(int effectId) {
        return this.effects.containsKey(effectId);
    }

    public void addEffect(Effect effect) {
        if (this.effects.containsKey(effect.getId())) {
            Effect oldEffect = this.effects.get(effect.getId());
            if (Math.abs(effect.getAmplifier()) <= (oldEffect.getAmplifier())
                    || (Math.abs(effect.getAmplifier())) == Math.abs(oldEffect.getAmplifier())
                    && effect.getDuration() < oldEffect.getDuration()) {
                return;
            }

            effect.add(this, true);
        } else {
            effect.add(this, false);
        }

        this.effects.put(effect.getId(), effect);

        this.recalculateEffectColor();

        if (effect.getId() == Effect.HEALTH_BOOST) {
            this.setHealth(this.getHealth() + 4 * (effect.getAmplifier() + 1));
        }
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

            this.setDataProperty(Entity.DATA_POTION_COLOR, new IntEntityData((r << 16) + (g << 8) + b));
            this.setDataProperty(Entity.DATA_POTION_AMBIENT, new ByteEntityData((byte) (ambient ? 1 : 0)));
        } else {
            this.setDataProperty(Entity.DATA_POTION_COLOR, new IntEntityData(0));
            this.setDataProperty(Entity.DATA_POTION_AMBIENT, new ByteEntityData((byte) 0));
        }
    }

    public static Entity createEntity(String name, FullChunk chunk, CompoundTag nbt, Object... args) {
        Entity entity = null;

        if (shortNames.containsKey(name)) {
            Class<? extends Entity> clazz = shortNames.get(name);

            if (clazz == null) {
                return null;
            }

            for (Constructor constructor : clazz.getConstructors()) {
                if (entity != null) {
                    break;
                }

                if (constructor.getParameterCount() != (args == null ? 2 : args.length + 2)) {
                    continue;
                }

                try {
                    if (args == null || args.length == 0) {
                        entity = (Entity) constructor.newInstance(chunk, nbt);
                    } else {
                        Object[] objects = new Object[args.length + 2];

                        objects[0] = chunk;
                        objects[1] = nbt;
                        System.arraycopy(args, 0, objects, 2, args.length);
                        entity = (Entity) constructor.newInstance(objects);

                    }
                } catch (Exception e) {
                    //ignore
                }

            }
        }

        return entity;
    }

    public static Entity createEntity(int type, FullChunk chunk, CompoundTag nbt, Object... args) {
        Entity entity = null;

        if (knownEntities.containsKey(type)) {
            Class<? extends Entity> clazz = knownEntities.get(type);

            if (clazz == null) {
                return null;
            }

            for (Constructor constructor : clazz.getConstructors()) {
                if (entity != null) {
                    break;
                }

                if (constructor.getParameterCount() != (args == null ? 2 : args.length + 2)) {
                    continue;
                }

                try {
                    if (args == null || args.length == 0) {
                        entity = (Entity) constructor.newInstance(chunk, nbt);
                    } else {
                        Object[] objects = new Object[args.length + 2];

                        objects[0] = chunk;
                        objects[1] = nbt;
                        System.arraycopy(args, 0, objects, 2, args.length);
                        entity = (Entity) constructor.newInstance(objects);

                    }
                } catch (Exception e) {
                    //ignore
                }

            }
        }

        return entity;
    }

    public static boolean registerEntity(Class<? extends Entity> clazz) {
        return registerEntity(clazz, false);
    }

    public static boolean registerEntity(Class<? extends Entity> clazz, boolean force) {
        if (clazz == null) {
            return false;
        }
        try {
            int networkId = clazz.getField("NETWORK_ID").getInt(null);
            if (networkId != -1) {
                knownEntities.put(networkId, clazz);
            } else if (!force) {
                return false;
            }

            shortNames.put(clazz.getSimpleName(), clazz);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getSaveId() {
        return this.getClass().getSimpleName();
    }

    public void saveNBT() {
        if (!(this instanceof Player)) {
            if (!this.getNameTag().equals("")) {
                this.namedTag.putString("id", this.getSaveId());
                this.namedTag.putString("CustomName", this.getNameTag());
                this.namedTag.putString("CustomNameVisible", String.valueOf(this.isNameTagVisible()));
            } else {
                this.namedTag.remove("CustomName");
                this.namedTag.remove("CustomNameVisible");
            }
        }

        this.namedTag.putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("0", this.x))
                        .add(new DoubleTag("1", this.y))
                        .add(new DoubleTag("2", this.z))
        );

        this.namedTag.putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("0", this.motionX))
                        .add(new DoubleTag("1", this.motionY))
                        .add(new DoubleTag("2", this.motionZ))
        );

        this.namedTag.putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("0", (float) this.yaw))
                        .add(new FloatTag("1", (float) this.pitch))
        );

        this.namedTag.putFloat("FallDistance", this.fallDistance);
        this.namedTag.putShort("Fire", this.fireTicks);
        this.namedTag.putShort("Air", this.getDataPropertyShort(DATA_AIR).data);
        this.namedTag.putBoolean("OnGround", this.onGround);
        this.namedTag.putBoolean("Invulnerable", this.invulnerable);

        if (!this.effects.isEmpty()) {
            ListTag<CompoundTag> list = new ListTag<>("ActiveEffects");
            for (Effect effect : this.effects.values()) {
                list.add(new CompoundTag(String.valueOf(effect.getId()))
                                .putByte("Id", (byte) effect.getId())
                                .putByte("Amplifier", (byte) effect.getAmplifier())
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

    protected void initEntity() {
        if (this.namedTag.contains("ActiveEffects")) {
            ListTag<CompoundTag> effects = this.namedTag.getList("ActiveEffects", new ListTag<>());
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
        }

        this.scheduleUpdate();
    }

    public void spawnTo(Player player) {
        if (!this.hasSpawned.containsKey(player.getLoaderId()) && player.usedChunks.containsKey(Level.chunkHash(this.chunk.getX(), this.chunk.getZ()))) {
            this.hasSpawned.put(player.getLoaderId(), player);
        }
    }

    public Map<Integer, Player> getViewers() {
        return hasSpawned;
    }

    public void sendPotionEffects(Player player) {
        for (Effect effect : this.effects.values()) {
            MobEffectPacket pk = new MobEffectPacket();
            pk.eid = 0;
            pk.effectId = effect.getId();
            pk.amplifier = effect.getAmplifier();
            pk.particles = effect.isVisible();
            pk.duration = effect.getDuration();
            pk.eventId = MobEffectPacket.EVENT_ADD;

            player.dataPacket(pk);
        }
    }

    public void sendData(Player player) {
        this.sendData(player, null);
    }

    public void sendData(Player player, Map<Integer, EntityData> data) {
        SetEntityDataPacket pk = new SetEntityDataPacket();
        pk.eid = (player.equals(this) ? 0 : this.getId());
        pk.metadata = data == null ? this.dataProperties : data;

        player.dataPacket(pk);
    }

    public void sendData(Player[] players) {
        this.sendData(players, null);
    }

    public void sendData(Player[] players, Map<Integer, EntityData> data) {
        SetEntityDataPacket pk = new SetEntityDataPacket();
        pk.eid = this.getId();
        pk.metadata = data == null ? this.dataProperties : data;

        Server.broadcastPacket(players, pk);
    }

    public void despawnFrom(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = this.getId();
            player.dataPacket(pk);
            this.hasSpawned.remove(player.getLoaderId());
        }
    }

    public void attack(EntityDamageEvent source) {
        if (hasEffect(Effect.FIRE_RESISTANCE)
                && (source.getCause() == EntityDamageEvent.CAUSE_FIRE
                || source.getCause() == EntityDamageEvent.CAUSE_FIRE_TICK
                || source.getCause() == EntityDamageEvent.CAUSE_LAVA)) {
            source.setCancelled();
        }

        getServer().getPluginManager().callEvent(source);
        if (source.isCancelled()) {
            return;
        }
        setLastDamageCause(source);
        setHealth(getHealth() - source.getFinalDamage());
    }

    public void attack(float damage) {
        this.attack(new EntityDamageEvent(this, EntityDamageEvent.CAUSE_VOID, damage));
    }

    public void heal(float amount, EntityRegainHealthEvent source) {
        this.server.getPluginManager().callEvent(source);
        if (source.isCancelled()) {
            return;
        }

        this.setHealth(this.getHealth() + source.getAmount());
    }

    public int getHealth() {
        return health;
    }

    public boolean isAlive() {
        return this.health > 0;
    }

    public void setHealth(float health) {
        int h = (int) health;
        if (this.health == h) {
            return;
        }

        if (h <= 0) {
            if (this.isAlive()) {
                this.kill();
            }
        } else if (h <= this.getMaxHealth() || h < this.health) {
            this.health = h;
        } else {
            this.health = this.getMaxHealth();
        }
    }

    public void setLastDamageCause(EntityDamageEvent type) {
        this.lastDamageCause = type;
    }

    public EntityDamageEvent getLastDamageCause() {
        return lastDamageCause;
    }

    public int getMaxHealth() {
        return maxHealth + (this.hasEffect(Effect.HEALTH_BOOST) ? 4 * (this.getEffect(Effect.HEALTH_BOOST).getAmplifier() + 1) : 0);
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public boolean canCollideWith(Entity entity) {
        return !this.justCreated && !this.equals(entity);
    }

    protected boolean checkObstruction(double x, double y, double z) {
        int i = NukkitMath.floorDouble(x);
        int j = NukkitMath.floorDouble(y);
        int k = NukkitMath.floorDouble(z);

        double diffX = x - i;
        double diffY = y - j;
        double diffZ = z - k;

        if (Block.solid[this.level.getBlockIdAt(i, j, k)]) {
            boolean flag = !Block.solid[this.level.getBlockIdAt(i - 1, j, k)];
            boolean flag1 = !Block.solid[this.level.getBlockIdAt(i + 1, j, k)];
            boolean flag2 = !Block.solid[this.level.getBlockIdAt(i, j - 1, k)];
            boolean flag3 = !Block.solid[this.level.getBlockIdAt(i, j + 1, k)];
            boolean flag4 = !Block.solid[this.level.getBlockIdAt(i, j, k - 1)];
            boolean flag5 = !Block.solid[this.level.getBlockIdAt(i, j, k + 1)];

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
                this.motionY = force;

                return true;
            }
        }

        return false;
    }

    public boolean entityBaseTick() {
        return this.entityBaseTick(1);
    }

    public boolean entityBaseTick(int tickDiff) {
        this.blocksAround = null;
        this.justCreated = false;

        if (!this.isAlive()) {
            this.removeAllEffects();
            this.despawnFromAll();
            if (!this.isPlayer) {
                this.close();
            }

            return false;
        }

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
            EntityDamageEvent ev = new EntityDamageEvent(this, EntityDamageEvent.CAUSE_VOID, 10);
            this.attack(ev);
            hasUpdate = true;
        }

        if (this.fireTicks > 0) {
            if (this.fireProof) {
                this.fireTicks -= 4 * tickDiff;
                if (this.fireTicks < 0) {
                    this.fireTicks = 0;
                }
            } else {
                if (!this.hasEffect(Effect.FIRE_RESISTANCE) && (this.fireTicks % 20) == 0 || tickDiff > 20) {
                    EntityDamageEvent ev = new EntityDamageEvent(this, EntityDamageEvent.CAUSE_FIRE_TICK, 1);
                    this.attack(ev);
                }
                this.fireTicks -= tickDiff;
            }

            if (this.fireTicks <= 0) {
                this.extinguish();
            } else {
                this.setDataFlag(DATA_FLAGS, DATA_FLAG_ONFIRE, true);
                hasUpdate = true;
            }
        }

        if (this.noDamageTicks > 0) {
            this.noDamageTicks -= tickDiff;
            if (this.noDamageTicks < 0) {
                this.noDamageTicks = 0;
            }
        }

        this.age += tickDiff;
        this.ticksLived += tickDiff;

        return hasUpdate;
    }

    protected void updateMovement() {
        double diffPosition = (this.x - this.lastX) * (this.x - this.lastX) + (this.y - this.lastY) * (this.y - this.lastY) + (this.z - this.lastZ) * (this.z - this.lastZ);
        double diffRotation = (this.yaw - this.lastYaw) * (this.yaw - this.lastYaw) + (this.pitch - this.lastPitch) * (this.pitch - this.lastPitch);

        double diffMotion = (this.motionX - this.lastMotionX) * (this.motionX - this.lastMotionX) + (this.motionY - this.lastMotionY) * (this.motionY - this.lastMotionY) + (this.motionZ - this.lastMotionZ) * (this.motionZ - this.lastMotionZ);

        if (diffPosition > 0.04 || diffRotation > 2.25 && (diffMotion > 0.0001 && this.getMotion().lengthSquared() <= 0.00001)) { //0.2 ** 2, 1.5 ** 2
            this.lastX = this.x;
            this.lastY = this.y;
            this.lastZ = this.z;

            this.lastYaw = this.yaw;
            this.lastPitch = this.pitch;

            this.level.addEntityMovement(this.chunk.getX(), this.chunk.getZ(), this.id, this.x, this.y + this.getEyeHeight(), this.z, this.yaw, this.pitch, this.yaw);
        }

        if (diffMotion > 0.0025 || (diffMotion > 0.0001 && this.getMotion().lengthSquared() <= 0.0001)) { //0.05 ** 2
            this.lastMotionX = this.motionX;
            this.lastMotionY = this.motionY;
            this.lastMotionZ = this.motionZ;

            this.level.addEntityMotion(this.chunk.getX(), this.chunk.getZ(), this.id, this.motionX, this.motionY, this.motionZ);
        }
    }

    public Vector3 getDirectionVector() {
        double y = -Math.sin(Math.toRadians(this.pitch));
        double xz = Math.sin(Math.toRadians(this.pitch));
        double x = -xz * Math.sin(Math.toRadians(this.yaw));
        double z = xz * Math.cos(Math.toRadians(this.yaw));

        return this.temporalVector.setComponents(x, y, z).normalize();
    }

    public Vector2 getDirectionPlane() {
        return (new Vector2((float) (-Math.cos(Math.toRadians(this.yaw) - Math.PI / 2)), (float) (-Math.sin(Math.toRadians(this.yaw) - Math.PI / 2)))).normalize();
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

        return hasUpdate;
    }

    public final void scheduleUpdate() {
        this.level.updateEntities.put(this.id, this);
    }

    public boolean isOnFire() {
        return this.fireTicks > 0;
    }

    public void setOnFire(int seconds) {
        int ticks = seconds * 20;
        if (ticks > this.fireTicks) {
            this.fireTicks = ticks;
        }
    }

    public Integer getDirection() {
        double rotation = (this.yaw - 90) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if ((0 <= rotation && rotation < 45) || (315 <= rotation && rotation < 360)) {
            return 2; //North
        } else if (45 <= rotation && rotation < 135) {
            return 3; //East
        } else if (135 <= rotation && rotation < 225) {
            return 0; //South
        } else if (225 <= rotation && rotation < 315) {
            return 1; //West
        } else {
            return null;
        }
    }

    public void extinguish() {
        this.fireTicks = 0;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ONFIRE, false);
    }

    public boolean canTriggerWalking() {
        return true;
    }

    public void resetFallDistance() {
        this.fallDistance = 0;
    }

    protected void updateFallState(float distanceThisTick, boolean onGround) {
        if (onGround) {
            if (this.fallDistance > 0) {
                if (this instanceof Living) {
                    this.fall(this.fallDistance);
                }
                this.resetFallDistance();
            }
        } else if (distanceThisTick < 0) {
            this.fallDistance -= distanceThisTick;
        }
    }

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public void fall(float fallDistance) {
        float damage = (float) Math.floor(fallDistance - 3 - (this.hasEffect(Effect.JUMP) ? this.getEffect(Effect.JUMP).getAmplifier() + 1 : 0));
        if (damage > 0) {
            EntityDamageEvent ev = new EntityDamageEvent(this, EntityDamageEvent.CAUSE_FALL, damage);
            this.attack(ev);
        }
    }

    public void handleLavaMovement() {
        //todo
    }

    public void moveFlying() {
        //todo
    }

    public void onCollideWithPlayer(Human entityPlayer) {

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
        Block block = this.level.getBlock(this.temporalVector.setComponents(NukkitMath.floorDouble(this.x), NukkitMath.floorDouble(y), NukkitMath.floorDouble(this.z)));

        if (block instanceof Water) {
            double f = (block.y + 1) - (((Water) block).getFluidHeightPercent() - 0.1111111);
            return y < f;
        }

        return false;
    }

    public boolean isInsideOfSolid() {
        double y = this.y + this.getEyeHeight();
        Block block = this.level.getBlock(
                this.temporalVector.setComponents(
                        NukkitMath.floorDouble(this.x),
                        NukkitMath.floorDouble(y),
                        NukkitMath.floorDouble(this.z))
        );

        AxisAlignedBB bb = block.getBoundingBox();

        return bb != null && block.isSolid() && !block.isTransparent() && bb.intersectsWith(this.getBoundingBox());

    }


    public boolean fastMove(double dx, double dy, double dz) {
        if (dx == 0 && dy == 0 && dz == 0) {
            return true;
        }

        AxisAlignedBB newBB = this.boundingBox.getOffsetBoundingBox(dx, dy, dz);

        AxisAlignedBB[] list = this.level.getCollisionCubes(this, newBB, false);

        if (list.length == 0) {
            this.boundingBox = newBB;
        }

        this.x = (this.boundingBox.minX + this.boundingBox.maxX) / 2;
        this.y = this.boundingBox.minY - this.ySize;
        this.z = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2;

        this.checkChunks();

        if (!this.onGround || dy != 0) {
            AxisAlignedBB bb = this.boundingBox.clone();
            bb.minY -= 0.75;

            this.onGround = this.level.getCollisionBlocks(bb).length > 0;
        }
        this.isCollided = this.onGround;
        this.updateFallState((float) dy, this.onGround);

        return true;
    }

    public boolean move(double dx, double dy, double dz) {

        if (dx == 0 && dz == 0 && dy == 0) {
            return true;
        }

        if (this.keepMovement) {
            this.boundingBox.offset(dx, dy, dz);
            this.setPosition(this.temporalVector.setComponents((this.boundingBox.minX + this.boundingBox.maxX) / 2, this.boundingBox.minY, (this.boundingBox.minZ + this.boundingBox.maxZ) / 2));
            this.onGround = this.isPlayer;
            return true;
        } else {

            this.ySize *= 0.4;

            double movX = dx;
            double movY = dy;
            double movZ = dz;

            AxisAlignedBB axisalignedbb = this.boundingBox.clone();

            AxisAlignedBB[] list = this.level.getCollisionCubes(this, this.level.getTickRate() > 1 ? this.boundingBox.getOffsetBoundingBox(dx, dy, dz) : this.boundingBox.addCoord(dx, dy, dz), false);

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

            this.x = (this.boundingBox.minX + this.boundingBox.maxX) / 2;
            this.y = this.boundingBox.minY - this.ySize;
            this.z = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2;

            this.checkChunks();

            this.checkGroundState(movX, movY, movZ, dx, dy, dz);
            this.updateFallState((float) dy, this.onGround);

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

            return true;
        }
    }

    protected void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        this.isCollidedVertically = movY != dy;
        this.isCollidedHorizontally = (movX != dx || movZ != dz);
        this.isCollided = (this.isCollidedHorizontally || this.isCollidedVertically);
        this.onGround = (movY != dy && movY < 0);
    }

    public List<Block> getBlocksAround() {
        if (this.blocksAround == null) {
            int minX = NukkitMath.floorDouble(this.boundingBox.minX);
            int minY = NukkitMath.floorDouble(this.boundingBox.minY);
            int minZ = NukkitMath.floorDouble(this.boundingBox.minZ);
            int maxX = NukkitMath.ceilDouble(this.boundingBox.maxX);
            int maxY = NukkitMath.ceilDouble(this.boundingBox.maxY);
            int maxZ = NukkitMath.ceilDouble(this.boundingBox.maxZ);

            this.blocksAround = new ArrayList<>();

            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = this.level.getBlock(this.temporalVector.setComponents(x, y, z));
                        if (block.hasEntityCollision()) {
                            this.blocksAround.add(block);
                        }
                    }
                }
            }
        }

        return this.blocksAround;
    }

    protected void checkBlockCollision() {
        Vector3 vector = new Vector3(0, 0, 0);

        for (Block block : this.getBlocksAround()) {
            block.onEntityCollide(this);
            block.addVelocityToEntity(this, vector);
        }

        if (vector.lengthSquared() > 0) {
            vector = vector.normalize();
            double d = 0.014d;
            this.motionX += vector.x * d;
            this.motionY += vector.y * d;
            this.motionZ += vector.z * d;
        }
    }

    public boolean setPositionAndRotation(Vector3 pos, double yaw, double pitch) {
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

    protected void checkChunks() {
        if (this.chunk == null || (this.chunk.getX() != ((int) this.x >> 4)) || this.chunk.getZ() != ((int) this.z >> 4)) {
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }
            this.chunk = this.level.getChunk((int) this.x >> 4, (int) this.z >> 4, true);

            if (!this.justCreated) {
                Map<Integer, Player> newChunk = this.level.getChunkPlayers((int) this.x >> 4, (int) this.z >> 4);
                for (Player player : new ArrayList<>(this.hasSpawned.values())) {
                    if (!newChunk.containsKey(player.getLoaderId())) {
                        this.despawnFrom(player);
                    } else {
                        newChunk.remove(player.getLoaderId());
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

    public boolean setPosition(Vector3 pos) {
        if (this.closed) {
            return false;
        }

        if (pos instanceof Position && ((Position) pos).level != null && !((Position) pos).level.equals(this.level)) {
            if (!this.switchLevel(((Position) pos).getLevel())) {
                return false;
            }
        }

        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;

        double radius = this.getWidth() / 2d;

        this.boundingBox.setBounds(pos.x - radius, pos.y, pos.z - radius, pos.x + radius, pos.y + this.getHeight(), pos.z +
                radius);

        this.checkChunks();

        return true;
    }

    public Vector3 getMotion() {
        return new Vector3(this.motionX, this.motionY, this.motionZ);
    }

    public boolean setMotion(Vector3 motion) {
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

    public void kill() {
        this.health = 0;
        this.scheduleUpdate();
    }

    public boolean teleport(Vector3 pos) {
        if (pos instanceof Location) {
            return this.teleportYawAndPitch(pos, ((Location) pos).yaw, ((Location) pos).pitch);
        } else {
            return this.teleportYawAndPitch(pos, this.yaw, this.pitch);
        }
    }

    public boolean teleport(Vector3 pos, double yaw, double pitch) {
        return this.teleportYawAndPitch(pos, yaw, pitch);
    }

    public boolean teleportYaw(Vector3 pos, double yaw) {
        if (pos instanceof Location) {
            return this.teleportYawAndPitch(pos, ((Location) pos).yaw, ((Location) pos).pitch);
        } else {
            return this.teleportYawAndPitch(pos, yaw, this.pitch);
        }
    }

    public boolean teleportPitch(Vector3 pos, double pitch) {
        if (pos instanceof Location) {
            return this.teleportYawAndPitch(pos, ((Location) pos).yaw, ((Location) pos).pitch);
        } else {
            return this.teleportYawAndPitch(pos, this.yaw, pitch);
        }
    }

    public boolean teleportYawAndPitch(Vector3 pos, double yaw, double pitch) {
        Position from = Position.fromObject(this, this.level);
        Position to = Position.fromObject(pos, pos instanceof Position ? ((Position) pos).getLevel() : this.level);
        EntityTeleportEvent ev = new EntityTeleportEvent(this, from, to);
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }

        this.ySize = 0;
        pos = ev.getTo();

        this.setMotion(this.temporalVector.setComponents(0, 0, 0));

        if (this.setPositionAndRotation(pos, yaw, pitch)) {
            this.resetFallDistance();
            this.onGround = true;

            this.lastX = this.x;
            this.lastY = this.y;
            this.lastZ = this.z;

            this.lastYaw = this.yaw;
            this.lastPitch = this.pitch;

            this.updateMovement();

            return true;
        }

        return false;
    }

    public long getId() {
        return this.id;
    }

    public void respawnToAll() {
        for (Player player : this.hasSpawned.values()) {
            this.spawnTo(player);
        }
        this.hasSpawned = new HashMap<>();
    }

    public void spawnToAll() {
        if (this.chunk == null || this.closed) {
            return;
        }

        for (Player player : this.level.getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values()) {
            if (player.isOnline()) {
                this.spawnTo(player);
            }
        }
    }

    public void despawnFromAll() {
        for (Player player : new ArrayList<>(this.hasSpawned.values())) {
            this.despawnFrom(player);
        }
    }

    public void close() {
        if (!this.closed) {
            this.server.getPluginManager().callEvent(new EntityDespawnEvent(this));
            this.closed = true;
            this.despawnFromAll();
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }

            if (this.level != null) {
                this.level.removeEntity(this);
            }
        }
    }

    public boolean setDataProperty(int id, EntityData dataEntry) {
        if (!dataEntry.getData().equals(this.getDataProperty(id).getData())) {
            this.dataProperties.put(id, dataEntry);

            this.sendData(this.hasSpawned.values().stream().toArray(Player[]::new), new HashMap<Integer, EntityData>() {
                {
                    put(id, dataProperties.get(id));
                }
            });

            return true;
        }

        return false;
    }

    public EntityData getDataProperty(int id) {
        return this.dataProperties.containsKey(id) ? this.dataProperties.get(id) : new IntEntityData();
    }

    public IntEntityData getDataPropertyInt(int id) {
        return this.dataProperties.containsKey(id) && (this.dataProperties.get(id) instanceof IntEntityData) ? (IntEntityData) this.dataProperties.get(id) : new IntEntityData();
    }

    public ShortEntityData getDataPropertyShort(int id) {
        return this.dataProperties.containsKey(id) && (this.dataProperties.get(id) instanceof ShortEntityData) ? (ShortEntityData) this.dataProperties.get(id) : new ShortEntityData();
    }

    public ByteEntityData getDataPropertyByte(int id) {
        return this.dataProperties.containsKey(id) && (this.dataProperties.get(id) instanceof ByteEntityData) ? (ByteEntityData) this.dataProperties.get(id) : new ByteEntityData();
    }

    public LongEntityData getDataPropertyLong(int id) {
        return this.dataProperties.containsKey(id) && (this.dataProperties.get(id) instanceof LongEntityData) ? (LongEntityData) this.dataProperties.get(id) : new LongEntityData();
    }

    public StringEntityData getDataPropertyString(int id) {
        return this.dataProperties.containsKey(id) && (this.dataProperties.get(id) instanceof StringEntityData) ? (StringEntityData) this.dataProperties.get(id) : new StringEntityData();
    }

    public FloatEntityData getDataPropertyFloat(int id) {
        return this.dataProperties.containsKey(id) && (this.dataProperties.get(id) instanceof FloatEntityData) ? (FloatEntityData) this.dataProperties.get(id) : new FloatEntityData();
    }

    public SlotEntityData getDataPropertySlot(int id) {
        return this.dataProperties.containsKey(id) && (this.dataProperties.get(id) instanceof SlotEntityData) ? (SlotEntityData) this.dataProperties.get(id) : new SlotEntityData();
    }

    public PositionEntityData getDataPropertyPos(int id) {
        return this.dataProperties.containsKey(id) && (this.dataProperties.get(id) instanceof PositionEntityData) ? (PositionEntityData) this.dataProperties.get(id) : new PositionEntityData();
    }

    public int getDataPropertyType(int id) {
        return (this.dataProperties.containsKey(id) ? this.dataProperties.get(id).getType() : -1);
    }

    public void setDataFlag(int propertyId, int id) {
        this.setDataFlag(propertyId, id, true);
    }

    public void setDataFlag(int propertyId, int id, boolean value) {
        if (this.getDataFlag(propertyId, id) != value) {
            int flags = this.getDataPropertyInt(propertyId).data;
            flags ^= 1 << id;
            this.setDataProperty(propertyId, new ByteEntityData((byte) flags));
        }
    }

    public boolean getDataFlag(int propertyId, int id) {
        return ((this.getDataPropertyByte(propertyId).data & 0xff) & (1 << id)) > 0;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
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

}
