package cn.nukkit.item;

import cn.nukkit.nbt.tag.CompoundTag;

public class ItemPotionLingering extends ProjectileItem {

    public ItemPotionLingering() {
        this(0, 1);
    }

    public ItemPotionLingering(Integer meta) {
        this(meta, 1);
    }

    public ItemPotionLingering(Integer meta, int count) {
        super(LINGERING_POTION, meta, count, "Lingering Potion");
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
        return "ThrownLingeringPotion";
    }

    @Override
    public float getThrowForce() {
        return 0.50f;
    }

    @Override
    protected void correctNBT(CompoundTag nbt) {
        nbt.putInt("PotionId", this.meta);
    }
}
