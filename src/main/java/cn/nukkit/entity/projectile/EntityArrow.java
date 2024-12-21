package cn.nukkit.entity.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class EntityArrow extends EntityProjectile {

    public static final int NETWORK_ID = 80;

    protected int pickupMode;
    protected boolean critical;
    private int arrowData;
    private boolean isFromCrossbow;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.05f;
    }

    @Override
    public float getLength() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 0.05f;
    }

    @Override
    public float getGravity() {
        return 0.05f;
    }

    @Override
    public float getDrag() {
        return 0.01f;
    }

    public EntityArrow(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityArrow(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        this(chunk, nbt, shootingEntity, false);
    }

    public EntityArrow(FullChunk chunk, CompoundTag nbt, Entity shootingEntity, boolean critical) {
        this(chunk, nbt, shootingEntity, critical, false);
    }

    public EntityArrow(FullChunk chunk, CompoundTag nbt, Entity shootingEntity, boolean critical, boolean isFromCrossbow) {
        super(chunk, nbt, shootingEntity);
        this.setCritical(critical);
        this.isFromCrossbow = isFromCrossbow;
    }

    /**
     * Set arrow data.
     * Used internally for tipped arrows.
     * Notice: The data is not updated to players unless you call sendData().
     *
     * @param data arrow data
     */
    public void setData(int data) {
        if (data < 0) throw new IllegalArgumentException("data < 0");
        this.arrowData = data;
        this.setDataProperty(new ByteEntityData(DATA_HAS_DISPLAY, this.arrowData), false);
    }

    /**
     * Get arrow data.
     *
     * @return arrow data
     */
    public int getData() {
        return this.arrowData;
    }

    /**
     * Get whether the arrow was shot from a crossbow.
     *
     * @return arrow is from crossbow
     */
    public boolean isFromCrossbow() {
        return this.isFromCrossbow;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.pickupMode = namedTag.contains("pickup") ? namedTag.getByte("pickup") : PICKUP_ANY;

        int data = namedTag.getByte("arrowData");
        if (data != 0) {
            this.setData(data);
        }

        this.isFromCrossbow = namedTag.getBoolean("isFromCrossbow");
    }

    public void setCritical() {
        this.setCritical(true);
    }

    public void setCritical(boolean value) {
        if (this.critical != value) {
            this.critical = value;
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_CRITICAL, value);
        }
    }

    public boolean isCritical() {
        return this.critical;
    }

    @Override
    public int getResultDamage() {
        int base = NukkitMath.ceilDouble(Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ) * getDamage());

        if (this.isCritical()) {
            base += ThreadLocalRandom.current().nextInt((base >> 1) + 2);
        }

        if (this.isFromCrossbow) {
            base += 2; // magic value
        }

        return base;
    }

    @Override
    protected double getBaseDamage() {
        return 2;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (this.age > 1200) {
            this.close();
            return false;
        }

        if (this.fireTicks > 0 && this.level.isRaining() && this.canSeeSky()) {
            this.extinguish();
        }

        return super.onUpdate(currentTick);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putByte("pickup", this.pickupMode);
        this.namedTag.putByte("arrowData", this.arrowData);
        this.namedTag.putBoolean("isFromCrossbow", this.isFromCrossbow);
    }

    public int getPickupMode() {
        return this.pickupMode;
    }

    public void setPickupMode(int pickupMode) {
        this.pickupMode = pickupMode;
    }

    @Override
    public void onHit() {
        this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BOW_HIT);
    }

    @Override
    public void onHitGround(Vector3 moveVector) {
        super.onHitGround(moveVector);

        this.setCritical(false);
    }
}
