package cn.nukkit.entity.hostile;

import cn.nukkit.entity.Arthropod;
import cn.nukkit.entity.EntityType;
import cn.nukkit.item.Item;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

import static cn.nukkit.item.ItemIds.SPIDER_EYE;
import static cn.nukkit.item.ItemIds.STRING;

/**
 * @author PikyCZ
 */
public class Spider extends Mob implements Arthropod {

    public Spider(EntityType<Spider> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(16);
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 0.9f;
    }

    @Override
    public String getName() {
        return "Spider";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(STRING), Item.get(SPIDER_EYE)};
    }
}
