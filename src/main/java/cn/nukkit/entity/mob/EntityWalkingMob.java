package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntityWalking;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityWalkingMob extends EntityWalking implements EntityMob {

    public EntityWalkingMob(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
