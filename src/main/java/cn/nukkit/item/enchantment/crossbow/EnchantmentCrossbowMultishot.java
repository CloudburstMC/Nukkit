package cn.nukkit.item.enchantment.crossbow;

import cn.nukkit.api.Since;
import cn.nukkit.item.enchantment.Enchantment;

@Since("1.4.0.0-PN")
public class EnchantmentCrossbowMultishot extends EnchantmentCrossbow {

    @Since("1.4.0.0-PN")
    public EnchantmentCrossbowMultishot() {
        super(Enchantment.ID_CROSSBOW_MULTISHOT, "crossbowMultishot", Rarity.RARE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 20;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment.id != ID_CROSSBOW_PIERCING;
    }
}
