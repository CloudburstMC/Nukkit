package cn.nukkit.entity.mob;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityCreaking extends EntityWalkingMob {

    public static final int NETWORK_ID = 146;

    public EntityCreaking(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(1);
        super.initEntity();
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 2.7f;
    }

    @Override
    public int getKillExperience() {
        return 0;
    }
}
