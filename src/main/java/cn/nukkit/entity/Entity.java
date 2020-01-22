package cn.nukkit.entity;

import cn.nukkit.Server;
import cn.nukkit.entity.data.EntityData;
import cn.nukkit.entity.data.EntityDataMap;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.misc.LightningBolt;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.*;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.player.Player;
import cn.nukkit.potion.Effect;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Entity extends Metadatable {

    EntityType<?> getType();

    Level getLevel();

    Server getServer();

    long getUniqueId();

    long getRuntimeId();

    float getHeight();

    float getEyeHeight();

    float getWidth();

    float getLength();

    boolean canCollide();

    void onEntityCollision(Entity entity);

    float getGravity();

    float getDrag();

    EntityDataMap getData();

    boolean getFlag(EntityFlag flag);

    void setFlag(EntityFlag flag, boolean value);

    boolean hasCustomName();

    String getNameTag();

    void setNameTag(String name);

    boolean isNameTagVisible();

    float getScale();

    void setScale(float scale);

    List<Entity> getPassengers();

    boolean isPassenger(Entity entity);

    boolean isControlling(Entity entity);

    boolean hasControllingPassenger();

    Vector3f getSeatPosition();

    void setSeatPosition(Vector3f pos);

    Entity getVehicle();

    default boolean mount(Entity entity) {
        return this.mount(entity, MountMode.RIDER);
    }

    /**
     * Enter into a vehicle
     *
     * @param vehicle vehicle to mount
     * @param mode    mode
     * @return whether or not the mount was successful
     */
    boolean mount(Entity vehicle, MountMode mode);

    boolean dismount(Entity vehicle);

    void onMount(Entity passenger);

    void onDismount(Entity passenger);

    Map<Integer, Effect> getEffects();

    void removeAllEffects();

    Effect getEffect(int effectId);

    void removeEffect(int effectId);

    boolean hasEffect(int effectId);

    void addEffect(Effect effect);

    String getName();

    void spawnTo(Player player);

    void spawnToAll();

    void despawnFrom(Player player);

    void despawnFromAll();

    Set<Player> getViewers();

    default boolean attack(float damage) {
        return this.attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.CUSTOM, damage));
    }

    boolean attack(EntityDamageEvent source);

    default void heal(float amount) {
        this.heal(new EntityRegainHealthEvent(this, amount, EntityRegainHealthEvent.CAUSE_REGEN));
    }

    void heal(EntityRegainHealthEvent source);

    float getHealth();

    void setHealth(float health);

    int getMaxHealth();

    void setMaxHealth(int maxHealth);

    default boolean isAlive() {
        return getHealth() > 0;
    }

    EntityDamageEvent getLastDamageCause();

    boolean canCollideWith(Entity entity);

    BlockFace getDirection();

    Vector3f getDirectionVector();

    Vector2f getDirectionPlane();

    BlockFace getHorizontalFacing();

    boolean onUpdate(int currentTick);

    float getAbsorption();

    void setAbsorption(float absorption);

    default boolean isOnFire() {
        return getFireTicks() > 0;
    }

    void setOnFire(@Nonnegative int seconds);

    @Nonnegative
    int getFireTicks();

    void extinguish();

    int getNoDamageTicks();

    void setNoDamageTicks(int noDamageTicks);

    double getHighestPosition();

    void setHighestPosition(double highestPosition);

    void resetFallDistance();

    AxisAlignedBB getBoundingBox();

    void fall(float fallDistance);

    void onStruckByLightning(LightningBolt lightningBolt);

    boolean onInteract(Player player, Item item, Vector3f clickedPos);

    double getX();

    double getY();

    double getZ();

    Vector3f getPosition();

    boolean setPosition(Vector3f pos);

    Location getLocation();

    Vector3f getMotion();

    boolean setMotion(Vector3f motion);

    void setRotation(double yaw, double pitch);

    boolean setPositionAndRotation(Vector3f pos, double yaw, double pitch);

    double getPitch();

    double getYaw();

    boolean canBeMovedByCurrents();

    /**
     * Whether the entity can active pressure plates.
     * Used for {@link cn.nukkit.entity.passive.Bat}s only.
     *
     * @return triggers pressure plate
     */
    boolean canTriggerPressurePlate();

    boolean canPassThrough();

    boolean isOnGround();

    void setOnGround(boolean onGround);

    void kill();

    default boolean teleport(Vector3f pos) {
        return this.teleport(pos, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    boolean teleport(Vector3f pos, PlayerTeleportEvent.TeleportCause cause);

    default boolean teleport(Position pos) {
        return this.teleport(pos, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    boolean teleport(Position pos, PlayerTeleportEvent.TeleportCause cause);

    default boolean teleport(Location location) {
        return this.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause);

    @Nullable
    Entity getOwner();

    void setOwner(@Nullable Entity entity);

    boolean hasData(EntityData data);

    int getIntData(EntityData data);

    void setIntData(EntityData data, int value);

    short getShortData(EntityData data);

    void setShortData(EntityData data, int value);

    byte getByteData(EntityData data);

    void setByteData(EntityData data, int value);

    boolean getBooleanData(EntityData data);

    void setBooleanData(EntityData data, boolean value);

    long getLongData(EntityData data);

    void setLongData(EntityData data, long value);

    String getStringData(EntityData data);

    void setStringData(EntityData data, String value);

    float getFloatData(EntityData data);

    void setFloatData(EntityData data, float value);

    CompoundTag getTagData(EntityData data);

    void setTagData(EntityData data, CompoundTag value);

    Vector3i getPosData(EntityData data);

    void setPosData(EntityData data, Vector3i value);

    Vector3f getVector3fData(EntityData data);

    void setVector3fData(EntityData data, Vector3f value);

    void saveNBT();

    CompoundTag getTag();

    boolean isClosed();

    void close();
}
