package cn.nukkit.entity.mob;

import cn.nukkit.item.Item;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntitySlime extends EntityMob {

    public static final int NETWORK_ID = 37;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntitySlime(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(16);
    }

    @Override
    public float getWidth() {
        return 2.04f;
    }

    @Override
    public float getHeight() {
        return 2.04f;
    }

    @Override
    public String getName() {
        return "Slime";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.SLIMEBALL)};
    }
}
