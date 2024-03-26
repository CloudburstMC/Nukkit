package cn.nukkit.entity.passive;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityTameableAnimal extends EntityWalkingAnimal /*implements EntityTameable*/ {

    public EntityTameableAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
