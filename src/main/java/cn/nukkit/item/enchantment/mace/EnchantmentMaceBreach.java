package cn.nukkit.item.enchantment.mace;

import cn.nukkit.item.enchantment.Enchantment;

public class EnchantmentMaceBreach extends EnchantmentMace {

    public EnchantmentMaceBreach() {
        super(EnchantmentMace.ID_BREACH, "breach", Rarity.RARE);
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) &&
                enchantment.id != EnchantmentMace.ID_DENSITY &&
                enchantment.id != EnchantmentMace.ID_DAMAGE_SMITE &&
                enchantment.id != EnchantmentMace.ID_DAMAGE_ARTHROPODS;
    }
}
