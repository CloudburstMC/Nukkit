package cn.nukkit.entity.mob;

import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

public class EntitySnowGolem extends EntityWalkingMob {

    public static final int NETWORK_ID = 21;

    public EntitySnowGolem(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(4);
        super.initEntity();
        this.noFallDamage = true;
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.SNOWBALL, 0, Utils.rand(0, 15))};
    }

    @Override
    public int getKillExperience() {
        return 0;
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.getNameTag() : "Snow Golem";
    }
}
