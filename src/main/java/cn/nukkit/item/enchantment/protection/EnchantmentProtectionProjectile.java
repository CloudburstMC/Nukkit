package cn.nukkit.item.enchantment.protection;

import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentProtectionProjectile extends EnchantmentProtection {

    public EnchantmentProtectionProjectile() {
        super(ID_PROTECTION_PROJECTILE, "projectile", 5, TYPE.PROJECTILE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 3 + (level - 1) * 6;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 15;
    }

    @Override
    public double getTypeModifier() {
        return 3;
    }

    @Override
    public float getProtectionFactor(EntityDamageEvent e) {
        DamageCause cause = e.getCause();

        if (level <= 0 || (cause != DamageCause.PROJECTILE)) {
            return 0;
        }

        return (float) (getLevel() * getTypeModifier());
    }
}
