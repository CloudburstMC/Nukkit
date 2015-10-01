package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntitySpawnEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.nbt.CompoundTag;
import cn.nukkit.nbt.DoubleTag;
import cn.nukkit.nbt.FloatTag;
import cn.nukkit.nbt.ListTag;
import cn.nukkit.network.Network;
import cn.nukkit.network.protocol.MobEffectPacket;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.ChunkException;
import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public static final int DATA_FLAG_ONFIRE = 0;
    public static final int DATA_FLAG_SNEAKING = 1;
    public static final int DATA_FLAG_RIDING = 2;
    public static final int DATA_FLAG_ACTION = 4;
    public static final int DATA_FLAG_INVISIBLE = 5;

    public static int entityCount = 1;

    private static Map<Integer, Class<? extends Entity>> knownEntities = new HashMap<>();
    private static Map<Class<? extends Entity>, String> shortNames = new HashMap<>();

    private Map<Integer, Player> hasSpawned = new HashMap<>();

    protected Map<Integer, Effect> effects = new HashMap<>();

    protected int id;

    protected int dataFlags = 0;

    protected Map<Integer, Object[]> dataProerties = new HashMap<Integer, Object[]>() {{
        put(DATA_FLAGS, new Object[]{DATA_TYPE_BYTE, 0});
        put(DATA_AIR, new Object[]{DATA_TYPE_SHORT, 300});
        put(DATA_NAMETAG, new Object[]{DATA_TYPE_STRING, ""});
        put(DATA_SHOW_NAMETAG, new Object[]{DATA_TYPE_BYTE, 1});
        put(DATA_SILENT, new Object[]{DATA_TYPE_BYTE, 0});
        put(DATA_NO_AI, new Object[]{DATA_TYPE_BYTE, 0});
    }};

    public Entity passenger = null;

    public Entity vehicle = null;

    public FullChunk chunk;

    protected EntityDamageEvent lastDamageCause = null;

    private List<Block> blocksAround = new ArrayList<>();

    public Double lastX = null;
    public Double lastY = null;
    public Double lastZ = null;

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

    public float height;
    public Float eyeHeight = null;

    public float width;
    public float length;

    private int health = 20;
    private int maxHealth = 20;

    protected float ySize = 0;
    protected float stepHeight = 20;
    public boolean keepMovement = false;

    public float fallDistance = 0;
    public int ticksLived = 0;
    public int lastUpdate;
    public int maxFireTicks;
    public short fireTicks = 0;

    public CompoundTag namedTag;
    public boolean canCollide = true;

    protected boolean isStatic = false;

    public boolean isCollided = false;
    public boolean isCollidedHorizontally = false;
    public boolean isCollidedVertically = false;

    public int noDamageTicks;
    public boolean justCreated;
    public boolean fireProof;
    public boolean invulnerable;

    protected float gravity;
    protected float drag;

    protected Server server;

    public boolean closed = false;

    protected boolean isPlayer = false;

    public Entity(FullChunk chunk, CompoundTag nbt) {
        if (chunk == null || chunk.getProvider() == null) {
            throw new ChunkException("Invalid garbage Chunk given to Entity");
        }

        this.isPlayer = this instanceof Player;

        this.temporalVector = new Vector3();

        if (this.eyeHeight == null) {
            this.eyeHeight = (float) (this.height / 2 + 0.1);
        }

        this.id = Entity.entityCount++;
        this.justCreated = true;
        this.namedTag = nbt;

        this.chunk = chunk;
        this.setLevel(chunk.getProvider().getLevel());
        this.server = chunk.getProvider().getLevel().getServer();

        this.boundingBox = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

        ListTag<DoubleTag> posList = (ListTag<DoubleTag>) this.namedTag.getList("Pos");
        ListTag<FloatTag> rotationList = (ListTag<FloatTag>) this.namedTag.getList("Rotation");
        ListTag<DoubleTag> motionList = (ListTag<DoubleTag>) this.namedTag.getList("Motion");
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
            this.namedTag.putShort("Fire", (short) 0);
        }
        this.fireTicks = this.namedTag.getShort("Fire");

        if (!this.namedTag.contains("Air")) {
            this.namedTag.putShort("Air", (short) 300);
        }
        this.setDataProperty(DATA_AIR, DATA_TYPE_SHORT, this.namedTag.getShort("Air"));

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
        return String.valueOf(this.getDataProperty(DATA_NAMETAG));
    }

    public boolean isNameTagVisible() {
        return (int) this.getDataProperty(DATA_SHOW_NAMETAG) > 0;
    }

    public void setNameTag(String name) {
        this.setDataProperty(DATA_NAMETAG, DATA_TYPE_STRING, name);
    }

    public void setNameTagVisible() {
        this.setNameTagVisible(true);
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

            this.setDataProperty(Entity.DATA_POTION_COLOR, Entity.DATA_TYPE_INT, (r << 16) + (g << 8) + b);
            this.setDataProperty(Entity.DATA_POTION_AMBIENT, Entity.DATA_TYPE_BYTE, ambient ? 1 : 0);
        } else {
            this.setDataProperty(Entity.DATA_POTION_COLOR, Entity.DATA_TYPE_INT, 0);
            this.setDataProperty(Entity.DATA_POTION_AMBIENT, Entity.DATA_TYPE_BYTE, 0);
        }
    }

    public static Entity createEntity(String name, FullChunk chunk, CompoundTag nbt, Object... args) {
        if (shortNames.containsValue(name)) {
            Class<? extends Entity> clazz = null;
            for (Map.Entry<Class<? extends Entity>, String> entry : shortNames.entrySet()) {
                if (name.equals(entry.getValue())) {
                    clazz = entry.getKey();
                }
            }
            if (clazz == null) {
                return null;
            }
            try {
                return clazz.getConstructor(FullChunk.class, CompoundTag.class, Object[].class).newInstance(chunk, nbt, args);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static Entity createEntity(int type, FullChunk chunk, CompoundTag nbt, Object... args) {
        if (knownEntities.containsKey(type)) {
            Class<? extends Entity> clazz = knownEntities.get(type);
            try {
                return clazz.getConstructor(FullChunk.class, CompoundTag.class, Object[].class).newInstance(chunk, nbt, args);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
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

            shortNames.put(clazz, clazz.getSimpleName());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getSaveId() {
        return shortNames.get(this.getClass());
    }

    public void saveNBT() {
        if (!(this instanceof Player)) {
            this.namedTag.putString("id", this.getSaveId());
            this.namedTag.putString("CustomName", this.getNameTag());
            this.namedTag.putString("CustomeNameVisible", String.valueOf(this.isNameTagVisible()));
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
                        .add(new FloatTag("0", this.yaw))
                        .add(new FloatTag("1", this.pitch))
        );

        this.namedTag.putFloat("FallDistance", this.fallDistance);
        this.namedTag.putShort("Fire", this.fireTicks);
        this.namedTag.putShort("Air", (Short) this.getDataProperty(DATA_AIR));
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
            ListTag<CompoundTag> effects = (ListTag<CompoundTag>) this.namedTag.getList("ActiveEffects");
            for (CompoundTag e : effects.list) {
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
            this.setNameTagVisible(this.namedTag.getBoolean("CustomNameVisible"));
        }

        this.scheduleUpdate();
    }

    public void spawnTo(Player player) {
        if (!this.hasSpawned.containsKey(player.getLoaderId()) && player.usedChunks.containsKey(Level.chunkHash(this.chunk.getX(), this.chunk.getZ()))) {
            this.hasSpawned.put(player.getLoaderId(), player);
        }
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

            player.dataPacket(pk.setChannel(Network.CHANNEL_WORLD_EVENTS));
        }
    }

    public void sendData(Player player) {
        this.sendData(player, null);
    }

    public void sendData(Player player, Map<Integer, Object[]> data) {

    }

    public void sendData(Player[] players) {
        this.sendData(players, null);
    }

    public void sendData(Player[] players, Map<Integer, Object[]> data) {

    }

    public void setNameTagVisible(boolean visible) {
        this.setDataProperty(DATA_SHOW_NAMETAG, DATA_TYPE_BYTE, visible ? (byte) 1 : (byte) 0);
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

    public int getId() {
        return this.id;
    }

    public void spawnToAll() {
        //todo
    }

    public void close() {
        //todo
    }

    public boolean setDataProperty(int id, int type, @NotNull Object value) {
        if (!value.equals(this.getDataProperty(id))) {
            this.dataProerties.put(id, new Object[]{type, value});

            this.sendData(this.hasSpawned, new ArrayList<Object>() {
                {
                    add(id, dataProerties.get(id));
                }
            });

            return true;
        }

        return false;
    }

    public Object getDataProperty(int id) {
        return this.dataProerties.containsKey(id) ? this.dataProerties.get(id)[1] : null;
    }

    public int getDataPropertyType(int id) {
        return (int) (this.dataProerties.containsKey(id) ? this.dataProerties.get(id)[0] : null);
    }

    public void setDataFlag(int propertyId, int id) {
        this.setDataFlag(propertyId, id, true);
    }

    public void setDataFlag(int propertyId, int id, boolean value) {
        this.setDataFlag(propertyId, id, value, DATA_TYPE_BYTE);
    }

    public void setDataFlag(int propertyId, int id, boolean value, int type) {
        if (this.getDataFlag(propertyId, id) != value) {
            int flags = (int) this.getDataProperty(propertyId);
            flags ^= 1 << id;
            this.setDataProperty(propertyId, type, flags);
        }
    }

    public boolean getDataFlag(int propertyId, int id) {
        return (((int) this.getDataProperty(propertyId)) & (1 << id)) > 0;
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

}
