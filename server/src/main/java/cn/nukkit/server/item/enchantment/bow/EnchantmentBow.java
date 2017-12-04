package cn.nukkit.server.item.enchantment.bow;

import cn.nukkit.server.item.enchantment.Enchantment;
import cn.nukkit.server.item.enchantment.EnchantmentType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EnchantmentBow extends Enchantment {
    protected EnchantmentBow(int id, String name, int weight) {
        super(id, name, weight, EnchantmentType.BOW);
    }

}
