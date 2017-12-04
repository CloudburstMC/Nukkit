package cn.nukkit.server.item.enchantment.damage;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.enchantment.Enchantment;
import cn.nukkit.server.item.enchantment.EnchantmentType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EnchantmentDamage extends Enchantment {

    public enum TYPE {
        ALL,
        SMITE,
        ARTHROPODS
    }

    protected final TYPE damageType;

    protected EnchantmentDamage(int id, String name, int weight, TYPE type) {
        super(id, name, weight, EnchantmentType.SWORD);
        this.damageType = type;
    }

    @Override
    public boolean isCompatibleWith(Enchantment enchantment) {
        return !(enchantment instanceof EnchantmentDamage);
    }

    @Override
    public boolean canEnchant(Item item) {
        return item.isAxe() || super.canEnchant(item);
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public String getName() {
        return "%enchantment.damage." + this.name;
    }

    @Override
    public boolean isMajor() {
        return true;
    }
}
