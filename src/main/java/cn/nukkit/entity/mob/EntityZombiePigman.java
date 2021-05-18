package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntitySmite;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.world.format.FullChunk;

/**
 * @author PikyCZ
 */
public class EntityZombiePigman extends EntityMob implements EntitySmite {

    public static final int NETWORK_ID = 36;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityZombiePigman(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.95f;
    }

    @Override
    public String getName() {
        return "ZombiePigman";
    }
}
