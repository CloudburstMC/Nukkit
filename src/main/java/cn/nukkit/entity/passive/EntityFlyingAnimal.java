package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityFlying;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityFlyingAnimal extends EntityFlying implements EntityAnimal {

    public EntityFlyingAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
