package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.ai.EntityFlying;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;

public abstract class EntityFlyingAnimal extends EntityFlying implements EntityAnimal {

    public EntityFlyingAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.getDataProperty(DATA_AGEABLE_FLAGS) == null) {
            this.setDataProperty(new ByteEntityData(DATA_AGEABLE_FLAGS, (byte) 0));
        }
    }

    @Override
    public boolean isBaby() {
        return this.getDataFlag(DATA_AGEABLE_FLAGS, DATA_FLAG_BABY);
    }

    @Override
    protected boolean entityBaseTick(int tickDiff) {
        //Timings.timerEntityBaseTick.startTiming();

        boolean hasUpdate = super.entityBaseTick(tickDiff);

        if (!this.hasEffect(Effect.WATER_BREATHING) && this.isInsideOfWater()) {
            hasUpdate = true;
            int airTicks = this.getDataPropertyShort(DATA_AIR) - tickDiff;
            if (airTicks <= -20) {
                airTicks = 0;
                this.attack(new EntityDamageEvent(this, EntityDamageEvent.CAUSE_DROWNING, 2));
            }
            this.setDataProperty(new ShortEntityData(DATA_AIR, airTicks));
        } else {
            this.setDataProperty(new ShortEntityData(DATA_AIR, 300));
        }

        //Timings.timerEntityBaseTick.stopTiming();
        return hasUpdate;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (!this.isAlive()) {
            if (++this.deadTicks >= 23) {
                this.close();
                return false;
            }
            return true;
        }

        int tickDiff = currentTick - this.lastUpdate;
        this.lastUpdate = currentTick;
        this.entityBaseTick(tickDiff);

        Vector3 target = this.updateMove(tickDiff);
        if (target instanceof Player) {
            if (this.distanceSquared(target) <= 2) {
                this.pitch = 22;
                this.x = this.lastX;
                this.y = this.lastY;
                this.z = this.lastZ;
            }
        } else if (target != null && this.distanceSquared(target) <= 1) {
            this.moveTime = 0;
        }
        return true;
    }

}