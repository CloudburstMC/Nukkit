package cn.nukkit.item.enchantment;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHumanType;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentThorns extends Enchantment {
    protected EnchantmentThorns() {
        super(ID_THORNS, "thorns", 2, EnchantmentType.ARMOR_TORSO);
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
        return 3;
    }

    @Override
    public void doPostAttack(Entity attacker, Entity entity) {
        if (!(entity instanceof EntityHumanType)) {
            return;
        }

        EntityHumanType human = (EntityHumanType) entity;

        int thornsLevel = 0;

        for (Item armor : human.getInventory().getArmorContents()) {
            Enchantment thorns = armor.getEnchantment(Enchantment.ID_THORNS);
            if (thorns != null) {
                thornsLevel = Math.max(thorns.getLevel(), thornsLevel);
            }
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();

        if (shouldHit(random, thornsLevel)) {
            attacker.attack(new EntityDamageByEntityEvent(entity, attacker, EntityDamageEvent.DamageCause.MAGIC, getDamage(random, level)));
        }
    }

    private static boolean shouldHit(ThreadLocalRandom random, int level) {
        return level > 0 && random.nextFloat() < 0.15 * level;
    }

    private static int getDamage(ThreadLocalRandom random, int level) {
        return level > 10 ? level - 10 : random.nextInt(1, 5);
    }
}
