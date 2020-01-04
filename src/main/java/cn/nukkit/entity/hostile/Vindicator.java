package cn.nukkit.entity.hostile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.item.Item;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

import static cn.nukkit.item.ItemIds.IRON_AXE;

/**
 * @author PikyCZ
 */
public class Vindicator extends Mob {

    public Vindicator(EntityType<Vindicator> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(24);
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.95f;
    }

    @Override
    public String getName() {
        return "Vindicator";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(IRON_AXE)};
    }
}
