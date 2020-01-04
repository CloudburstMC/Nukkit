package cn.nukkit.entity.passive;

import cn.nukkit.entity.Creature;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.entity.EntityType;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

import static cn.nukkit.entity.data.EntityFlag.BABY;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class WaterAnimal<T extends Entity> extends Creature<T> implements EntityAgeable {
    public WaterAnimal(EntityType<T> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public boolean isBaby() {
        return this.getFlag(BABY);
    }
}
