package cn.nukkit.entity.passive;

import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
public class EntityRabbit extends EntityAnimal {

    public static final int NETWORK_ID = 18;

    public EntityRabbit(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.268f;
        }
        return 0.67f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.268f;
        }
        return 0.67f;
    }

    @Override
    public String getName() {
        return "Rabbit";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(((this.isOnFire()) ? Item.COOKED_RABBIT : Item.RAW_RABBIT)), Item.get(Item.RABBIT_HIDE), Item.get(Item.RABBIT_FOOT)};
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        setMaxHealth(10);
    }
}
