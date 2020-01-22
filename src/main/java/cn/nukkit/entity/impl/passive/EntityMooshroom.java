package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.Mooshroom;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Author: BeYkeRYkt Nukkit Project
 */
public class EntityMooshroom extends Animal implements Mooshroom {

    public EntityMooshroom(EntityType<Mooshroom> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public float getWidth() {
        if (isBaby()) {
            return 0.45f;
        }
        return 0.9f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.7f;
        }
        return 1.4f;
    }

    @Override
    public String getName() {
        return "Mooshroom";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(ItemIds.LEATHER), Item.get(ItemIds.BEEF)};
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        setMaxHealth(10);
    }
}
