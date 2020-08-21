package cn.nukkit.item.enchantment.damage;

import cn.nukkit.entity.Entity;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EnchantmentDamageAll extends EnchantmentDamage {

    public EnchantmentDamageAll() {
        super(ID_DAMAGE_ALL, "all", 10, TYPE.ALL);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 1 + (level - 1) * 11;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 20;
    }

    @Override
    public int getMaxEnchantableLevel() {
        return 4;
    }

    @Override
    public double getDamageBonus(Entity entity) {
        if (this.getLevel() <= 0) {
            return 0;
        }

        return 0.5 + getLevel() * 0.5;
    }
}
