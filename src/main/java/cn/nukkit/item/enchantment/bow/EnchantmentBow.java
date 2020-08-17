package cn.nukkit.item.enchantment.bow;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EnchantmentBow extends Enchantment {
    protected EnchantmentBow(int id, String name, int weight) {
        super(id, name, weight, EnchantmentType.BOW);
    }

}
