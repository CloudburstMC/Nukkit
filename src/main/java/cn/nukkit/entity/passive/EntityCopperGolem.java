package cn.nukkit.entity.passive;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityCopperGolem extends EntityWalkingAnimal {

    public static final int NETWORK_ID = 148;

    public EntityCopperGolem(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(12);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        return 0.875f;
    }

    @Override
    public float getHeight() {
        return 1f;
    }

    @Override
    public int getKillExperience() {
        return 0;
    }
}
