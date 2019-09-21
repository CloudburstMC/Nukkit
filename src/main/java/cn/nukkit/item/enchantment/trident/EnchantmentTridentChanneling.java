package cn.nukkit.item.enchantment.trident;

import cn.nukkit.item.enchantment.Enchantment;

public class EnchantmentTridentChanneling extends EnchantmentTrident {
    public EnchantmentTridentChanneling() {
        super(Enchantment.ID_TRIDENT_CHANNELING, "channeling", 1);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 20;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}
