package cn.nukkit.entity.mob;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityVex extends EntityFlyingMob {

    public static final int NETWORK_ID = 105;

    public EntityVex(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.8f;
    }

    @Override
    public float getHeight() {
        return 0.4f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(14);
        super.initEntity();
    }

    @Override
    public int getKillExperience() {
        return 3;
    }
}
