package cn.nukkit.entity.mob;

import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityRavager extends EntityWalkingMob {

    public static final int NETWORK_ID = 59;

    public EntityRavager(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(100);
        super.initEntity();
    }

    @Override
    public float getHeight() {
        return 2.2f;
    }

    @Override
    public float getWidth() {
        return 1.9f;
    }

    @Override
    public int getKillExperience() {
        return 20;
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.SADDLE, 0, 1)};
    }
}