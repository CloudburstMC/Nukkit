package cn.nukkit.entity.item;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.entity.data.FloatEntityData;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityVehicle extends Entity implements EntityRideable {
    public static final int DATA_VEHICLE_HURT_TIME = 17;
    public static final int DATA_VEHICLE_HURT_DIRECTION = 18;
    public static final int DATA_VEHICLE_DAMAGE = 19;

    public EntityVehicle(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public int getHurtTime() {
        return this.getDataPropertyInt(EntityVehicle.DATA_VEHICLE_HURT_TIME);
    }

    public void setHurtTime(int time) {
        this.setDataProperty(new IntEntityData(EntityVehicle.DATA_VEHICLE_HURT_TIME, time));
    }

    public int getHurtDirection() {
        return this.getDataPropertyInt(EntityVehicle.DATA_VEHICLE_HURT_DIRECTION);
    }

    public void setHurtDirection(int direction) {
        this.setDataProperty(new IntEntityData(EntityVehicle.DATA_VEHICLE_HURT_DIRECTION, direction));
    }

    public float getDamage() {
        return this.getDataPropertyInt(EntityVehicle.DATA_VEHICLE_DAMAGE);
    }

    public void setDamage(float damage) {
        this.setDataProperty(new FloatEntityData(EntityVehicle.DATA_VEHICLE_HURT_DIRECTION, damage));
    }
}
