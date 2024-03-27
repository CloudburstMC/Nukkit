package cn.nukkit.entity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityJumping extends BaseEntity {

    public EntityJumping(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
