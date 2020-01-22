package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.passive.SnowGolem;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntitySnowGolem extends BaseEntity implements SnowGolem {

    public EntitySnowGolem(EntityType<?> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }
}
