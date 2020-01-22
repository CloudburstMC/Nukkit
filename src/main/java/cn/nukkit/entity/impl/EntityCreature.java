package cn.nukkit.entity.impl;

import cn.nukkit.entity.EntityType;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityCreature extends EntityLiving {

    public EntityCreature(EntityType<?> type, Chunk chunk, CompoundTag tag) {
        super(type, chunk, tag);
    }
}
