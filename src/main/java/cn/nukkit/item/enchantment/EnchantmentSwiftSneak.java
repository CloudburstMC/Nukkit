package cn.nukkit.item.enchantment;

public class EnchantmentSwiftSneak extends Enchantment {

    protected EnchantmentSwiftSneak() {
        super(ID_SWIFT_SNEAK, "swift_sneak", Rarity.VERY_RARE, EnchantmentType.ARMOR_LEGS);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 10 * level;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
