package cn.nukkit.entity.passive;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityHorseBase extends EntityWalkingAnimal /*implements EntityRideable, EntityControllable*/ {

    public EntityHorseBase(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getKillExperience() {
        return 0;
    }
}
