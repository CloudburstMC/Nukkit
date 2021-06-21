package cn.nukkit.item.enchantment.crossbow;

import cn.nukkit.api.Since;
import cn.nukkit.item.enchantment.Enchantment;

@Since("1.4.0.0-PN")
public class EnchantmentCrossbowQuickCharge extends EnchantmentCrossbow {

    @Since("1.4.0.0-PN")
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
