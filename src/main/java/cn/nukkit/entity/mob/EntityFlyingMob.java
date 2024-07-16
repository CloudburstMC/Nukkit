package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntityFlying;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityFlyingMob extends EntityFlying implements EntityMob {

    public EntityFlyingMob(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
