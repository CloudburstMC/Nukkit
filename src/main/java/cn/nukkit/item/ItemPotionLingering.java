package cn.nukkit.item;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Identifier;

public class ItemPotionLingering extends ProjectileItem {

    public ItemPotionLingering(Identifier id) {
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
    public EntityType<?> getProjectileEntityType() {
        return EntityTypes.LINGERING_POTION;
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
