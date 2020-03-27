package cn.nukkit.item;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.entity.Projectile;
import cn.nukkit.entity.projectile.LingeringPotion;
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
    public EntityType<? extends Projectile> getProjectileEntityType() {
        return EntityTypes.LINGERING_POTION;
    }

    @Override
    public float getThrowForce() {
        return 0.5f;
    }

    @Override
    protected void onProjectileCreation(Projectile projectile) {
        ((LingeringPotion) projectile).setPotionId(this.getMeta());
    }
}
