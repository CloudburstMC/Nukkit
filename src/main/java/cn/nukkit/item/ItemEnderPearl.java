package cn.nukkit.item;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.utils.Identifier;

public class ItemEnderPearl extends ProjectileItem {

    public ItemEnderPearl(Identifier id) {
        super(id);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    public EntityType<?> getProjectileEntityType() {
        return EntityTypes.ENDER_PEARL;
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }
}
