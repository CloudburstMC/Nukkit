package cn.nukkit.entity.mob;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.world.format.FullChunk;

public class EntitySnowGolem extends EntityMob {    
    public EntitySnowGolem(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
    
    public static final int NETWORK_ID = 21;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Snow Golem";
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(4);
    }
}
