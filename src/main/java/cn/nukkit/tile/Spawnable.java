package cn.nukkit.tile;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Spawnable extends Tile {
    public Spawnable(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        //todo
    }

    public abstract CompoundTag getSpawnCompound();

    //todo
}
