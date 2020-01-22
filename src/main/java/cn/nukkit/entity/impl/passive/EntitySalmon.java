package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.Salmon;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created by PetteriM1
 */
public class EntitySalmon extends Animal implements Salmon {

    public EntitySalmon(EntityType<Salmon> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    public String getName() {
        return "Salmon";
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 0.4f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(3);
    }
}
