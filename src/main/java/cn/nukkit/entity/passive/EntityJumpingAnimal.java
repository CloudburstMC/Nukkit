package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityJumping;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityJumpingAnimal extends EntityJumping implements EntityAnimal {

    public EntityJumpingAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
