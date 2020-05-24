package cn.nukkit.entity.mob;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntitySnowGolem extends EntityMob {
    public static final int NETWORK_ID = 21;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntitySnowGolem(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}