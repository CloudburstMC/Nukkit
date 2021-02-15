package cn.nukkit.item.enchantment;

public class EnchantmentFrostWalker extends Enchantment {
    protected EnchantmentFrostWalker() {
        super(ID_FROST_WALKER, "frostwalker", Rarity.RARE, EnchantmentType.ARMOR_FEET);
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
    public boolean isCompatibleWith(Enchantment enchantment) {
        return super.isCompatibleWith(enchantment) && enchantment.id != ID_WATER_WALKER;
    }
}
