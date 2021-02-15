package cn.nukkit.item.enchantment.crossbow;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

public abstract class EnchantmentCrossbow extends Enchantment {

    protected EnchantmentCrossbow(int id, String name, Rarity rarity) {
        super(id, name, rarity, EnchantmentType.CROSSBOW);
    }
}
