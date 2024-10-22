package cn.nukkit.entity.mob;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityTameableMob extends EntityWalkingMob /*implements EntityTameable*/ {

    public EntityTameableMob(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
