package cn.nukkit.item.enchantment.mace;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

public abstract class EnchantmentMace extends Enchantment {

    protected EnchantmentMace(int id, String name, Rarity rarity) {
        super(id, name, rarity, EnchantmentType.MACE);
    }

    @Override
    public String getName() {
        return "%enchantment.heavy_weapon." + this.name;
    }
}
