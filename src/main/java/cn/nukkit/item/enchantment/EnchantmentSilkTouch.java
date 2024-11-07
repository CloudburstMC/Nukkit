package cn.nukkit.item.enchantment;

import cn.nukkit.item.Item;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class EnchantmentSilkTouch extends Enchantment {

    protected EnchantmentSilkTouch() {
        super(ID_SILK_TOUCH, "untouching", Rarity.VERY_RARE, EnchantmentType.DIGGER);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 15;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return super.getMinEnchantAbility(level) + 50;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment.id != ID_FORTUNE_DIGGING;
    }

    @Override
    public boolean canEnchant(Item item) {
        return item.isShears() || super.canEnchant(item);
    }
}
