package cn.nukkit.item;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.entity.projectile.Projectile;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Identifier;

/**
 * Created on 2015/12/27 by xtypr.
 * Package cn.nukkit.item in project Nukkit .
 */
public class ItemPotionSplash extends ProjectileItem {

    public ItemPotionSplash(Identifier id) {
        super(id);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public EntityType<? extends Projectile> getProjectileEntityType() {
        return EntityTypes.SPLASH_POTION;
    }

    @Override
    public float getThrowForce() {
        return 0.5f;
    }

    @Override
    protected void correctNBT(CompoundTag nbt) {
        nbt.putInt("PotionId", this.getDamage());
    }
}
