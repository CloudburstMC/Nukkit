package cn.nukkit.entity.mob;

import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntityEnderman extends EntityMob {

    public static final int NETWORK_ID = 38;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityEnderman(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(40);
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 2.9f;
    }

    @Override
    public String getName() {
        return "Enderman";
    }
}
