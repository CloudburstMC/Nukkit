package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntitySmite;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntityWither extends EntityMob implements EntitySmite {

    public static final int NETWORK_ID = 52;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityWither(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 3.5f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(300);
    }

    @Override
    public String getName() {
        return "Wither";
    }
}
