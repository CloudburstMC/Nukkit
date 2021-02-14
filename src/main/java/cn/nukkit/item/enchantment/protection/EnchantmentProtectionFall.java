package cn.nukkit.item.enchantment.protection;

import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentProtectionFall extends EnchantmentProtection {

    public EnchantmentProtectionFall() {
        super(ID_PROTECTION_FALL, "fall", Rarity.UNCOMMON, TYPE.FALL);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 5 + (level - 1) * 6;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 10;
    }

    @Override
    public double getTypeModifier() {
        return 2;
    }

    @Override
    public float getProtectionFactor(EntityDamageEvent e) {
        DamageCause cause = e.getCause();

        if (level <= 0 || (cause != DamageCause.FALL)) {
            return 0;
        }

        return (float) (getLevel() * getTypeModifier());
    }
}
