package cn.nukkit.entity.impl.misc;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.misc.AreaEffectCloud;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityAreaEffectCloud extends BaseEntity implements AreaEffectCloud {
    public EntityAreaEffectCloud(EntityType<?> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }
}
