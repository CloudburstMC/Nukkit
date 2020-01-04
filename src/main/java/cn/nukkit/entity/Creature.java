package cn.nukkit.entity;

import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Creature<T extends Entity> extends LivingEntity {

    public Creature(EntityType<T> type, Chunk chunk, CompoundTag tag) {
        super(type, chunk, tag);
    }
}
