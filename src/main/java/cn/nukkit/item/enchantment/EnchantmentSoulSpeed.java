package cn.nukkit.item.enchantment;

public class EnchantmentSoulSpeed extends Enchantment {

    protected EnchantmentSoulSpeed() {
        super(ID_SOUL_SPEED, "soulSpeed", 1, EnchantmentType.ARMOR_FEET);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

}