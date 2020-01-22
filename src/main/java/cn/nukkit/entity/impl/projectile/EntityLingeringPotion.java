package cn.nukkit.entity.impl.projectile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.projectile.LingeringPotion;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityLingeringPotion extends BaseEntity implements LingeringPotion {
    public EntityLingeringPotion(EntityType<?> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }
}
