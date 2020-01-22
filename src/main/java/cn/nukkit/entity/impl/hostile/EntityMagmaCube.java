package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.hostile.MagmaCube;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntityMagmaCube extends EntityHostile implements MagmaCube {

    public EntityMagmaCube(EntityType<MagmaCube> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(16);
    }

    @Override
    public float getWidth() {
        return 2.04f;
    }

    @Override
    public float getHeight() {
        return 2.04f;
    }

    @Override
    public String getName() {
        return "Magma Cube";
    }
}
