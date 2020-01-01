package cn.nukkit.entity.mob;

import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

import static cn.nukkit.entity.data.EntityFlag.ELDER;

/**
 * @author PikyCZ
 */
public class EntityElderGuardian extends EntityMob {

    public static final int NETWORK_ID = 50;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityElderGuardian(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(80);
        this.setFlag(ELDER, true);
    }

    @Override
    public float getWidth() {
        return 1.9975f;
    }

    @Override
    public float getHeight() {
        return 1.9975f;
    }

    @Override
    public String getName() {
        return "Elder Guardian";
    }
}
