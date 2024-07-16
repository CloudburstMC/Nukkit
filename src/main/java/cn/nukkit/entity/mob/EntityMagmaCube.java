package cn.nukkit.entity.mob;

import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

public class EntityMagmaCube extends EntityJumpingMob {

    public static final int NETWORK_ID = 42;

    public EntityMagmaCube(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 1f;
    }

    @Override
    public float getHeight() {
        return 1f;
    }

    @Override
    public float getLength() {
        return 1f;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(16);
        super.initEntity();

        this.fireProof = true;
        this.noFallDamage = true;
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.MAGMA_CREAM, 0, Utils.rand(0, 1))};
    }

    @Override
    public int getKillExperience() {
        return 4;
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.getNameTag() : "Magma Cube";
    }
}
