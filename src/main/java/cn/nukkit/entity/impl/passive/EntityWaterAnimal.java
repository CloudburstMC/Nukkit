package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.EntityCreature;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

import static cn.nukkit.entity.data.EntityFlag.BABY;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityWaterAnimal extends EntityCreature implements EntityAgeable {
    public EntityWaterAnimal(EntityType<?> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public boolean isBaby() {
        return this.getFlag(BABY);
    }
}
