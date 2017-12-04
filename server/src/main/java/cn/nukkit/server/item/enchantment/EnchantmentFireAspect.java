package cn.nukkit.server.item.enchantment;

import cn.nukkit.server.entity.Entity;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentFireAspect extends Enchantment {
    protected EnchantmentFireAspect() {
        super(ID_FIRE_ASPECT, "fire", 2, EnchantmentType.SWORD);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 10 + (level - 1) * 20;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public void doPostAttack(Entity attacker, Entity entity) {
        entity.setOnFire(Math.max(entity.fireTicks * 20, getLevel() * 4));
    }
}
