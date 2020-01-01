package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

import static cn.nukkit.entity.data.EntityFlag.BABY;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityWaterAnimal extends EntityCreature implements EntityAgeable {
    public EntityWaterAnimal(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBaby() {
        return this.getFlag(BABY);
    }
}
