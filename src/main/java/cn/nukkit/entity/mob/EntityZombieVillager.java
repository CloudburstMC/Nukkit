package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntityType;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntityZombieVillager extends EntityMob {

    public static final int NETWORK_ID = 44;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.ZOMBIE_VILLAGER;
    }

    public EntityZombieVillager(FullChunk chunk, CompoundTag nbt) {
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
        return "ZombieVillager";
    }
}
