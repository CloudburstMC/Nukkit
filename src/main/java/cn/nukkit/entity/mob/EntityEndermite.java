package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntityArthropod;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityEndermite extends EntityWalkingMob implements EntityArthropod {

    public static final int NETWORK_ID = 55;

    public EntityEndermite(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.4f;
    }

    @Override
    public float getHeight() {
        return 0.3f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(8);
        super.initEntity();
    }

    @Override
    public int getKillExperience() {
        return 3;
    }
}
