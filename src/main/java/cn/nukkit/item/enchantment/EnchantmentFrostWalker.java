package cn.nukkit.item.enchantment;

public class EnchantmentFrostWalker extends Enchantment {
    protected EnchantmentFrostWalker() {
        super(ID_FROST_WALKER, "frostwalker", Rarity.VERY_RARE, EnchantmentType.ARMOR_FEET);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return level * 10;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    protected boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment.id != ID_WATER_WALKER;
    }
}
