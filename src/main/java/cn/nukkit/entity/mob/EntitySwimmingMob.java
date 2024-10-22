package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntitySwimming;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntitySwimmingMob extends EntitySwimming implements EntityMob {

    public EntitySwimmingMob(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
