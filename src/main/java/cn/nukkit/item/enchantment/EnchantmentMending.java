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
        return 25 + (level - 1) * 9;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 50;
    }

    @Override
    public int getMaxLevel() { return 1; }

    @Override
    public boolean isCompatibleWith(Enchantment enchantment) {
        return super.isCompatibleWith(enchantment) && enchantment.id != ID_BOW_INFINITY;
    }
}