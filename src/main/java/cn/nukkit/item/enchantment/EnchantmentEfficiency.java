package cn.nukkit.item.enchantment;

import cn.nukkit.item.Item;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class EnchantmentEfficiency extends Enchantment {

    protected EnchantmentEfficiency() {
        super(ID_EFFICIENCY, "digging", Rarity.COMMON, EnchantmentType.DIGGER);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 1 + (level - 1) * 10;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return super.getMinEnchantAbility(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean canEnchant(Item item) {
        return item.isShears() || super.canEnchant(item);
    }
}
