package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntityJumping;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityJumpingMob extends EntityJumping implements EntityMob {

    public EntityJumpingMob(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
