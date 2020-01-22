package cn.nukkit.entity.impl.projectile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.projectile.EyeOfEnderSignal;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityEyeOfEnderSignal extends BaseEntity implements EyeOfEnderSignal {

    public EntityEyeOfEnderSignal(EntityType<?> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }
}
