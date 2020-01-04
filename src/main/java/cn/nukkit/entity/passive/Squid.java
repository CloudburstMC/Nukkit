package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;

/**
 * @author PikyCZ
 */
public class Squid extends WaterAnimal<Squid> {

    public static final int NETWORK_ID = 17;

    public Squid(EntityType<Squid> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.8f;
    }

    @Override
    public float getHeight() {
        return 0.8f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(10);
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(ItemIds.DYE, DyeColor.BLACK.getDyeData())};
    }
}
