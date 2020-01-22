package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.hostile.Guardian;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntityGuardian extends EntityHostile implements Guardian {

    public EntityGuardian(EntityType<Guardian> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(30);
    }

    @Override
    public String getName() {
        return "Guardian";
    }

    @Override
    public float getWidth() {
        return 0.85f;
    }

    @Override
    public float getHeight() {
        return 0.85f;
    }
}
