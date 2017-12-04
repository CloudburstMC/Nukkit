package cn.nukkit.server.item.enchantment;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentLure extends Enchantment {
    protected EnchantmentLure() {
        super(ID_LURE, "fishingSpeed", 2, EnchantmentType.FISHING_ROD);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 15 + (level - 1) * 9;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

}
