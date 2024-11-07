package cn.nukkit.entity.passive;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

public class EntityArmadillo extends EntityWalkingAnimal {

    public static final int NETWORK_ID = 142;

    public EntityArmadillo(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(12);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.42f;
        }
        return 0.7f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.39f;
        }
        return 0.65f;
    }

    @Override
    public int getKillExperience() {
        return this.isBaby() ? 0 : Utils.rand(1, 3);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }
}
