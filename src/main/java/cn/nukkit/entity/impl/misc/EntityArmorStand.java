package cn.nukkit.entity.impl.misc;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.misc.ArmorStand;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityArmorStand extends BaseEntity implements ArmorStand {

    public EntityArmorStand(EntityType<?> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }
}
