package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityWalking;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityWalkingAnimal extends EntityWalking implements EntityAnimal {

    public EntityWalkingAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
