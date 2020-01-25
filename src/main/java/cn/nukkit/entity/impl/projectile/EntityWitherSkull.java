package cn.nukkit.entity.impl.projectile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.projectile.WitherSkull;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityWitherSkull extends BaseEntity implements WitherSkull {
    public EntityWitherSkull(EntityType<?> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public boolean isCritical() {
        return false;
    }

    @Override
    public void setCritical(boolean critical) {

    }
}
