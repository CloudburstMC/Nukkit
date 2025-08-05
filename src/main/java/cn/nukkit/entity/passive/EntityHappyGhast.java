package cn.nukkit.entity.passive;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

public class EntityHappyGhast extends EntityFlyingAnimal {

    public static final int NETWORK_ID = 147;

    public EntityHappyGhast(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.95f;
        }
        return 4f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.95f;
        }
        return 4f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
    }

    @Override
    public int getKillExperience() {
        return this.isBaby() ? 0 : Utils.rand(2, 3);
    }
}
