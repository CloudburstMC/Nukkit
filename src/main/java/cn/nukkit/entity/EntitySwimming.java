package cn.nukkit.entity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntitySwimming extends BaseEntity {

    public EntitySwimming(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
