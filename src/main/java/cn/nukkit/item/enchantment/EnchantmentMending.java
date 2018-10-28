package cn.nukkit.item.enchantment;

/**
 * @author Rover656
 */
public class EnchantmentMending extends Enchantment {
    protected EnchantmentMending() {
        super(ID_MENDING, "mending", 2, EnchantmentType.ALL);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return level * 25;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isCompatibleWith(Enchantment enchantment) {
        return super.isCompatibleWith(enchantment) && enchantment.id != ID_BOW_INFINITY;
    }
}