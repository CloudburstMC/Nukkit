package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.format.FullChunk;
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

    public EntityEnderman(FullChunk chunk, CompoundTag nbt) {
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

    @PowerNukkitOnly
    @Since("FUTURE")
    @Override
    public String getOriginalName() {
        return "Enderman";
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return this.getDataPropertyBoolean(DATA_FLAG_ANGRY);
    }
}
