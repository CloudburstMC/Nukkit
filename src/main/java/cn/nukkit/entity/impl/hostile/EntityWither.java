package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.Smiteable;
import cn.nukkit.entity.hostile.Wither;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntityWither extends EntityHostile implements Wither, Smiteable {

    public EntityWither(EntityType<Wither> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 3.5f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(300);
    }

    @Override
    public String getName() {
        return "Wither";
    }

    @Override
    public boolean isUndead() {
        return true;
    }

}
