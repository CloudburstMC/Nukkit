package cn.nukkit.server.entity.passive;

import cn.nukkit.server.entity.EntityLiving;
import cn.nukkit.server.level.format.FullChunk;
import cn.nukkit.server.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityWaterAnimal extends EntityLiving implements EntityAgeable {
    public EntityWaterAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBaby() {
        return this.getDataFlag(DATA_FLAGS, Entity.DATA_FLAG_BABY);
    }
}
