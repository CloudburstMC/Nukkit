package cn.nukkit.entity.passive;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
public class EntityChicken extends EntityAnimal {

    public static final int NETWORK_ID = 10;

    public EntityChicken(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.3f;
        }
        return 0.6f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.4f;
        }
        return 0.8f;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Chicken";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(((this.isOnFire()) ? Item.COOKED_CHICKEN : Item.RAW_CHICKEN)), Item.get(Item.FEATHER)};
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        setMaxHealth(4);
    }

    @Override
    public boolean isBreedingItem(Item item) {
        int id = item.getId();

        return id == Item.WHEAT_SEEDS || id == Item.MELON_SEEDS || id == Item.PUMPKIN_SEEDS || id == Item.BEETROOT_SEEDS;
    }
}
