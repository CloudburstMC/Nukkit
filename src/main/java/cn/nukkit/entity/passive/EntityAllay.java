package cn.nukkit.entity.passive;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityAllay extends EntityFlyingAnimal {

    public static final int NETWORK_ID = 134;

    public EntityAllay(FullChunk chunk, CompoundTag nbt) {
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
        return 0.6f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
    }

    @Override
    public int getKillExperience() {
        return 0;
    }
}
