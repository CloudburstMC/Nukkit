package cn.nukkit.item.enchantment.crossbow;

import cn.nukkit.item.enchantment.Enchantment;

public class EnchantmentCrossbowQuickCharge extends EnchantmentCrossbow {

    public EnchantmentCrossbowQuickCharge() {
        super(Enchantment.ID_CROSSBOW_QUICK_CHARGE, "crossbowQuickCharge", Rarity.UNCOMMON);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 12 + 20 * (level - 1);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
