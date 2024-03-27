package cn.nukkit.entity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityFlying extends BaseEntity {

    public EntityFlying(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.noFallDamage = true;
    }
}
