package cn.nukkit.entity;

import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityCreature extends EntityLiving {
    public EntityCreature(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
