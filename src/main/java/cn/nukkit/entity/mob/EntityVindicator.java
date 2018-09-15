package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntityType;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntityVindicator extends EntityMob {

    public static final int NETWORK_ID = 57;

    public EntityVindicator(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.VINDICATION_ILLAGER;
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
        return new Item[]{Item.get(Item.IRON_AXE)};
    }
}
