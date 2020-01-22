package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.hostile.EnderDragon;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntityEnderDragon extends EntityHostile implements EnderDragon {

    public EntityEnderDragon(EntityType<EnderDragon> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 13f;
    }

    @Override
    public float getHeight() {
        return 4f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(200);
    }

    @Override
    public String getName() {
        return "EnderDragon";
    }
}
