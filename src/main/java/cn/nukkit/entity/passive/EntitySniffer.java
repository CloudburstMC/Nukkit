package cn.nukkit.entity.passive;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

public class EntitySniffer extends EntityWalkingAnimal {

    public static final int NETWORK_ID = 139;

    public EntitySniffer(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(14);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.855f;
        }
        return 1.9f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.7875f;
        }
        return 1.75f;
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
