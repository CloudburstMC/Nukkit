package cn.nukkit.entity.passive;

import cn.nukkit.entity.mob.EntityTameableMob;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityWolf extends EntityTameableMob {

    public static final int NETWORK_ID = 14;

    public EntityWolf(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 0.8f;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(8);
        super.initEntity();
    }

    @Override
    public int getKillExperience() {
        return this.isBaby() ? 0 : 3;
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.getNameTag() : "Wolf";
    }
}
