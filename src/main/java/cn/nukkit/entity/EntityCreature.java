package cn.nukkit.entity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityCreature extends EntityLiving {
    public EntityCreature(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
