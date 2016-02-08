package cn.nukkit.entity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityHanging extends Entity {
    public EntityHanging(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
