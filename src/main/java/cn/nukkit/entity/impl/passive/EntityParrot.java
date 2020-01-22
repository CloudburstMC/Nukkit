package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.Parrot;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntityParrot extends Animal implements Parrot {

    public EntityParrot(EntityType<Parrot> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    public String getName() {
        return "Parrot";
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 0.9f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(6);
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(ItemIds.FEATHER)};
    }
}
