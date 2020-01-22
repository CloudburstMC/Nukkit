package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.Pig;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Identifier;

/**
 * Author: BeYkeRYkt Nukkit Project
 */
public class EntityPig extends Animal implements Pig {

    public static final int NETWORK_ID = 12;

    public EntityPig(EntityType<Pig> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.45f;
        }
        return 0.9f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.45f;
        }
        return 0.9f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(10);
    }

    @Override
    public String getName() {
        return "Pig";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(ItemIds.PORKCHOP)};
    }

    @Override
    public boolean isBreedingItem(Item item) {
        Identifier id = item.getId();

        return id == ItemIds.CARROT || id == ItemIds.POTATO || id == ItemIds.BEETROOT;
    }
}
