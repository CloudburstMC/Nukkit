package cn.nukkit.entity.passive;

import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

public class EntityNautilus extends EntityWalkingAnimal {

    public static final int NETWORK_ID = 149;

    public EntityNautilus(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(15);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        return this.isBaby() ? 0.44f : 0.875f;
    }

    @Override
    public float getHeight() {
        return this.isBaby() ? 0.5f : 0.95f;
    }

    @Override
    public Item[] getDrops() {
        if (!this.isBaby()) {
            return new Item[]{Item.get(Item.NAUTILUS_SHELL, 0, Utils.rand(0, 1))};
        }

        return new Item[0];
    }

    @Override
    public int getKillExperience() {
        return this.isBaby() ? 0 : Utils.rand(1, 3);
    }
}
