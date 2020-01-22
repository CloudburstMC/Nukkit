package cn.nukkit.item;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSnowball extends ProjectileItem {

    public ItemSnowball(Identifier id) {
        super(id);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    public EntityType<?> getProjectileEntityType() {
        return EntityTypes.SNOWBALL;
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }
}
