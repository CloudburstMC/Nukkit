package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.Arthropod;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.hostile.CaveSpider;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntityCaveSpider extends EntityHostile implements CaveSpider, Arthropod {

    public EntityCaveSpider(EntityType<CaveSpider> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(12);
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 0.5f;
    }

    @Override
    public String getName() {
        return "CaveSpider";
    }
}
