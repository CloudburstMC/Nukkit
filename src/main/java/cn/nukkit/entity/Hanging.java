package cn.nukkit.entity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Hanging extends Entity implements Attachable {
    public Hanging(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
