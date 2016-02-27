package cn.nukkit.item.enchantment.protection;

import cn.nukkit.item.enchantment.Enchantment;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentProtectionAll extends EnchantmentProtection {

    public EnchantmentProtectionAll() {
        super(Enchantment.ID_PROTECTION_ALL, "all", 10, TYPE.ALL);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 1 + (level - 1) * 11;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 20;
    }
}
