package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntityArthropod;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Box.
 */
public class EntityEndermite extends EntityMob implements EntityArthropod {

    public static final int NETWORK_ID = 55;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityEndermite(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(8);
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
    public String getName() {
        return "Endermite";
    }
}
