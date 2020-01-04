package cn.nukkit.entity;

import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

@FunctionalInterface
public interface EntityFactory<T extends Entity> {

    T create(EntityType<T> type, Chunk chunk, CompoundTag tag);
}
