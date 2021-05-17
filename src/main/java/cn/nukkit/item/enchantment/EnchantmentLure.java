package cn.nukkit.item.enchantment;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentLure extends Enchantment {
    protected EnchantmentLure() {
        super(ID_LURE, "fishingSpeed", Rarity.RARE, EnchantmentType.FISHING_ROD);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return level + 8 * level + 6;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return getMinEnchantAbility(level) + 45 + level;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
