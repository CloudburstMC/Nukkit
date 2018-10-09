package cn.nukkit.item.enchantment;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentWaterBreath extends Enchantment {
    protected EnchantmentWaterBreath() {
        super(ID_WATER_BREATHING, "oxygen", 2, EnchantmentType.ARMOR_TORSO);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 10 * level;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 30;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
