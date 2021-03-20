package cn.nukkit.item.enchantment.crossbow;

import cn.nukkit.api.Since;
import cn.nukkit.item.enchantment.Enchantment;

@Since("1.4.0.0-PN")
public class EnchantmentCrossbowPiercing extends EnchantmentCrossbow {

    @Since("1.4.0.0-PN")
    public EnchantmentCrossbowPiercing() {
        super(Enchantment.ID_CROSSBOW_PIERCING, "crossbowPiercing", Rarity.COMMON);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 1 + 10 * (level - 1);
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment.id != ID_CROSSBOW_MULTISHOT;
    }
}
