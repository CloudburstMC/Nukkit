package cn.nukkit.item.enchantment;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentWaterWalker extends Enchantment {
    protected EnchantmentWaterWalker() {
        super(ID_WATER_WALKER, "waterWalker", Rarity.RARE, EnchantmentType.ARMOR_FEET);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return level * 10;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 10;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
