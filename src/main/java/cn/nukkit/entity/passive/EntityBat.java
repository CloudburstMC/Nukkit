package cn.nukkit.entity.passive;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityBat extends EntityFlyingAnimal {

    public static final int NETWORK_ID = 19;

    public EntityBat(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 0.9f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(6);
        super.initEntity();
    }

    @Override
    public int getKillExperience() {
        return 0;
    }
}
