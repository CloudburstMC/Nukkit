package cn.nukkit.server.item;

import cn.nukkit.server.nbt.tag.CompoundTag;

/**
 * Created on 2015/12/27 by xtypr.
 * Package cn.nukkit.server.item in project Nukkit .
 */
public class ItemPotionSplash extends ProjectileItem {

    public ItemPotionSplash(Integer meta) {
        this(meta, 1);
    }

    public ItemPotionSplash(Integer meta, int count) {
        super(SPLASH_POTION, meta, count, "Splash Potion");
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
    public String getProjectileEntityType() {
        return "ThrownPotion";
    }

    @Override
    public float getThrowForce() {
        return 1f;
    }

    @Override
    protected void correctNBT(CompoundTag nbt) {
        nbt.putInt("PotionId", this.meta);
    }
}
