package cn.nukkit.entity;

import cn.nukkit.Server;
import cn.nukkit.entity.data.SyncedEntityData;
import cn.nukkit.entity.misc.LightningBolt;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.player.Player;
import cn.nukkit.potion.Effect;
import com.nukkitx.math.vector.Vector2f;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import java.util.List;
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

    void loadAdditionalData(CompoundTag tag);

    void saveAdditionalData(CompoundTagBuilder tag);

    boolean canCollide();

    void onEntityCollision(Entity entity);

    float getGravity();

    float getDrag();

    boolean hasCustomName();

    String getCustomName();

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

    Short2ObjectMap<Effect> getEffects();

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

    float getHighestPosition();

    void setHighestPosition(float highestPosition);

    void resetFallDistance();

    AxisAlignedBB getBoundingBox();

    void fall(float fallDistance);

    void onStruckByLightning(LightningBolt lightningBolt);

    boolean onInteract(Player player, Item item, Vector3f clickedPos);

    float getX();

    float getY();

    float getZ();

    Vector3f getPosition();

    boolean setPosition(Vector3f pos);

    Location getLocation();

    Vector3f getMotion();

    boolean setMotion(Vector3f motion);

    void setRotation(float yaw, float pitch);

    boolean setPositionAndRotation(Vector3f pos, float yaw, float pitch);

    float getPitch();

    float getYaw();

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

    default boolean isUndead() {
        return false;
    }

    void kill();

    default boolean teleport(Vector3f pos) {
        return this.teleport(pos, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    boolean teleport(Vector3f pos, PlayerTeleportEvent.TeleportCause cause);

    default boolean teleport(Location location) {
        return this.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause);

    @Nullable
    Entity getOwner();

    void setOwner(@Nullable Entity entity);

    SyncedEntityData getData();

    CompoundTag getTag();

    boolean isClosed();

    void close();
}
