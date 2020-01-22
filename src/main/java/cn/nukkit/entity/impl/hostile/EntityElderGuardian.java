package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.hostile.ElderGuardian;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

import static cn.nukkit.entity.data.EntityFlag.ELDER;

/**
 * @author PikyCZ
 */
public class EntityElderGuardian extends EntityHostile implements ElderGuardian {

    public EntityElderGuardian(EntityType<ElderGuardian> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(80);
        this.setFlag(ELDER, true);
    }

    @Override
    public float getWidth() {
        return 1.9975f;
    }

    @Override
    public float getHeight() {
        return 1.9975f;
    }

    @Override
    public String getName() {
        return "Elder Guardian";
    }
}
