package cn.nukkit.item.enchantment;

public class EnchantmentSoulSpeed extends Enchantment {

    protected EnchantmentSoulSpeed() {
        super(ID_SOUL_SPEED, "soul_speed", Rarity.VERY_RARE, EnchantmentType.ARMOR_FEET);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 10 * level;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }
}
