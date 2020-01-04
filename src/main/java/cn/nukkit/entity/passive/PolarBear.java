package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.item.Item;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

import static cn.nukkit.item.ItemIds.FISH;
import static cn.nukkit.item.ItemIds.SALMON;

/**
 * @author PikyCZ
 */
public class PolarBear extends Animal<PolarBear> {

    public PolarBear(EntityType<PolarBear> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.65f;
        }
        return 1.3f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.7f;
        }
        return 1.4f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(30);
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(FISH), Item.get(SALMON)};
    }
}
