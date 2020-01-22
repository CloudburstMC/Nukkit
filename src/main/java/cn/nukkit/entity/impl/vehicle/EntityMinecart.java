package cn.nukkit.entity.impl.vehicle;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.EntityLiving;
import cn.nukkit.entity.impl.passive.EntityWaterAnimal;
import cn.nukkit.entity.vehicle.Minecart;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.player.Player;
import cn.nukkit.utils.MinecartType;

/**
 * Created by Snake1999 on 2016/1/30.
 * Package cn.nukkit.entity.item in project Nukkit.
 */
public class EntityMinecart extends EntityAbstractMinecart implements Minecart {

    public EntityMinecart(EntityType<Minecart> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public MinecartType getMinecartType() {
        return MinecartType.valueOf(0);
    }

    @Override
    public boolean isRideable() {
        return true;
    }

    @Override
    protected void activate(int x, int y, int z, boolean flag) {
        if (flag) {
            if (this.vehicle != null) {
                mount(vehicle);
            }
            // looks like MCPE and MCPC not same XD
            // removed rolling feature from here because of MCPE logic?
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        boolean update = super.onUpdate(currentTick);

        if (this.passengers.isEmpty()) {
            for (Entity entity : this.level.getCollidingEntities(this.boundingBox.grow(0.20000000298023224, 0.0D, 0.20000000298023224), this)) {
                if (entity.getVehicle() != null || !(entity instanceof EntityLiving) || entity instanceof Player || entity instanceof EntityWaterAnimal) {
                    continue;
                }

                entity.mount(this);
                update = true;
                break;
            }
        }

        return update;
    }
}
