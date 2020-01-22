package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.TropicalFish;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created by PetteriM1
 */
public class EntityTropicalFish extends Animal implements TropicalFish {

    public EntityTropicalFish(EntityType<TropicalFish> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    public String getName() {
        return "Tropical Fish";
    }

    @Override
    public float getWidth() {
        return 0.5f;
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
