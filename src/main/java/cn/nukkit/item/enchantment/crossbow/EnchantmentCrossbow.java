package cn.nukkit.item.enchantment.crossbow;

import cn.nukkit.api.Since;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

@Since("1.4.0.0-PN")
public abstract class EnchantmentCrossbow extends Enchantment {

    @Since("1.4.0.0-PN")
    protected EnchantmentCrossbow(int id, String name, Rarity rarity) {
        super(id, name, rarity, EnchantmentType.CROSSBOW);
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return 50;
    }
}
