package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntityElderGuardian extends EntityMob {

    public static final int NETWORK_ID = 50;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityElderGuardian(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(80);
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ELDER, true);
    }

    @Override
    public float getWidth() {
        return 1.99f;
    }

    @Override
    public float getHeight() {
        return 1.99f;
    }

    @Override
    public String getName() {
        return "Elder Guardian";
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }
}
