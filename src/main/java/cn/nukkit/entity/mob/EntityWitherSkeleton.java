package cn.nukkit.entity.mob;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntityWitherSkeleton extends EntityMob {

    public static final int NETWORK_ID = 48;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityWitherSkeleton(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 2.4f;
    }

    @Override
    public String getName() {
        return "WitherSkeleton";
    }
}
