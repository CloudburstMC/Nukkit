package cn.nukkit.entity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Living extends Entity {
    public Living(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
    //todo alot
}
