package cn.nukkit.entity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Animal extends Creature implements Ageable {
    public Animal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        if (this.getDataProperty(DATA_AGEABLE_FLAGS) == null) {
            this.setDataProperty(DATA_AGEABLE_FLAGS, DATA_TYPE_BYTE, 0);
        }
    }

    @Override
    public boolean isBaby() {
        return this.getDataFlag(DATA_AGEABLE_FLAGS, DATA_FLAG_BABY);
    }
}
