package cn.nukkit.item.enchantment;

import cn.nukkit.api.Since;

@Since("1.4.0.0-PN")
public class EnchantmentSoulSpeed extends Enchantment {

    @Since("1.4.0.0-PN")
    protected EnchantmentSoulSpeed() {
        super(ID_SOUL_SPEED, "soul_speed", Rarity.VERY_RARE, EnchantmentType.ARMOR_FEET);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 10 * level;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return getMinEnchantAbility(level) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
