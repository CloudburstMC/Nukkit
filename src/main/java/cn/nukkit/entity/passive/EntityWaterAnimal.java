package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntitySwimming;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityWaterAnimal extends EntitySwimming implements EntityAnimal {

    public EntityWaterAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
