package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.Wolf;
import cn.nukkit.item.Item;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Author: BeYkeRYkt Nukkit Project
 */
public class EntityWolf extends Animal implements Wolf {

    public EntityWolf(EntityType<Wolf> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 0.85f;
    }

    @Override
    public String getName() {
        return "Wolf";
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(8);
    }

    @Override
    public boolean isBreedingItem(Item item) {
        return false; //only certain food
    }
}
