package cn.nukkit.entity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Vehicle extends Entity implements Rideable {
    public Vehicle(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
