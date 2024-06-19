package cn.nukkit.item.enchantment.bow;

import cn.nukkit.item.enchantment.Enchantment;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class EnchantmentBowFlame extends EnchantmentBow {
    public EnchantmentBowFlame() {
        super(Enchantment.ID_BOW_FLAME, "arrowFire", Rarity.RARE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 20;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return 50;
    }
}
